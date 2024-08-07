package ru.salfa.messenger.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.salfa.messenger.config.JwtConfig;
import ru.salfa.messenger.entity.postgres.User;
import ru.salfa.messenger.exception.UserTokenException;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {
    private static final String BEARER_PREFIX = "Bearer ";
    private final JwtConfig jwtConfig;
    private final Map<String, String> accessCache = new ConcurrentHashMap<>();
    private final Map<String, String> refreshCache = new ConcurrentHashMap<>();

    public String createAccessToken(UserDetails userDetails) {
        accessCache.remove(userDetails.getUsername());
        String accessToken = createToken(userDetails, jwtConfig.getAccessTokenSecret(), jwtConfig.getAccessTokenExpiration());
        accessCache.put(userDetails.getUsername(), accessToken);
        return accessToken;
    }

    public String createRefreshToken(UserDetails userDetails) {
        refreshCache.remove(userDetails.getUsername());
        String refreshToken = createToken(userDetails, jwtConfig.getRefreshTokenSecret(), jwtConfig.getRefreshTokenExpiration());
        refreshCache.put(userDetails.getUsername(), refreshToken);
        return refreshToken;
    }

    public void removeTokens(String phone) {
        accessCache.remove(phone);
        refreshCache.remove(phone);
    }

    public boolean accessTokenIsValid(String token, UserDetails userDetails) throws UserTokenException {
        return tokenIsValid(token, jwtConfig.getAccessTokenSecret(), userDetails);
    }

    public boolean accessTokenIsValid(String token) throws UserTokenException {
        return tokenIsValid(token, jwtConfig.getAccessTokenSecret());
    }

    public boolean accessTokenExistsInCache(String token) throws UserTokenException {
        String phone = getPhoneFromAccessToken(token);
        return accessCache.containsKey(phone) && accessCache.get(phone).equals(extractToken(token));
    }

    public boolean anotherAccessTokenExistsInCache(String token) throws UserTokenException {
        String phone = getPhoneFromAccessToken(token);
        return accessCache.containsKey(phone);
    }

    public boolean refreshTokenIsValid(String token, UserDetails userDetails) throws UserTokenException {
        return tokenIsValid(token, jwtConfig.getRefreshTokenSecret(), userDetails);
    }

    public boolean refreshTokenIsValid(String token) throws UserTokenException {
        return tokenIsValid(token, jwtConfig.getRefreshTokenSecret());
    }

    public boolean refreshTokenExistsInCache(String token) throws UserTokenException {
        String phone = getPhoneFromRefreshToken(token);
        return refreshCache.containsKey(phone) && refreshCache.get(phone).equals(extractToken(token));
    }

    public String getPhoneFromAccessToken(String token) throws UserTokenException {
        return getPhone(token, jwtConfig.getAccessTokenSecret());
    }

    public String getPhoneFromRefreshToken(String token) throws UserTokenException {
        return getPhone(token, jwtConfig.getRefreshTokenSecret());
    }

    private String createToken(UserDetails userDetails, String secretPhrase, Long expirationTime) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof User customUserDetails) {
            claims.put("id", customUserDetails.getId());
            claims.put("phone", customUserDetails.getPhone());
        }
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSecretKey(secretPhrase))
                .compact();
    }

    private boolean tokenIsValid(String token, String secretPhrase, UserDetails userDetails) throws UserTokenException {
        token = extractToken(token);
        return (getPhone(token, secretPhrase).equals(userDetails.getUsername()) && !getExpirationDate(token, secretPhrase).before(new Date()));
    }

    private boolean tokenIsValid(String token, String secretPhrase) throws UserTokenException {
        token = extractToken(token);
        try {
            getAllClaims(token, secretPhrase);
        } catch (ExpiredJwtException e) {
            genUserException("Token expired.");
        } catch (UnsupportedJwtException e) {
            genUserException("Token unsupported.");
        } catch (MalformedJwtException e) {
            genUserException("Malformed token.");
        } catch (Exception e) {
            genUserException("Invalid token.");
        }
        return true;
    }

    private SecretKey getSecretKey(String secretPhrase) {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretPhrase));
    }

    private String extractToken(String token) {
        return StringUtils.startsWith(token, BEARER_PREFIX) ? token.substring(BEARER_PREFIX.length()) : token;
    }

    private Claims getAllClaims(String token, String secretPhrase) {
        return Jwts.parser()
                .verifyWith(getSecretKey(secretPhrase))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private <T> T getClaim(String token, String secretPhrase, Function<Claims, T> claimsResolve) throws UserTokenException {
        try {
            return claimsResolve.apply(getAllClaims(token, secretPhrase));
        } catch (ExpiredJwtException e) {
            genUserException("Token expired.");
        } catch (UnsupportedJwtException e) {
            genUserException("Token unsupported.");
        } catch (MalformedJwtException e) {
            genUserException("Malformed token.");
        } catch (Exception e) {
            genUserException("Invalid token.");
        }
        return null;
    }

    private void genUserException(String message) throws UserTokenException {
        throw new UserTokenException("Token error: " + message);
    }

    private String getPhone(String token, String secretPhase) throws UserTokenException {
        token = extractToken(token);
        return getClaim(token, secretPhase, Claims::getSubject);
    }

    private Date getExpirationDate(String token, String secretPhase) throws UserTokenException {
        return getClaim(token, secretPhase, Claims::getExpiration);
    }
}