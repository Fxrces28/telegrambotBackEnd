package com.example.telegrambotBackEnd.entity;

import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name="weather_request")
public class WeatherRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String city;
    private Double temperature;
    private LocalDateTime timestamp;
}
