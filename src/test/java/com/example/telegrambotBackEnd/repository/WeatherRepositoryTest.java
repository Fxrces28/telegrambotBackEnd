package com.example.telegrambotBackEnd.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import com.example.telegrambotBackEnd.entity.WeatherRequest;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class WeatherRepositoryTest {
    @Autowired
    private WeatherRepository weatherRepository;

    @Test
    public void testSaveWeatherRequest() {
        WeatherRequest request = new WeatherRequest();
        request.setCity("Москва");
        request.setTemperature(24.2);
        request.setTimestamp(LocalDateTime.now());

        WeatherRequest saved = weatherRepository.save(request);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCity()).isEqualTo("Москва");
        assertThat(saved.getTemperature()).isEqualTo(24.2);
        System.out.println("Test SaveWeatherRequest passed: Verified WeatherRequest saved with id " + saved.getId());
    }
}