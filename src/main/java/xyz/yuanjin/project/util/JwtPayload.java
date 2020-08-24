package xyz.yuanjin.project.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import lombok.Data;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * @author yuanjin
 */
@Data
public class JwtPayload {
    /**
     * not before
     */
    private Long nbf;
    /**
     * jwt签发者
     */
    private String iss;
    /**
     * jwt所面向的用户
     */
    private String sub;
    /**
     * 接收jwt的一方
     */
    private String aud;
    /**
     * jwt的过期时间，这个过期时间必须要大于签发时间
     */
    private Long exp;
    /**
     * 签发日期
     */
    private Long iat;
    /**
     * jwt的唯一身份标识，主要用来作为一次性token,从而回避重放攻击。
     */
    private String jti;
    /**
     * 用户id
     */
    private String uid;

    public void setNbf(Long nbf) {
        this.nbf = nbf;
    }

    public void setExp(Long exp) {
        this.exp = exp;
    }

    public void setIat(Long iat) {
        this.iat = iat;
    }

    public void setNbf(Date nbf) {
        this.nbf = nbf.getTime() / 1000;
    }

    public void setExp(Date exp) {
        this.exp = exp.getTime() / 1000;
    }

    public void setIat(Date iat) {
        this.iat = iat.getTime() / 1000;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
