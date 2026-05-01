package com.algaworks.algasensors.device.management.domain.repository;

import com.algaworks.algasensors.device.management.domain.model.Comment;
import com.algaworks.algasensors.device.management.domain.model.CommentId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, CommentId> {

    Page<Comment> findAll(Pageable pageable);

    boolean existsByText(String text);
}
