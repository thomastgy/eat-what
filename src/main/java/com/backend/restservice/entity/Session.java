package com.backend.restservice.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private boolean isActive;
    private String creator;

    @OneToOne
    @JoinColumn(name = "restaurant_id", referencedColumnName = "id")
    private Restaurant result;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "session_id", referencedColumnName = "id")
    private List<Participant> participant = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "session_id", referencedColumnName = "id")
    private List<Restaurant> restaurant = new ArrayList<>();

    public Session() {
    }

    public Session(String name) {
        this.name = name;
        this.isActive = true;
    }

    public Session(String name, String creator, boolean isActive, List<Participant> participant,
            List<Restaurant> restaurant) {
        this.name = name;
        this.creator = creator;
        this.isActive = isActive;
        this.participant = participant;
        this.restaurant = restaurant;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreator() {
        return this.creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
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

    public Restaurant getResult() {
        return this.result;
    }

    public void setResult(Restaurant result) {
        this.result = result;
    }
}
