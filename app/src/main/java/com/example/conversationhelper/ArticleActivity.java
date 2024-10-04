package com.example.conversationhelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.conversationhelper.adapter.CommentAdapter;
import com.example.conversationhelper.adapter.OnCommentDeletedListener;
import com.example.conversationhelper.auth.Authentication;
import com.example.conversationhelper.db.model.Article;
import com.example.conversationhelper.db.model.Comment;
import com.example.conversationhelper.db.repository.CommentRepository;
import com.example.conversationhelper.db.repository.LikeRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ArticleActivity extends AppCompatActivity implements OnCommentDeletedListener {
    private LikeRepository likeRepository;
    private CommentRepository commentRepository;
    private final List<Comment> commentList = new ArrayList<>();
    private CommentAdapter adapter;
    private ImageView like;
    private TextView countLikeText, editComment, countCommentText;
    private int countLike, countComment;
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
        countCommentText = findViewById(R.id.count_comment_article);
        ImageView photo = findViewById(R.id.article_photo);

        if (article != null) {
            title.setText(article.getTitle());
            description.setText(article.getDescription());
            content.setText(article.getContent());

            if (article.getPhoto() != null) {
                Glide.with(this)
                        .load(article.getPhoto())
                        .apply(new RequestOptions()
                                .centerCrop()
                                .transform(new RoundedCorners(30)))
                        .into(photo);

                photo.setVisibility(View.VISIBLE);
            }

            editComment.setTag("invisible");

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

            commentRepository.getCountCommentByArticleId(article.getId())
                    .thenAccept(comments -> {
                        countCommentText.setText(String.valueOf(comments));
                        countComment = comments;
                    });


            adapter = new CommentAdapter(this, commentList, FirebaseFirestore.getInstance(), this);
            commentRecyclerView.setAdapter(adapter);
            commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(commentRecyclerView.getContext(), LinearLayoutManager.VERTICAL);
            dividerItemDecoration.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.divider)));
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

        countComment++;
        countCommentText.setText(String.valueOf(countComment));
    }

    @Override
    public void onCommentDeleted() {
        countComment--;
        countCommentText.setText(String.valueOf(countComment));
    }

    public void onClickComment(View view) {
        if (editComment.getTag().equals("visible")) {
            findViewById(R.id.list_comment_article).setVisibility(View.GONE);
            findViewById(R.id.title_comments).setVisibility(View.GONE);
            findViewById(R.id.edit_comment_text).setVisibility(View.GONE);
            findViewById(R.id.send_comment_button).setVisibility(View.GONE);
            editComment.setTag("invisible");
        } else {
            findViewById(R.id.list_comment_article).setVisibility(View.VISIBLE);
            findViewById(R.id.title_comments).setVisibility(View.VISIBLE);
            findViewById(R.id.edit_comment_text).setVisibility(View.VISIBLE);
            findViewById(R.id.send_comment_button).setVisibility(View.VISIBLE);
            editComment.setTag("visible");
        }
    }

    public void onClickBackActivity(View view) {
        finish();
    }
}