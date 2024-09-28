package com.example.conversationhelper.db.repository;

import com.example.conversationhelper.db.model.Article;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ArticleRepository {
    private final CollectionReference articleCollection;

    public ArticleRepository(FirebaseFirestore db) {
        this.articleCollection = db.collection("articles");
    }

    public void deleteArticleById(String id) {
        articleCollection.document(id).delete();
    }

    public CompletableFuture<List<Article>> getAllArticle() {
        CompletableFuture<List<Article>> future = new CompletableFuture<>();
        List<Article> articleList = new ArrayList<>();

        articleCollection.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Article article = document.toObject(Article.class);
                            articleList.add(article);
                        }
                        future.complete(articleList);
                    }
                });

        return future;
    }

    public Article addArticle(String title, String description, String content, String userId) {
        String articleId = articleCollection.document().getId();
        Timestamp createTime = Timestamp.now();

        Article article = new Article(articleId, userId, title, description, content, 0, createTime);
        articleCollection.document(articleId).set(article);
        return article;
    }

    public void updateArticle(Article article) {
        articleCollection.document(article.getId()).set(article);
    }
}
