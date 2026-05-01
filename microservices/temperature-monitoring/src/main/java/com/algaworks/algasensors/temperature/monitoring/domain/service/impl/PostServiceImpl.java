package com.algaworks.algasensors.temperature.monitoring.domain.service.impl;

import com.algaworks.algasensors.temperature.monitoring.api.model.PostInput;
import com.algaworks.algasensors.temperature.monitoring.api.model.PostOutput;
import com.algaworks.algasensors.temperature.monitoring.api.model.PostProcessingRequest;
import com.algaworks.algasensors.temperature.monitoring.api.model.PostProcessingResult;
import com.algaworks.algasensors.temperature.monitoring.api.model.PostSummaryOutput;
import com.algaworks.algasensors.temperature.monitoring.domain.model.Post;
import com.algaworks.algasensors.temperature.monitoring.domain.repository.PostRepository;
import com.algaworks.algasensors.temperature.monitoring.domain.service.PostService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.algaworks.algasensors.temperature.monitoring.infrastructure.rabbitmq.RabbitMQConfig.POST_QUEUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository repository;
    private final AmqpTemplate amqpTemplate;

    @Override
    public PostOutput findById(UUID id) {
        return getPostOutput(
                repository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
        );
    }

    @Override
    public PagedModel<PostSummaryOutput> findAll(Pageable pageable) {
        return new PagedModel<>(
                repository.findAll(pageable)
                        .map(PostServiceImpl::getPostSummary)
        );
    }

    @Transactional
    @Override
    public PostOutput create(PostInput payload) {
        if (repository.existsByTitleAndBody(payload.title(), payload.body()))
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Esse Post já existe com esses mesmos Título e Body.");
        Post post = repository.saveAndFlush(Post.builder()
                .id(UUID.randomUUID())
                .title(payload.title())
                .body(payload.body())
                .author(payload.author())
                .build());

        // Envia para fila do RabbitMQ
        amqpTemplate.convertAndSend(POST_QUEUE, new PostProcessingRequest(post.getId(), post.getBody()));

        PostOutput response = getPostOutput(post);
        log.info("Post criado com sucesso: {}", response);
        return response;
    }

    @Transactional
    @Override
    public void updatePostInfo(PostProcessingResult result) {
        Post found = repository.findById(result.postId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        found.setWordCount(result.wordCount());
        found.setCalculatedValue(result.calculatedValue());
        repository.save(found);
        log.info("Post com mensagem calculada com sucesso: {}", found);
    }


    private static PostOutput getPostOutput(Post post) {
        return new PostOutput(
                post.getId(),
                post.getTitle(),
                post.getBody(),
                post.getAuthor(),
                post.getWordCount(),
                post.getCalculatedValue()
        );
    }
    private static PostSummaryOutput getPostSummary(Post post) {
        return new PostSummaryOutput(
                post.getId(),
                post.getTitle(),
                getSummary(post.getBody()),
                post.getAuthor()
        );
    }
    private static String getSummary(String body) {
        Pattern regex = Pattern.compile("\\n");
        if (body == null) return "";
        else if (regex.matcher(body).find()) {
            return Arrays.stream(body.split("\\n"))
                    .limit(3)
                    .collect(Collectors.joining("\n"));
        } else return body;
    }
}
