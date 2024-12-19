package com.mtuci.poklad.service;


import com.mtuci.poklad.models.ApplicationUser;

public interface AutheneticationService {
    boolean authenticate(ApplicationUser user, String password);
}
