//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.xxpay.agent.secruity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.xxpay.agent.user.service.UserService;
import org.xxpay.core.entity.AgentInfo;

@Service
public class JwtUserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserService userService;

    public JwtUserDetailsServiceImpl() {
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AgentInfo agentInfo = this.userService.findByLoginName(username);
        return agentInfo == null ? null : JwtUserFactory.create(username, agentInfo);
    }
}
