package com.example.conversationhelper.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.conversationhelper.R;
import com.example.conversationhelper.SettingArticleActivity;
import com.example.conversationhelper.auth.Authentication;
import com.example.conversationhelper.db.UserRole;
import com.example.conversationhelper.db.model.Article;
import com.example.conversationhelper.db.repository.ArticleRepository;
import com.example.conversationhelper.db.repository.CommentRepository;
import com.example.conversationhelper.db.repository.LikeRepository;
import com.example.conversationhelper.db.repository.UserRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ArticleAdapter extends ArrayAdapter<Article> {

    private final ArticleRepository articleRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public ArticleAdapter(Context context, List<Article> articles) {
        super(context, R.layout.list_item_article, articles);
        articleRepository = new ArticleRepository(FirebaseFirestore.getInstance());
        likeRepository = new LikeRepository(FirebaseFirestore.getInstance());
        commentRepository = new CommentRepository(FirebaseFirestore.getInstance());
        userRepository = new UserRepository(FirebaseFirestore.getInstance());
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Article article = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_article, parent, false);
        }

        ImageView deleteButton = convertView.findViewById(R.id.article_delete_button);
        ImageView editButton = convertView.findViewById(R.id.article_edit_button);
        TextView titleText = convertView.findViewById(R.id.article_title);
        TextView descriptionText = convertView.findViewById(R.id.article_description);
        TextView likeNumberText = convertView.findViewById(R.id.number_like_article_list);
        TextView commentNumberText = convertView.findViewById(R.id.number_comment_article_list);
        TextView dateText = convertView.findViewById(R.id.date_article_list);
        ImageView articlePhoto = convertView.findViewById(R.id.article_photo);
        ImageView authorAvatar = convertView.findViewById(R.id.article_author_avatar);
        TextView authorName = convertView.findViewById(R.id.article_author_name);

        if (article != null) {
            titleText.setText(article.getTitle());
            descriptionText.setText(article.getDescription());

            userRepository.getUserById(article.getUserId()).thenAccept(user -> {
                if (user.getAvatar() != null) {
                    Glide.with(getContext())
                            .load(user.getAvatar())
                            .apply(new RequestOptions()
                                    .centerCrop()
                                    .circleCrop())
                            .into(authorAvatar);
                }

                authorName.setText(user.getName());
            });

            if (article.getPhoto() != null) {
                Glide.with(convertView.getContext())
                        .load(article.getPhoto())
                        .apply(new RequestOptions()
                                .centerCrop()
                                .transform(new RoundedCorners(30)))
                        .into(articlePhoto);

                articlePhoto.setVisibility(View.VISIBLE);
            } else articlePhoto.setVisibility(View.GONE);

            likeRepository.getCountLikeByArticleId(article.getId()).thenAccept(countLike -> likeNumberText.setText(String.valueOf(countLike)));
            commentRepository.getCountCommentByArticleId(article.getId()).thenAccept(countComment -> commentNumberText.setText(String.valueOf(countComment)));

            Date createTime = article.getCreateTime().toDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            String formattedDate = dateFormat.format(createTime);
            dateText.setText(String.format("Дата: %s", formattedDate));

            if (Authentication.getUser() == null || (Authentication.getUser().getRole() == UserRole.USER)) {
                deleteButton.setVisibility(View.GONE);
                editButton.setVisibility(View.GONE);
            }

            deleteButton.setOnClickListener(view -> {
                articleRepository.deleteArticleById(article.getId());
                remove(article);
                notifyDataSetChanged();
            });

            editButton.setOnClickListener(view -> {
                Intent intent = new Intent(getContext(), SettingArticleActivity.class);
                intent.putExtra("ARTICLE", article.getId());
                getContext().startActivity(intent);
            });
        }

        return convertView;
    }
}
