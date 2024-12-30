package com.payby.pos.ecr.ui

import com.google.protobuf.Any
import com.google.protobuf.Timestamp
import com.kongzue.dialogx.dialogs.WaitDialog
import com.payby.pos.ecr.connect.ConnectService.send
import com.payby.pos.ecr.connect.ConnectionKernel
import com.payby.pos.ecr.databinding.ActivitySettlementBinding
import com.payby.pos.ecr.internal.processor.Processor
import com.payby.pos.ecr.ui.ResultActivity.Companion.start
import com.payby.pos.ecr.ui.base.ViewBindingActivity
import com.uaepay.pos.ecr.Ecr
import com.uaepay.pos.ecr.Ecr.EcrEnvelope
import com.uaepay.pos.ecr.acquire.Settlement

class SettlementActivity : ViewBindingActivity<ActivitySettlementBinding>() {



  override fun init() {
    binding.settlement.setOnClickListener {
      val operatorId = binding.editInputMoney.text.toString()
      if (operatorId.isEmpty()) {
        showToast("Please input operatorId")
        return@setOnClickListener
      }
      startSettlement(operatorId)

    }
  }

  private fun startSettlement(operatorId: String) {
    val body = Any.pack(Settlement.CloseBatchRequest.newBuilder().setOperatorId(operatorId).build())

    val timestamp = Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000).build()
    val request = Ecr.Request.newBuilder().setMessageId(4).setTimestamp(timestamp).setServiceName(
      Processor.SETTLEMENT_CLOSE
    ).setBody(body)
      .build()
    val envelope = EcrEnvelope.newBuilder().setVersion(1).setRequest(request).build()
    val byteArray = envelope.toByteArray()
    runOnUiThread { WaitDialog.dismiss() }
    ConnectionKernel.getInstance().send(byteArray)

  }


}