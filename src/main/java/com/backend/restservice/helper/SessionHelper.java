package com.backend.restservice.helper;

import java.util.List;
import java.util.Random;

import org.apache.commons.text.StringEscapeUtils;

import com.backend.restservice.entity.Participant;
import com.backend.restservice.entity.Restaurant;

public class SessionHelper {

    public static final String sanitizeInput(String input) {
        return StringEscapeUtils.escapeHtml4(input.trim());
    }

    public static final boolean isUserInList(String currUser, List<Participant> participantsList) {
        for (Participant participant : participantsList) {
            if (participant.getUsername().equals(currUser)) {
                return true;
            }
        }

        return false;
    }

    public static final boolean isRestaurantInList(Restaurant newRestaurant, List<Restaurant> restaurantList) {
        for (Restaurant restaurant : restaurantList) {
            if (restaurant.getName().equals(newRestaurant.getName())) {
                return true;
            }
        }

        return false;
    }

    public static final Restaurant selectRestaurant(List<Restaurant> restaurants) {
        return restaurants.isEmpty() ? null : restaurants.get(new Random().nextInt(restaurants.size()));
    }

}
