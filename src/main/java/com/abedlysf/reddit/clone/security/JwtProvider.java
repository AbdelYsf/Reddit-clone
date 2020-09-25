package com.abedlysf.reddit.clone.security;

import com.abedlysf.reddit.clone.exceptions.SpringRedditException;
import com.abedlysf.reddit.clone.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.util.Date;

import static io.jsonwebtoken.Jwts.parser;
import static java.util.Date.from;

@Service
public class JwtProvider {

    private static final String KEYSTOR_FILE_NAME="springblog";
    private static final String KEYSTOR_FILE_NAME_WITH_EXTENSION="/springblog.jks";
    private static final String KEYSTOR_FILE_SECRET="secret";


    //storage facility for cryptographic keys and certificates
    private KeyStore keyStore;
    @PostConstruct
    public void init() {
        try {
            keyStore = KeyStore.getInstance("JKS"); // providing a keyStore instance of type JKS
            // getting the inputstream from the keystore file with the name pringblog.jks
            InputStream resourceAsStream = getClass().getResourceAsStream(KEYSTOR_FILE_NAME_WITH_EXTENSION);
            // provide the inputstream to the keystore , and the password of the keysore
            keyStore.load(resourceAsStream, KEYSTOR_FILE_SECRET.toCharArray());
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new SpringRedditException("Exception occurred while loading keystore", e);
        }

    }

    public String generateToken(Authentication authentication){
        User principal = (User) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(principal.getUsername())
                .signWith(getPrivateKey())
                .compact();

    }

//    public String generateTokenWithUserName(String username) {
//        return Jwts.builder()
//                .setSubject(username)
//                .setIssuedAt(from(Instant.now()))
//                .signWith(getPrivateKey())
//                .setExpiration(from(Instant.now().plusMillis(jwtExpirationInMillis)))
//                .compact();
//    }

    private PrivateKey getPrivateKey() {
        try {
            //getting the private key
            return (PrivateKey) keyStore.getKey(KEYSTOR_FILE_NAME, KEYSTOR_FILE_SECRET.toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new SpringRedditException("Exception occured while retrieving public key from keystore", e);
        }
    }

    public boolean validateToken(String jwt) {
        parser().setSigningKey(getPublickey()).parseClaimsJws(jwt);
        return true;
    }

    private PublicKey getPublickey() {
        try {
            // getting the public key
            return keyStore.getCertificate(KEYSTOR_FILE_NAME).getPublicKey();
        } catch (KeyStoreException e) {
            throw new SpringRedditException("Exception occured while " +
                    "retrieving public key from keystore", e);
        }
    }

    public String getUsernameFromJwt(String token) {
        Claims claims = parser()
                .setSigningKey(getPublickey())
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

//    public Long getJwtExpirationInMillis() {
//        return jwtExpirationInMillis;
//    }
}
