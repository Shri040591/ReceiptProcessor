package com.example.receiptprocessor.service;

import com.example.receiptprocessor.model.Item;
import com.example.receiptprocessor.model.Receipt;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.UUID;
import java.util.Map;


@Service
public class ReceiptService {
    private Map<String, Integer> receiptPointsMap = new HashMap<>();

    public String processReceipt(Receipt receipt) {
        System.out.println(receipt);
        int points = 0;

        // Rule 1: Points based on retailer name
        points += receipt.getRetailer().replaceAll("[^a-zA-Z0-9]", "").length();

        // Rule 2: Points for round total amount
        BigDecimal total = new BigDecimal(receipt.getTotal()).setScale(2, RoundingMode.HALF_UP); // Ensure two decimal places
        if (total.stripTrailingZeros().scale() <= 0) {
            points += 50;
        }

        // Rule 3: Points for total being a multiple of 0.25
        total = total.setScale(2, RoundingMode.HALF_UP);
        if(total.remainder(BigDecimal.valueOf(0.25)).compareTo(BigDecimal.ZERO) == 0){
            points += 25;
        }

        // Rule 4: Points for number of items
        points += (receipt.getItems().size() / 2) * 5;
        System.out.println("Rule 4: Points for number of items : " +points);

        // Rule 5: Points for item description length
        for(Item item : receipt.getItems()){
            String description  = item.getShortDescription().trim();
            if(description.length() % 3 == 0){
                BigDecimal price = new BigDecimal(item.getPrice());
                points += (int) Math.ceil(price.multiply(BigDecimal.valueOf(0.2)).doubleValue());
            }
        }

        // Rule 7: Points if purchase day is odd
        String day = receipt.getPurchaseDate().split("-")[2];
        if(Integer.parseInt(day) % 2 != 0){
            points += 6;
        }

        // Rule 8: Points if purchase time is between 2:00pm and 4:00pm
        String time = receipt.getPurchaseTime();
        if(time.compareTo("14:00") >= 0 && time.compareTo("16:00") < 0 ){
            points += 10;
        }

        // Generate a unique ID for the receipt
        String receiptId = UUID.randomUUID().toString();

        // Store the points for this receipt ID in memory
        receiptPointsMap.put(receiptId, points);

        return receiptId;
    }

    public int getPoints(String id){
        return receiptPointsMap.getOrDefault(id, 0);
    }
}
