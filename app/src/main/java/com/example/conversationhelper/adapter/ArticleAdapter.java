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

import com.example.conversationhelper.R;
import com.example.conversationhelper.SettingArticleActivity;
import com.example.conversationhelper.auth.Authentication;
import com.example.conversationhelper.db.model.Article;
import com.example.conversationhelper.db.repository.ArticleRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ArticleAdapter extends ArrayAdapter<Article> {

    private final ArticleRepository articleRepository;

    public ArticleAdapter(Context context, List<Article> articles) {
        super(context, R.layout.list_item_article, articles);
        articleRepository = new ArticleRepository(FirebaseFirestore.getInstance());
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
        TextView dateText = convertView.findViewById(R.id.date_article_list);

        if (article != null) {
            titleText.setText(article.getTitle());
            descriptionText.setText(article.getDescription());
            likeNumberText.setText(String.valueOf(article.getCountLike()));

            Date createTime = article.getCreateTime().toDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String formattedDate = dateFormat.format(createTime);
            dateText.setText(String.format("Дата: %s", formattedDate));

            if (Authentication.getUser().getRole().equals("user")) {
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
                intent.putExtra("ARTICLE", article);
                getContext().startActivity(intent);
            });
        }

        return convertView;
    }
}
