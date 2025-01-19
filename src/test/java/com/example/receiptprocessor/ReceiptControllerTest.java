package com.example.receiptprocessor;

import com.example.receiptprocessor.controller.ReceiptController;
import com.example.receiptprocessor.model.Receipt;
import com.example.receiptprocessor.service.ReceiptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ReceiptControllerTest {

    @Mock
    private ReceiptService receiptService;

    @InjectMocks
    private ReceiptController receiptController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Initialize the MockMvc object with the controller and mock the service
        mockMvc = MockMvcBuilders.standaloneSetup(receiptController).build();
    }

    @Test
    void testProcessReceipt_ValidInput() throws Exception {
        // Prepare data
        Receipt receipt = new Receipt();
        receipt.setRetailer("Retailer Name");
        receipt.setPurchaseDate("2025-01-18");

        String receiptId = "12345";

        // Mock the service method
        when(receiptService.processReceipt(receipt)).thenReturn(receiptId);

        // Perform the request and verify the result
        mockMvc.perform(post("/receipts/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"retailer\":\"Retailer Name\",\"purchaseDate\":\"2025-01-18\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":\"12345\"}"));

        // Verify the service interaction
        verify(receiptService, times(1)).processReceipt(receipt);
    }

    @Test
    void testProcessReceipt_InvalidInput() throws Exception {
        // Prepare invalid data (missing required fields)
        Receipt receipt = new Receipt();
        receipt.setRetailer("");
        receipt.setPurchaseDate("");

        // Perform the request and verify the result
        mockMvc.perform(post("/receipts/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"retailer\":\"\",\"purchaseDate\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\":\"Please verify input.\"}"));

        // Verify the service interaction (should not be called)
        verify(receiptService, never()).processReceipt(any(Receipt.class));
    }

    @Test
    void testGetPoints_ValidId() throws Exception {
        String receiptId = "12345";
        Integer points = 100;

        // Mock the service method
        when(receiptService.getPoints(receiptId)).thenReturn(points);

        // Perform the request and verify the result
        mockMvc.perform(get("/receipts/{id}/points", receiptId))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"points\":100}"));

        // Verify the service interaction
        verify(receiptService, times(1)).getPoints(receiptId);
    }

    @Test
    void testGetPoints_InvalidId() throws Exception {
        String receiptId = "12345";

        // Mock the service method to return null (receipt not found)
        when(receiptService.getPoints(receiptId)).thenReturn(null);

        // Perform the request and verify the result
        mockMvc.perform(get("/receipts/{id}/points", receiptId))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"message\":\"No receipt found for that ID.\"}"));

        // Verify the service interaction
        verify(receiptService, times(1)).getPoints(receiptId);
    }

}
