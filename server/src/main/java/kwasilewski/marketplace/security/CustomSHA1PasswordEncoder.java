package kwasilewski.marketplace.security;

import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CustomSHA1PasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return new ShaPasswordEncoder().encodePassword(rawPassword.toString(), null);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return rawPassword != null && encodedPassword != null && encode(rawPassword).equals(encodedPassword);
    }

}
