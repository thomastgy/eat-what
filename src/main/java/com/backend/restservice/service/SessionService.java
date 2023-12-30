package com.backend.restservice.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.backend.restservice.dto.RestaurantDto;
import com.backend.restservice.dto.SessionDto;
import com.backend.restservice.entity.Participant;
import com.backend.restservice.entity.Restaurant;
import com.backend.restservice.entity.Session;
import com.backend.restservice.exception.SessionNotFoundException;
import com.backend.restservice.helper.ApiResponse;
import com.backend.restservice.repository.ParticipantRepository;
import com.backend.restservice.repository.SessionRepository;

import static com.backend.restservice.helper.SessionHelper.isRestaurantInList;
import static com.backend.restservice.helper.SessionHelper.isUserInList;
import static com.backend.restservice.helper.SessionHelper.sanitizeInput;
import static com.backend.restservice.helper.SessionHelper.selectRestaurant;

import static com.backend.restservice.constant.Constants.UNAUTHORIZED_MSG;
import static com.backend.restservice.constant.Constants.JOINED_SESSION_MSG;
import static com.backend.restservice.constant.Constants.RESTAURANT_EXIST_MSG;
import static com.backend.restservice.constant.Constants.SESSION_ENDED_MSG;

@Service
public class SessionService implements ISessionService {
    private final SessionRepository repository;
    private final ParticipantRepository participantRepository;
    private static final Logger logger = LoggerFactory.getLogger(SessionService.class);

    @Autowired
    CacheManager cacheManager;

    SessionService(SessionRepository repository, ParticipantRepository participantRepository) {
        this.repository = repository;
        this.participantRepository = participantRepository;
    }

    @Override
    public ApiResponse<?> createSession(SessionDto newSessionDto, String currUser) {
        logger.info("Creating session for user: {}", currUser);

        Session newSession = new Session();
        newSession.setName(sanitizeInput(newSessionDto.getName()));
        newSession.setIsActive(true);
        newSession.setCreator(currUser);
        Participant existingParticipant = participantRepository.findByUsername(currUser);
        newSession.getParticipant().add(existingParticipant != null ? existingParticipant : new Participant(currUser));
        Session createdSession = repository.save(newSession);

        logger.info("Session created with ID: {}", createdSession.getId());
        return new ApiResponse<>(createdSession);
    }

    @Override
    public ApiResponse<?> getSessionById(Long id, String currUser) {
        logger.info("Retrieving session with ID: {} for user: {}", id, currUser);

        Cache sessionCache = cacheManager.getCache("sessionResults");
        if (sessionCache != null && sessionCache.get(id) != null) {
            logger.info("Session data retrieved from cache for ID: {}", id);
            return new ApiResponse<>(sessionCache.get(id).get().toString());
        }

        Session session = repository.findById(id).orElseThrow(() -> new SessionNotFoundException(id));

        if (!isUserInList(currUser, session.getParticipant())) {
            logger.warn("Unauthorized access attempt by user: {} for session: {}", currUser, id);
            return new ApiResponse<>(false, UNAUTHORIZED_MSG);
        }

        if (!session.getIsActive()) {
            logger.info("Session is inactive, caching result for session ID: {}", id);
            sessionCache.put(id, session.getResult());
            return new ApiResponse<>(session.getResult().getName());
        }

        Map<String, Object> returnObj = new HashMap<>();
        returnObj.put("name", session.getName());
        returnObj.put("creator", session.getCreator());
        returnObj.put("participant", session.getParticipant());
        returnObj.put("restaurant", session.getRestaurant());

        logger.info("Active session retrieved for ID: {}", id);
        return new ApiResponse<>(true, returnObj);
    }

    @Override
    public ApiResponse<?> joinSession(Long id, String currUser) {
        logger.info("User: {} attempting to join session with ID: {}", currUser, id);

        Session session = repository.findById(id).orElseThrow(() -> new SessionNotFoundException(id));

        if (isUserInList(currUser, session.getParticipant())) {
            logger.warn("User: {} has already joined session ID: {}", currUser, id);
            return new ApiResponse<>(false, JOINED_SESSION_MSG);
        }

        if (!session.getIsActive()) {
            logger.info("User: {} attempted to join inactive session ID: {}", currUser, id);
            return new ApiResponse<>(false, SESSION_ENDED_MSG);
        }

        Participant existingParticipant = participantRepository.findByUsername(currUser);
        session.getParticipant().add(existingParticipant != null ? existingParticipant : new Participant(currUser));
        repository.save(session);
        logger.info("User: {} joined session ID: {}", currUser, id);

        return new ApiResponse<>(true, currUser + " successfully joined session " + session.getName());
    }

    @Override
    public ApiResponse<?> addRestaurantToSession(RestaurantDto newRestaurantDto, Long id, String currUser) {
        logger.info("User: {} adding restaurant to session ID: {}", currUser, id);

        Session session = repository.findById(id).orElseThrow(() -> new SessionNotFoundException(id));

        if (!isUserInList(currUser, session.getParticipant())) {
            logger.warn("Unauthorized attempt by user: {} to add restaurant to session ID: {}", currUser, id);
            return new ApiResponse<>(false, UNAUTHORIZED_MSG);
        }

        if (!session.getIsActive()) {
            logger.info("Attempt to add restaurant to inactive session ID: {}", id);
            return new ApiResponse<>(false, SESSION_ENDED_MSG);
        }

        Restaurant newRestaurant = new Restaurant();
        newRestaurant.setName(sanitizeInput(newRestaurantDto.getName()));

        if (isRestaurantInList(newRestaurant, session.getRestaurant())) {
            logger.info("Restaurant already exists in session ID: {}", id);
            return new ApiResponse<>(false, RESTAURANT_EXIST_MSG);
        }

        session.getRestaurant().add(newRestaurant);
        repository.save(session);
        logger.info("Restaurant: {} added to session ID: {}", newRestaurant.getName(), id);

        return new ApiResponse<>(true, newRestaurant.getName() + " successfully added to session " + session.getName());
    }

    @Override
    public ApiResponse<?> endSession(Long id, String currUser) {
        logger.info("User: {} ending session ID: {}", currUser, id);

        Session session = repository.findById(id).orElseThrow(() -> new SessionNotFoundException(id));

        if (!session.getCreator().equals(currUser)) {
            logger.warn("Unauthorized attempt by user: {} to end session ID: {}", currUser, id);
            return new ApiResponse<>(false, UNAUTHORIZED_MSG);
        }

        if (!session.getIsActive()) {
            logger.info("Attempt to end already inactive session ID: {}", id);
            return new ApiResponse<>(false, SESSION_ENDED_MSG);
        }

        session.setIsActive(false);
        List<Restaurant> restaurantList = session.getRestaurant();
        Restaurant result = selectRestaurant(restaurantList);
        session.setResult(result);
        repository.save(session);
        logger.info("Session ID: {} ended by user: {}", id, currUser);

        return new ApiResponse<>(true, "Session ended. Result: " + result.getName());
    }
}
