package com.example.receiptprocessor.controller;


import com.example.receiptprocessor.model.Receipt;
import com.example.receiptprocessor.service.ReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/receipts")
public class ReceiptController {

    @Autowired
    private ReceiptService receiptService;

    @PostMapping("process")
    public String processReceipt(@RequestBody Receipt receipt) {
        String receiptId = receiptService.processReceipt(receipt);
        return "{\"id\":\"" + receiptId + "\"}";
    }

    @GetMapping("/{id}/points")
    public String getPoints(@PathVariable("id") String receiptId) {
        int points = receiptService.getPoints(receiptId);
        return "{\"points\":" + points + "}";
    }
}
