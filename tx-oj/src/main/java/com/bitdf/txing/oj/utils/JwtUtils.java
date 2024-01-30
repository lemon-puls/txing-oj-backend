package com.bitdf.txing.oj.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

/**
 * @author Lizhiwei
 * @date 2024/1/30 18:17:02
 * 注释：
 */
@Data
@Slf4j
public class JwtUtils {

    private final static String secret = "txing_lizhiwei";

    private final static String USERID_CLAIM = "user_id";

    private final static String CREATE_TIME = "create_time";

    /**
     * 生成token
     *
     * @param userId
     * @return
     */
    public static String createToken(Long userId) {
        String token = JWT.create()
                .withClaim(USERID_CLAIM, userId)
                .withClaim(CREATE_TIME, new Date())
                .sign(Algorithm.HMAC256(secret));
        return token;
    }

    /**
     * 校验token
     */
    public static Map<String, Claim> verifyToken(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT.getClaims();
        } catch (Exception e) {
            log.info("token解密出错：{}", token, e);
        }
        return null;
    }

    /**
     * 根据token获取userId
     */
    public static Long getUserIdOrNull(String token) {
        return Optional.ofNullable(verifyToken(token))
                .map(map -> map.get(USERID_CLAIM))
                .map(Claim::asLong)
                .orElse(null);
    }


}
