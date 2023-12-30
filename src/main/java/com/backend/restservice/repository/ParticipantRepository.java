package com.backend.restservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.restservice.entity.Participant;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    Participant findByUsername(String username);
}