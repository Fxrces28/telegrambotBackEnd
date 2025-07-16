package com.example.telegrambotBackEnd.controlles;

import com.example.telegrambotBackEnd.entity.WeatherRequest;
import com.example.telegrambotBackEnd.repository.WeatherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.time.LocalDateTime;

@RestController
@RequestMapping("api/weather")
@RequiredArgsConstructor
public class WeatherController {
    private final WeatherRepository weatherRepository;

    @Value("${openweathermap.api-key}")
    private String apiKey;

    @GetMapping("/{city}")
    public String getWeather(@PathVariable String city) throws Exception {
        OkHttpClient client = new OkHttpClient();
        String url = String.format("http://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric&lang=ru", city, apiKey);
        okhttp3.Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return "Город не найден!";
            }

            String jsonData = response.body().string();
            JsonObject json = JsonParser.parseString(jsonData).getAsJsonObject();
            JsonObject main = json.getAsJsonObject("main");
            JsonObject weather = json.getAsJsonArray("weather").get(0).getAsJsonObject();

            String cityName = json.get("name").getAsString();
            double temp = main.get("temp").getAsDouble();
            String description = weather.get("description").getAsString();
            int humidity = main.get("humidity").getAsInt();

            WeatherRequest weatherRequest = new WeatherRequest();
            weatherRequest.setCity(cityName);
            weatherRequest.setTemperature(temp);
            weatherRequest.setTimestamp(LocalDateTime.now());
            weatherRepository.save(weatherRequest);

            return String.format("Погода в %s:\nТемпература: %.1f°C\nОписание: %s\nВлажность: %d%%",
                    cityName, temp, description, humidity);

        }
    }
}
