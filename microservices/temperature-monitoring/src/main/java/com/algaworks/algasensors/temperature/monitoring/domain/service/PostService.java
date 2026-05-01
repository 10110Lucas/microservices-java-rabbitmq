package com.algaworks.algasensors.temperature.monitoring.domain.service;

import com.algaworks.algasensors.temperature.monitoring.api.model.PostInput;
import com.algaworks.algasensors.temperature.monitoring.api.model.PostOutput;
import com.algaworks.algasensors.temperature.monitoring.api.model.PostProcessingResult;
import com.algaworks.algasensors.temperature.monitoring.api.model.PostSummaryOutput;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;

import java.util.UUID;

public interface PostService {

    PostOutput findById(UUID id);

    PagedModel<PostSummaryOutput> findAll(Pageable pageable);

    PostOutput create(PostInput payload);

    void updatePostInfo(PostProcessingResult result);
}
