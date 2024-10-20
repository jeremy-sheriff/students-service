package org.app.users.dto


data class InventoryItem(
    val id: String,
    val itemid: String,
    val quantityOnHand: String,
    val averageCost: String
)

