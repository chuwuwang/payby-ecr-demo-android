package com.payby.pos.ecr.utils

import android.util.Log
import com.google.protobuf.MessageOrBuilder
import com.google.protobuf.util.JsonFormat
import com.payby.pos.ecr.App
import com.payby.pos.ecr.internal.processor.Processor
import com.uaepay.pos.ecr.Ecr
import com.uaepay.pos.ecr.acquire.Acquire
import com.uaepay.pos.ecr.acquire.Refund
import com.uaepay.pos.ecr.acquire.Settlement

object EnvelopeLogger {

    fun printRequest(message: MessageOrBuilder) {
        val string = "Request ---> " + transformMessage(message)
        Log.e(App.TAG, string)
    }

    fun printResponse(envelope: Ecr.EcrEnvelope) {
        try {
            var msg = ""
            var print = ""
            val contentCase = envelope.contentCase
            if (contentCase == Ecr.EcrEnvelope.ContentCase.RESPONSE) {
                val response = envelope.response
                val messageId = response.messageId
                val serviceName = response.serviceName
                val responseCode = response.responseCode
                val hasBody = response.hasBody()
                val body = response.body
                if (serviceName == Processor.ACQUIRE_PLACE_ORDER && hasBody) {
                    val message = body.unpack(Acquire.AcquireOrder::class.java)
                    print = transformMessage(message)
                } else if (serviceName == Processor.ACQUIRE_GET_ORDER && hasBody) {
                    val message = body.unpack(Acquire.AcquireOrder::class.java)
                    print = transformMessage(message)
                } else if (serviceName == Processor.ACQUIRE_GET_ORDER_LIST && hasBody) {
                    val message = body.unpack(Acquire.AcquireOrderPage::class.java)
                    print = transformMessage(message)
                } else if (serviceName == Processor.REFUND_PLACE_ORDER && hasBody) {
                    val message = body.unpack(Refund.RefundOrder::class.java)
                    print = transformMessage(message)
                } else if (serviceName == Processor.REFUND_GET_ORDER && hasBody) {
                    val message = body.unpack(Refund.RefundOrder::class.java)
                    print = transformMessage(message)
                } else if (serviceName == Processor.SETTLEMENT_CLOSE && hasBody) {
                    val message = body.unpack(Settlement.TransactionReport::class.java)
                    print = transformMessage(message)
                }
                msg = "Response <--- (serviceName: $serviceName, messageId: $messageId, responseCode: $responseCode) $print"
            } else if (contentCase == Ecr.EcrEnvelope.ContentCase.REQUEST) {
                val request = envelope.request
                val messageId = request.messageId
                val serviceName = request.serviceName
                val body = request.body
                val hasBody = request.hasBody()
                if (serviceName == Processor.ACQUIRE_NOTIFICATION && hasBody) {
                    val message = body.unpack(Acquire.AcquireOrder::class.java)
                    print = transformMessage(message)
                }
                msg = "Request <--- (serviceName: $serviceName, messageId: $messageId) $print"
            } else if (contentCase == Ecr.EcrEnvelope.ContentCase.EVENT) {
                val event = envelope.event
                val serviceName = event.serviceName
                val body = event.body
                val hasBody = event.hasBody()
                if (serviceName == Processor.ACQUIRE_NOTIFICATION && hasBody) {
                    val message = body.unpack(Acquire.AcquireOrder::class.java)
                    print = transformMessage(message)
                }
                msg = "Event <--- (serviceName: $serviceName) $print"
            } else {
                msg = "Response <--- message handle failure, contentCase: $contentCase"
            }
            Log.e(App.TAG, msg)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun transformMessage(message: MessageOrBuilder): String {
        try {
            return JsonFormat.printer().print(message)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return ""
    }

}