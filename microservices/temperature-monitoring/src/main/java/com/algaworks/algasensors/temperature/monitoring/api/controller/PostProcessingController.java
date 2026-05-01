package com.algaworks.algasensors.temperature.monitoring.api.controller;

import com.algaworks.algasensors.temperature.monitoring.api.model.PostInput;
import com.algaworks.algasensors.temperature.monitoring.api.model.PostOutput;
import com.algaworks.algasensors.temperature.monitoring.api.model.PostSummaryOutput;
import com.algaworks.algasensors.temperature.monitoring.domain.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostProcessingController {

    private final PostService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PagedModel<PostSummaryOutput> getAll(@PageableDefault Pageable pageable) {
        return service.findAll(pageable);
    }

    @GetMapping("/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public PostOutput getOne(@PathVariable String postId) {
        return service.findById(UUID.fromString(postId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostOutput create(@RequestBody PostInput body) {
        return service.create(body);
    }
}
