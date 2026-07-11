package org.example.project.domain.model

enum class PaymentMethod(val displayName: String) {
    CASH("Cash"),
    UPI("UPI"),
    CARD("Card"),
    BANK_TRANSFER("Bank Transfer"),
    WALLET("Wallet"),
    CHEQUE("Cheque"),
    OTHER("Other")
}
