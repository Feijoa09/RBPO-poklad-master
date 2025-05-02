package com.mtuci.poklad.controllers;

import com.mtuci.poklad.configuration.JwtTokenProvider;
import com.mtuci.poklad.models.ApplicationUser;
import com.mtuci.poklad.models.Signature;
import com.mtuci.poklad.models.SignatureAudit;
import com.mtuci.poklad.requests.GuidsRequest;
import com.mtuci.poklad.service.impl.SignatureServiceImpl;
import com.mtuci.poklad.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class SignatureController {
    private final SignatureServiceImpl signatureService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserServiceImpl userService;

    @PostMapping("/signatures/by-guids")
    public ResponseEntity<List<Signature>> getSignaturesByGuid(@RequestBody GuidsRequest guidsRequest) {
        try {
            List<UUID> guids = guidsRequest.getGuids();
            List<Signature> signatures = signatureService.getSignaturesByIds(guids);
            return ResponseEntity.ok(signatures);
        } catch (OptimisticLockingFailureException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();  // Конфликт версий
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();  // Общая ошибка
        }
    }

    @GetMapping("/signatures")
    public ResponseEntity<List<Signature>> getAllActiveSignatures() {
        try {
            List<Signature> signatures = signatureService.getAllActiveSignatures();
            return ResponseEntity.ok(signatures);
        } catch (OptimisticLockingFailureException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();  // Конфликт версий
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();  // Общая ошибка
        }
    }

    @PostMapping("/audits/by-guids")
    public ResponseEntity<List<SignatureAudit>> getSignatureAudits(@RequestBody GuidsRequest guidsRequest) {
        try {
            List<SignatureAudit> audits = signatureService.getSignatureAudits(guidsRequest.getGuids());
            return ResponseEntity.ok(audits);
        } catch (OptimisticLockingFailureException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();  // Конфликт версий
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();  // Общая ошибка
        }
    }

    @GetMapping("/signatures/since")
    public ResponseEntity<List<Signature>> getSignaturesUpdatedAfter(@RequestParam("since") Integer since) {
        try {
            List<Signature> signatures = signatureService.getSignaturesUpdatedAfter(LocalDateTime.ofInstant(Instant.ofEpochSecond(since), ZoneId.systemDefault()));
            return ResponseEntity.ok(signatures);
        } catch (OptimisticLockingFailureException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();  // Конфликт версий
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();  // Общая ошибка
        }
    }

    @PostMapping("/signatures/add")
    public ResponseEntity<Signature> addSignature(
            @RequestParam String threatName,
            @RequestParam String signatureData,
            @RequestParam String fileType,
            @RequestParam Integer offsetStart,
            @RequestParam Integer offsetEnd,
            @RequestHeader("Authorization") String authorization
    ) {
        try {
            // Извлечение логина из токенам
            String login = jwtTokenProvider.getUsername(authorization.split(" ")[1]);

            // Получение пользователя по логину
            ApplicationUser user = userService.getUserByLogin(login)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            Signature signature = signatureService.addSignature(threatName, signatureData, fileType, offsetStart, offsetEnd, user.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(signature);  // Успех, создание новой записи
        } catch (OptimisticLockingFailureException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();  // Конфликт версий
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();  // Общая ошибка
        }
    }

    @PostMapping("/signatures/update")
    public ResponseEntity<Signature> updateSignature(
            @RequestParam UUID id,
            @RequestParam String threatName,
            @RequestParam String signatureData,
            @RequestParam String fileType,
            @RequestParam Integer offsetStart,
            @RequestParam Integer offsetEnd,
            @RequestHeader("Authorization") String authorization
    ) {
        try {
            // Извлечение логина из токена
            String login = jwtTokenProvider.getUsername(authorization.split(" ")[1]);

            // Получение пользователя по логину
            ApplicationUser user = userService.getUserByLogin(login)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            Signature signature = signatureService.updateSignature(id, threatName, signatureData, fileType, offsetStart, offsetEnd, user.getId());
            return ResponseEntity.ok(signature);  // Успех, обновление записи
        } catch (OptimisticLockingFailureException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();  // Конфликт версий
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();  // Общая ошибка
        }
    }

    @PostMapping("/signatures/delete")
    public ResponseEntity<Void> deleteSignature(@RequestParam UUID id,
                                                @RequestHeader("Authorization") String authorization) {
        try {
            // Извлечение логина из токена
            String login = jwtTokenProvider.getUsername(authorization.split(" ")[1]);

            // Получение пользователя по логину
            ApplicationUser user = userService.getUserByLogin(login)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            signatureService.deleteSignature(id, user.getId());
            return ResponseEntity.noContent().build();  // Успешное удаление без содержимого
        } catch (OptimisticLockingFailureException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();  // Конфликт версий
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();  // Общая ошибка
        }
    }
}