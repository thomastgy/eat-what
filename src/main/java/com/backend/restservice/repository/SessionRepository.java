package com.backend.restservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.restservice.entity.Session;

public interface SessionRepository extends JpaRepository<Session, Long> {

}