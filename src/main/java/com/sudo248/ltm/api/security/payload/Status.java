package com.sudo248.ltm.api.security.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Status implements Serializable {
    private Boolean success;
    private String message;
}

