package lessmeaning.easymessage;

import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Максим on 23.11.2016.
 */
public class MessageEncryption {


    private static final String TAG = "encryptionmessage";
    private static SecretKeySpec secretKey;
    private static byte[] key;

    public static void setKey(long conversationID)
    {
        MessageDigest sha = null;
        String password = "DfnDj32uDdssDK";
        StringBuilder gen = new StringBuilder();
        for(int i = 0; i < password.length(); i++) {
            char c = (char) ((password.charAt(i) + conversationID * i) % 1000);
            gen.append(c);
        }
        password = gen.toString();
        try {
            key = password.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static String encrypt(String strToEncrypt, long conversationID) {
        try {
            setKey(conversationID);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")), Base64.DEFAULT);
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public static String decrypt(String strToDecrypt, long conversationID)
    {
        try {
            setKey(conversationID);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.decode(strToDecrypt, Base64.DEFAULT)));
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }

    public static String encryptPassword(String x) throws Exception {
        java.security.MessageDigest d = null;
        d = java.security.MessageDigest.getInstance("SHA-1");
        d.reset();
        d.update(x.getBytes());
        return new String(d.digest());
    }
}
