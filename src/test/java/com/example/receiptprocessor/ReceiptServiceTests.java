package com.example.receiptprocessor;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.receiptprocessor.model.Item;
import com.example.receiptprocessor.model.Receipt;
import com.example.receiptprocessor.service.ReceiptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class ReceiptServiceTests {

    @InjectMocks
    private ReceiptService service;

    @Mock
    private Receipt receipt;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateReceipt() {

        Item item1 = new Item("Mountain Dew 12P", "6.49");
        Item item2 = new Item("Emils Cheese Pizza", "12.25");
        Item item3 = new Item("Knorr Creamy Chicken", "1.26");
        Item item4 = new Item("Doritos Nacho Cheese", "3.35");
        Item item5 = new Item("Klarbrunn 12-PK 12 FL OZ", "12.00");

        List<Item> items = Arrays.asList(item1, item2, item3, item4, item5);

        when(receipt.getItems()).thenReturn(items);
        when(receipt.getRetailer()).thenReturn("Target");
        when(receipt.getPurchaseDate()).thenReturn("2022-01-01");
        when(receipt.getPurchaseTime()).thenReturn("13:01");
        when(receipt.getTotal()).thenReturn("35.35");

        String receiptId = service.processReceipt(receipt);

        // Assert
        assertNotNull(receiptId);  // ID should not be null

        Integer points = service.getPoints(receiptId);

        assertEquals(28,points);

    }

    @Test
    void testNullProcessReceipt() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            service.processReceipt(null);
        });
        assertEquals("Receipt or required fields are missing.", thrown.getMessage());
    }

    @Test
    void testProcessReceipt_MissingItems() {
        // Arrange
        when(receipt.getItems()).thenReturn(null);

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            service.processReceipt(receipt);
        });
        assertEquals("Receipt or required fields are missing.", thrown.getMessage());
    }

    @Test
    void testProcessReceipt_InvalidTotal() {
        // Arrange
        Item item1 = new Item("Description1", "5.00");
        List<Item> items = Arrays.asList(item1);

        when(receipt.getItems()).thenReturn(items);
        when(receipt.getRetailer()).thenReturn("Retailer");
        when(receipt.getPurchaseDate()).thenReturn("2025-01-17");
        when(receipt.getPurchaseTime()).thenReturn("15:00");
        when(receipt.getTotal()).thenReturn("InvalidTotal");

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            service.processReceipt(receipt);
        });
        assertEquals("Invalid total value.", thrown.getMessage());
    }

    @Test
    void testProcessReceipt_ValidPointsCalculation() {
        // Arrange: Simulating receipt with valid data
        Item item1 = new Item("Item Description", "5.00");
        List<Item> items = Arrays.asList(item1);

        when(receipt.getItems()).thenReturn(items);
        when(receipt.getRetailer()).thenReturn("Retailer");
        when(receipt.getPurchaseDate()).thenReturn("2025-01-17");
        when(receipt.getPurchaseTime()).thenReturn("15:00");
        when(receipt.getTotal()).thenReturn("15.00");

        // Act
        String receiptId = service.processReceipt(receipt);
        Integer points = service.getPoints(receiptId);

        // Assert: Verify the points are calculated as expected.
        assertEquals(99, points); // Example expected points based on the input
    }
}
