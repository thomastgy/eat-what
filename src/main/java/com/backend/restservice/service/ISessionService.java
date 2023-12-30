package com.backend.restservice.service;

import com.backend.restservice.dto.RestaurantDto;
import com.backend.restservice.dto.SessionDto;
import com.backend.restservice.helper.ApiResponse;

public interface ISessionService {
    ApiResponse<?> createSession(SessionDto newSessionDto, String creator);

    ApiResponse<?> getSessionById(Long id, String currUser);

    ApiResponse<?> joinSession(Long id, String currUser);

    ApiResponse<?> addRestaurantToSession(RestaurantDto newRestaurantDto, Long id, String currUser);

    ApiResponse<?> endSession(Long id, String currUser);
}
