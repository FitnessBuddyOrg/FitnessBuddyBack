package api.fitnessbuddyback.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    public String generateToken(UserDetails userDetails) throws JOSEException {
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(userDetails.getUsername())
                .claim("roles", roles)
                .expirationTime(new Date(System.currentTimeMillis() + expiration))
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
        signedJWT.sign(new MACSigner(secret));

        return signedJWT.serialize();
    }

    public boolean validateToken(String token, UserDetails userDetails) throws JOSEException, ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        return signedJWT.verify(new MACVerifier(secret))
                && signedJWT.getJWTClaimsSet().getSubject().equals(userDetails.getUsername())
                && new Date().before(signedJWT.getJWTClaimsSet().getExpirationTime());
    }

    public String extractEmail(String token) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        return signedJWT.getJWTClaimsSet().getSubject();
    }

    public Date extractExpiration(String token) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        return signedJWT.getJWTClaimsSet().getExpirationTime();
    }

    public boolean isTokenExpired(Date expirationTime) {
        return new Date().after(expirationTime);
    }

    public List<String> extractRoles(String token) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        return (List<String>) signedJWT.getJWTClaimsSet().getClaim("roles");
    }

    public String getUsernameFromToken(String token) {
        try {
            return extractEmail(token);
        } catch (ParseException e) {
            return null;
        }
    }
}

