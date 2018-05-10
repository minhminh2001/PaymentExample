package com.example.minhpq.paymentexample.model;

import com.adyen.core.models.Amount;

public class PaymentSetupRequest {
    private Amount amount;
    private String merchantReference;
    private String shopperIP;
    private String shopperLocale;
    private String merchantAccount;
    private String countryCode;
    private String paymentDeadline;
    private String returnURL;
    private String paymentToken;

    public PaymentSetupRequest() {
    }

    public PaymentSetupRequest(Amount amount, String merchantReference, String shopperIP,
                               String shopperLocale, String merchantAccount, String countryCode,
                               String paymentDeadline, String returnURL, String paymentToken) {
        this.amount = amount;
        this.merchantReference = merchantReference;
        this.shopperIP = shopperIP;
        this.shopperLocale = shopperLocale;
        this.merchantAccount = merchantAccount;
        this.countryCode = countryCode;
        this.paymentDeadline = paymentDeadline;
        this.returnURL = returnURL;
        this.paymentToken = paymentToken;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public String getMerchantReference() {
        return merchantReference;
    }

    public void setMerchantReference(String merchantReference) {
        this.merchantReference = merchantReference;
    }

    public String getShopperIP() {
        return shopperIP;
    }

    public void setShopperIP(String shopperIP) {
        this.shopperIP = shopperIP;
    }

    public String getShopperLocale() {
        return shopperLocale;
    }

    public void setShopperLocale(String shopperLocale) {
        this.shopperLocale = shopperLocale;
    }

    public String getMerchantAccount() {
        return merchantAccount;
    }

    public void setMerchantAccount(String merchantAccount) {
        this.merchantAccount = merchantAccount;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPaymentDeadline() {
        return paymentDeadline;
    }

    public void setPaymentDeadline(String paymentDeadline) {
        this.paymentDeadline = paymentDeadline;
    }

    public String getReturnURL() {
        return returnURL;
    }

    public void setReturnURL(String returnURL) {
        this.returnURL = returnURL;
    }

    public String getPaymentToken() {
        return paymentToken;
    }

    public void setPaymentToken(String paymentToken) {
        this.paymentToken = paymentToken;
    }
    
}
