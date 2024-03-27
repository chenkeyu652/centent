package com.centent.core.bean;

import lombok.Data;

@Data
public class RSAKeyPair {

    private String publicKey;

    private String privateKey;

    public RSAKeyPair() {
    }

    public RSAKeyPair(String publicKey, String privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }
}
