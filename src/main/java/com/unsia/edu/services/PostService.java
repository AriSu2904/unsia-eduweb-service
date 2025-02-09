package com.unsia.edu.services;

import com.unsia.edu.entities.constant.EApproval;
import com.unsia.edu.models.request.CommentRequest;
import com.unsia.edu.models.request.PostRequest;
import com.unsia.edu.models.response.CommentResponse;
import com.unsia.edu.models.response.PostResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    PostResponse create(PostRequest post, List<MultipartFile> multipartFiles);
    List<PostResponse> getPendingPosts();
    List<PostResponse> getPublishedPost();
    PostResponse getPost(String id);
    PostResponse approvePost(String id);
    CommentResponse postComment(String postId, CommentRequest comment);
    Resource loadPostMaterial(String materialId);
}
