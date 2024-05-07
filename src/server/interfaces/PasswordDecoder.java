package server.interfaces;

public interface PasswordDecoder {
    String bytesToString(byte[] bytes);
    boolean isExpectedPassword(byte[] password, byte[] salt, byte[] expectedHash);
}
