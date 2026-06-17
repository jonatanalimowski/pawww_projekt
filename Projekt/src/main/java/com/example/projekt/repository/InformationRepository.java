package com.example.projekt.repository;

import com.example.projekt.model.Information;
import com.example.projekt.model.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InformationRepository extends JpaRepository<Information, Long> {
    List<Information> findByOwner(User owner, Sort sort);
    List<Information> findByOwnerAndCategoryId(User owner, Long categoryId, Sort sort);
    List<Information> findByOwnerAndAddedDate(User owner, LocalDate addedDate, Sort sort);
    Optional<Information> findByShareToken(String shareToken);
}

