package com.example.conversationhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

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

        Spinner spinner = findViewById(R.id.spinner_sort_article);
        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(this, R.array.spinner_sort_article_item, R.layout.custom_spinner_item);
        adapterSpinner.setDropDownViewResource(R.layout.custom_spinner_item);
        spinner.setAdapter(adapterSpinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getItemAtPosition(i).toString();

                switch (selectedItem) {
                    case "По дате публикации" :
                        articleList.sort(Comparator.comparing(Article::getCreateTime));
                        break;
                    case "По заголовку" :
                        articleList.sort(Comparator.comparing(Article::getTitle));
                        break;
                    case "По продолжительности" :
                        articleList.sort(Comparator.comparingInt(ar -> ar.getContent().length()));
                        break;
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
}