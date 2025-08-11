package com.example.demo.service;

import com.example.demo.service.dto.RegisterRequest;
import com.example.demo.service.dto.UserDTO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    public UserDTO register(RegisterRequest request) throws Exception {
        UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest()
                .setEmail(request.getEmail())
                .setPassword(request.getPassword())
                .setDisplayName(request.getDisplayName())
                .setEmailVerified(false)
                .setDisabled(false);

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(createRequest);

        FirebaseAuth.getInstance().setCustomUserClaims(userRecord.getUid(), Map.of("role", "User"));

        return new UserDTO(
                userRecord.getUid(),
                userRecord.getEmail(),
                userRecord.getDisplayName(),
                userRecord.getPhotoUrl(),
                "User"
        );
    }
}
