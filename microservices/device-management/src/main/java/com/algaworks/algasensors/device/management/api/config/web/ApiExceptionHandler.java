package com.algaworks.algasensors.device.management.api.config.web;

import com.algaworks.algasensors.device.management.api.client.SensorMonitoringClientBadGatewayException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.nio.channels.ClosedChannelException;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({
            SocketTimeoutException.class,
            ConnectException.class,
            ClosedChannelException.class
    })
    public ProblemDetail handle(IOException exception) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.GATEWAY_TIMEOUT);
        pd.setTitle("Gateway Timeout");
        pd.setDetail(exception.getMessage());
        pd.setType(URI.create("/errors/gateway-timeout"));
        return pd;
    }

    @ExceptionHandler(SensorMonitoringClientBadGatewayException.class)
    public ProblemDetail handle(SensorMonitoringClientBadGatewayException exception) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_GATEWAY);
        pd.setTitle("Bad Gateway");
        pd.setDetail(exception.getMessage());
        pd.setType(URI.create("/errors/bad-gateway"));
        return pd;
    }
}
