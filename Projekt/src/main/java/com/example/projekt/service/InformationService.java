package com.example.projekt.service;

import com.example.projekt.model.Category;
import com.example.projekt.model.Information;
import com.example.projekt.model.User;
import com.example.projekt.model.SharedInformation;
import com.example.projekt.repository.InformationRepository;
import com.example.projekt.repository.SharedInformationRepository;
import com.example.projekt.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class InformationService {

    private final InformationRepository informationRepository;
    private final SharedInformationRepository sharedInformationRepository;
    private final UserRepository userRepository;

    public InformationService(InformationRepository informationRepository, 
                              SharedInformationRepository sharedInformationRepository,
                              UserRepository userRepository) {
        this.informationRepository = informationRepository;
        this.sharedInformationRepository = sharedInformationRepository;
        this.userRepository = userRepository;
    }

    public List<Information> getSharedWithUser(User user) {
        return sharedInformationRepository.findByRecipient(user).stream()
                .map(SharedInformation::getInformation)
                .collect(Collectors.toList());
    }

    public void shareWithUser(Long informationId, String username, User owner) {
        Information info = getInformationById(informationId, owner);
        User recipient = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie istnieje"));
        
        if (info.getOwner().getId().equals(recipient.getId())) {
            throw new RuntimeException("Nie możesz udostępnić notatki samemu sobie");
        }

        if (!sharedInformationRepository.existsByInformationIdAndRecipientId(informationId, recipient.getId())) {
            SharedInformation shared = new SharedInformation();
            shared.setInformation(info);
            shared.setRecipient(recipient);
            sharedInformationRepository.save(shared);
        }
    }

    public List<Information> getInformationsForUser(User user, String sortBy, String sortDir, Long categoryId, LocalDate date) {
        // obsluga sortowania po popularnosci
        if ("categoryPopularity".equals(sortBy)) {
            boolean isDesc = sortDir == null || sortDir.equalsIgnoreCase("desc");

            List<Information> infos;
            if (categoryId != null && date != null) {
                infos = informationRepository.findByOwnerAndCategoryIdAndAddedDate(user, categoryId, date);
            } else if (categoryId != null) {
                infos = informationRepository.findByOwnerAndCategoryId(user, categoryId);
            } else if (date != null) {
                infos = informationRepository.findByOwnerAndAddedDate(user, date);
            } else {
                infos = informationRepository.findByOwner(user);
            }

            return infos.stream()
                    .sorted((info1, info2) -> {
                        long count1 = info1.getCategory() != null
                                ? informationRepository.countByCategoryId(info1.getCategory().getId()) : 0;
                        long count2 = info2.getCategory() != null
                                ? informationRepository.countByCategoryId(info2.getCategory().getId()) : 0;

                        if (isDesc) {
                            return Long.compare(count2, count1); // Od największej popularności do najmniejszej
                        } else {
                            return Long.compare(count1, count2); // Od najmniejszej popularności do największej
                        }
                    })
                    .collect(Collectors.toList());
        }

        Sort.Direction direction = sortDir != null && sortDir.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        String sortProperty = (sortBy != null && !sortBy.isEmpty()) ? sortBy : "addedDate";
        Sort sort = Sort.by(direction, sortProperty);

        if (categoryId != null && date != null) {
            return informationRepository.findByOwnerAndCategoryIdAndAddedDate(user, categoryId, date, sort);
        }
        if (categoryId != null) {
            return informationRepository.findByOwnerAndCategoryId(user, categoryId, sort); //
        }
        if (date != null) {
            return informationRepository.findByOwnerAndAddedDate(user, date, sort); //
        }

        return informationRepository.findByOwner(user, sort); //
    }

    public void createInformation(String title, String content, User user, Category category) {
        Information information = new Information();
        information.setTitle(title);
        information.setContent(content);
        information.setOwner(user);
        information.setCategory(category);
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

    public void updateInformation(Long id, String title, String content, User user, Category category) {
        Information information = getInformationById(id, user);
        information.setTitle(title);
        information.setContent(content);
        information.setCategory(category);
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
