package com.example.telegrambotBackEnd.controllers;

import com.example.telegrambotBackEnd.repository.WeatherRepository;
import com.example.telegrambotBackEnd.entity.WeatherRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class WeatherControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WeatherRepository weatherRepository;

    private MockWebServer mockWebServer;

    @BeforeEach
    public void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        JsonObject mockResponse = new JsonObject();
        mockResponse.addProperty("name", "Москва");
        JsonObject main = new JsonObject();
        main.addProperty("temp", 24.2);
        main.addProperty("humidity", 52);
        JsonObject weather = new JsonObject();
        weather.addProperty("description", "облачно с прояснениями");
        mockResponse.add("main", main);
        mockResponse.add("weather", new Gson().toJsonTree(new JsonObject[]{weather}));
        mockWebServer.enqueue(new MockResponse()
                .setBody(new Gson().toJson(mockResponse))
                .addHeader("Content-Type", "application/json"));
    }

    @AfterEach
    public void tearDown() throws IOException {
        if (mockWebServer != null) {
            mockWebServer.shutdown();
        }
    }

    @Test
    public void testGetWeather_Success() throws Exception {
        when(weatherRepository.save(any(WeatherRequest.class))).thenReturn(new WeatherRequest());

        mockMvc.perform(get("/api/weather/Moscow"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Погода в Москва")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Температура: 24,2°C")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Описание: облачно с прояснениями")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Влажность: 52%")))
                .andDo(result -> System.out.println("Test GetWeather_Success passed: Verified weather data for Москва"));

        verify(weatherRepository).save(any(WeatherRequest.class));
    }

    @Test
    public void testGetWeather_CityNotFound() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("{\"cod\": 404, \"message\": \"city not found\"}")
                .addHeader("Content-Type", "application/json"));

        mockMvc.perform(get("/api/weather/InvalidCity"))
                .andExpect(status().isOk())
                .andExpect(content().string("Город не найден"))
                .andDo(result -> System.out.println("Test GetWeather_CityNotFound passed: Verified error message for invalid city"));
    }
}