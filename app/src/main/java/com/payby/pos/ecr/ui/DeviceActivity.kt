package com.payby.pos.ecr.ui

import com.google.protobuf.Timestamp
import com.kongzue.dialogx.dialogs.WaitDialog
import com.payby.pos.ecr.connect.ConnectService.send
import com.payby.pos.ecr.databinding.ActivityDeviceBinding
import com.payby.pos.ecr.internal.processor.Processor
import com.payby.pos.ecr.ui.ResultActivity.Companion.start
import com.payby.pos.ecr.ui.base.ViewBindingActivity
import com.uaepay.pos.ecr.Ecr
import com.uaepay.pos.ecr.Ecr.EcrEnvelope

class DeviceActivity : ViewBindingActivity<ActivityDeviceBinding>() {
  var processor: Processor = Processor()


  override fun init() {

    processor.onDeviceInfo = {
      runOnUiThread {
        binding.widgetTxtReceive.text =
          binding.widgetTxtReceive.getText().toString() + "/" + parserResponse(it)
        start(this, binding.widgetTxtReceive.getText().toString())
      }
    }
    binding.getDeviceInfo.setOnClickListener {
      getDeviceInfo()

    }

  }

  fun getDeviceInfo() {
    val timestamp = Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000).build()
    val request = Ecr.Request.newBuilder().setMessageId(4).setTimestamp(timestamp).setServiceName(
      Processor.DEVICE_GET_THIS
    ).build()
    val envelope = EcrEnvelope.newBuilder().setVersion(1).setRequest(request).build()
    val byteArray = envelope.toByteArray()
    send(byteArray) { bytes: ByteArray ->
      runOnUiThread { WaitDialog.dismiss() }
      processor.messageHandle(bytes)
    }
  }

}