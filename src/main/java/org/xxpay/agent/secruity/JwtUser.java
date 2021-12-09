//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.xxpay.agent.secruity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collection;
import java.util.Date;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class JwtUser implements UserDetails {
    private Long id;
    private String name;
    private Byte status;
    private String username;
    private String password;
    private String email;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Date lastPasswordResetDate;

    public JwtUser(Long id, String name, Byte status, String username, String password, String email, Collection<? extends GrantedAuthority> authorities, Date lastPasswordResetDate) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.username = username;
        this.password = password;
        this.email = email;
        this.authorities = authorities;
        this.lastPasswordResetDate = lastPasswordResetDate;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @JsonIgnore
    public Long getId() {
        return this.id;
    }

    @JsonIgnore
    public String getPassword() {
        return this.password;
    }

    public String getUsername() {
        return this.username;
    }

    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

    @JsonIgnore
    public Date getLastPasswordResetDate() {
        return this.lastPasswordResetDate;
    }

    public String getName() {
        return this.name;
    }

    public Byte getStatus() {
        return this.status;
    }

    public String getEmail() {
        return this.email;
    }

    public static JwtUser getCurrentJWTUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        } else {
            try {
                return (JwtUser)authentication.getPrincipal();
            } catch (Exception var2) {
                return null;
            }
        }
    }

    public static String getCurrentUserName() {
        JwtUser user = getCurrentJWTUser();
        return user == null ? "匿名用户" : user.getName();
    }

    public static Long getCurrentUserId() {
        JwtUser user = getCurrentJWTUser();
        return user == null ? 0L : user.getId();
    }
}
