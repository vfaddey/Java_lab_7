package server.interfaces;

public interface PasswordEncoder {
    byte[] getSalt();
    byte[] hash(byte[] password, byte[] salt);
}
