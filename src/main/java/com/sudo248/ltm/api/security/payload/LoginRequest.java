package com.sudo248.ltm.api.security.payload;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginRequest implements Serializable {

    private String username;
    private String password;
}
