package com.mtuci.poklad.requests;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class GuidsRequest {
    private List<UUID> guids;
}
