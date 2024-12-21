package com.mtuci.poklad.requests;

import lombok.Data;

@Data
public class AuthenticationRequest {
    private String login, password;
}
