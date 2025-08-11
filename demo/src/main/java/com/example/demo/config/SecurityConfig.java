package com.example.demo.config;

import com.example.demo.security.CookieAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/", "/home", "/login", "/register", "/error",
                    "/favicon.ico", "/css/**", "/js/**", "/img/**", "/fonts/**", "/webjars/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            // CookieAuthenticationFilter CHỈ set auth nếu token hợp lệ, KHÔNG tự trả 401
            .addFilterBefore(new CookieAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
.exceptionHandling(ex -> ex.authenticationEntryPoint((req, res, e) -> {
    System.out.println("[Auth] EntryPoint 401 uri=" + req.getRequestURI()
        + " asyncStarted=" + req.isAsyncStarted()
        + " dispatchType=" + req.getDispatcherType()
        + " auth=" + SecurityContextHolder.getContext().getAuthentication());
    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    res.setContentType("application/json;charset=UTF-8");
    res.getWriter().write("{\"error\":\"Unauthorized\"}");
}));


        return http.build();
    }
}
