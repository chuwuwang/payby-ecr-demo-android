package com.payby.pos.common.tlv

data class TLV(var tag: String = "", var length: Int = 0, var value: String = "") {

    fun recover2HexString(): String {
        return TLVHelper.tlv2HexString(this)
    }

    fun recover2Bytes(): ByteArray {
        return TLVHelper.tlv2Bytes(this)
    }

}