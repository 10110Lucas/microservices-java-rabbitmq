package com.algaworks.algasensors.device.management.api.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RestClientFactory {

    private final RestClient.Builder builder;

    @Value("${base.url.temperatureMonitoringApi}")
    private String baseUrlTemperatureMonitoringApi;

    public RestClient temperatureMonitoringRestClient() {
        return builder.baseUrl(baseUrlTemperatureMonitoringApi)
                .requestFactory(generateClientHttpRequestFactory())
                .defaultStatusHandler(HttpStatusCode::isError, ((request, response) -> {
                    throw new SensorMonitoringClientBadGatewayException();
                }))
                .build();
    }

    private ClientHttpRequestFactory generateClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(Duration.ofSeconds(5));
        factory.setConnectTimeout(Duration.ofSeconds(3));
        return factory;
    }
}
