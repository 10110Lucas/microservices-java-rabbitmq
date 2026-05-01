package com.algaworks.algasensors.temperature.processing;

import com.algaworks.algasensors.temperature.processing.common.UUIDv7Utils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

class UUIDv7Test {

    @Test
    void sholdGenerateUUIDv7_getDateTime() {
        UUID uuid1 = IdGenerator.generateTimeBasedUID();
        UUID uuid2 = IdGenerator.generateTimeBasedUID();
        UUID uuid3 = IdGenerator.generateTimeBasedUID();
        UUID uuid4 = IdGenerator.generateTimeBasedUID();

        Assertions.assertThat(uuid1).isNotNull();
        System.out.println("\nuuid1 = " + uuid1);
        System.out.println("tempo = " + UUIDv7Utils.extractOffsetDateTime(uuid1));

        Assertions.assertThat(uuid2).isNotNull();
        System.out.println("\nuuid2 = " + uuid2);
        System.out.println("tempo = " + UUIDv7Utils.extractOffsetDateTime(uuid2));
        Assertions.assertThat(uuid1).isLessThan(uuid2);

        Assertions.assertThat(uuid3).isNotNull();
        System.out.println("\nuuid3 = " + uuid3);
        System.out.println("tempo = " + UUIDv7Utils.extractOffsetDateTime(uuid3));
        Assertions.assertThat(uuid2).isLessThan(uuid3);

        Assertions.assertThat(uuid4).isNotNull();
        System.out.println("\nuuid4 = " + uuid4);
        System.out.println("tempo = " + UUIDv7Utils.extractOffsetDateTime(uuid4));
        Assertions.assertThat(uuid3).isLessThan(uuid4);
    }

    @Test
    void sholdGenerateUUIDv7_getDateTimeOther() {
        UUID uuid = IdGenerator.generateTimeBasedUID();

        OffsetDateTime uuidDateTime = UUIDv7Utils.extractOffsetDateTime(uuid).truncatedTo(ChronoUnit.MINUTES);
        OffsetDateTime currentDateTime = OffsetDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        Assertions.assertThat(uuidDateTime).isEqualTo(currentDateTime);
    }
}
