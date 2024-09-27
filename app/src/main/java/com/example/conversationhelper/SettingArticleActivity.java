package com.example.conversationhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.conversationhelper.auth.Authentication;
import com.example.conversationhelper.db.model.Article;
import com.example.conversationhelper.db.repository.ArticleRepository;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingArticleActivity extends AppCompatActivity {

    private ArticleRepository articleRepository;
    private EditText title;
    private EditText content;
    private EditText description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_article);

        title = findViewById(R.id.edit_title_article);
        description = findViewById(R.id.edit_description_article);
        content = findViewById(R.id.edit_content_article);

        Intent intent = getIntent();
        Article article = (Article) intent.getSerializableExtra("ARTICLE");
        if (article != null) {
            title.setText(article.getTitle());
            description.setText(article.getDescription());
            content.setText(article.getContent());
        }

        articleRepository = new ArticleRepository(FirebaseFirestore.getInstance());
    }

    public void onClickAddArticle(View view) {
        Article article = articleRepository.addArticle(title.getText().toString(), description.getText().toString(), content.getText().toString(), Authentication.getUser().getId());

        Intent intent = new Intent(SettingArticleActivity.this, ArticleActivity.class);
        intent.putExtra("ARTICLE", article);
        startActivity(intent);
        finish();
    }
}