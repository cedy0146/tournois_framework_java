package com.tpe.tournoi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Pages publiques
                .requestMatchers("/", "/login", "/css/**", "/js/**", "/img/**", "/h2-console/**").permitAll()

                // --- Application web : accès aux formulaires ---
                .requestMatchers(HttpMethod.GET, "/tournaments/new", "/tournaments/*/edit",
                        "/teams/new", "/teams/*/edit", "/teams/*/players/*/edit")
                    .hasAnyRole("ADMIN", "MANAGER")
                // --- Application web : saisie d'un résultat ---
                .requestMatchers(HttpMethod.POST, "/tournaments/*/matches/*/result")
                    .hasAnyRole("OPERATOR", "MANAGER", "ADMIN")
                // --- Application web : gestion des tournois ---
                .requestMatchers(HttpMethod.POST, "/tournaments/**").hasAnyRole("ADMIN", "MANAGER")
                // --- Application web : gestion des équipes et des joueurs ---
                .requestMatchers(HttpMethod.POST, "/teams/**").hasAnyRole("ADMIN", "MANAGER")

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
                .requestMatchers(HttpMethod.POST, "/api/teams/*/players").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers(HttpMethod.PUT, "/api/teams/*/players/*").hasAnyRole("ADMIN", "MANAGER")
                // Saisie de résultats / planning = OPERATOR, MANAGER, ADMIN
                .requestMatchers(HttpMethod.PUT, "/api/matches/**").hasAnyRole("OPERATOR", "MANAGER", "ADMIN")
                // Toute autre requête
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/?logout=true")
                .permitAll()
            )
            .csrf(AbstractHttpConfigurer::disable)
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            );

        return http.build();
    }
}