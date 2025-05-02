package com.mtuci.poklad.repositories;

import com.mtuci.poklad.models.SignatureHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SignatureHistoryRepository extends JpaRepository<SignatureHistory, UUID> {
    List<SignatureHistory> findBySignatureId(UUID signatureId);
}
