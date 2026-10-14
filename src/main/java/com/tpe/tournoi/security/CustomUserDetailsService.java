package com.tpe.tournoi.security;

import com.tpe.tournoi.entity.AppUser;
import com.tpe.tournoi.repository.AppUserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    public CustomUserDetailsService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Utilisateur introuvable : " + username));

        if (!user.isActif()) {
            throw new UsernameNotFoundException("Compte désactivé : " + username);
        }

        return User.withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(Arrays.stream(user.getRoles().split(","))
                        .map(String::trim)
                        .filter(r -> !r.isEmpty())
                        .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList()))
                .disabled(false)
                .build();
    }
}
