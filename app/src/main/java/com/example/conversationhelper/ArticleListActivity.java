package com.example.conversationhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.example.conversationhelper.adapter.ArticleAdapter;
import com.example.conversationhelper.auth.Authentication;
import com.example.conversationhelper.db.model.Article;
import com.example.conversationhelper.db.repository.ArticleRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ArticleListActivity extends AppCompatActivity {

    private final List<Article> articleList = new ArrayList<>();
    private ArticleRepository articleRepository;
    private ArticleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);

        articleRepository = new ArticleRepository(FirebaseFirestore.getInstance());
        ListView listArticle = findViewById(R.id.admin_article_list);
        adapter = new ArticleAdapter(this, articleList);
        listArticle.setAdapter(adapter);

        loadArticles();

        if (Authentication.getUser().getRole().equals("user")) {
            findViewById(R.id.add_article_button).setVisibility(View.GONE);
        }

        listArticle.setOnItemClickListener((adapterView, view, i, l) -> {
            Article selectedArticle = adapter.getItem(i);
            if (selectedArticle != null) {
                Intent intent = new Intent(ArticleListActivity.this, ArticleActivity.class);
                intent.putExtra("ARTICLE", selectedArticle);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadArticles();
    }

    private void loadArticles() {
        articleRepository.getAllArticle()
                .thenAccept(list -> {
                    articleList.clear();
                    articleList.addAll(list);
                    adapter.notifyDataSetChanged();
                });
    }

    public void onClickAddArticle(View view) {
        Intent intent = new Intent(ArticleListActivity.this, SettingArticleActivity.class);
        startActivity(intent);
    }

    public void onClickBackActivity(View view) {
        finish();
    }

    public void onClickPopupMenu(View view) {
        Context wrapper = new ContextThemeWrapper(this, R.style.CustomPopupMenu);
        PopupMenu popupMenu = new PopupMenu(wrapper, view);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu_sort, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.option1) {
                articleList.sort(Comparator.comparing(Article::getCreateTime));
            } else if (item.getItemId() == R.id.option2) {
                articleList.sort(Comparator.comparing(Article::getTitle));
            } else if (item.getItemId() == R.id.option3) {
                articleList.sort(Comparator.comparingInt(ar -> ar.getContent().length()));
            }

            adapter.notifyDataSetChanged();
            return true;
        });

        popupMenu.show();
    }
}