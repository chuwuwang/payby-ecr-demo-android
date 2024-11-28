package com.payby.pos.common.extension

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment

inline fun <reified T : Activity> Activity.navigateTo() {
    val intent = Intent(this, T::class.java)
    startActivity(intent)
}

inline fun <T : Activity> Activity.navigateTo(activity: Class<T>, options: Bundle ? = null, crossinline extras: Intent.() -> Unit = { } ) {
    this.startActivity(Intent(this, activity).apply { this.extras() }, options)
}

inline fun <reified T : Activity> Activity.navigateTo(block: Intent.() -> Unit) {
    val intent = Intent(this, T::class.java)
    intent.block()
    startActivity(intent)
}

inline fun <reified T : Activity> Fragment.navigateTo() {
    val intent = Intent(context, T::class.java)
    startActivity(intent)
}

inline fun <reified T : Activity> navigateTo(context: Context) {
    val intent = Intent(context, T::class.java)
    context.startActivity(intent)
}

inline fun <reified T : Activity> navigateTo(context: Context, block: Intent.() -> Unit) {
    val intent = Intent(context, T::class.java)
    intent.block()
    context.startActivity(intent)
}

inline fun <reified T : Activity> Activity.navigateTo(launch: ActivityResultLauncher<Intent>, block: Intent.() -> Unit) {
    val intent = Intent(this, T::class.java)
    intent.block()
    launch.launch(intent)
}