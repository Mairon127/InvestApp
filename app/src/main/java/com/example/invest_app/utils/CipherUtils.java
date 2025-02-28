package com.example.invest_app.utils;

import android.annotation.SuppressLint;
import android.util.Base64;

import androidx.annotation.NonNull;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class CipherUtils {
    private static final String PASSWORD = "Th15-15-4-P455w0rd";
    private static final String ALGORITHM = "DES";

    public static String encryptIt(@NonNull String value) {
        try {
            DESKeySpec keySpec = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                keySpec = new DESKeySpec(PASSWORD.getBytes(StandardCharsets.UTF_8));
            }
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            SecretKey key = keyFactory.generateSecret(keySpec);

            byte[] clearText = new byte[0];
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                clearText = value.getBytes(StandardCharsets.UTF_8);
            }
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            return Base64.encodeToString(cipher.doFinal(clearText), Base64.DEFAULT);
        } catch (InvalidKeyException | InvalidKeySpecException | NoSuchAlgorithmException |
                 BadPaddingException | NoSuchPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return value;
    }

}