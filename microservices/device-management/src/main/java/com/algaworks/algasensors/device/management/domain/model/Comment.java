package com.algaworks.algasensors.device.management.domain.model;

import io.hypersistence.tsid.TSID;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @AttributeOverride(name = "value",
            column = @Column(name = "id", columnDefinition = "BIGINT"))
    private CommentId id;
    @Column(name = "text", nullable = false)
    private String text;
    private String author;
    private OffsetDateTime createdAt;

    public Comment(CommentId id, String text, String author) {
        this.id = id;
        this.text = text;
        this.author = author;
        this.createdAt = OffsetDateTime.now();
    }
}
