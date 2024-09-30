package com.example.conversationhelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.conversationhelper.adapter.CommentAdapter;
import com.example.conversationhelper.auth.Authentication;
import com.example.conversationhelper.db.model.Article;
import com.example.conversationhelper.db.model.Comment;
import com.example.conversationhelper.db.repository.CommentRepository;
import com.example.conversationhelper.db.repository.LikeRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ArticleActivity extends AppCompatActivity {

    private LikeRepository likeRepository;
    private CommentRepository commentRepository;
    private final List<Comment> commentList = new ArrayList<>();
    private CommentAdapter adapter;
    private ImageView like;
    private TextView countLikeText;
    private EditText editComment;
    private int countLike;
    private Article article;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        Intent intent = getIntent();
        article = (Article) intent.getSerializableExtra("ARTICLE");

        RecyclerView commentRecyclerView = findViewById(R.id.list_comment_article);
        TextView title = findViewById(R.id.title_article);
        TextView description = findViewById(R.id.description_article);
        TextView content = findViewById(R.id.content_article);
        editComment = findViewById(R.id.edit_comment_text);
        like = findViewById(R.id.like_article_button);
        countLikeText = findViewById(R.id.count_like_article);

        if (article != null) {
            title.setText(article.getTitle());
            description.setText(article.getDescription());
            content.setText(article.getContent());

            likeRepository = new LikeRepository(FirebaseFirestore.getInstance());
            commentRepository = new CommentRepository(FirebaseFirestore.getInstance());

            likeRepository.getIfUserLikedArticle(Authentication.getUser().getId(), article.getId())
                    .thenAccept(aBoolean -> {
                        if (aBoolean) {
                            like.setImageResource(R.drawable.baseline_favorite_24);
                            like.setTag("liked");
                        } else {
                            like.setImageResource(R.drawable.baseline_favorite_border_24);
                            like.setTag("not_liked");
                        }
                    });

            likeRepository.getCountLikeByArticleId(article.getId())
                    .thenAccept(likes -> {
                        countLikeText.setText(String.valueOf(likes));
                        countLike = likes;
                    });


            adapter = new CommentAdapter(this, commentList, FirebaseFirestore.getInstance());
            commentRecyclerView.setAdapter(adapter);
            commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(commentRecyclerView.getContext(), LinearLayoutManager.VERTICAL);
            commentRecyclerView.addItemDecoration(dividerItemDecoration);

            commentRepository.getAllCommentByArticleId(article.getId())
                    .thenAccept(list -> {
                        commentList.addAll(list);
                        adapter.notifyItemInserted(list.size() - 1);
                    });
        }
    }

    public void onClickLike(View view) {
        if (like.getTag().equals("liked")) {
            like.setImageResource(R.drawable.baseline_favorite_border_24);
            like.setTag("not_liked");
            likeRepository.deleteLike(Authentication.getUser().getId(), article.getId());
            countLike--;
        } else {
            like.setImageResource(R.drawable.baseline_favorite_24);
            like.setTag("liked");
            likeRepository.addLike(Authentication.getUser().getId(), article.getId());
            countLike++;
        }

        countLikeText.setText(String.valueOf(countLike));
    }

    public void OnClickAddComment(View view) {
        Comment comment = commentRepository.addComment(editComment.getText().toString(), Authentication.getUser().getId(), article.getId());
        commentList.add(comment);
        adapter.notifyItemInserted(commentList.size() - 1);
        editComment.setText("");
    }
}