package com.example.payment.dto;

import jakarta.validation.constraints.*;
import java.time.Instant;

public class PaymentRequest {

    @NotBlank(message = "Order ID cannot be blank")
    private String orderId;

    @NotBlank(message = "Payer name cannot be blank")
    private String payerName;

    @Positive(message = "Amount must be greater than 0")
    private double amount;

    @NotBlank(message = "Currency is required")
    private String currency;

    @NotBlank(message = "Timestamp is required")
    private String timestamp;

    @NotBlank(message = "Source is required")
    private String source;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;


    private GeoLocation geoLocation;

    private String transactionId; // No validation → optional

    // ---- Nested class for geoLocation ----
    public static class GeoLocation {

        @NotNull(message = "Latitude is required")
        private Double lat;

        @NotNull(message = "Longitude is required")
        private Double lon;

        public Double getLat() { return lat; }
        public void setLat(Double lat) { this.lat = lat; }

        public Double getLon() { return lon; }
        public void setLon(Double lon) { this.lon = lon; }
    }

    // ---- Getters & Setters ----

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getPayerName() { return payerName; }
    public void setPayerName(String payerName) { this.payerName = payerName; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public GeoLocation getGeoLocation() { return geoLocation; }
    public void setGeoLocation(GeoLocation geoLocation) { this.geoLocation = geoLocation; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
}
