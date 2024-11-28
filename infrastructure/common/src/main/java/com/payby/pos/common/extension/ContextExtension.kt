package com.payby.pos.common.extension

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import androidx.annotation.ColorRes
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment

fun Context.getColorRes(@ColorRes resId: Int): Int {
    return this.resources.getColor(resId)
}

fun Activity.getColorRes(@ColorRes resId: Int): Int {
    return this.resources.getColor(resId)
}

fun Fragment.getColorRes(@ColorRes resId: Int): Int {
    return this.resources.getColor(resId)
}

fun Context.getFontRes(@FontRes resId: Int): Typeface ? {
    return ResourcesCompat.getFont(this, resId)
}

fun Activity.getFontRes(@FontRes resId: Int): Typeface ? {
    return ResourcesCompat.getFont(this, resId)
}

fun Fragment.getFontRes(@FontRes resId: Int): Typeface ? {
    return ResourcesCompat.getFont(requireContext(), resId)
}