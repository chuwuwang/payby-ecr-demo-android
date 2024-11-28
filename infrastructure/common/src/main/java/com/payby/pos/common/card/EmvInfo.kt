package com.payby.pos.common.card

data class EmvInfo(
    var tc: String ? = null,
    var aid: String ? = null,
    var tvr: String ? = null,
    var atc: String ? = null,
    var tsi: String ? = null,
    var cid: String ? = null,
    var aRqc: String ? = null,
    var appName: String ? = null,
    var appLabel: String ? = null,
    var isSignature: Boolean = false,
    var scriptProcessData: String ? = null,
)