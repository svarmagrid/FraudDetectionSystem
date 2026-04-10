package org.example.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MerchantTest {

    private Merchant createMerchant(){
        return new Merchant(1,"Amazon", "E-commerce");
    }

    @Test
    void testShouldInitializeCorrectly() {
        Merchant merchant = createMerchant();
        assertEquals(1, merchant.getMerchantId());
        assertEquals("Amazon", merchant.getMerchantName());
        assertEquals("E-commerce", merchant.getMerchantCategory());
    }
}