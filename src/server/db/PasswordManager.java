package server.db;

import server.interfaces.PasswordDecoder;
import server.interfaces.PasswordEncoder;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class PasswordManager implements PasswordDecoder, PasswordEncoder {
    private final Random randomizer = new SecureRandom();
    @Override
    public String bytesToString(byte[] bytes) {
        BigInteger no = new BigInteger(1, bytes);
        return no.toString(16);
    }

    @Override
    public boolean isExpectedPassword(byte[] password, byte[] salt, byte[] expectedHash) {
        byte[] passwordHash = hash(password, salt);
        if (passwordHash.length != expectedHash.length) {
            return false;
        }
        for (int i = 0; i < passwordHash.length; i++) {
            if (passwordHash[i] != expectedHash[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public byte[] getSalt() {
        byte[] result = new byte[16];
        randomizer.nextBytes(result);
        return result;
    }

    @Override
    public byte[] hash(byte[] password, byte[] salt) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-224");

            byte[] saltedPassword = new byte[password.length + salt.length];
            System.arraycopy(password, 0, saltedPassword, 0, password.length);
            System.arraycopy(salt, 0, saltedPassword, password.length, salt.length);

            return messageDigest.digest(saltedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError("Error while hashing a password: " + e.getMessage());
        }
    }

}
