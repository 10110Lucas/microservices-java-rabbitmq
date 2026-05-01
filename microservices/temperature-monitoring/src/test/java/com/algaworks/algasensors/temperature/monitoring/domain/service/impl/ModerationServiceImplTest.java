package com.algaworks.algasensors.temperature.monitoring.domain.service.impl;

import com.algaworks.algasensors.temperature.monitoring.api.model.ModerationInput;
import com.algaworks.algasensors.temperature.monitoring.api.model.ModerationOutput;
import com.algaworks.algasensors.temperature.monitoring.domain.service.ModerationService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ModerationServiceImplTest {

    private final ModerationService moderationService = new ModerationServiceImpl();

    @ParameterizedTest(name = "[{index}] Texto: \"{0}\" -> aprovado={1}, motivo=\"{2}\"")
    @CsvSource({
            // texto                          | aprovado | motivo
            "'Esse cara é um idiota',        false,     xingamento",
            "'Você é um viadinho!',          false,     homofobia",
            "'Esse negão falou besteira',    false,     racismo",
            "'Vai tomar no cú agora!',       false,     'ódio geral'",
            "'Bom dia, tudo certo?',         true,      ''",
            "'Ganhei no pódio da corrida',   true,      ''"
    })
    void deveValidarComentariosCorretamente(String texto, Boolean esperadoAprovado, String esperadoMotivo) {

        System.out.printf("Texto: '%s', Aprovado: '%s', Motivo: '%s'%n", texto, esperadoAprovado, esperadoMotivo);
        ModerationInput input = new ModerationInput(texto, null);

        ModerationOutput output = moderationService.verify(input).orElse(null);
        System.out.printf("Result {Aprovado: '%s', Reason: '%s'}%n", output.getApproved(), output.getReason());

        assertNotNull(output);
        assertEquals(esperadoAprovado, output.getApproved());
        assertEquals(esperadoMotivo, output.getReason());
    }

//    @Test
//    void deveDetectarPalavraProibidaEReprovar() {
//        ModerationInput input = new ModerationInput( "Esse cara é um idiota", null);
//        Optional<ModerationOutput> result = moderationService.verify(input);
//
//        assertTrue(result.isPresent());
//        assertFalse(result.get().getApproved());
//        assertEquals("xingamento", result.get().getReason());
//    }
//
//    @Test
//    void deveAprovarComentarioSemPalavraProibida() {
//        ModerationInput input = new ModerationInput("Hoje é um ótimo dia de sol!", null);
//        Optional<ModerationOutput> result = moderationService.verify(input);
//
//        assertTrue(result.isPresent());
//        assertTrue(result.get().getApproved());
//        assertEquals("", result.get().getReason());
//    }
//
//    @Test
//    void deveDetectarCategoriaRacismo() {
//        ModerationInput input = new ModerationInput("Esse branquelo falou besteira", null);
//        Optional<ModerationOutput> result = moderationService.verify(input);
//
//        assertTrue(result.isPresent());
//        assertFalse(result.get().getApproved());
//        assertEquals("racismo", result.get().getReason());
//    }
//
//    @Test
//    void naoDeveConfundirPalavraParecida() {
//        ModerationInput input = new ModerationInput("Ganhei no pódio da corrida", null);
//        Optional<ModerationOutput> result = moderationService.verify(input);
//
//        assertTrue(result.isPresent());
//        assertTrue(result.get().getApproved()); // "pódio" não deve ser confundido com "ódio"
//    }
}