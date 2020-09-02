package com.example.itifighter.ui.MailApi;

import android.annotation.SuppressLint;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {
    private String key = "IneedSmileshubha"; // 128 bit key
    private Key aesKey = new SecretKeySpec(key.getBytes(), "AES");

    public String encrypt(String text){
        try{
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("AES");   // Create key and cipher
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(text.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b: encrypted) sb.append((char)b);
            return sb.toString();
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public String decrypt(String enc){
        try{
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("AES");   // Create key and cipher
            byte[] bb = new byte[enc.length()];
            for (int i=0; i<enc.length(); i++) bb[i] = (byte) enc.charAt(i);
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            return new String(cipher.doFinal(bb));
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
