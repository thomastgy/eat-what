package com.backend.restservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.restservice.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

}