package org.example.model;

public class Merchant {
    private int merchantId;
    private String merchantName;
    private String merchantCategory;

    public Merchant(int merchantId, String merchantName, String merchantCategory) {
        this.merchantId = merchantId;
        this.merchantName = merchantName;
        this.merchantCategory = merchantCategory;
    }

    public int getMerchantId() { return merchantId; }
    public String getMerchantName() { return merchantName; }
    public String getMerchantCategory() { return merchantCategory; }
}