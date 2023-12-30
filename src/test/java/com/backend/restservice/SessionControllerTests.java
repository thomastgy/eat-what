package com.backend.restservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import static com.backend.restservice.constant.Constants.INVALID_REQUEST_BODY;
import static com.backend.restservice.constant.Constants.JOINED_SESSION_MSG;
import static com.backend.restservice.constant.Constants.UNAUTHORIZED_MSG;
import static org.hamcrest.Matchers.containsString;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import com.backend.restservice.dto.RestaurantDto;
import com.backend.restservice.dto.SessionDto;
import com.backend.restservice.entity.Participant;
import com.backend.restservice.entity.Restaurant;
import com.backend.restservice.entity.Session;
import com.backend.restservice.exception.SessionNotFoundException;
import com.backend.restservice.helper.ApiResponse;
import com.backend.restservice.service.SessionService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class SessionControllerTests {

	private static String BASE_URL = "/api/sessions";
	private static String JOHN_USERNAME = "john";
	private static String SESSION_NAME = "Welcome Lunch for Sarah";
	private Session EXPECTED_RESPONSE_SESSION = new Session(SESSION_NAME, JOHN_USERNAME, true,
			new ArrayList<Participant>(),
			new ArrayList<Restaurant>());

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private SessionService sessionService;

	@Test
	public void whenPostSession_thenCreateSession() throws Exception {
		SessionDto newSessionDto = new SessionDto();
		newSessionDto.setName(SESSION_NAME);

		ApiResponse<?> expectedResponse = new ApiResponse<>(true, EXPECTED_RESPONSE_SESSION);

		doReturn(expectedResponse).when(sessionService).createSession(any(SessionDto.class), anyString());

		mockMvc.perform(post(BASE_URL)
				.with(SecurityMockMvcRequestPostProcessors.user(JOHN_USERNAME))
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(newSessionDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.result.name").value(SESSION_NAME))
				.andExpect(jsonPath("$.result.creator").value(JOHN_USERNAME))
				.andExpect(jsonPath("$.result.isActive").value(true));
	}

	@Test
	public void whenPostSession_withInvalidData_thenError() throws Exception {

		mockMvc.perform(post("/api/sessions")
				.with(SecurityMockMvcRequestPostProcessors.user(JOHN_USERNAME))
				.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value(INVALID_REQUEST_BODY));
	}

	@Test
	public void whenGetSessionById_thenReturnActiveSession() throws Exception {
		Long validId = 1L;
		ApiResponse<?> expectedResponse = new ApiResponse<>(true, EXPECTED_RESPONSE_SESSION);

		doReturn(expectedResponse).when(sessionService).getSessionById(validId, JOHN_USERNAME);

		mockMvc.perform(get(BASE_URL + "/" + validId)
				.with(SecurityMockMvcRequestPostProcessors.user(JOHN_USERNAME)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.result.name").value(SESSION_NAME))
				.andExpect(jsonPath("$.result.creator").value(JOHN_USERNAME))
				.andExpect(jsonPath("$.result.participant").exists())
				.andExpect(jsonPath("$.result.restaurant").exists());
	}

	@Test
	public void whenGetSessionById_thenReturnInactiveSession() throws Exception {
		Long validId = 1L;

		ApiResponse<?> expectedResponse = new ApiResponse<>("sample restaurant");

		doReturn(expectedResponse).when(sessionService).getSessionById(validId, JOHN_USERNAME);

		mockMvc.perform(get(BASE_URL + "/" + validId)
				.with(SecurityMockMvcRequestPostProcessors.user(JOHN_USERNAME)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.result").value("sample restaurant"));
	}

	@Test
	public void whenGetSessionByInvalidId_thenNotFound() throws Exception {
		Long invalidId = 99L;

		doThrow(new SessionNotFoundException(invalidId)).when(sessionService).getSessionById(invalidId, JOHN_USERNAME);

		mockMvc.perform(get(BASE_URL + "/" + invalidId)
				.with(SecurityMockMvcRequestPostProcessors.user(JOHN_USERNAME)))
				.andExpect(status().isNotFound());
	}

	@Test
	public void whenGetSessionByIdByNonParticipant_thenReturnError() throws Exception {
		Long validId = 1L;
		ApiResponse<?> expectedResponse = new ApiResponse<>(true, UNAUTHORIZED_MSG);

		doReturn(expectedResponse).when(sessionService).getSessionById(validId, "HACKERMAN");

		mockMvc.perform(get(BASE_URL + "/" + validId)
				.with(SecurityMockMvcRequestPostProcessors.user("HACKERMAN")))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(UNAUTHORIZED_MSG)));
	}

	@Test
	public void whenJoinSession_thenParticipantAdded() throws Exception {
		Long validId = 1L;
		ApiResponse<?> expectedResponse = new ApiResponse<>(true, "Participant added successfully");

		doReturn(expectedResponse).when(sessionService).joinSession(validId, JOHN_USERNAME);

		mockMvc.perform(put(BASE_URL + "/" + validId + "/participant")
				.with(SecurityMockMvcRequestPostProcessors.user(JOHN_USERNAME)))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Participant added successfully")));
	}

	@Test
	public void whenJoinSessionAsExistingParticipant_thenError() throws Exception {
		Long validId = 1L;

		doReturn(new ApiResponse<>(false, JOINED_SESSION_MSG)).when(sessionService).joinSession(validId, JOHN_USERNAME);

		mockMvc.perform(put(BASE_URL + "/" + validId + "/participant")
				.with(SecurityMockMvcRequestPostProcessors.user(JOHN_USERNAME)))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(JOINED_SESSION_MSG)));
	}

	@Test
	public void whenAddRestaurantToSession_thenRestaurantAdded() throws Exception {
		Long validId = 1L;
		RestaurantDto newRestaurantDto = new RestaurantDto("Some Restaurant");
		ApiResponse<?> expectedResponse = new ApiResponse<>(true, "Restaurant added successfully");

		doReturn(expectedResponse).when(sessionService).addRestaurantToSession(any(RestaurantDto.class), eq(validId),
				anyString());

		mockMvc.perform(put(BASE_URL + "/" + validId + "/restaurant")
				.with(SecurityMockMvcRequestPostProcessors.user(JOHN_USERNAME))
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(newRestaurantDto)))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Restaurant added successfully")));
	}

	@Test
	public void whenEndSession_thenReturnSuccessMessage() throws Exception {
		Long validId = 1L;
		ApiResponse<?> expectedResponse = new ApiResponse<>(true, "Session ended successfully");

		doReturn(expectedResponse).when(sessionService).endSession(validId, JOHN_USERNAME);

		mockMvc.perform(delete(BASE_URL + "/" + validId)
				.with(SecurityMockMvcRequestPostProcessors.user(JOHN_USERNAME)))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Session ended successfully")));
	}

	@Test
	public void whenEndSessionUnauthorised_thenError() throws Exception {
		Long validId = 1L;

		doReturn(new ApiResponse<>(false, "Unauthorized or session already ended")).when(sessionService)
				.endSession(validId, JOHN_USERNAME);

		mockMvc.perform(delete(BASE_URL + "/" + validId)
				.with(SecurityMockMvcRequestPostProcessors.user(JOHN_USERNAME)))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Unauthorized or session already ended")));
	}
}
