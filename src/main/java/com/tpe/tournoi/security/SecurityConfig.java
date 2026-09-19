package com.tpe.tournoi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();

        manager.createUser(User.withUsername("admin")
                .password(encoder.encode("admin123"))
                .roles("ADMIN")
                .build());

        manager.createUser(User.withUsername("manager")
                .password(encoder.encode("manager123"))
                .roles("MANAGER")
                .build());

        manager.createUser(User.withUsername("operator")
                .password(encoder.encode("operator123"))
                .roles("OPERATOR")
                .build());

        manager.createUser(User.withUsername("user")
                .password(encoder.encode("user123"))
                .roles("USER")
                .build());

        manager.createUser(User.withUsername("demo")
                .password(encoder.encode("demo123"))
                .roles("ADMIN", "MANAGER", "OPERATOR")
                .build());

        return manager;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Pages publiques
                .requestMatchers("/", "/login", "/css/**", "/js/**", "/h2-console/**").permitAll()
                // API REST : lecture = tous authentifiés
                .requestMatchers(HttpMethod.GET, "/api/tournaments/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/teams/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/matches/**").authenticated()
                // Tournois : écriture = ADMIN ou MANAGER
                .requestMatchers(HttpMethod.POST, "/api/tournaments").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers(HttpMethod.PUT, "/api/tournaments/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers(HttpMethod.DELETE, "/api/tournaments/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers(HttpMethod.POST, "/api/tournaments/*/teams").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers(HttpMethod.DELETE, "/api/tournaments/*/teams/*").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers(HttpMethod.POST, "/api/tournaments/*/generate-matches").hasAnyRole("ADMIN", "MANAGER")
                // Equipes : écriture = ADMIN ou MANAGER
                .requestMatchers(HttpMethod.POST, "/api/teams").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers(HttpMethod.PUT, "/api/teams/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers(HttpMethod.DELETE, "/api/teams/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers(HttpMethod.POST, "/api/*/players").hasAnyRole("ADMIN", "MANAGER")
                // Saisie de résultats = OPERATOR, MANAGER, ADMIN
                .requestMatchers(HttpMethod.PUT, "/api/matches/**").hasAnyRole("OPERATOR", "MANAGER", "ADMIN")
                // Toute autre requête
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            )
            .csrf(AbstractHttpConfigurer::disable)
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            );

        return http.build();
    }
}
