package com.qrio.shared.config.security;

import com.qrio.appAdmin.repository.AppAdminRepository;
import com.qrio.customer.repository.CustomerRepository;
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
    private final CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String subject) throws UsernameNotFoundException {
        // Try employee/owner by email
        var userOpt = userRepository.findByEmail(subject);
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

        // Try app admin by email
        var adminOpt = appAdminRepository.findByEmail(subject);
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

        // Customer login via Firebase removed

        throw new UsernameNotFoundException("Principal not found (user/admin/customer)");
    }
}