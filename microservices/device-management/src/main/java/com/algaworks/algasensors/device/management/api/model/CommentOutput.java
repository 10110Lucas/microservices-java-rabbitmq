package com.algaworks.algasensors.device.management.api.model;

import io.hypersistence.tsid.TSID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentOutput {

    private TSID id;
    private String text;
    private String author;
    private OffsetDateTime createdAt;
}
