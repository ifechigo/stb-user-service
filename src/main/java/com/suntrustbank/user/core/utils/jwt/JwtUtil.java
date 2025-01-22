package com.suntrustbank.user.core.utils.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class JwtUtil {
    private static final int MAX_LENGTH = 3;
    private static final long MULTIPLIER = 1000L;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Gets the specified data value from
     * the authentication token by providing the
     * key name e.g 'email', 'sub' e.t.c.
     *
     * @param authorizationHeader
     * @param fieldKeyName
     * @return
     */
    public static <T> Optional<T> getClaim(String authorizationHeader, String fieldKeyName) {
        try {
            if (!isValidBearerToken(authorizationHeader)) {
                return Optional.empty();
            }

            String payload = decodeToken(authorizationHeader);
            Map<String, Object> payloadMap = OBJECT_MAPPER.readValue(payload, Map.class);

            if (payloadMap.containsKey(fieldKeyName)) {
                return Optional.ofNullable((T) payloadMap.get(fieldKeyName));
            } else {
                log.error("Field key not found in token");
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Error occurred when decoding token: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private static String decodeToken(String bearerToken) {
        String[] chunks = bearerToken.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        return new String(decoder.decode(chunks[1]));
    }

    public static Map getPrincipalPayload(String bearerToken) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(decodeToken(bearerToken), Map.class);
    }

    public static boolean isValidBearerToken(String bearerToken) {
        try {
            Map<String, Object> jwt = getPrincipalPayload(bearerToken);
            int expiryTime = (int) jwt.get("exp");
            return new Date().before(new Date(expiryTime * MULTIPLIER));
        } catch (JsonProcessingException ex) {
            return false;
        }
    }
}