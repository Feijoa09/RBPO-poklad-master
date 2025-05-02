package com.mtuci.poklad.service;

import org.springframework.util.LinkedMultiValueMap;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SignatureBinaryService {

    // Получить все активные сигнатуры
    LinkedMultiValueMap<String, Object> getAllActiveSignatures();

    // Получить сигнатуры, обновленные после указанного времени
    LinkedMultiValueMap<String, Object> getSignaturesUpdatedAfter(LocalDateTime since);


    // Получить сигнатуры по списку их ID
    LinkedMultiValueMap<String, Object> getSignaturesByIds(List<UUID> ids);
}



