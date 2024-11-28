package com.payby.pos.common.card

data class CardPaymentInfo(
    var aid: String ? = null,
    var atc: String ? = null,
    var tsi: String ? = null,
    var tvr: String ? = null,
    var cid: String ? = null,
    var aRqc: String ? = null,
    var appName: String ? = null,
    var appLabel: String ? = null,

    var tc: String ? = null,
    var aRpc: String ? = null,
    var scriptProcessCode: Int = -1,
    var scriptProcessData: String ? = null,

    var cardType: Int = -1,
    var cardBrand: String ? = null,
    var cardNumber: String ? = null,
    var cardTrack2: String ? = null,
    var cardExpireDate: String ? = null,
    var cardHolderName: String ? = null,
    var cardSerialNumber: String ? = null,

    var requestDE55: String ? = null,

    var authNumber: String ? = null,
    var responseCode: String ? = null,
    var responseDE55: String ? = null,

    var passwordMode: Int = -1,                  // no password / offline password / online password
    var cipherTextPIN: String ? = null,
    var cipherTextKSN: String ? = null,

    var isSignature: Boolean = false,
    var signatureData: String ? = null,

    var isOfflineTransaction: Boolean = false,
    var isFallbackTransaction: Boolean = false,
    var isMagneticTransaction: Boolean = false,
)