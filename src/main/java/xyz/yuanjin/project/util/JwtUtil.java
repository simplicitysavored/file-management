package xyz.yuanjin.project.util;

import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yuanjin
 */
@Slf4j
public class JwtUtil {
    private static Key KEY = new SecretKeySpec("javastack".getBytes(), SignatureAlgorithm.HS512.getJcaName());

    public static String createToken() {

        JwtPayload payload = new JwtPayload();
        payload.setUid("用户ID");
        payload.setExp(new Date(System.currentTimeMillis() + 60_000 * 10));
        payload.setIat(new Date());
        payload.setIss("System");

        return Jwts.builder()
                .setHeader(new HashMap<>())
                .setPayload(payload.toString())
                .signWith(SignatureAlgorithm.HS512, KEY)
                .compact();
    }

    public static JwtPayload authToken(String compactJws) {
        if (null == compactJws) {
            return null;
        }
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(KEY).parseClaimsJws(compactJws);
            JwsHeader header = claimsJws.getHeader();
            Claims body = claimsJws.getBody();

            JwtPayload payload = JSON.parseObject(JSON.toJSONString(body), JwtPayload.class);


            if (body.getExpiration().after(new Date())) {
                return payload;
            } else {
                log.error("token 已过期！");

            }

        } catch (ExpiredJwtException e) {
            e.printStackTrace();
            log.error("token 已过期！");
        } catch (UnsupportedJwtException | IllegalArgumentException | MalformedJwtException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            log.error("JWT签名与本地计算的签名不匹配。 JWT的有效性不能断言，不应该被信任。");
        }

        return null;
    }
}
