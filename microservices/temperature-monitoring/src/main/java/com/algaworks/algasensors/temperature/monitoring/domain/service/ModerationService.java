package com.algaworks.algasensors.temperature.monitoring.domain.service;

import com.algaworks.algasensors.temperature.monitoring.api.model.ModerationInput;
import com.algaworks.algasensors.temperature.monitoring.api.model.ModerationOutput;

import java.util.Optional;

public interface ModerationService {

    Optional<ModerationOutput> verify(ModerationInput input);
}
