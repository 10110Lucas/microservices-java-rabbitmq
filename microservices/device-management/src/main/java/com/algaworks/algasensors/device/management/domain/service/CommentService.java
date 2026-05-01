package com.algaworks.algasensors.device.management.domain.service;

import com.algaworks.algasensors.device.management.api.model.CommentInput;
import com.algaworks.algasensors.device.management.api.model.CommentOutput;
import com.algaworks.algasensors.device.management.domain.model.CommentId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CommentService {

    Optional<CommentOutput> findById(CommentId id);

    Page<CommentOutput> findAll(Pageable pageable);

    Optional<CommentOutput> create(CommentInput input);
}
