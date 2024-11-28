package com.payby.pos.common.helper

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

object Executors {

    val mainThread: Executor = MainThreadExecutor()

    val diskIO: Executor = Executors.newSingleThreadExecutor()

    val networkIO: Executor = Executors.newFixedThreadPool(3)

    private class MainThreadExecutor : Executor {

        private val looper = Looper.getMainLooper()
        private val mainThreadHandler = Handler(looper)

        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }

    }

}