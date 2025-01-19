package com.example.receiptprocessor.controller;

import com.example.receiptprocessor.model.Receipt;
import com.example.receiptprocessor.service.ReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/receipts")
public class ReceiptController {
    private final ReceiptService receiptService;

    @Autowired
    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @PostMapping("/process")
    public ResponseEntity<?> processReceipt(@RequestBody Receipt receipt) {
        if (receipt == null || receipt.getRetailer().isEmpty() || receipt.getPurchaseDate().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{ \"message\": \"The receipt is invalid.\" }");
        }

        try {
            String receiptId = receiptService.processReceipt(receipt);
            if (receiptId == null) {
                // Return a 500 error if the receipt ID is null (unexpected)
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{ \"message\": \"Error processing receipt.\" }");
            }
            else {
                return ResponseEntity.status(HttpStatus.OK).body("{ \"id\": \"" + receiptId + "\" }");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{ \"message\": \"An unexpected error occurred.\" }");
        }
    }

    @GetMapping("/{id}/points")
    public ResponseEntity<?> getPoints(@PathVariable String id) {
        Integer points = receiptService.getPoints(id);
        if (points == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{ \"message\": \"No receipt found for that ID.\" }");
        }
        return ResponseEntity.status(HttpStatus.OK).body("{ \"points\": " + points + " }");
    }
}
