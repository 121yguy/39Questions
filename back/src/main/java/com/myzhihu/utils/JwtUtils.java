package com.myzhihu.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {

    private static final String secret = "121yguy";

    public static String createJwt(Integer uid) {
        Map<String,Object> claim = new HashMap<>();
        claim.put("userId",uid);
        return JWT.create()
                .withClaim("user", claim)
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + 7 * 24 * 3600 * 1000))
                .sign(Algorithm.HMAC256(secret));
    }

    public static String createJwtWithUidAndDeviceId(Integer uid, String deviceId) {
        Map<String,Object> claim = new HashMap<>();
        claim.put("userId",uid);
        claim.put("deviceId",deviceId);
        return JWT.create()
                .withClaim("user", claim)
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + 7 * 24 * 3600 * 1000))
                .sign(Algorithm.HMAC256(secret));
    }

    public static String createJwtWithExpires(Integer uid, Integer Expires) {
        Map<String,Object> claim = new HashMap<>();
        claim.put("userId",uid);
        return JWT.create()
                .withClaim("user", claim)
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + Expires))
                .sign(Algorithm.HMAC256(secret));
    }


    /**
     *
     * @param token JWT令牌
     * @return 解析后的JWT令牌，以哈希表的形式返回，哈希表的值是Claim对象
     */
    public static Map<String, Claim> getJwtPayload(String token) {
        try {
            // 解析 JWT，解码并不进行签名验证
            DecodedJWT decodedJWT = JWT.decode(token);
            // 返回载体部分的 Claims
            return decodedJWT.getClaims();
        } catch (JWTDecodeException e) {
            throw new RuntimeException("无效的 JWT 格式", e);
        }
    }



    public static Map<String, Claim> parseJwt(String token) {
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(secret)).build();
        try {
            DecodedJWT decodedJWT = jwtVerifier.verify(token);
            return decodedJWT.getClaims();
        }catch (Exception e){
            throw new RuntimeException("登录信息过期，请重新登录");
        }
    }
}
