package com.upc.photo.component;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Created by  waiter on 18-11-18  上午8:54.
 *
 * @author waiter
 */
@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()||authentication.getPrincipal() instanceof String) {
            return Optional.empty();
        }
        Optional<String> username = Optional.of(((UserDetails) authentication.getPrincipal()).getUsername());
        return username ;
    }
}