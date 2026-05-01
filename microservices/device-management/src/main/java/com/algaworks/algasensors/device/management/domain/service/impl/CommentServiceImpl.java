package com.algaworks.algasensors.device.management.domain.service.impl;

import com.algaworks.algasensors.device.management.api.client.ModerationClient;
import com.algaworks.algasensors.device.management.api.model.CommentInput;
import com.algaworks.algasensors.device.management.api.model.CommentOutput;
import com.algaworks.algasensors.device.management.api.model.ModerationInput;
import com.algaworks.algasensors.device.management.api.model.ModerationOutput;
import com.algaworks.algasensors.device.management.common.IdGenerator;
import com.algaworks.algasensors.device.management.domain.model.Comment;
import com.algaworks.algasensors.device.management.domain.model.CommentId;
import com.algaworks.algasensors.device.management.domain.repository.CommentRepository;
import com.algaworks.algasensors.device.management.domain.service.CommentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository repository;
    private final ModerationClient moderation;

    @Override
    public Optional<CommentOutput> findById(CommentId id) {
        Optional<Comment> comment = repository.findById(id);
        return comment.map(CommentServiceImpl::getCommentOutput);
    }

    @Override
    public Page<CommentOutput> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(CommentServiceImpl::getCommentOutput);
    }

    @Transactional
    @Override
    public Optional<CommentOutput> create(CommentInput input) {
        ModerationOutput output = moderation.moderate(new ModerationInput(input.getText(), null));
        if (Boolean.TRUE.equals(output.getApproved())) {
            if (repository.existsByText(input.getText())) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Esse comentário já existe");
            }
            Comment entity = new Comment(new CommentId(IdGenerator.generateTSID()), input.getText(), input.getAuthor());
            return Optional.ofNullable(
                getCommentOutput(
                    repository.saveAndFlush(entity)
                )
            );
        } else if (Boolean.FALSE.equals(output.getApproved())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Comentário negado por motivo: "+output.getReason());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }


    private static CommentOutput getCommentOutput(Comment comment) {
        return CommentOutput.builder()
                .id(comment.getId().getValue())
                .text(comment.getText())
                .author(comment.getAuthor())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
