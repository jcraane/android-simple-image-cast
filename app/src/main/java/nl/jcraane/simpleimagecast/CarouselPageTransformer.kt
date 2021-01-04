package nl.jcraane.simpleimagecast

import android.view.View
import androidx.core.view.ViewCompat
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs
import kotlin.math.max

/**
 * @property translationYFactor Determines how much translation occurs over the y-axis. The higher the factor the more translation.
 */
class CarouselPageTransformer(
    private val pageMargin: Int,
    private val pageOffset: Int,
    private val viewPager: ViewPager2,
    private val translationYFactor: Float = 100f
) : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        val absPos = abs(position)
        val offset = position * -(2 * pageMargin + pageOffset)

        val translationX = if (viewPager.orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
            if (ViewCompat.getLayoutDirection(viewPager) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                -offset
            } else {
                offset
            }
        } else {
            offset
        }

        page.apply {
            translationY = absPos * translationYFactor
            page.translationX = translationX
        }
        val nonVisibleAlpha = 0.5f
        when {
            position < -1 -> page.alpha = nonVisibleAlpha
            position <= 1 -> {
                page.alpha = max(nonVisibleAlpha, 1 - abs(position))
            }
            else -> page.alpha = nonVisibleAlpha
        }
    }
}