package org.example.service;

import java.util.Random;

public class RandomOtpGenerator implements OtpGenerator {

    @Override
    public int generate() {
        return new Random().nextInt(9000) + 1000;
    }
}