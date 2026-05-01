package com.algaworks.algasensors.temperature.monitoring.domain.utils;

import com.algaworks.algasensors.temperature.monitoring.api.model.ModerationInput;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;

public class PalavraProibidaLoader {

    public static List<ModerationInput> carregar(String resourceFile) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = PalavraProibidaLoader.class
                    .getClassLoader()
                    .getResourceAsStream(resourceFile);

            if (is == null) {
                throw new IllegalStateException("Arquivo " + resourceFile + " não encontrado no classpath!");
            }

            return mapper.readValue(is,
                    new TypeReference<List<ModerationInput>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar palavras proibidas do arquivo " + resourceFile, e);
        }
    }
}
