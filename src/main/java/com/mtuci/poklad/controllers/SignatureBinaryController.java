package com.mtuci.poklad.controllers;

import com.mtuci.poklad.configuration.JwtTokenProvider;
import com.mtuci.poklad.requests.GuidsRequest;
import com.mtuci.poklad.service.impl.SignatureBinaryServiceImpl;
import com.mtuci.poklad.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class SignatureBinaryController {
    private final SignatureBinaryServiceImpl signatureService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserServiceImpl userService;

    @PostMapping("/signatures_binary/by-guids")
    public ResponseEntity<?> getSignaturesByGuid(@RequestBody GuidsRequest guidsRequest) {
        try {
            List<UUID> guids = guidsRequest.getGuids();
            LinkedMultiValueMap<String, Object> signatures = signatureService.getSignaturesByIds(guids);
            return getResponseEntity(HttpStatus.OK,signatures);
        } catch (OptimisticLockingFailureException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();  // Конфликт версий
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();  // Общая ошибка
        }
    }

    @GetMapping("/signatures_binary")
    public ResponseEntity<?> getAllActiveSignatures() {
        try {
            LinkedMultiValueMap<String, Object> signatures = signatureService.getAllActiveSignatures();
            return getResponseEntity(HttpStatus.OK,signatures);
        } catch (OptimisticLockingFailureException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();  // Конфликт версий
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();  // Общая ошибка
        }
    }

    @GetMapping("/signatures_binary/since")
    public ResponseEntity<?> getSignaturesUpdatedAfter(@RequestParam("since") Integer since) {
        try {
            LinkedMultiValueMap<String, Object> signatures = signatureService.getSignaturesUpdatedAfter(LocalDateTime.ofInstant(Instant.ofEpochSecond(since), ZoneId.systemDefault()));
            return getResponseEntity(HttpStatus.OK,signatures);
        } catch (OptimisticLockingFailureException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();  // Конфликт версий
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();  // Общая ошибка
        }
    }
    private ResponseEntity<MultiValueMap<String, Object>> getResponseEntity(HttpStatus status, MultiValueMap<String, Object> parts) {
        return ResponseEntity.status(status).contentType(MediaType.parseMediaType("multipart/mixed")).body(parts);
    }
}
