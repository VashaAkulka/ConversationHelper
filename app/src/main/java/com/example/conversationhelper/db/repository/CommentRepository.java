package com.example.conversationhelper.db.repository;

import com.example.conversationhelper.db.model.Comment;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CommentRepository {
    private final CollectionReference commentCollection;

    public CommentRepository(FirebaseFirestore db) {
        commentCollection = db.collection("comments");
    }

    public CompletableFuture<List<Comment>> getAllCommentByArticleId(String articleId) {
        CompletableFuture<List<Comment>> future = new CompletableFuture<>();
        List<Comment> commentList = new ArrayList<>();

        commentCollection.whereEqualTo("articleId", articleId).get()
                .addOnCompleteListener(task -> {
                   if (task.isSuccessful()) {
                       for (QueryDocumentSnapshot document : task.getResult()) {
                           Comment comment = document.toObject(Comment.class);
                           commentList.add(comment);
                       }
                       commentList.sort(Comparator.comparing(Comment::getCreateTime));
                       future.complete(commentList);
                   }
                });

        return future;
    }

    public CompletableFuture<Integer> getCountCommentByArticleId(String articleId) {
        CompletableFuture<Integer> future = new CompletableFuture<>();

        commentCollection.whereEqualTo("articleId", articleId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        future.complete(task.getResult().size());
                    }
                });

        return future;
    }

    public Comment addComment(String content, String userId, String articleId) {
        String id = commentCollection.document().getId();
        Timestamp createTime = Timestamp.now();

        Comment comment = new Comment(id, content, userId, articleId, createTime);
        commentCollection.document(id).set(comment);

        return comment;
    }

    public void deleteCommentById(String id) {
        commentCollection.document(id).delete();
    }
}
