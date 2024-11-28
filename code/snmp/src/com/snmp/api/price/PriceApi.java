package com.snmp.api.price;


public class PriceApi {
    public interface onApiCallback {
        void onPriceResponce(int price, String show, int fee);
    }
}
