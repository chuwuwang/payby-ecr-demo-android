package com.payby.pos.ecr.connect

import android.Manifest
import android.app.Activity
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.payby.pos.ecr.App
import com.payby.pos.ecr.bluetooth.BTOperate
import com.payby.pos.ecr.bluetooth.ClassicBTManager
import com.payby.pos.ecr.bluetooth.ClassicBTService
import com.payby.pos.ecr.bluetooth.ConnectionListener
import com.payby.pos.ecr.inapp.InAppServiceBinder
import com.payby.pos.ecr.internal.InAppCallback
import com.payby.pos.ecr.utils.ThreadPoolManager
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.callback.RequestCallback

object ConnectService {


  var connectType = ConnectType.BLUETOOTH
  var connectCallback: ((Boolean) -> Unit)? = null


  var callback: (bytes: ByteArray) -> Unit = {}

  fun connect(context: Activity, connectType: ConnectType) {
    this.connectType =connectType
    when (connectType) {
      ConnectType.BLUETOOTH -> {
        ClassicBTManager.getInstance().addListener(connectionListener)
        BTOperate(context).findPairedBTDevices()
      }

      ConnectType.IN_APP -> {
        InAppServiceBinder.connectCallback = {
          connectCallback?.invoke(true)
        }
        InAppServiceBinder.bindInAppService(App.instance)
      }

      else -> {}
    }

  }

  fun disconnect() {
    when (connectType) {
      ConnectType.IN_APP -> {
        InAppServiceBinder.unregister()
      }

      ConnectType.BLUETOOTH -> {
        val connected = ClassicBTManager.getInstance().isConnected
        if (connected) {
          ClassicBTManager.getInstance().removeListener(connectionListener)
          ClassicBTManager.getInstance().disconnect()
          ClassicBTService.startAction(App.instance, ClassicBTService.ACTION_DISCONNECT, null)
        }
      }
      else -> {}
    }
  }

  fun isConnected(): Boolean {
    when (connectType) {
      ConnectType.BLUETOOTH -> {
        return ClassicBTManager.getInstance().isConnected
      }

      ConnectType.IN_APP -> {
        return false
      }

      else -> {}
    }
    return false
  }

  fun send(byteArray: ByteArray, callback: (bytes: ByteArray) -> Unit) {
    ConnectService.callback = callback

    when (connectType) {
      ConnectType.BLUETOOTH -> {
        ClassicBTManager.getInstance().send(byteArray)
      }

      ConnectType.IN_APP -> {
        ThreadPoolManager.executeCacheTask {
          InAppServiceBinder.send(byteArray, inAppCallback)
        }
      }

      else -> {}
    }
  }

  private val inAppCallback = object : InAppCallback.Stub() {

    override fun onReceive(bytes: ByteArray) {
      callback.invoke(bytes)
    }

  }


  private val connectionListener: ConnectionListener = object : ConnectionListener {
    override fun onConnected() {
      Log.e("Demo", "==onConnected==")
      connectCallback?.invoke(true)

    }

    override fun onDisconnected() {
      Log.e("Demo", "==onDisconnected==")
      connectCallback?.invoke(false)

    }

    override fun onMessage(bytes: ByteArray) {
      callback.invoke(bytes)
    }
  }

  fun initPermission(activity: FragmentActivity, callback: RequestCallback) {
    val permissions: MutableList<String> = ArrayList()
    permissions.add(Manifest.permission.BLUETOOTH)
    permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
    permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
    PermissionX.init(activity).permissions(permissions).request(callback)
  }
}