import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentLinkedDeque

class ScreenNavigator(application: Application): Navigator, CoroutineScope {
    override val coroutineContext = SupervisorJob() + Dispatchers.Main
    override val screen: Navigator.ScreenType get() = currentScreen

    private val routeFactory: Routes by lazy { Routes(navController()) }
    private val pendingTasks = ConcurrentLinkedDeque<NavigateTask>()
    private val listeners = mutableListOf<ScreenChangeListener>()
    private val navigationTaskChannel = ConflatedBroadcastChannel<Unit>()
    private val stateChannel = ConflatedBroadcastChannel(NavState.IDLE)
    private val activityLifeCycleChannel = ConflatedBroadcastChannel(isFragmentStateSaved().not())
    private val lifecycleEventObserver = lazy {
        LifecycleEventObserver { _, event ->
            activityLifeCycleChannel.offer(event == Lifecycle.Event.ON_RESUME)
        }
    }
    private val initCompleteChannel = ConflatedBroadcastChannel(false)
    private val currentScreenChannel = ConflatedBroadcastChannel(ScreenType.UNKNOWN)

    private var currentScreen: ScreenType = ScreenType.UNKNOWN
    private var previousScreen: ScreenType = ScreenType.UNKNOWN
    private var weakActivity: WeakReference<Activity?> = WeakReference(null)


    init {
        application.registerActivityLifecycleCallbacks(object :SimpleActivityLifeCycleCallback() {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                super.onActivityCreated(activity, savedInstanceState)
                weakActivity = WeakReference(activity)
                init()
            }
        })
    }

    override fun nextScreen(
        screen: ScreenType?,
        params: IScreenNavigator.Params?,
        extras: Map<View, String>?
    ) {
        if (screen == null || navigationTaskChannel.isClosedForSend) {
            return
        }
        val screenExtras = extras?.toScreenExtras()
        pendingTasks.add(
            NavigateTask(
                screen = screen,
                params = params,
                isPopBackStack = false,
                extras = screenExtras
            )
        )
        navigationTaskChannel.offer(Unit)
    }

    override fun backPressHandled(): Boolean {
        return if (currentScreen.hasBackStack.not()) {
            false
        } else {
            pendingTasks.add(NavigateTask(isPopBackStack = true))
            navigationTaskChannel.offer(Unit)
        }
    }

    override fun addScreenChangeListener(listener: ScreenChangeListener) {
        listeners.add(listener)
    }

    override fun removeScreenChangeListener(listener: ScreenChangeListener) {
        listeners.remove(listener)
    }

    private fun init() {
        (weakActivity.get() as? AppCompatActivity)?.lifecycle?.addObserver(lifecycleEventObserver.value)


        fun openPendingScreen(task: NavigateTask) {
            stateChannel.offer(NavState.IN_PROGRESS)
            launch(coroutineContext) {
                if (task.isPopBackStack) {
                    controller.popBackStack()
                    logBackStack(BACK)
                    return@launch
                }
                if (currentScreen != task.screen) {
                    createRoute(task.screen, task.params, task.extras)
                        ?.navigate()
                        ?: stateChannel.offer(NavState.IDLE)
                    logBackStack("$NAVIGATE ${task.screen.label}")
                } else {
                    stateChannel.offer(NavState.IDLE)
                }
            }
        }

        val navigationFlow = navigationTaskChannel.asFlow()
        val stateFlow = stateChannel
            .asFlow()
            .filter { it == NavState.IDLE }
        val activityLifeCycleFlow = activityLifeCycleChannel
            .asFlow()
            .filter { it }

        launch(coroutineContext) {
            navigationFlow
                .combine(stateFlow) { _, _ -> }
                .combine(activityLifeCycleFlow) { _, _ -> }
                .flowOn(Dispatchers.Main)
                .collect {
                    if (pendingTasks.isEmpty().not()) {
                        pendingTasks.poll()?.let {
                            openPendingScreen(it)
                        }
                    }
                }
        }
    }

    private fun isFragmentStateSaved(): Boolean {
        return weakActivity.get()?.supportFragmentManager?.isStateSaved ?: false
    }

    private fun notifyScreenChanged(screen: ScreenType) {
        listeners.forEach { it.screenChanged(screen) }
    }

    private fun createRoute(
        screen: ScreenType,
        params: IScreenNavigator.Params? = null,
        extras: FragmentNavigator.Extras? = null
    ): IRoute? {
        return try {
            routeFactory.create(from = currentScreen, to = screen, params = params, extras = extras)
        } catch (e: UnknownRouteException) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Timber.e(e)
            null
        }
    }

    private data class NavigateTask(
        val screen: Navigator.ScreenType = ScreenType.UNKNOWN,
        val params: Navigator.Params? = null,
        val isPopBackStack: Boolean = false
    )

    private enum class NavState {
        IDLE, IN_PROGRESS
    }
}