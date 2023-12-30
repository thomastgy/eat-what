package com.backend.restservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RestaurantDto {

    @NotBlank(message = "name is mandatory")
    @Size(max = 50, message = "Restaurant name must not exceed 50 characters")
    private String name;

    public RestaurantDto() {
    }

    public RestaurantDto(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
