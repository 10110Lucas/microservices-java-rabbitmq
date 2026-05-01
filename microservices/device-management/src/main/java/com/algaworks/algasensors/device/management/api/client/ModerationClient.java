package com.algaworks.algasensors.device.management.api.client;

import com.algaworks.algasensors.device.management.api.model.ModerationInput;
import com.algaworks.algasensors.device.management.api.model.ModerationOutput;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("/api/moderate")
public interface ModerationClient {

    @PostExchange
    ModerationOutput moderate(@RequestBody ModerationInput input);
}
