package com.example.demo.security;

import com.example.demo.service.dto.UserDTO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class CookieAuthenticationFilter extends OncePerRequestFilter {

@Override
protected void doFilterInternal(HttpServletRequest request,
                                HttpServletResponse response,
                                FilterChain filterChain) throws ServletException, IOException {

    // ĐỪNG chặn theo attribute; cho phép chạy lại ở async dispatch
    if (SecurityContextHolder.getContext().getAuthentication() == null) {
        String idToken = null;

        // 1) Ưu tiên Bearer
        String authz = request.getHeader("Authorization");
        if (authz != null && authz.startsWith("Bearer ")) {
            idToken = authz.substring(7);
            System.out.println("[Auth] Using Bearer token from Authorization header.");
        }

        // 2) Fallback cookie
        if (idToken == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies == null) {
                System.out.println("[Auth] No cookies present in request.");
            } else {
                for (Cookie c : cookies) {
                    if ("idToken".equals(c.getName())) {
                        idToken = c.getValue();
                        System.out.println("[Auth] Using idToken from cookie.");
                        break;
                    }
                }
            }
        }

        if (idToken == null || idToken.isBlank()) {
            System.out.println("[Auth] Missing token (no Bearer header & no idToken cookie).");
        } else {
            System.out.println("[Auth] Verifying token...");
            try {
                FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(idToken);
                String uid = decoded.getUid();

                Object roleClaim = decoded.getClaims().get("role");
                String role = (roleClaim != null && "Admin".equalsIgnoreCase(roleClaim.toString()))
                        ? "ROLE_ADMIN" : "ROLE_USER";

                UserDTO userDTO = new UserDTO();
                userDTO.setUid(uid);
                userDTO.setRole(role.replace("ROLE_", ""));

                var auth = new UsernamePasswordAuthenticationToken(
                        userDTO, null,
                        java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority(role))
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
                System.out.printf("[Auth] Verified token for uid=%s, role=%s (dispatch=%s)%n",
                        uid, role, request.getDispatcherType());

            } catch (com.google.firebase.auth.FirebaseAuthException e) {
                System.out.printf("[Auth] verifyIdToken failed: code=%s, message=%s%n",
                        e.getAuthErrorCode(), e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }
    } else {
        System.out.println("[Auth] SecurityContext already has authentication, skipping verification. dispatch=" + request.getDispatcherType());
    }

    filterChain.doFilter(request, response);
}

@Override
protected boolean shouldNotFilterAsyncDispatch() {
    // PHẢI trả về false để filter chạy ở ASYNC dispatch
    return false;
}

    
}
