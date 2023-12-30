package com.backend.restservice.data;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import com.backend.restservice.entity.Session;
import com.backend.restservice.repository.SessionRepository;

import com.backend.restservice.entity.Participant;

import com.backend.restservice.entity.Restaurant;
import com.backend.restservice.repository.RestaurantRepository;

@Configuration
public class Seed {

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {

        UserDetails admin = User.withUsername("admin")
                .password(encoder.encode("admin"))
                .roles("ADMIN")
                .build();

        UserDetails john = User.withUsername("john")
                .password(encoder.encode("john"))
                .roles("USER")
                .build();

        UserDetails mary = User.withUsername("mary")
                .password(encoder.encode("mary"))
                .roles("USER")
                .build();

        UserDetails sarah = User.withUsername("sarah")
                .password(encoder.encode("sarah"))
                .roles("USER")
                .build();

        UserDetails hacker = User.withUsername("hacker")
                .password(encoder.encode("hacker"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, john, mary, sarah, hacker);
    }

    @Bean
    CommandLineRunner initDatabase(SessionRepository sessionRepository, RestaurantRepository restaurantRepository) {

        Session session1 = new Session("Team Bonding Lunch Q1 2024");
        session1.setCreator("john");

        Participant participant1 = new Participant("john");
        Participant participant2 = new Participant("mary");
        Participant participant3 = new Participant("sarah");
        List<Participant> session1Participants = new ArrayList<Participant>() {
            {
                add(participant1);
                add(participant2);
                add(participant3);
            }
        };

        Restaurant restaurant1a = new Restaurant("McDonalds");
        Restaurant restaurant2a = new Restaurant("KFC");
        Restaurant restaurant2b = new Restaurant("Burger King");
        Restaurant restaurant3a = new Restaurant("Popeyes");

        List<Restaurant> session1Restaurants = new ArrayList<Restaurant>() {
            {
                add(restaurant1a);
                add(restaurant2a);
                add(restaurant2b);
                add(restaurant3a);
            }
        };

        session1.setParticipant(session1Participants);
        session1.setRestaurant(session1Restaurants);

        sessionRepository.save(session1);

        return args -> {
        };
    }
}
