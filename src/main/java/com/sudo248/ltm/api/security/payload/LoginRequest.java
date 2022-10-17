package com.sudo248.ltm.api.security.payload;

import lombok.Data;

@Data
public class LoginRequest {

    private String username;
    private String password;
}
