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
import com.example.conversationhelper.db.model.Article;
import com.example.conversationhelper.db.repository.ArticleRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ArticleAdminAdapter extends ArrayAdapter<Article> {

    private final ArticleRepository articleRepository;

    public ArticleAdminAdapter(Context context, List<Article> articles) {
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

        if (article != null) {
            titleText.setText(article.getTitle());
            descriptionText.setText(article.getDescription());

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
