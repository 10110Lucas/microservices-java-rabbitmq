package com.algaworks.algasensors.temperature.monitoring.api.controller;

import com.algaworks.algasensors.temperature.monitoring.api.model.ModerationInput;
import com.algaworks.algasensors.temperature.monitoring.api.model.ModerationOutput;
import com.algaworks.algasensors.temperature.monitoring.domain.service.ModerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/moderate")
@RequiredArgsConstructor
public class ModerationController {

    private final ModerationService service;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ModerationOutput moderate(@RequestBody ModerationInput input) {
        return service.verify(input)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
