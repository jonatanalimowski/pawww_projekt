package com.example.projekt.service;

import com.example.projekt.model.Information;
import com.example.projekt.model.User;
import com.example.projekt.repository.InformationRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


@Service
public class InformationService {

    private final InformationRepository informationRepository;

    public InformationService(InformationRepository informationRepository) {
        this.informationRepository = informationRepository;
    }

    public List<Information> getInformationsForUser(User user, String sortBy, String sortDir) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Sort sort = Sort.by(direction, sortBy);
        return informationRepository.findByOwner(user, sort);
    }

    public void createInformation(String title, String content, User user) {
        Information information = new Information();
        information.setTitle(title);
        information.setContent(content);
        information.setOwner(user);
        information.setShareToken(UUID.randomUUID().toString());
        informationRepository.save(information);
    }

    public Information getInformationById(Long id, User user) {
        Information information = informationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono notatki"));
        if (!information.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("Brak dostępu");
        }
        return information;
    }

    public void updateInformation(Long id, String title, String content, User user) {
        Information information = getInformationById(id, user);
        information.setTitle(title);
        information.setContent(content);
        informationRepository.save(information);
    }

    public void deleteInformation(Long id, User user) {
        Information information = getInformationById(id, user);
        informationRepository.delete(information);
    }

    public Information getInformationByToken(String token) {
        return informationRepository.findByShareToken(token)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono notatki"));
    }
}
