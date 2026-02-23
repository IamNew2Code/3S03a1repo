package com.example.shop;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

public class TestSuiteTask5 {

	// -------------------------------------------------------------------------
    // OrderItem
    // -------------------------------------------------------------------------
    @Test
    public void posQtPosPrice() {
        OrderItem item = new OrderItem("Widget", 3, 10.00);
        assertEquals(30.00, item.getTotalPrice(), 0.001);
    }

    @Test
    public void negQtPosPrice_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new OrderItem("Widget", -1, 10.00));
    }

    @Test
    public void posQtNegPrice_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new OrderItem("Widget", 1, -5.00));
    }
    
    //--------------------------------------------------------------------------
    //Added missing combinations in OrderItem tests to achieve higher combinatorial coverage (task 5b)
    //--------------------------------------------------------------------------
    @Test
    public void negQtnegPrice() {
        assertThrows(IllegalArgumentException.class,
                () -> new OrderItem("Widget", -1, -5.00));
    }

    @Test
    public void zeroQtNegPrice() {
        assertThrows(IllegalArgumentException.class,
                () -> new OrderItem("Widget", 0, -5.00));
    }

    @Test
    public void zeroQtPosPrice() {
        assertThrows(IllegalArgumentException.class,
                 () -> new OrderItem("Widget", 0, 10.00));
    }
    
    // -------------------------------------------------------------------------
    // Order
    // -------------------------------------------------------------------------

    @Test
    public void newOrder_hasCreatedStatus() {
    	Order order = new Order();
        assertEquals(OrderStatus.CREATED, order.getStatus());
    }

    @Test
    public void addItem_whileCreated_succeeds() {
    	Order order = new Order();
        order.addItem(new OrderItem("Widget", 1, 10.00));
        assertEquals(1, order.getItems().size());
    }

    @Test
    public void addItem_afterProcessed_throws() {
    	Order order = new Order();
        order.setStatus(OrderStatus.PAID);
        assertThrows(IllegalStateException.class,
                () -> order.addItem(new OrderItem("Widget", 1, 10.00)));
    }

    //--------------------------------------------------------------------------
    //Added missing combinations in Order tests to achieve higher combinatorial coverage (task 5b)
    //--------------------------------------------------------------------------
    @Test
    public void addItem_afterCancelled_throws() {
    	Order order = new Order();
        order.setStatus(OrderStatus.CANCELLED);
        assertThrows(IllegalStateException.class,
                () -> order.addItem(new OrderItem("Widget", 1, 10.00)));
    }
    
    // -------------------------------------------------------------------------
    // PricingService
    // -------------------------------------------------------------------------

    private PricingService pricingService;

    @Before
    public void setUpPricingService() {
        pricingService = new PricingService();
    }

    @Test
    public void calculateSubtotal_sumsItems() {
        Order order = new Order();
        order.addItem(new OrderItem("A", 2, 5.00));  // 10.00
        order.addItem(new OrderItem("B", 1, 15.00)); // 15.00
        assertEquals(25.00, pricingService.calculateSubtotal(order), 0.001);
    }

    @Test
    public void calculateSubtotal_emptyOrder_returnsZero() {
        assertEquals(0.0, pricingService.calculateSubtotal(new Order()), 0.001);
    }

    @Test
    public void calculateTax_validSubTotal() {
        assertEquals(20.00, pricingService.calculateTax(100.00), 0.001);
    }

    @Test
    public void calculateTax_zeroSubtotal() {
        assertEquals(0.0, pricingService.calculateTax(0.0), 0.001);
    }

    @Test
    public void calculateTax_negativeSubtotal_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> pricingService.calculateTax(-1.00));
    }
   
    // -------------------------------------------------------------------------
    // DiscountService
    // -------------------------------------------------------------------------
    private DiscountService discountService;

    @Before
    public void setUpDiscountService() {
        discountService = new DiscountService();
    }

    @Test
    public void student10_applies10PercentDiscount() {
        assertEquals(90.00, discountService.applyDiscount(100.00, "STUDENT10"), 0.001);
    }

    @Test
    public void student10_caseInsensitive() {
        assertEquals(90.00, discountService.applyDiscount(100.00, "student10"), 0.001);
    }

    @Test
    public void blackFriday_applies30PercentDiscount() {
        assertEquals(70.00, discountService.applyDiscount(100.00, "BLACKFRIDAY"), 0.001);
    }

    @Test
    public void nullCode_returnsSubtotal() {
        assertEquals(100.00, discountService.applyDiscount(100.00, null), 0.001);
    }

    @Test
    public void blankCode_returnsSubtotal() {
        assertEquals(100.00, discountService.applyDiscount(100.00, "   "), 0.001);
    }

    @Test
    public void unknownCode_returnsSubtotal() {
        assertEquals(100.00, discountService.applyDiscount(100.00, "UNKNOWN"), 0.001);
    }

    @Test
    public void invalidCode_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> discountService.applyDiscount(100.00, "INVALID"));
    }
    
    // -------------------------------------------------------------------------
    // PaymentValidator
    // -------------------------------------------------------------------------
    private PaymentValidator paymentValidator;

    @Before
    public void setUpPaymentValidator() {
        paymentValidator = new PaymentValidator();
    }

    @Test
    public void card_isValid() {
        assertTrue(paymentValidator.isPaymentMethodValid("card"));
    }

    @Test
    public void paypal_isValid() {
        assertTrue(paymentValidator.isPaymentMethodValid("paypal"));
    }

    @Test
    public void paymentMethod_caseInsensitive() {
        assertTrue(paymentValidator.isPaymentMethodValid("CARD"));
        assertTrue(paymentValidator.isPaymentMethodValid("PayPal"));
    }

    @Test
    public void crypto_isInvalid() {
        assertFalse(paymentValidator.isPaymentMethodValid("crypto"));
    }

    @Test
    public void nullPaymentMethod_returnsFalse() {
        assertFalse(paymentValidator.isPaymentMethodValid(null));
    }

    @Test
    public void unknownPaymentMethod_throws() {
        assertThrows(UnsupportedOperationException.class,
                () -> paymentValidator.isPaymentMethodValid("cash"));
    }

    // -------------------------------------------------------------------------
    // OrderService (integration-style)
    // -------------------------------------------------------------------------
    private OrderService orderService;

    @Before
    public void setUpOrderService() {
        orderService = new OrderService();
    }

    private Order orderWithItem(double unitPrice) {
        Order order = new Order();
        order.addItem(new OrderItem("Item", 1, unitPrice));
        return order;
    }

    @Test
    public void nullDiscount_cardPayment() {
        Order order = orderWithItem(100.00);
        double total = orderService.processOrder(order, null, "card");
        assertEquals(120.00, total, 0.001);
        assertEquals(OrderStatus.PAID, order.getStatus());
    }

    @Test
    public void student10Discount_cardPayment() {
        Order order = orderWithItem(100.00);
        double total = orderService.processOrder(order, "STUDENT10", "card");
        assertEquals(108.00, total, 0.001);
    }

    //--------------------------------------------------------------------------
    //Added missing combinations for "card" payment to achieve higher combinatorial coverage (task 5b)
    //--------------------------------------------------------------------------
    
    @Test
    public void blackFridayDiscount_cardPayment() {
        Order order = orderWithItem(100.00);
        double total = orderService.processOrder(order, "BLACKFRIDAY", "card");
        assertEquals(84.00, total, 0.001);
    }

    @Test
    public void invalidDiscount_cardPayment() {
        Order order = orderWithItem(100.00);
        assertThrows(IllegalArgumentException.class, () -> orderService.processOrder(order, "INVALID", "card"));
        
    }

    @Test
    public void unknownDiscount_cardPayment() {
        Order order = orderWithItem(100.00);
        double total = orderService.processOrder(order, "UNKNOWN", "card");
        assertEquals(120.00, total, 0.001);
    }

    @Test
    public void blankDiscount_cardPayment() {
        Order order = orderWithItem(100.00);
        double total = orderService.processOrder(order, "   ", "card");
        assertEquals(120.00, total, 0.001);
    }
    
    //--------------------------------------------------------------------------
    //End of adding extra combinations for "card" payment for task 5b
    //--------------------------------------------------------------------------

    @Test
    public void blackFridayDiscount_appliedBeforeTax() {
        Order order = orderWithItem(100.00);
        double total = orderService.processOrder(order, "BLACKFRIDAY", "paypal");
        // 100 * 0.7 = 70, tax = 14 → 84
        assertEquals(84.00, total, 0.001);
    }
    
    //--------------------------------------------------------------------------
    //Added missing combinations for "paypal" payment to achieve higher combinatorial coverage (task 5b)
    //--------------------------------------------------------------------------
    @Test
    public void nullDiscount_paypalPayment() {
        Order order = orderWithItem(100.00);
        double total = orderService.processOrder(order, null, "paypal");
        assertEquals(120.00, total, 0.001);
    }
    
    @Test
    public void student10Discount_paypalPayment() {
        Order order = orderWithItem(100.00);
        double total = orderService.processOrder(order, "STUDENT10", "paypal");
        assertEquals(108.00, total, 0.001);
    }
    
    @Test
    public void invalidDiscount_paypalPayment() {
        Order order = orderWithItem(100.00);
        assertThrows(IllegalArgumentException.class, () -> orderService.processOrder(order, "INVALID", "paypal"));
    }

    @Test
    public void unknownDiscount_paypalPayment() {
        Order order = orderWithItem(100.00);
        double total = orderService.processOrder(order, "UNKNOWN", "paypal");
        assertEquals(120.00, total, 0.001);
    }

    @Test
    public void blankDiscount_paypalPayment() {
        Order order = orderWithItem(100.00);
        double total = orderService.processOrder(order, "   ", "paypal");
        assertEquals(120.00, total, 0.001);
    }
    
    //--------------------------------------------------------------------------
    //End of adding extra combinations for "paypal" payment for task 5b
    //--------------------------------------------------------------------------

    @Test
    public void nullDiscount_cryptoPayment() {
        Order order = orderWithItem(100.00);
        double total = orderService.processOrder(order, null, "crypto");
        assertEquals(0.0, total, 0.001);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    //--------------------------------------------------------------------------
    //Added missing combinations for "crypto" payment to achieve higher combinatorial coverage (task 5b)
    //--------------------------------------------------------------------------
    
    //missing order, student10, crypto
    @Test
    public void student10Discount_cryptoPayment() {
        Order order = orderWithItem(100.00);
        double total = orderService.processOrder(order, "STUDENT10", "crypto");
        assertEquals(0, total, 0.001);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    public void blackFridayDiscount_cryptoPayment() {
        Order order = orderWithItem(100.00);
        double total = orderService.processOrder(order, "BLACKFRIDAY", "crypto");
        assertEquals(0, total, 0.001);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    public void invalidDiscount_cryptoPayment() {
        Order order = orderWithItem(100.00);
        double total = orderService.processOrder(order, "INVALID", "crypto");
        assertEquals(0, total, 0.001);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    public void unknownDiscount_cryptoPayment() {
        Order order = orderWithItem(100.00);
        double total = orderService.processOrder(order, "UNKNOWN", "crypto");
        assertEquals(0, total, 0.001);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    public void blankDiscount_cryptoPayment() {
        Order order = orderWithItem(100.00);
        double total = orderService.processOrder(order, "   ", "crypto");
        assertEquals(0, total, 0.001);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }
    
    //--------------------------------------------------------------------------
    //End of adding extra combinations for "crypto" payment for task 5b
    //--------------------------------------------------------------------------

    @Test
    public void nullDiscount_nullPayment() {
        Order order = orderWithItem(100.00);
        double total = orderService.processOrder(order, null, null);
        assertEquals(0.0, total, 0.001);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    //--------------------------------------------------------------------------
    //Added missing combinations for "null" payment to achieve higher combinatorial coverage (task 5b)
    //--------------------------------------------------------------------------
    
    @Test
    public void student10Discount_nullPayment() {
        Order order = orderWithItem(100.00);
        double total = orderService.processOrder(order, "STUDENT10", null);
        assertEquals(0.0, total, 0.001);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    public void blackFridayDiscount_nullPayment() {
        Order order = orderWithItem(100.00);
        double total = orderService.processOrder(order, "BLACKFRIDAY", null);
        assertEquals(0.0, total, 0.001);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
     public void invalidDiscount_nullPayment() {
        Order order = orderWithItem(100.00);
        double total = orderService.processOrder(order, "INVALID", null);
        assertEquals(0.0, total, 0.001);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    public void unknownDiscount_nullPayment() {
        Order order = orderWithItem(100.00);
        double total = orderService.processOrder(order, "UNKNOWN", null);
        assertEquals(0.0, total, 0.001);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    public void blankDiscount_nullPayment() {
        Order order = orderWithItem(100.00);
        double total = orderService.processOrder(order, "   ", null);
        assertEquals(0.0, total, 0.001);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    //--------------------------------------------------------------------------
    //End of adding extra combinations for "null" payment for task 5b
    //--------------------------------------------------------------------------

    @Test
    public void nullDiscount_unknownPayment() {
        Order order = orderWithItem(100.00);
        assertThrows(UnsupportedOperationException.class,
                () -> orderService.processOrder(order, null, "unknownPayment"));
    }
    
    //--------------------------------------------------------------------------
    //Added missing combinations for "cash" payment to achieve higher combinatorial coverage (task 5b)
    //--------------------------------------------------------------------------

    @Test
    public void student10Discount_unknownPayment() {
        Order order = orderWithItem(100.00);
        assertThrows(UnsupportedOperationException.class,
                () -> orderService.processOrder(order, "STUDENT10", "unknownPayment"));
    }

    @Test
    public void blackFridayDiscount_unknownPayment() {
        Order order = orderWithItem(100.00);
        assertThrows(UnsupportedOperationException.class,
                () -> orderService.processOrder(order, "BLACKFRIDAY", "unknownPayment"));
    }

    @Test
    public void invalidDiscount_unknownPayment() {
        Order order = orderWithItem(100.00);
        assertThrows(UnsupportedOperationException.class,
                () -> orderService.processOrder(order, "INVALID", "unknownPayment"));
    }

    @Test
    public void unknownDiscount_unknownPayment() {
        Order order = orderWithItem(100.00);
        assertThrows(UnsupportedOperationException.class,
                () -> orderService.processOrder(order, "UNKNOWN", "unknownPayment"));
    }

    @Test
    public void blankDiscount_unknownPayment() {
        Order order = orderWithItem(100.00);
        assertThrows(UnsupportedOperationException.class,
                () -> orderService.processOrder(order, "   ", "unknownPayment"));
    }

    //--------------------------------------------------------------------------
    //End of adding extra combinations for "crypto" payment for task 5b
    //--------------------------------------------------------------------------
    
}
