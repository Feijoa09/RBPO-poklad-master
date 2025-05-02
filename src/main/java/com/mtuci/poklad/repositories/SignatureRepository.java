package com.mtuci.poklad.repositories;

import com.mtuci.poklad.models.Signature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SignatureRepository extends JpaRepository<Signature, UUID> {
    List<Signature> findAllByStatus(String status);
    List<Signature> findAllByIdInAndStatus(List<UUID> ids, String status);
    List<Signature> findByUpdatedAtAfter(LocalDateTime time);
}
