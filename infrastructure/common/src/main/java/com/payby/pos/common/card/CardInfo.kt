package com.payby.pos.common.card

data class CardInfo(
    var track1: String = "",
    var track2: String = "",
    var track3: String = "",
    var cardNo: String = "",
    var serialNo: String = "",
    var holderName: String = "",
    var expireDate: String = "",
    var serviceCode: String = "",
    var fallback: Boolean = false,
)