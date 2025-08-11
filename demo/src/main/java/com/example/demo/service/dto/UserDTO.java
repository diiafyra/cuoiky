package com.example.demo.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String uid;
    private String email;
    private String displayName;
    private String photoUrl;
    private String role; // USER hoặc ADMIN
}
