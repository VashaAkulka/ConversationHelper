package com.example.conversationhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    private boolean isCreate = true;
    private Article article;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_article);

        title = findViewById(R.id.edit_title_article);
        description = findViewById(R.id.edit_description_article);
        content = findViewById(R.id.edit_content_article);
        Button addEditButton = findViewById(R.id.save_update_article_button);

        Intent intent = getIntent();
        article = (Article) intent.getSerializableExtra("ARTICLE");
        if (article != null) {
            isCreate = false;
            addEditButton.setText("Обновить");

            title.setText(article.getTitle());
            description.setText(article.getDescription());
            content.setText(article.getContent());
        }

        articleRepository = new ArticleRepository(FirebaseFirestore.getInstance());
    }

    public void onClickAddUpdateArticle(View view) {
        String titleStr = title.getText().toString();
        String descriptionStr = description.getText().toString();
        String contentStr = content.getText().toString();


        if (isCreate) article = articleRepository.addArticle(titleStr, descriptionStr, contentStr, Authentication.getUser().getId());
        else {
            article.setTitle(titleStr);
            article.setTitle(descriptionStr);
            article.setTitle(contentStr);
            articleRepository.updateArticle(article);
        }

        Intent intent = new Intent(SettingArticleActivity.this, ArticleActivity.class);
        intent.putExtra("ARTICLE", article);
        startActivity(intent);
        finish();
    }

    public void onClickBackActivity(View view) {
        finish();
    }
}