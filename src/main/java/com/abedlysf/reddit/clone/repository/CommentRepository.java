package com.abedlysf.reddit.clone.repository;

import com.abedlysf.reddit.clone.model.Comment;
import com.abedlysf.reddit.clone.model.Post;
import com.abedlysf.reddit.clone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);

    List<Comment> findAllByUser(User user);
}