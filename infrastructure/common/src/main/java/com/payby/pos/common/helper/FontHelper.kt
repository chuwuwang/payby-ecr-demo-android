package com.payby.pos.common.helper

import android.graphics.Typeface
import android.widget.TextView

object FontHelper {

    fun setFontBold(textView: TextView) {
        FontHelper.setFontFamily(textView, "font/roboto_bold", Typeface.BOLD)
    }

    fun setFontRegular(textView: TextView) {
        FontHelper.setFontFamily(textView, "font/roboto_regular", Typeface.NORMAL)
    }

    fun setFontFamily(textView: TextView, familyName: String, style: Int) {
        val typeface = Typeface.create(familyName, style)
        if (typeface != null) textView.typeface = typeface
    }

}