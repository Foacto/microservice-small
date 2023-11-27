package com.microtest.productservice.generator;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.UUID;

public class ProductIdGenerator implements IdentifierGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException {
//        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
//                + "0123456789"
//                + "abcdefghijklmnopqrstuvxyz";
//
//        StringBuilder sb = new StringBuilder(24);
//
//        for (int i = 0; i < 24; i++) {
//            int index = (int)(AlphaNumericString.length() * Math.random());
//
//            // add Character one by one in end of sb
//            sb.append(AlphaNumericString.charAt(index));
//        }
//
//        String generatedId = sb.toString();

        String generatedId = UUID.randomUUID().toString();

        return generatedId;
    }
}
