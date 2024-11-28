package com.payby.pos.common.card

object CardHelper {

    fun formatterCardNumber(cardNo: String): String {
        return cardNo.substring(0, 4) + " " + cardNo.substring(4, 6) + "** **** " + cardNo.substring(cardNo.length - 4)
    }

    fun cardNumberDesensitization(cardNumber: String): String {
        return cardNumber.substring(0, 6) + " **** **** " + cardNumber.substring(cardNumber.length - 4)
    }

    fun getCardOrganization(cardNumber: String ? , aid: String ? = null): String ? {
        val cardNetwork: String ?
        if (aid != null && aid.length >= 10) {
            val group = aid.substring(0, 10)
            if (group == "A000000003") {
                cardNetwork = CARD_VISA
            } else if (group == "A000000004" || group == "A000000005") {
                cardNetwork = CARD_MASTERCARD
            } else if (group == "A000000025") {
                cardNetwork = CARD_AMERICAN_EXPRESS
            } else if (group == "A000000065") {
                cardNetwork = CARD_JCB
            } else if (group == "A000000524") {
                cardNetwork = CARD_INDIAN
            } else if (group == "A000000333") {
                cardNetwork = CARD_UNION_PAY
            } else if (group == "A000000615") {
                cardNetwork = CARD_PURE
            } else {
                cardNetwork = null
            }
        } else {
            cardNetwork = getCardOrganization(cardNumber)
        }
        return cardNetwork
    }

    fun isAmex(aid: String ? , cardNumber: String ? = null): Boolean {
        val b1 = aid != null && aid.startsWith("A000000025")
        var b2 = false
        if (cardNumber != null) {
            b2 = cardNumber.startsWith("34") || cardNumber.startsWith("37")
        }
        return b1 || b2
    }

    fun isJCB(aid: String ? , cardNumber: String ? = null): Boolean {
        val b1 = aid != null && aid.startsWith("A000000065")
        var b2 = false
        if (cardNumber != null) {
            b2 = cardNumber.startsWith("3528") || cardNumber.startsWith("3529") ||
                cardNumber.startsWith("353") || cardNumber.startsWith("354") ||
                cardNumber.startsWith("355") || cardNumber.startsWith("356") ||
                cardNumber.startsWith("357") || cardNumber.startsWith("358")
        }
        return b1 || b2
    }

    fun isUPI(aid: String ? , cardNumber: String ? = null): Boolean {
        val b1 = aid != null && aid.startsWith("A000000333")
        val b2 = cardNumber != null && cardNumber.startsWith("62")
        return b1 || b2
    }

    fun isMastercard(aid: String ? , cardNumber: String ? = null): Boolean {
        var b1 = false
        var b2 = false
        if (aid != null) {
            b1 = aid.startsWith("A000000004") || aid.startsWith("A000000005")
        }
        if (cardNumber != null) {
            b2 = cardNumber.startsWith("51") || cardNumber.startsWith("52") ||
                cardNumber.startsWith("53") || cardNumber.startsWith("54") ||
                cardNumber.startsWith("55") || cardNumber.startsWith("56")
        }
        return b1 || b2
    }

    private fun getCardOrganization(cardNumber: String ? ): String ? {
        if (cardNumber == null || cardNumber.trim().length < 13) return null

        var bool = cardNumber.startsWith("4")
        if (bool) return CARD_VISA

        bool = cardNumber.startsWith("51") || cardNumber.startsWith("52") ||
            cardNumber.startsWith("53") || cardNumber.startsWith("54") ||
            cardNumber.startsWith("55") || cardNumber.startsWith("56")
        if (bool) return CARD_MASTERCARD

        bool = cardNumber.startsWith("62")
        if (bool) return CARD_UNION_PAY

        bool = cardNumber.startsWith("35")
        if (bool) return CARD_JCB

        bool = cardNumber.startsWith("34") || cardNumber.startsWith("37")
        if (bool) return CARD_AMERICAN_EXPRESS

        return null
    }

    private const val CARD_JCB = "JCB"
    private const val CARD_VISA = "Visa"
    private const val CARD_PURE = "Pure"
    private const val CARD_INDIAN = "RuPay"
    private const val CARD_UNION_PAY = "UnionPay"
    private const val CARD_MASTERCARD = "MasterCard"
    private const val CARD_AMERICAN_EXPRESS = "AmericanExpress"

}