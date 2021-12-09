//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.xxpay.agent.secruity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenUtil implements Serializable {
    private static final long serialVersionUID = -3301605591108950415L;
    private static final String CLAIM_KEY_USERNAME = "sub";
    private static final String CLAIM_KEY_MCHID = "id";
    private static final String CLAIM_KEY_CREATED = "created";
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private Long expiration;

    public JwtTokenUtil() {
    }

    public String getUsernameFromToken(String token) {
        String username;
        try {
            Claims claims = this.getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception var4) {
            username = null;
        }

        return username;
    }

    public Date getCreatedDateFromToken(String token) {
        Date created;
        try {
            Claims claims = this.getClaimsFromToken(token);
            created = new Date((Long)claims.get("created"));
        } catch (Exception var4) {
            created = null;
        }

        return created;
    }

    public Date getExpirationDateFromToken(String token) {
        Date expiration;
        try {
            Claims claims = this.getClaimsFromToken(token);
            expiration = claims.getExpiration();
        } catch (Exception var4) {
            expiration = null;
        }

        return expiration;
    }

    private Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = (Claims)Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token).getBody();
        } catch (Exception var4) {
            claims = null;
        }

        return claims;
    }

    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + this.expiration * 1000L);
    }

    private Boolean isTokenExpired(String token) {
        Date expiration = this.getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return lastPasswordReset != null && created.before(lastPasswordReset);
    }

    public String generateToken(UserDetails userDetails) {
        JwtUser jwtUser = (JwtUser)userDetails;
        Map<String, Object> claims = new HashMap();
        claims.put("sub", userDetails.getUsername());
        claims.put("created", new Date());
        claims.put("id", jwtUser.getId());
        return this.generateToken((Map)claims);
    }

    public String generateToken(Long mchId, String userName) {
        Map<String, Object> claims = new HashMap();
        claims.put("sub", userName);
        claims.put("created", new Date());
        claims.put("id", mchId);
        return this.generateToken((Map)claims);
    }

    String generateToken(Map<String, Object> claims) {
        return Jwts.builder().setClaims(claims).setExpiration(this.generateExpirationDate()).signWith(SignatureAlgorithm.HS512, this.secret).compact();
    }

    public Boolean canTokenBeRefreshed(String token, Date lastPasswordReset) {
        Date created = this.getCreatedDateFromToken(token);
        return !this.isCreatedBeforeLastPasswordReset(created, lastPasswordReset) && !this.isTokenExpired(token);
    }

    public String refreshToken(String token) {
        String refreshedToken;
        try {
            Claims claims = this.getClaimsFromToken(token);
            claims.put("created", new Date());
            refreshedToken = this.generateToken((Map)claims);
        } catch (Exception var4) {
            refreshedToken = null;
        }

        return refreshedToken;
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        JwtUser user = (JwtUser)userDetails;
        String username = this.getUsernameFromToken(token);
        Date created = this.getCreatedDateFromToken(token);
        return username.equals(user.getUsername()) && !this.isTokenExpired(token) && !this.isCreatedBeforeLastPasswordReset(created, user.getLastPasswordResetDate());
    }
}
