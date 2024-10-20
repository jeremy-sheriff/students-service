package org.app.users.controllers

import org.app.users.dto.InventoryItem
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/students/netsuite")
class InventoryController {

    private val logger = LoggerFactory.getLogger(InventoryController::class.java)
    @PostMapping("/inventory-upload")
    fun receiveInventoryItem(
        @RequestBody inventoryItem: InventoryItem): ResponseEntity<String> {
        // Log the received item
        logger.info("Received inventory item: $inventoryItem")
        // Here you can process the inventory item, e.g., save it to a database
        // Return a success response
        return ResponseEntity("Inventory item received successfully.", HttpStatus.OK)
    }
}