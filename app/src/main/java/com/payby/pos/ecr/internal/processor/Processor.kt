package com.payby.pos.ecr.internal.processor

import com.payby.pos.ecr.connect.ConnectService
import com.payby.pos.ecr.utils.EnvelopeLogger
import com.uaepay.pos.ecr.Ecr

class Processor {

  companion object {

    const val ACQUIRE_PLACE_ORDER = "/acquire/place"                      // 收款
    const val ACQUIRE_GET_ORDER = "/acquire/get"                          // 查询收款订单
    const val ACQUIRE_GET_ORDER_LIST = "/acquire/queryPage"
    @Deprecated("")// 查询收款订单分⻚
    const val ACQUIRE_GET_ORDER_RECEIPT = "/acquire/receipt/get"          // 查询收单订单的票据
    const val ACQUIRE_PRINT_RECEIPTS = "/acquire/receipts/print"          // 打印收单订单的票据
    const val ACQUIRE_NOTIFICATION = "/acquire/notification"

    const val VOID_PLACE_ORDER = "/acquire/void"                          // 撤销

    const val REFUND_PLACE_ORDER = "/acquire/refund/place"                // 退款
    const val REFUND_GET_ORDER = "/acquire/refund/get"                    // 查询单笔退款
    const val REFUND_PRINT_RECEIPTS = "/acquire/refund/receipts/print"    // 打印退款订单的票据

    const val SETTLEMENT_CLOSE = "/settlement/closeBatch"
    const val DEVICE_GET_THIS = "/device/getThis"

    const val STATUS_FAILED = "FAILED"
    const val STATUS_SUCCESS = "SUCCESS"
  }


  init {
    ConnectService.callback = {
      messageHandle(byteArray = it)
    }
  }


  var onPlaceAcquireOrderComplete: (Ecr.Response) -> Unit = {}
  var onInquiryAcquireOrderComplete: (Ecr.Response) -> Unit = {}

  // TODO:
  var onInquiryAcquireOrderListComplete: (Ecr.Response) -> Unit = {}

  var onRefundGetOrderComplete: (Ecr.Response) -> Unit = {}
  var onRefundPrintReceipts: (Ecr.Response) -> Unit = {}

  @Deprecated("")
  var onAcquireGetOrderReceipt: (Ecr.Response) -> Unit = {}

  var onAcquirePrintReceipts: (Ecr.Response) -> Unit = {}

  var onRefundPlaceOrderComplete: (Ecr.Response) -> Unit = {}
  var onVoidPlaceOrderComplete: (Ecr.Response) -> Unit = {}
  var onAcquireNotification: (Ecr.Response) -> Unit = {}
  var onSettlementClose: (Ecr.Response) -> Unit = {}

  var onDeviceInfo: (Ecr.Response) -> Unit = {}




  fun messageHandle(byteArray: ByteArray) {
    val envelope = Ecr.EcrEnvelope.parseFrom(byteArray)
    when (envelope.contentCase) {
      Ecr.EcrEnvelope.ContentCase.PING -> {}
      Ecr.EcrEnvelope.ContentCase.PONG -> {}
      Ecr.EcrEnvelope.ContentCase.REQUEST -> {
        // ProcessorRequest.request(envelope)
      }

      Ecr.EcrEnvelope.ContentCase.RESPONSE -> {
        response(envelope)
      }

      Ecr.EcrEnvelope.ContentCase.EVENT -> {}
      else -> {}
    }
  }

  private fun response(envelope: Ecr.EcrEnvelope) {
    val response = envelope.response
    val serviceName = response.serviceName
    EnvelopeLogger.printResponse(envelope)
    if (serviceName == ACQUIRE_PLACE_ORDER) {
      onPlaceAcquireOrderComplete(response)
    } else if (serviceName == ACQUIRE_GET_ORDER) {
      onInquiryAcquireOrderComplete(response)
    } else if (serviceName == ACQUIRE_GET_ORDER_LIST) {
      onInquiryAcquireOrderListComplete(response)
    } else if (serviceName == SETTLEMENT_CLOSE) {
      onSettlementClose(response)
    } else if (serviceName == REFUND_GET_ORDER) {
      onRefundGetOrderComplete(response)
    } else if (serviceName == ACQUIRE_GET_ORDER_RECEIPT) {
      onAcquireGetOrderReceipt(response)
    } else if (serviceName == ACQUIRE_PRINT_RECEIPTS) {
      onAcquirePrintReceipts(response)
    } else if (serviceName == REFUND_PLACE_ORDER) {
      onRefundPlaceOrderComplete(response)
    } else if (serviceName == ACQUIRE_NOTIFICATION) {
      onAcquireNotification(response)
    } else if (serviceName == VOID_PLACE_ORDER) {
      onVoidPlaceOrderComplete(response)
    }  else if (serviceName == DEVICE_GET_THIS) {
      onDeviceInfo(response)
    } else if (serviceName == REFUND_PRINT_RECEIPTS) {
      onRefundPrintReceipts(response)
    }


  }

}