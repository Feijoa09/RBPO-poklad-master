package com.mtuci.poklad.service.impl;

import com.mtuci.poklad.models.Signature;
import com.mtuci.poklad.models.SignatureAudit;
import com.mtuci.poklad.models.SignatureHistory;
import com.mtuci.poklad.repositories.SignatureAuditRepository;
import com.mtuci.poklad.repositories.SignatureHistoryRepository;
import com.mtuci.poklad.repositories.SignatureRepository;
import com.mtuci.poklad.service.SignatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class SignatureServiceImpl implements SignatureService {

    private final SignatureRepository signatureRepository;
    private final SignatureHistoryRepository signatureHistoryRepository;
    private final SignatureAuditRepository signatureAuditRepository;

    @Value("${PUBLIC_KEY_SIGNATURE}")
    private String publicKeyBase64;
    @Value("${PRIVATE_KEY_SIGNATURE}")
    private String privateKeyBase64;

    @Autowired
    public SignatureServiceImpl(SignatureRepository signatureRepository,
                                SignatureHistoryRepository signatureHistoryRepository,
                                SignatureAuditRepository signatureAuditRepository) {
        this.signatureRepository = signatureRepository;
        this.signatureHistoryRepository = signatureHistoryRepository;
        this.signatureAuditRepository = signatureAuditRepository;
    }

    @Override
    public List<Signature> getAllActiveSignatures() {
        return signatureRepository.findAllByStatus("ACTUAL");
    }

    @Override
    public List<Signature> getSignaturesUpdatedAfter(LocalDateTime time) {
        return signatureRepository.findByUpdatedAtAfter(time);}

    @Override
    public List<Signature> getSignaturesByIds(List<UUID> ids) {
        return signatureRepository.findAllByIdInAndStatus(ids, "ACTUAL");

    }

    @Override
    @Transactional
    public Signature addSignature(
            String threatName,
            String signatureData,
            String fileType,
            Integer offsetStart,
            Integer offsetEnd

    ) {
        // Преобразуем строку signatureData в массив байт
        byte[] signatureDataBytes = signatureData.getBytes(StandardCharsets.UTF_8);

        // Извлекаем первые 8 байт данных
        byte[] newFirstBytes = Arrays.copyOfRange(signatureDataBytes, 0, Math.min(signatureDataBytes.length, 8));

        // Извлекаем остаток данных после первых 8 байт
        byte[] newRemainderBytes = Arrays.copyOfRange(signatureDataBytes, Math.min(signatureDataBytes.length, 8), signatureDataBytes.length);

        String newRemainderHash = generateRemainderHash(signatureData, offsetStart, offsetEnd);

        // Генерируем цифровую подпись (ЭЦП)
        String newDigitalSignature = generateDigitalSignature(
                threatName,
                new String(newFirstBytes, StandardCharsets.UTF_8),
                newRemainderHash,
                signatureDataBytes.length - 8,
                fileType,
                offsetStart,
                offsetEnd
        );


        // Создаём новую сущность Signature
        Signature newSignature = new Signature();
        newSignature.setThreatName(threatName);
        newSignature.setFirstBytes(newFirstBytes);
        newSignature.setRemainderHash(newRemainderHash);
        newSignature.setRemainderLength(newRemainderBytes.length);
        newSignature.setFileType(fileType);
        newSignature.setOffsetStart(offsetStart);
        newSignature.setOffsetEnd(offsetEnd);
        newSignature.setDigitalSignature(newDigitalSignature);

        // Сохранение аудита добавления новой сигнатуры
        SignatureAudit audit = new SignatureAudit();
        audit.setSignatureId(newSignature.getId());
        audit.setChangeType("CREATED");
        audit.setChangedAt(LocalDateTime.now());
        audit.setFieldsChanged("threat_name, first_bytes, remainder_hash, file_type, offset_start, offset_end");
        signatureAuditRepository.save(audit);

        // Сохраняем новую сигнатуру в репозитории
        return signatureRepository.save(newSignature);
    }

    public String generateDigitalSignature(String threatName, String firstBytesString, String remainderHash,
                                           int remainderLength, String fileType, int offsetStart, int offsetEnd) {
        try {
            // Формируем данные для подписи
            String dataToSign = threatName + firstBytesString + remainderHash + remainderLength + fileType + offsetStart + offsetEnd;

            // Декодируем приватный ключ из Base64
            byte[] byteKey = Base64.getDecoder().decode(privateKeyBase64);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(byteKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);


            // Инициализация подписи с алгоритмом SHA256withRSA
            java.security.Signature signature = java.security.Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);

            // Добавляем данные для подписи
            signature.update(dataToSign.getBytes());

            // Генерация подписи
            byte[] signedData = signature.sign();

            // Возвращаем подписанные данные в строковом формате Base64
            return Base64.getEncoder().encodeToString(signedData);
        } catch (Exception e) {
            throw new RuntimeException("Error generating digital signature", e);
        }
    }


    @Override
    @Transactional
    public void deleteSignature(UUID id) {
        Optional<Signature> signatureOptional = signatureRepository.findById(id);
        if (signatureOptional.isPresent()) {
            Signature signature = signatureOptional.get();

            SignatureHistory signatureHistory = new SignatureHistory();
            signatureHistory.setSignatureId(signature.getId());
            signatureHistory.setThreatName(signature.getThreatName());
            signatureHistory.setFirstBytes(signature.getFirstBytes());
            signatureHistory.setRemainderHash(signature.getRemainderHash());
            signatureHistory.setRemainderLength(signature.getRemainderLength());
            signatureHistory.setFileType(signature.getFileType());
            signatureHistory.setOffsetStart(signature.getOffsetStart());
            signatureHistory.setOffsetEnd(signature.getOffsetEnd());
            signatureHistory.setDigitalSignature(signature.getDigitalSignature());
            signatureHistory.setStatus(signature.getStatus());
            signatureHistory.setUpdatedAt(signature.getUpdatedAt());
            signatureHistory.setVersionCreatedAt(LocalDateTime.now());
            signatureHistoryRepository.save(signatureHistory);

            signature.setStatus("DELETED");
            signature.setUpdatedAt(LocalDateTime.now());
            signatureRepository.save(signature);

            SignatureAudit signatureAudit = new SignatureAudit();
            signatureAudit.setSignatureId(signature.getId());
            signatureAudit.setChangedBy("user_id");
            signatureAudit.setChangeType("DELETED");
            signatureAudit.setChangedAt(LocalDateTime.now());
            signatureAudit.setFieldsChanged("status");
            signatureAuditRepository.save(signatureAudit);
        } else {
            throw new IllegalArgumentException("Signature not found with ID: " + id);
        }
    }

    @Transactional
    public Signature updateSignature(
            UUID id,
            String threatName,
            String signatureData,
            String fileType,
            Integer offsetStart,
            Integer offsetEnd
    ) {
        Optional<Signature> oldSignatureOptional = signatureRepository.findById(id);
        if (oldSignatureOptional.isEmpty()) {
            throw new IllegalArgumentException("Signature not found");
        }

        Signature oldSignature = oldSignatureOptional.get();

        SignatureHistory history = new SignatureHistory();
        history.setSignatureId(oldSignature.getId());
        history.setThreatName(oldSignature.getThreatName());
        history.setFirstBytes(oldSignature.getFirstBytes());
        history.setRemainderHash(oldSignature.getRemainderHash());
        history.setRemainderLength(oldSignature.getRemainderLength());
        history.setFileType(oldSignature.getFileType());
        history.setOffsetStart(oldSignature.getOffsetStart());
        history.setOffsetEnd(oldSignature.getOffsetEnd());
        history.setDigitalSignature(oldSignature.getDigitalSignature());
        history.setStatus(oldSignature.getStatus());
        history.setUpdatedAt(oldSignature.getUpdatedAt());
        history.setVersionCreatedAt(LocalDateTime.now());
        signatureHistoryRepository.save(history);

        byte[] newFirstBytes = new byte[8];
        byte[] signatureDataBytes = signatureData.getBytes();
        System.arraycopy(signatureDataBytes, 0, newFirstBytes, 0, Math.min(8, signatureDataBytes.length));

        String newRemainderHash = generateRemainderHash(signatureData, offsetStart, offsetEnd);
        String newDigitalSignature = generateDigitalSignature(threatName, new String(newFirstBytes), newRemainderHash, signatureDataBytes.length - 8, fileType, offsetStart, offsetEnd);

        StringBuilder changedFields = new StringBuilder();
        if (!Objects.equals(oldSignature.getThreatName(), threatName)) changedFields.append("threat_name, ");
        if (!Objects.equals(oldSignature.getFirstBytes(), newFirstBytes)) changedFields.append("first_bytes, ");
        if (!Objects.equals(oldSignature.getFileType(), fileType)) changedFields.append("file_type, ");
        if (!Objects.equals(oldSignature.getOffsetStart(), offsetStart)) changedFields.append("offset_start, ");
        if (!Objects.equals(oldSignature.getOffsetEnd(), offsetEnd)) changedFields.append("offset_end, ");

        String fieldsChanged = !changedFields.isEmpty()
                ? changedFields.substring(0, changedFields.length() - 2)
                : "";

        oldSignature.setThreatName(threatName);
        oldSignature.setFirstBytes(newFirstBytes);
        oldSignature.setRemainderHash(newRemainderHash);
        oldSignature.setRemainderLength(signatureDataBytes.length - 8);
        oldSignature.setFileType(fileType);
        oldSignature.setOffsetStart(offsetStart);
        oldSignature.setOffsetEnd(offsetEnd);
        oldSignature.setUpdatedAt(LocalDateTime.now());
        oldSignature.setDigitalSignature(newDigitalSignature);

        SignatureAudit audit = new SignatureAudit();
        audit.setSignatureId(id);
        audit.setChangeType("UPDATED");
        audit.setChangedAt(LocalDateTime.now());
        audit.setFieldsChanged(fieldsChanged);
        signatureAuditRepository.save(audit);

        return signatureRepository.save(oldSignature);
    }

    public boolean verifySignature(Signature signature) {
        try {
            // Восстановим данные для проверки подписи, которые были использованы при её создании
            String dataToVerify = signature.getThreatName() +
                    new String(signature.getFirstBytes().getBytes(), StandardCharsets.UTF_8) +
                    signature.getRemainderHash() +
                    signature.getRemainderLength() +
                    signature.getFileType() +
                    signature.getOffsetStart() +
                    signature.getOffsetEnd();

            byte[] byteKey = Base64.getDecoder().decode(publicKeyBase64);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(byteKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            // Инициализация объекта для проверки подписи
            java.security.Signature signatureVerifier = java.security.Signature.getInstance("SHA256withRSA");
            signatureVerifier.initVerify(publicKey);

            // Добавляем данные для проверки подписи
            signatureVerifier.update(dataToVerify.getBytes(StandardCharsets.UTF_8));

            // Проверка подписи
            byte[] decodedSignature = Base64.getDecoder().decode(signature.getDigitalSignature());
            return signatureVerifier.verify(decodedSignature);  // Возвращает true, если подпись корректна
        } catch (Exception e) {
            // В случае ошибки (например, неверный ключ или подпись), возвращаем false
            System.out.println("Signature verification failed: " + e.getMessage());
            return false;
        }
    }



    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.DAYS) // Каждый день в полночь
    public void verifySignatures() {
        // Получаем список подписей, которые были обновлены после последней проверки
        List<Signature> signaturesToCheck = getAllActiveSignatures();

        for (Signature signature : signaturesToCheck) {

            // Проверка подписи
            boolean isValid = verifySignature(signature);

            if (!isValid) {
                // Если подпись неверна, помечаем её как поврежденную
                signature.setStatus("CORRUPTED");
                signatureRepository.save(signature);

                // Логируем ошибку
                System.out.println("Signature " + signature.getId() + " is corrupted!");
            }
        }

    }


    private String generateRemainderHash(String signatureData, int offsetStart, int offsetEnd) {
        try {
            if (offsetStart < 0 || offsetEnd <= offsetStart || offsetEnd > signatureData.length()) {
                throw new IllegalArgumentException("Invalid offsets");
            }

            byte[] signatureBytes = signatureData.getBytes();
            byte[] firstBytes = Arrays.copyOfRange(signatureBytes, 0, 8);
            byte[] remainderBytes = Arrays.copyOfRange(signatureBytes, offsetStart, offsetEnd);

            byte[] combinedData = new byte[firstBytes.length + remainderBytes.length];
            System.arraycopy(firstBytes, 0, combinedData, 0, firstBytes.length);
            System.arraycopy(remainderBytes, 0, combinedData, firstBytes.length, remainderBytes.length);

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(combinedData);

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating hash", e);
        }
    }

}
