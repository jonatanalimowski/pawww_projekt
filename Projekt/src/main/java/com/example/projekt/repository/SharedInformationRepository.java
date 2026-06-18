package com.example.projekt.repository;

import com.example.projekt.model.SharedInformation;
import com.example.projekt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SharedInformationRepository extends JpaRepository<SharedInformation, Long> {
    List<SharedInformation> findByRecipient(User recipient);
    boolean existsByInformationIdAndRecipientId(Long informationId, Long recipientId);
}
