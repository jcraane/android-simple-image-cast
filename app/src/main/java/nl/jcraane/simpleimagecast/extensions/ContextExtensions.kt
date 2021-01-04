package nl.jcraane.simpleimagecast

import android.content.Context
import android.util.TypedValue

fun Context.convertToPixels(dip: Float): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dip, resources.displayMetrics
    ).toInt()
}