import android.view.View
import androidx.fragment.app.FragmentManager

interface Navigator {
    val screen: ScreenType

    fun nextScreen(
        fragmentManager: FragmentManager,
        screen: ScreenType? = null,
        params: Params? = null
    )

    fun backPressHandled(): Boolean

    fun addScreenChangeListener(listener: ScreenChangeListener)

    fun removeScreenChangeListener(listener: ScreenChangeListener)

    interface Params

    interface Result

    interface ScreenType
}