package com.algaworks.algasensors.device.management.common;

import io.hypersistence.tsid.TSID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

class TSIDTest {

    @Test
    void shouldGenerateTSID() {
        TSID tsid = TSID.fast(); // apenas para testes, não deve usar em produção.

        Assertions.assertThat(tsid).isNotNull();
        System.out.println("tsid = " + tsid);
        System.out.println("tsid long = "+ tsid.toLong());
        System.out.println("tsid instant = "+ tsid.getInstant());
    }

    @Test
    void shouldGenerateTSID_withSystemProperties() {
        System.setProperty("tsid.node", "2"); // posição sendo o Nó 2
        System.setProperty("tsid.node.count", "32"); // tamanho do cluster

        // Visualizar os valores internos da Factory
        // referenciados pelas properties configuradas acima
        TSID.Factory factory = TSID.Factory.builder().build();
        TSID tsid = factory.generate();

        Assertions.assertThat(tsid).isNotNull();
        System.out.println("tsid = " + tsid);
        System.out.println("tsid long = "+ tsid.toLong());
        System.out.println("tsid instant = "+ tsid.getInstant());
    }

    @Test
    void shouldGenerateTSID_withEnvironments() {
        TSID tsid = IdGenerator.generateTSID();

        Assertions.assertThat(tsid).isNotNull();
        System.out.println("tsid = " + tsid);
        System.out.println("tsid long = "+ tsid.toLong());
        System.out.println("tsid instant = "+ tsid.getInstant());

        // Validar com o tempo atual com diferença de até 1min
        Assertions.assertThat(tsid.getInstant()).isCloseTo(Instant.now(), Assertions.within(1, ChronoUnit.MINUTES));
    }
}
