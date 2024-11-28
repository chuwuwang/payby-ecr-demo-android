package com.payby.pos.common.extension

import android.util.Log

inline fun <T> nonNullExecute(receiver: T ? , block: T.() -> Unit) {
    if (receiver == null) {
        Log.e("ktx", "The depend on call is null")
    } else {
        receiver.block()
    }
}

val String.empty: Boolean
    get() = isBlank() || isEmpty()

val String.valid: Boolean
    get() = isNotEmpty() && isNotBlank()

val <E> Collection<E>.empty: Boolean
    get() = isNullOrEmpty()

val <E> Collection<E>.valid: Boolean
    get() = isNotEmpty()

val String.isDataEmpty: Boolean
    get() = empty || this == "--"
