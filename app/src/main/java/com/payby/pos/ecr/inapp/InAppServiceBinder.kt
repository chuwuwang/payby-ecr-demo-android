package com.payby.pos.ecr.inapp

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.IBinder.DeathRecipient
import android.os.Message
import android.util.Log
import android.widget.Toast
import com.payby.pos.ecr.App
import com.payby.pos.ecr.BuildConfig
import com.payby.pos.ecr.internal.InAppCallback
import com.payby.pos.ecr.internal.InAppServiceEngine

@SuppressLint("StaticFieldLeak")
object InAppServiceBinder {

  private lateinit var context: Context
  private var inAppServiceEngine: InAppServiceEngine? = null

  private var count = 0
  private var packageName = ""

   var connectCallback: (() -> Unit)? = null


  fun bindInAppService(context: Context) {
    InAppServiceBinder.context = context
    if (BuildConfig.BUILD_TYPE == "release") {
      packageName = "com.pay" + "by.pos.acquirer"
    } else {
      packageName = "com.pay" + "by.pos.acquirer.uat"
    }
    val isAppExist = isAppExist(context, packageName)
    if (isAppExist == false) {
      Toast.makeText(context, "PayBy POS is not installed", Toast.LENGTH_SHORT).show()
      return
    }
    bindService()
    handler.removeCallbacksAndMessages(null)
    handler.sendEmptyMessageDelayed(0, 2000)
  }

  private fun bindService() {
    val intent = Intent()
    intent.setAction("com.pay" + "by.pos.ecr.ACTION_IN" + "APP_BRIDGE")
    intent.setPackage(packageName)
    intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(intent)
      } else {
        context.startService(intent)
      }
      context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    } catch (e: Throwable) {
      e.printStackTrace()
    }
  }

  fun send(byteArray: ByteArray, callback: InAppCallback) {
    val engine = inAppServiceEngine
    if (engine != null) {
      engine.send(byteArray, callback)
    }
  }

  fun register(callback: InAppCallback) {
    try {
      val engine = inAppServiceEngine
      if (engine != null) {
        engine.register(callback)
      }
    } catch (e: Throwable) {
      e.printStackTrace()
    }
  }

  fun unregister() {
    try {
      val engine = inAppServiceEngine
      if (engine != null) {
        engine.unregister()
      }
    } catch (e: Throwable) {
      e.printStackTrace()
    }
  }

  private val serviceConnection = object : ServiceConnection {

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
      Log.e(App.TAG, "onServiceConnected name: $name")
      val engine = InAppServiceEngine.Stub.asInterface(service)
      connectCallback?.invoke()
      inAppServiceEngine = engine
      if (service != null) {
        linkToDeath(service)
      }
      count = 0
      handler.removeCallbacksAndMessages(null)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
      Log.e(App.TAG, "onServiceDisconnected name: $name")
      inAppServiceEngine = null
      bindInAppService(context)
    }

  }

  private fun linkToDeath(service: IBinder) {
    try {
      val deathRecipient = DeathRecipient {
        Log.e(App.TAG, "linkToDeath")
        inAppServiceEngine = null
        bindInAppService(context)
      }
      service.linkToDeath(deathRecipient, 0)
    } catch (e: Throwable) {
      e.printStackTrace()
    }
  }

  private val handler = @SuppressLint("HandlerLeak") object : Handler() {

    override fun handleMessage(msg: Message) {
      super.handleMessage(msg)
      count++
      removeMessages(0)
      if (count >= 5) {
        Toast.makeText(
          context,
          "Failed to connect to PayBy POS, please try again",
          Toast.LENGTH_SHORT
        ).show()
      } else {
        bindService()
        sendEmptyMessageDelayed(0, 2000)
      }
    }

  }

  fun isAppExist(context: Context, packageName: String?): Boolean {
    var info = try {
      context.packageManager.getApplicationInfo(packageName!!, 0)
    } catch (e: Exception) {
      null
    }
    return info != null
  }

}