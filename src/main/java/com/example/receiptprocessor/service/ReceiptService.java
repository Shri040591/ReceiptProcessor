package com.example.receiptprocessor.service;

import com.example.receiptprocessor.model.Item;
import com.example.receiptprocessor.model.Receipt;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class ReceiptService {
    private Map<String, Integer> receiptPointsMap = new HashMap<>();//for saving receipt ID and points

    // Submits a receipt for processing
    public String processReceipt(Receipt receipt) {

        // Handling the case where receipt, items, or purchaseDate are null
        if (receipt == null || receipt.getItems() == null || receipt.getPurchaseDate() == null) {
            throw new IllegalArgumentException("Receipt or required fields are missing.");
        }

        int points = 0;

        // Rule 1: Points based on retailer name
        points += receipt.getRetailer().replaceAll("[^a-zA-Z0-9]", "").length();

        // Rule 2: Points for round total amount
        BigDecimal total;
        try {
            total = new BigDecimal(receipt.getTotal()).setScale(2, RoundingMode.HALF_UP); // Ensure two decimal places
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid total value.");
        }

        if (total.stripTrailingZeros().scale() <= 0) {
            points += 50;
        }

        // Rule 3: Points for total being a multiple of 0.25
        total = total.setScale(2, RoundingMode.HALF_UP);
        if(total.remainder(BigDecimal.valueOf(0.25)).compareTo(BigDecimal.ZERO) == 0){

            points += 25;
        }
        //System.out.println("Points for total being a multiple of 0.25 : "+points);

        // Rule 4: Points for number of items
        points += (receipt.getItems().size() / 2) * 5;

        // Rule 5: Points for item description length
        for(Item item : receipt.getItems()){
            String description  = item.getShortDescription().trim();
            if(description.length() % 3 == 0){
                BigDecimal price = new BigDecimal(item.getPrice());
                points += (int) Math.ceil(price.multiply(BigDecimal.valueOf(0.2)).doubleValue());
            }
        }
        //System.out.println("Points for number of items : "+points);

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

        String id = UUID.randomUUID().toString();

        // Storing the points for this receipt ID in memory
        receiptPointsMap.put(id, points);

        return id;// Returns the ID assigned to the receipt
    }

    //Returns the points awarded for the receipt
    public Integer getPoints(String id){
        return receiptPointsMap.get(id);
    }
}
