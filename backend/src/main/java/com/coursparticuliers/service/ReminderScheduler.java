package com.coursparticuliers.service;

import com.coursparticuliers.entity.Seance;
import com.coursparticuliers.entity.enums.StatutSeance;
import com.coursparticuliers.repository.SeanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReminderScheduler {

    private final SeanceRepository seanceRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 8 * * *")
    public void sendSeanceReminders() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Seance> seances = seanceRepository.findByStatutAndDate(StatutSeance.PLANIFIEE, tomorrow);

        for (Seance seance : seances) {
            emailService.sendSeanceReminder(seance);
            notificationService.create(
                    seance.getReservation().getEleve().getUtilisateur().getId(),
                    "Rappel: séance demain à " + seance.getHeureDebut()
            );
        }
        log.info("{} rappels de séance envoyés", seances.size());
    }
}
