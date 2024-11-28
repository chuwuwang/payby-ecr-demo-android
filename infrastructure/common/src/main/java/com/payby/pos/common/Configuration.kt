package com.payby.pos.common

object Configuration {

    var HSM_KEY_INDEX = 7
    const val RSA_PUBLIC_INDEX = 3
    const val RSA_PRIVATE_INDEX = 4

    const val COUNTRY_CODE = "0784"
    const val COUNTRY_CODE_SYMBOL = "AED"
    const val MAX_TRANSACTION_LIMIT_AMOUNT = 9999999999
    var MAX_CVM_LIMIT_AMOUNT: Long = 50000L
    val MAX_CVM_LIMIT_AMOUNT_STRING: String get() = String.format("%012d", MAX_CVM_LIMIT_AMOUNT)

    lateinit var buildInfo: BuildInfo

}