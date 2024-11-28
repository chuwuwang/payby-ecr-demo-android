package com.payby.pos.common.helper

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

object NumberHelper {

    fun formatAmount(amount: String): String {
        val value = BigDecimal(amount).toDouble()
        val decimalFormat = DecimalFormat()
        decimalFormat.maximumFractionDigits = 2
        decimalFormat.groupingSize = 3
        decimalFormat.roundingMode = RoundingMode.FLOOR
        return decimalFormat.format(value)
    }

    fun formatInputAmountWithThousandCharacter(amount: String): String {
        var endWith = ""
        var startWith = ""
        var index = amount.indexOf(".")
        if (index != -1) {
            endWith = amount.substring(index)
        }
        startWith = formatAmount(amount)
        index = startWith.indexOf(".")
        if (index != -1) {
            startWith = startWith.substring(0, index)
        }
        return startWith + endWith
    }

    fun formatAmountWithThousandCharacter(amount: String): String {
        val value = formatAmount(amount)
        val index = value.indexOf(".")
        if (index != -1) {
            val string = value.substring(index + 1)
            if (string.length >= 2) return value
            else return value + "0"
        }
        return "$value.00"
    }

}