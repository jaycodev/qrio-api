package com.qrio.shared.config.security;

import com.qrio.appAdmin.repository.AppAdminRepository;
import com.qrio.shared.type.Status;
import com.qrio.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final AppAdminRepository appAdminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var userOpt = userRepository.findByEmail(username);
        if (userOpt.isPresent()) {
            var user = userOpt.get();
            var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())
                    .password(user.getPassword())
                    .authorities(authorities)
                    .accountLocked(user.getStatus() != Status.ACTIVO)
                    .build();
        }

        var adminOpt = appAdminRepository.findByEmail(username);
        if (adminOpt.isPresent()) {
            var admin = adminOpt.get();
            var authorities = List.of(new SimpleGrantedAuthority("ROLE_APP_ADMIN"));
            return org.springframework.security.core.userdetails.User
                    .withUsername(admin.getEmail())
                    .password(admin.getPassword())
                    .authorities(authorities)
                    .accountLocked(admin.getStatus() != Status.ACTIVO)
                    .build();
        }

        throw new UsernameNotFoundException("User or AppAdmin not found");
    }
}