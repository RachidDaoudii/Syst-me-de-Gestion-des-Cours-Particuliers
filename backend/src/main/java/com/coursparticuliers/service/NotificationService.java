package com.coursparticuliers.service;

import com.coursparticuliers.dto.response.NotificationResponse;
import com.coursparticuliers.dto.response.PageResponse;
import com.coursparticuliers.entity.Notification;
import com.coursparticuliers.entity.Utilisateur;
import com.coursparticuliers.exception.ResourceNotFoundException;
import com.coursparticuliers.mapper.EntityMapper;
import com.coursparticuliers.repository.NotificationRepository;
import com.coursparticuliers.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UtilisateurRepository utilisateurRepository;

    public PageResponse<NotificationResponse> getByUtilisateur(Long utilisateurId, Pageable pageable) {
        Page<NotificationResponse> page = notificationRepository
                .findByUtilisateurIdOrderByDateDesc(utilisateurId, pageable)
                .map(EntityMapper::toNotificationResponse);
        return PageResponse.from(page);
    }

    @Transactional
    public NotificationResponse create(Long utilisateurId, String message) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        Notification notification = Notification.builder()
                .utilisateur(utilisateur)
                .message(message)
                .build();

        return EntityMapper.toNotificationResponse(notificationRepository.save(notification));
    }

    @Transactional
    public NotificationResponse markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification non trouvée"));
        notification.setLu(true);
        return EntityMapper.toNotificationResponse(notificationRepository.save(notification));
    }

    public long countUnread(Long utilisateurId) {
        return notificationRepository.countByUtilisateurIdAndLuFalse(utilisateurId);
    }
}
