package com.example.telegrambotBackEnd.repository;

import com.example.telegrambotBackEnd.entity.WeatherRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherRepository extends JpaRepository<WeatherRequest, Long> {

}
