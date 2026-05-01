package com.algaworks.algasensors.temperature.monitoring.domain.service.impl;

import com.algaworks.algasensors.temperature.monitoring.api.model.ModerationInput;
import com.algaworks.algasensors.temperature.monitoring.api.model.ModerationOutput;
import com.algaworks.algasensors.temperature.monitoring.domain.service.ModerationService;
import com.algaworks.algasensors.temperature.monitoring.domain.utils.PalavraProibidaLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@Service
public class ModerationServiceImpl implements ModerationService {

    // Lista em memória
    private static final List<ModerationInput> PALAVRAS_PROIBIDAS = PalavraProibidaLoader.carregar("palavras-proibidas.json");

    @Override
    public Optional<ModerationOutput> verify(ModerationInput input) {
        for (ModerationInput proibida: PALAVRAS_PROIBIDAS) {
            Pattern padrao = Pattern.compile("\\b" + Pattern.quote(proibida.text()) + "\\b",
                    Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.UNICODE_CHARACTER_CLASS);
            if (padrao.matcher(input.text().toLowerCase()).find()) {
                return Optional.of(new ModerationOutput(Boolean.FALSE, proibida.commentId()));
            }
        }
        return Optional.of(new ModerationOutput(Boolean.TRUE, ""));
    }
}
