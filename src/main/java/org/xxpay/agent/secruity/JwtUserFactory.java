//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.xxpay.agent.secruity;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.xxpay.core.entity.AgentInfo;

public final class JwtUserFactory {
    private JwtUserFactory() {
    }

    public static JwtUser create(String userName, AgentInfo agentInfo) {
        return new JwtUser(agentInfo.getAgentId(), agentInfo.getAgentName(), agentInfo.getStatus(), userName, agentInfo.getPassword(), agentInfo.getEmail(), mapToGrantedAuthorities("ROLE_AGENT_NORMAL"), agentInfo.getLastPasswordResetTime());
    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(String role) {
        String[] roles = role.split(":");
        List<String> authorities = Arrays.asList(roles);
        return (List)authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }
}
