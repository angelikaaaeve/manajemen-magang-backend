package com.bsi.manajement_magang.config;

import com.bsi.manajement_magang.modules.iam.presentation.filter.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/iam/login", "/api/iam/register").permitAll()
                .requestMatchers("/api/mahasiswa/**").permitAll()
                .requestMatchers("/api/absensi/**").permitAll()
                .requestMatchers("/api/kegiatan/**").permitAll()
                .requestMatchers("/api/penilaian/**").permitAll()
                .requestMatchers("/api/sertifikat/**").permitAll()
                .requestMatchers("/api/surat-keterangan/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(new JwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
