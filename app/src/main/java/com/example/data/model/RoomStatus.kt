package com.example.data.model

enum class RoomStatus(val labelAmharic: String, val labelEnglish: String) {
    VACANT("ክፍት", "Vacant"),
    OCCUPIED("የተያዘ", "Occupied"),
    CLEANING("ጽዳት ላይ", "Cleaning")
}

enum class DepositStatus(val labelAmharic: String, val labelEnglish: String) {
    HELD("ተይዟል", "Held"),
    REFUNDED("ተመልሷል", "Refunded"),
    DEDUCTED("ተቀናንሷል", "Deducted")
}
