package ru.vsu.core.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class JwtServiceImpl implements JwtService {
    @Value("${app.jwt.private-key}")
    private String privateKeyPem;

    @Value("${app.jwt.public-key}")
    private String publicKeyPem;

    @Value("${app.jwt.expiration-hours:24}")
    private long expirationHours;

    @Value("${app.jwt.key-id:core-admin-rsa-key}")
    private String keyId;

    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;

    @PostConstruct
    public void init() {
        privateKey = readPrivateKey(privateKeyPem);
        publicKey = readPublicKey(publicKeyPem);
    }

    @Override
    public String generateToken(String adminId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .header().keyId(keyId).and()
                .subject(adminId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expirationHours, ChronoUnit.HOURS)))
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    @Override
    public String extractAdminId(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    @Override
    public Map<String, Object> getJwks() {
        return Map.of(
                "keys", List.of(Map.of(
                        "kty", "RSA",
                        "use", "sig",
                        "alg", "RS256",
                        "kid", keyId,
                        "n", encodeBase64Url(publicKey.getModulus()),
                        "e", encodeBase64Url(publicKey.getPublicExponent())
                ))
        );
    }

    private RSAPrivateKey readPrivateKey(String pem) {
        try {
            byte[] der = decodePem(pem, "PRIVATE KEY");
            PrivateKey parsedKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(der));
            return (RSAPrivateKey) parsedKey;
        } catch (Exception exception) {
            throw new IllegalStateException("Invalid JWT private key", exception);
        }
    }

    private RSAPublicKey readPublicKey(String pem) {
        try {
            byte[] der = decodePem(pem, "PUBLIC KEY");
            PublicKey parsedKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(der));
            return (RSAPublicKey) parsedKey;
        } catch (Exception exception) {
            throw new IllegalStateException("Invalid JWT public key", exception);
        }
    }

    private byte[] decodePem(String pem, String type) {
        String normalizedPem = pem
                .replace("-----BEGIN " + type + "-----", "")
                .replace("-----END " + type + "-----", "")
                .replaceAll("\\s", "");
        return Base64.getDecoder().decode(normalizedPem);
    }

    private String encodeBase64Url(BigInteger value) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(toUnsignedBytes(value));
    }

    private byte[] toUnsignedBytes(BigInteger value) {
        byte[] bytes = value.toByteArray();
        if (bytes.length > 1 && bytes[0] == 0) {
            byte[] normalized = new byte[bytes.length - 1];
            System.arraycopy(bytes, 1, normalized, 0, normalized.length);
            return normalized;
        }
        return bytes;
    }
}
