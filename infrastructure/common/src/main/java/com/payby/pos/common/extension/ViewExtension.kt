package com.payby.pos.common.extension

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

fun View.getString(@StringRes resId: Int) = context.getString(resId)

fun View.hide() {
    this.visibility = View.GONE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

private var lastClickTime: Long = 0
private const val MIN_CLICK_DELAY_TIME = 1000
val isFastClick: Boolean
    get() {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - lastClickTime >= MIN_CLICK_DELAY_TIME) {
            return true
        }
        lastClickTime = currentTimeMillis
        return false
    }
fun View.setOnSingleClickListener(listener: (View) -> Unit) {
    if (isFastClick) this.setOnClickListener(listener)
}

fun ViewPager2.cancelOverScrollMode() {
    try {
        val field = javaClass.getDeclaredField("mRecyclerView")
        field.isAccessible = true
        val instance = field.get(this)
        if (instance is RecyclerView) {
            instance.overScrollMode = View.OVER_SCROLL_NEVER
        }
    } catch (ex: Throwable) {
        ex.printStackTrace()
    }
}

fun EditText.disableSoftInputKeyboard() {
    val textWatcher = object : TextWatcher {

        override fun beforeTextChanged(p0: CharSequence ? , p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence ? , p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(editable: Editable ? ) {
            if (editable != null) setSelection(text.length)
        }

    }
    showSoftInputOnFocus = false
    addTextChangedListener(textWatcher)
}

fun View.changeMarginTop(top: Int) {
    val layoutParams = this.layoutParams
    if (layoutParams is LinearLayout.LayoutParams) {
        layoutParams.topMargin = this.dimen(top)
    } else if (layoutParams is RelativeLayout.LayoutParams) {
        layoutParams.topMargin = this.dimen(top)
    }
    this.layoutParams = layoutParams
}

fun View.changeHeight(height: Int) {
    val layoutParams = this.layoutParams
    if (layoutParams is LinearLayout.LayoutParams) {
        layoutParams.height = this.dimen(height)
    } else if (layoutParams is RelativeLayout.LayoutParams) {
        layoutParams.height = this.dimen(height)
    }
    this.layoutParams = layoutParams
}