package com.mtuci.poklad.controllers;

import com.mtuci.poklad.models.Signature;
import com.mtuci.poklad.requests.GuidsRequest;
import com.mtuci.poklad.service.SignatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/signatures")
public class SignatureController {

    @Autowired
    private SignatureService signatureService;

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

    @GetMapping
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

    @GetMapping("/since")
    public ResponseEntity<List<Signature>> getSignaturesUpdatedAfter(@RequestParam("since") String since) {
        try {
            List<Signature> signatures = signatureService.getSignaturesUpdatedAfter(java.time.LocalDateTime.parse(since));
            return ResponseEntity.ok(signatures);
        } catch (OptimisticLockingFailureException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();  // Конфликт версий
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();  // Общая ошибка
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Signature> addSignature(
            @RequestParam String threatName,
            @RequestParam String signatureData,
            @RequestParam String fileType,
            @RequestParam Integer offsetStart,
            @RequestParam Integer offsetEnd,
            @RequestHeader("Authorization") String authorization
    ) {
        try {
            Signature signature = signatureService.addSignature(threatName, signatureData, fileType, offsetStart, offsetEnd);
            return ResponseEntity.status(HttpStatus.CREATED).body(signature);  // Успех, создание новой записи
        } catch (OptimisticLockingFailureException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();  // Конфликт версий
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();  // Общая ошибка
        }
    }

    @PostMapping("/update")
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
            Signature signature = signatureService.updateSignature(id, threatName, signatureData, fileType, offsetStart, offsetEnd);
            return ResponseEntity.ok(signature);  // Успех, обновление записи
        } catch (OptimisticLockingFailureException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();  // Конфликт версий
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();  // Общая ошибка
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> deleteSignature(@RequestParam UUID id) {
        try {
            signatureService.deleteSignature(id);
            return ResponseEntity.noContent().build();  // Успешное удаление без содержимого
        } catch (OptimisticLockingFailureException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();  // Конфликт версий
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();  // Общая ошибка
        }
    }
}