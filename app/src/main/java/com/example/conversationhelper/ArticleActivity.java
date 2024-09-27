package com.example.conversationhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.conversationhelper.db.model.Article;

public class ArticleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        Intent intent = getIntent();
        Article article = (Article) intent.getSerializableExtra("ARTICLE");

        TextView title = findViewById(R.id.title_article);
        TextView description = findViewById(R.id.description_article);
        TextView content = findViewById(R.id.content_article);

        if (article != null) {
            title.setText(article.getTitle());
            description.setText(article.getDescription());
            content.setText(article.getContent());
        }
    }
}