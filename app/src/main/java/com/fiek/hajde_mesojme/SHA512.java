package com.fiek.hajde_mesojme;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Random;

public class SHA512
{

    // metode e cila gjeneron salted hash si kombinim i salt dhe hash te passwordit
    public static String hash(String pass, String salt)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update((pass+salt).getBytes(StandardCharsets.UTF_8));
            byte[] digest = md.digest();
            String saveHashedPass = String.format("%064x", new java.math.BigInteger(1, digest));
            return saveHashedPass;
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
            return null;
        }
    }

    // metoda e cila gjeneron salt
    public static String gjeneroSalt()
    {
        String bashkesiaChar = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        char[] karakteret = bashkesiaChar.toCharArray();
        String salt="";
        Random rn = new Random();
        for(int i=0;i<20;i++)
        {
            salt += karakteret[rn.nextInt(karakteret.length)];
        }

        return salt;
    }
}