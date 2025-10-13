package com.qixiang.qixiang_system_api.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT工具类
 * 负责JWT Token的生成、解析和验证
 * 使用JJWT库实现
 */
public class JwtUtil {
    
    /**
     * JWT密钥
     * 注意：在生产环境中，应该从配置文件或环境变量中读取
     */
    private static final String SECRET_KEY = "qixiang-sports-system-jwt-secret-key-2025-very-secure-and-long-enough-for-hs256-algorithm";
    
    /**
     * Token有效期（24小时）
     */
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000L; // 24小时
    
    /**
     * Token发行者
     */
    private static final String ISSUER = "qixiang-sports-system";
    
    /**
     * 获取签名密钥
     * @return SecretKey 签名密钥
     */
    private static SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
    
    /**
     * 生成JWT Token
     * @param username 用户名
     * @return JWT Token字符串
     */
    public static String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", username);
        claims.put("iss", ISSUER);
        claims.put("iat", new Date(System.currentTimeMillis()));
        claims.put("type", "Bearer");
        
        return createToken(claims, username);
    }
    
    /**
     * 创建Token
     * @param claims 声明信息
     * @param subject 主题（用户名）
     * @return JWT Token字符串
     */
    private static String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);
        
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuer(ISSUER)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }
    
    /**
     * 验证Token并获取用户名
     * @param token JWT Token字符串
     * @return 用户名，如果Token无效则返回null
     */
    public static String validateTokenAndGetUsername(String token) {
        try {
            String username = extractUsername(token);
            if (username != null && !isTokenExpired(token)) {
                return username;
            }
        } catch (Exception e) {
            // Token无效，记录日志或进行其他处理
            return null;
        }
        return null;
    }
    
    /**
     * 从Token中提取用户名
     * @param token JWT Token字符串
     * @return 用户名
     */
    public static String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    /**
     * 从Token中提取过期时间
     * @param token JWT Token字符串
     * @return 过期时间
     */
    public static Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * 从Token中提取指定声明
     * @param token JWT Token字符串
     * @param claimsResolver 声明解析器
     * @param <T> 声明类型
     * @return 声明值
     */
    public static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * 从Token中提取所有声明
     * @param token JWT Token字符串
     * @return Claims对象
     */
    private static Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(null, null, "Token已过期");
        } catch (UnsupportedJwtException e) {
            throw new UnsupportedJwtException("不支持的Token类型");
        } catch (MalformedJwtException e) {
            throw new MalformedJwtException("Token格式错误");
        } catch (SignatureException e) {
            throw new SignatureException("Token签名验证失败");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Token参数非法");
        }
    }
    
    /**
     * 检查Token是否过期
     * @param token JWT Token字符串
     * @return 是否过期
     */
    private static Boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            return true; // 如果无法解析过期时间，认为Token无效
        }
    }
    
    /**
     * 验证Token是否对特定用户有效
     * @param token JWT Token字符串
     * @param userDetails 用户详情
     * @return 是否有效
     */
    public static Boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 验证Token格式是否正确
     * @param token JWT Token字符串
     * @return 是否格式正确
     */
    public static Boolean isTokenFormatValid(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                return false;
            }
            
            // 检查Token是否包含三个部分（header.payload.signature）
            String[] parts = token.split("\\.");
            return parts.length == 3;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 刷新Token（生成新的Token）
     * @param token 旧的Token
     * @return 新的Token
     */
    public static String refreshToken(String token) {
        try {
            String username = extractUsername(token);
            return generateToken(username);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 获取Token剩余有效时间（秒）
     * @param token JWT Token字符串
     * @return 剩余有效时间（秒），如果Token无效返回-1
     */
    public static long getTokenRemainingTime(String token) {
        try {
            Date expiration = extractExpiration(token);
            Date now = new Date();
            return (expiration.getTime() - now.getTime()) / 1000;
        } catch (Exception e) {
            return -1;
        }
    }
}
