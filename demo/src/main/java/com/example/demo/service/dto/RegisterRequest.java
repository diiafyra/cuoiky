package com.example.demo.service.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String displayName;
}
