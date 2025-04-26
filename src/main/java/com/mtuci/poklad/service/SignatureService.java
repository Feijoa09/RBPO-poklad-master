package com.mtuci.poklad.service;

import com.mtuci.poklad.models.Signature;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SignatureService {

    // Получить все активные сигнатуры
    List<Signature> getAllActiveSignatures();

    // Получить сигнатуры, обновленные после указанного времени
    List<Signature> getSignaturesUpdatedAfter(LocalDateTime since);


    // Получить сигнатуры по списку их ID
    List<Signature> getSignaturesByIds(List<UUID> ids);



    // Добавить новую сигнатуру
    Signature addSignature(String threatName,
                           String signatureData,
                           String fileType,
                           Integer offsetStart,
                           Integer offsetEnd);

    // Обновить существующую сигнатуру
    Signature updateSignature(UUID id,
                              String threatName,
                              String signatureData,
                              String fileType,
                              Integer offsetStart,
                              Integer offsetEnd);

    // Удалить (пометить как DELETED) сигнатуру
    void deleteSignature(UUID id);

    // Проверка цифровой подписи
    boolean verifySignature(Signature signature);

    // Генерация цифровой подписи (используется внутри, но ты явно вызываешь её в реализации, поэтому она может быть в интерфейсе)
    String generateDigitalSignature(String threatName,
                                    String firstBytesString,
                                    String remainderHash,
                                    int remainderLength,
                                    String fileType,
                                    int offsetStart,
                                    int offsetEnd);
}
