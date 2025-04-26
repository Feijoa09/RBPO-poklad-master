package com.mtuci.poklad.requests;

import java.util.List;
import java.util.UUID;

public class GuidsRequest {
    private List<UUID> guids;

    // Конструктор
    public GuidsRequest(List<UUID> guids) {
        this.guids = guids;
    }

    // Геттеры и сеттеры
    public List<UUID> getGuids() {
        return guids;
    }

    public void setGuids(List<UUID> guids) {
        this.guids = guids;
    }
}
