package com.payby.pos.common.card

enum class CardType(val value: Int, val text: String) {
    Manual(0, "Manual"),
    Magnetic(1, "Magnetic Stripe"),
    Contact(2, "Contact"),
    Contactless(4, "Contactless")
}