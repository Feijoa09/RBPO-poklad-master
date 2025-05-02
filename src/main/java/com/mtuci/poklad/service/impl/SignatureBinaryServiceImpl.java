package com.mtuci.poklad.service.impl;


import com.mtuci.poklad.models.Signature;
import com.mtuci.poklad.service.SignatureBinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor


public class SignatureBinaryServiceImpl implements SignatureBinaryService {
    private final SignatureServiceImpl signatureService;

    @Override
    public LinkedMultiValueMap<String, Object> getAllActiveSignatures() {
        List<Signature> signatures = signatureService.getAllActiveSignatures();
        return serializeSignaturesWithMetaForRequestGetAllSignatures(signatures);
    }

    @Override
    public LinkedMultiValueMap<String, Object> getSignaturesUpdatedAfter(LocalDateTime time) {
        List<Signature> signatures = signatureService.getAllActiveSignatures();
        return serializeSignaturesWithMetaForRequestGetSignaturesUpdatedAfter(signatures, time);

    }

    @Override
    public LinkedMultiValueMap<String, Object> getSignaturesByIds(List<UUID> ids) {
        List<Signature> signatures = signatureService.getSignaturesByIds(ids);
        return serializeSignaturesWithMetaForRequestGetAllSignatures(signatures);
    }


    @Value("${signature.resource}")
    private Resource signatureResource;

    @Value("${signature.password}")
    private String signaturePassword;

    public byte[] generateManifestSignature(int count, byte[] data) {
        try {
            KeyStore keyStore = KeyStore.getInstance("jks");
            InputStream is = signatureResource.getInputStream();

            keyStore.load(is, signaturePassword.toCharArray());

            Key key = keyStore.getKey("signature", signaturePassword.toCharArray());
            PrivateKey privateKey = (PrivateKey) key;

            // Формируем данные для подписи
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(ByteBuffer.allocate(4).putInt(count).array());
            outputStream.write(data);
            byte[] dataToSign = outputStream.toByteArray();

            java.security.Signature signature = java.security.Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(dataToSign);

            return signature.sign();
        } catch (Exception e) {
            throw new RuntimeException("Error generating digital signature", e);
        }
    }


    private byte[] buildManifest(int count, byte[] data, byte[] digitalSignature) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // 1. Записываем количество сигнатур (4 байта, big-endian)
            outputStream.write(ByteBuffer.allocate(Integer.BYTES).putInt(count).array());

            // 2. Записываем байты сигнатур
            outputStream.write(data);

