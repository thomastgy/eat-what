package com.backend.restservice.dto;

import java.util.ArrayList;
import java.util.List;

import com.backend.restservice.entity.Participant;
import com.backend.restservice.entity.Restaurant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SessionDto {

    @NotBlank(message = "name is mandatory")
    @Size(max = 50, message = "Session name must not exceed 50 characters")
    private String name;
    private boolean isActive;
    private String creator;
    private Restaurant result;
    private List<Participant> participant = new ArrayList<>();
    private List<Restaurant> restaurant = new ArrayList<>();

    public SessionDto() {
    }

    public SessionDto(String name) {
        this.name = name;
        this.isActive = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIsActive() {
        return this.isActive;
    }

    public boolean getIsActive() {
        return this.isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getCreator() {
        return this.creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Restaurant getResult() {
        return this.result;
    }

    public void setResult(Restaurant result) {
        this.result = result;
    }

    public List<Participant> getParticipant() {
        return this.participant;
    }

    public void setParticipant(List<Participant> participant) {
        this.participant = participant;
    }

    public List<Restaurant> getRestaurant() {
        return this.restaurant;
    }

    public void setRestaurant(List<Restaurant> restaurant) {
        this.restaurant = restaurant;
    }
}
