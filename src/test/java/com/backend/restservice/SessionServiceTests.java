package com.backend.restservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import com.backend.restservice.dto.RestaurantDto;
import com.backend.restservice.dto.SessionDto;
import com.backend.restservice.entity.Participant;
import com.backend.restservice.entity.Restaurant;
import com.backend.restservice.entity.Session;
import com.backend.restservice.exception.SessionNotFoundException;
import com.backend.restservice.helper.ApiResponse;
import com.backend.restservice.repository.ParticipantRepository;
import com.backend.restservice.repository.SessionRepository;
import com.backend.restservice.service.SessionService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static com.backend.restservice.constant.Constants.JOINED_SESSION_MSG;
import static com.backend.restservice.constant.Constants.RESTAURANT_EXIST_MSG;
import static com.backend.restservice.constant.Constants.UNAUTHORIZED_MSG;
import static org.junit.jupiter.api.Assertions.*;

class SessionServiceTests {

    final private static Long VALID_SESSION_ID = 1L;
    final private static String CURR_USER = "user1";

    private SessionRepository sessionRepository;
    private ParticipantRepository participantRepository;
    private CacheManager cacheManager;
    private Cache cache;
    private SessionService sessionService;

    @BeforeEach
    public void setUp() {
        sessionRepository = mock(SessionRepository.class);
        participantRepository = mock(ParticipantRepository.class);
        cacheManager = mock(CacheManager.class);
        cache = mock(Cache.class);

        when(cacheManager.getCache(anyString())).thenReturn(cache);

        sessionService = new SessionService(sessionRepository, participantRepository, cacheManager);
    }

    @Test
    void whenCreateSessionWithValidInput_thenSessionCreated() {
        SessionDto newSessionDto = new SessionDto();
        newSessionDto.setName("Test Session");
        Session createdSession = new Session();
        when(sessionRepository.save(any(Session.class))).thenReturn(createdSession);

        ApiResponse<?> response = sessionService.createSession(newSessionDto, CURR_USER);

        assertTrue(response.isSuccess());
        assertNotNull(response.getResult());
    }

    @Test
    void whenCreateSessionWithNullDto_thenHandleGracefully() {
        assertThrows(NullPointerException.class, () -> {
            sessionService.createSession(null, CURR_USER);
        });
    }

    @Test
    void whenGetSessionByIdWithValidId_thenReturnSession() {
        Session existingSession = new Session();
        existingSession.getParticipant().add(new Participant(CURR_USER));
        existingSession.setIsActive(true);
        when(sessionRepository.findById(VALID_SESSION_ID)).thenReturn(Optional.of(existingSession));

        ApiResponse<?> response = sessionService.getSessionById(VALID_SESSION_ID, CURR_USER);

        assertTrue(response.isSuccess());
    }

    @Test
    void whenGetSessionByIdWithNonExistentId_thenThrowException() {
        Long invalidId = 99L;

        assertThrows(SessionNotFoundException.class, () -> {
            sessionService.getSessionById(invalidId, CURR_USER);
        });
    }

    @Test
    void whenJoinSessionWithValidId_thenParticipantAdded() {
        Session session = new Session();
        session.setIsActive(true);
        when(sessionRepository.findById(VALID_SESSION_ID)).thenReturn(Optional.of(session));
        when(participantRepository.findByUsername(CURR_USER)).thenReturn(null);

        ApiResponse<?> response = sessionService.joinSession(VALID_SESSION_ID, CURR_USER);

        assertTrue(response.isSuccess());
    }

    @Test
    void whenJoinSessionAlreadyJoined_thenHandleGracefully() {
        Session session = new Session();
        when(sessionRepository.findById(VALID_SESSION_ID)).thenReturn(Optional.of(session));
        session.getParticipant().add(new Participant(CURR_USER));

        ApiResponse<?> response = sessionService.joinSession(VALID_SESSION_ID, CURR_USER);

        assertFalse(response.isSuccess());
        assertEquals(JOINED_SESSION_MSG, response.getResult());
    }

    @Test
    void whenAddRestaurantToSessionWithValidData_thenRestaurantAdded() {
        RestaurantDto newRestaurantDto = new RestaurantDto("Some Restaurant");
        Session session = new Session();
        session.setIsActive(true);
        session.getParticipant().add(new Participant(CURR_USER));
        when(sessionRepository.findById(VALID_SESSION_ID)).thenReturn(Optional.of(session));

        ApiResponse<?> response = sessionService.addRestaurantToSession(newRestaurantDto, VALID_SESSION_ID, CURR_USER);

        assertTrue(response.isSuccess());
    }

    @Test
    void whenAddExistingRestaurantToSession_thenHandleGracefully() {
        String existingRestaurantName = "Existing Restaurant";
        RestaurantDto existingRestaurantDto = new RestaurantDto(existingRestaurantName);
        Session session = new Session();
        session.setIsActive(true);
        session.getParticipant().add(new Participant(CURR_USER));
        session.getRestaurant().add(new Restaurant(existingRestaurantName));
        when(sessionRepository.findById(VALID_SESSION_ID)).thenReturn(Optional.of(session));

        ApiResponse<?> response = sessionService.addRestaurantToSession(existingRestaurantDto, VALID_SESSION_ID,
                CURR_USER);

        assertFalse(response.isSuccess());
        assertEquals(RESTAURANT_EXIST_MSG, response.getResult());
    }

    @Test
    void whenEndSessionWithValidIdAndUserIsCreator_thenSessionEnded() {
        Session session = new Session("session1");
        session.setCreator(CURR_USER);
        session.getRestaurant().add(new Restaurant("McDonalds"));
        when(sessionRepository.findById(VALID_SESSION_ID)).thenReturn(Optional.of(session));

        ApiResponse<?> response = sessionService.endSession(VALID_SESSION_ID, CURR_USER);

        assertTrue(response.isSuccess());
    }

    @Test
    void whenEndSessionUnauthorised_thenHandleGracefully() {
        String notCreatorUser = "user2";
        Session session = new Session();
        session.setCreator(CURR_USER);
        when(sessionRepository.findById(VALID_SESSION_ID)).thenReturn(Optional.of(session));

        ApiResponse<?> response = sessionService.endSession(VALID_SESSION_ID, notCreatorUser);

        assertFalse(response.isSuccess());
        assertEquals(UNAUTHORIZED_MSG, response.getResult());
    }
}
