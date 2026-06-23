package org.example;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface PercentageRepository extends JpaRepository<Percentage, LocalDateTime> {

    // Die neueste Stunde (= aktuelle Stunde).
    Percentage findTopByOrderByHourDesc();
}
