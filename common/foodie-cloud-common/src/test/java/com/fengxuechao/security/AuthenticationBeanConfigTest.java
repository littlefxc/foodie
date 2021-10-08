package com.fengxuechao.security;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fengxuechao
 * @date 2020/3/19
 */
public class AuthenticationBeanConfigTest {

    private PasswordEncoder passwordEncoder;

    @Before
    public void setUp() throws Exception {
        String encodingId = "bcrypt";
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put(encodingId, new BCryptPasswordEncoder());
        encoders.put("pbkdf2", new Pbkdf2PasswordEncoder());
        encoders.put("scrypt", new SCryptPasswordEncoder());
        passwordEncoder = new DelegatingPasswordEncoder(encodingId, encoders);
    }

    /**
     * Qpf0SxOVUjUkWySXOZ16kw==
     */
    @Test
    public void bcrypt() {
        String encodePassword = passwordEncoder.encode("123456");
        String encodePassword1 = passwordEncoder.encode("123456");
        String encodePassword2 = passwordEncoder.encode("123456");
        String encodePassword3 = passwordEncoder.encode("123456");
        String encodePassword4 = passwordEncoder.encode("123456");
        String encodePassword5 = passwordEncoder.encode("123456");
        System.out.println(encodePassword);
        System.out.println(encodePassword1);
        System.out.println(encodePassword2);
        System.out.println(encodePassword3);
        System.out.println(encodePassword4);
        System.out.println(encodePassword5);
    }
}