            // 3. Записываем байты ЭЦП
            outputStream.write(digitalSignature);

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сборке байтовой последовательности", e);
        }
    }


    private byte[] serializeSignatureFields(Signature signature) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // UUID -> 16 байт
            outputStream.write(ByteBuffer.wrap(signature.getId().toString().getBytes()).array());

            // threat_name -> строка в UTF-8
            if (signature.getThreatName() != null) {
                byte[] threatNameBytes = signature.getThreatName().getBytes(StandardCharsets.UTF_8);
                outputStream.write(ByteBuffer.allocate(4).putInt(threatNameBytes.length).array());
                outputStream.write(threatNameBytes);
            } else {
                outputStream.write(ByteBuffer.allocate(4).putInt(0).array());
            }

            // first_bytes -> может быть null
            byte[] firstBytes = signature.getFirstBytes().getBytes();
            if (firstBytes != null) {
                outputStream.write(ByteBuffer.allocate(4).putInt(firstBytes.length).array());
                outputStream.write(firstBytes);
            } else {
                outputStream.write(ByteBuffer.allocate(4).putInt(0).array());
            }

            // remainder_hash -> строка
            if (signature.getRemainderHash() != null) {
                byte[] remainderHashBytes = signature.getRemainderHash().getBytes(StandardCharsets.UTF_8);
                outputStream.write(ByteBuffer.allocate(4).putInt(remainderHashBytes.length).array());
                outputStream.write(remainderHashBytes);
            } else {
                outputStream.write(ByteBuffer.allocate(4).putInt(0).array());
            }

            // remainder_length -> int
            outputStream.write(ByteBuffer.allocate(4).putInt(signature.getRemainderLength()).array());

            // file_type -> строка
            if (signature.getFileType() != null) {
                byte[] fileTypeBytes = signature.getFileType().getBytes(StandardCharsets.UTF_8);
                outputStream.write(ByteBuffer.allocate(4).putInt(fileTypeBytes.length).array());
                outputStream.write(fileTypeBytes);
            } else {
                outputStream.write(ByteBuffer.allocate(4).putInt(0).array());
            }

            // offset_start -> int
            outputStream.write(ByteBuffer.allocate(4).putInt(signature.getOffsetStart()).array());

            // offset_end -> int
            outputStream.write(ByteBuffer.allocate(4).putInt(signature.getOffsetEnd()).array());

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка сериализации сигнатуры", e);
        }
    }

    private HttpHeaders createHeaders(String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return headers;
    }


    public LinkedMultiValueMap<String, Object> serializeSignaturesWithMetaForRequestGetAllSignatures(List<Signature> signatures) {
        try (ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
             ByteArrayOutputStream metaStream = new ByteArrayOutputStream()) {

            for (Signature signature : signatures) {
                // 1. Сериализуем поля сигнатуры (без digital_signature, updated_at, status)
                byte[] serialized = serializeSignatureFields(signature);
                dataStream.write(serialized);

                // 2. Формируем строку GUID:DigitalSignature
                UUID id = signature.getId();
                byte[] digitalSig = signature.getDigitalSignature().getBytes();
                if (digitalSig == null) digitalSig = new byte[0];

                metaStream.write(ByteBuffer.wrap(id.toString().getBytes()).array());
                metaStream.write(":".getBytes());
                metaStream.write(signature.getDigitalSignature().getBytes());
            }

            byte[] data = dataStream.toByteArray();

            dataStream.reset();

            byte[] countSignature = ByteBuffer.allocate(4).putInt(signatures.size()).array();
            byte[] massiveSignature = metaStream.toByteArray();
            byte[] manifestSignature = generateManifestSignature(signatures.size(), massiveSignature);

            dataStream.write(countSignature);
            dataStream.write(massiveSignature);
            dataStream.write(manifestSignature);

            byte[] manifest = dataStream.toByteArray();

            ByteArrayResource manifestRes = new ByteArrayResource(manifest) {
                @Override
                public String getFilename() {
                    return "manifest.bin";
                }
            };

            ByteArrayResource dataRes = new ByteArrayResource(data) {
                @Override
                public String getFilename() {
                    return "data.bin";
                }
            };

            LinkedMultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
            parts.add("manifest", new HttpEntity<>(manifestRes, createHeaders("manifest.bin")));
            parts.add("data", new HttpEntity<>(dataRes, createHeaders("data.bin")));

            return parts;

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сериализации сигнатур", e);
        }
    }


    public LinkedMultiValueMap<String, Object> serializeSignaturesWithMetaForRequestGetSignaturesUpdatedAfter(
            List<Signature> signatures, LocalDateTime time) {
        try (ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
             ByteArrayOutputStream metaStream = new ByteArrayOutputStream()) {

            List<Signature> filtered = signatures.stream()
                    .filter(sig -> sig.getUpdatedAt() != null && sig.getUpdatedAt().isAfter(time))
                    .toList();

            for (Signature signature : filtered) {
                // 1. Сериализуем поля сигнатуры (без digital_signature, updated_at, status)
                byte[] serialized = serializeSignatureFields(signature);
                dataStream.write(serialized);

                // 2. Формируем строку GUID:DigitalSignature
                UUID id = signature.getId();
                byte[] digitalSig = signature.getDigitalSignature().getBytes();
                if (digitalSig == null) digitalSig = new byte[0];

                metaStream.write(id.toString().getBytes());
                metaStream.write(":".getBytes());
                metaStream.write(digitalSig);
            }

            byte[] data = dataStream.toByteArray();
            dataStream.reset();

            byte[] countSignature = ByteBuffer.allocate(4).putInt(filtered.size()).array();
            byte[] massiveSignature = metaStream.toByteArray();
            byte[] manifestSignature = generateManifestSignature(filtered.size(), massiveSignature);

            dataStream.write(countSignature);
            dataStream.write(massiveSignature);
            dataStream.write(manifestSignature);

            byte[] manifest = dataStream.toByteArray();

            ByteArrayResource manifestRes = new ByteArrayResource(manifest) {
                @Override
                public String getFilename() {
                    return "manifest.bin";
                }
            };

            ByteArrayResource dataRes = new ByteArrayResource(data) {
                @Override
                public String getFilename() {
                    return "data.bin";
                }
            };

            LinkedMultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
            parts.add("manifest", new HttpEntity<>(manifestRes, createHeaders("manifest.bin")));
            parts.add("data", new HttpEntity<>(dataRes, createHeaders("data.bin")));

            return parts;

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сериализации обновлённых сигнатур", e);
        }
    }

}