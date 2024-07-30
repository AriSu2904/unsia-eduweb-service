package com.unsia.edu.repositories;

import com.unsia.edu.entities.Post;
import com.unsia.edu.entities.constant.EApproval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, String> {
    List<Post> findAllByApproval(EApproval approval);
}
