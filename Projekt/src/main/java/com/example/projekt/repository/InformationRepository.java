package com.example.projekt.repository;

import com.example.projekt.model.Information;
import com.example.projekt.model.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InformationRepository extends JpaRepository<Information, Long> {
    List<Information> findByOwner(User owner, Sort sort);
    Optional<Information> findByShareToken(String shareToken);
}

