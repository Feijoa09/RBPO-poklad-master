package com.mtuci.poklad.requests;

import lombok.Data;

@Data
public class RegistrationRequest {
    private String login, email, password;
}
