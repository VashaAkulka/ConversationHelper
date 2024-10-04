package com.example.conversationhelper;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.conversationhelper.auth.Authentication;
import com.example.conversationhelper.db.model.Article;
import com.example.conversationhelper.db.repository.ArticleRepository;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SettingArticleActivity extends AppCompatActivity {

    private ArticleRepository articleRepository;
    private EditText title, content, description;
    private boolean isCreate = true;
    private Uri photoUri = null;
    private Article article;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private StorageReference storageReference;
    private TextView photoUriLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_article);

        title = findViewById(R.id.edit_title_article);
        description = findViewById(R.id.edit_description_article);
        content = findViewById(R.id.edit_content_article);
        Button addEditButton = findViewById(R.id.save_update_article_button);
        TextView header = findViewById(R.id.header_setting_article);
        photoUriLabel = findViewById(R.id.photo_article_setting);

        Intent intent = getIntent();
        article = (Article) intent.getSerializableExtra("ARTICLE");
        if (article != null) {
            isCreate = false;
            addEditButton.setText("Обновить");
            header.setText("Редактирование статьи");

            title.setText(article.getTitle());
            description.setText(article.getDescription());
            content.setText(article.getContent());
            photoUriLabel.setText(article.getPhoto().substring(0, 75));
        } else header.setText("Создание статьи");

        articleRepository = new ArticleRepository(FirebaseFirestore.getInstance());
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            photoUri = imageUri;
                           photoUriLabel.setText(photoUri.toString().substring(0, 75));
                        }
                    }
                });
    }

    public void onClickAddUpdateArticle(View view) {
        String titleStr = title.getText().toString();
        String descriptionStr = description.getText().toString();
        String contentStr = content.getText().toString();

        if (isCreate) article = articleRepository.addArticle(null, null, null, Authentication.getUser().getId(), null);
        article.setTitle(titleStr);
        article.setDescription(descriptionStr);
        article.setContent(contentStr);

        if (photoUri == null) {
            articleRepository.updateArticle(article);

            Intent intent = new Intent(SettingArticleActivity.this, ArticleActivity.class);
            intent.putExtra("ARTICLE", article);
            startActivity(intent);
            finish();
        } else {
            StorageReference photoRef = storageReference.child("articles/" + article.getId() + ".jpg");
            UploadTask uploadTask = photoRef.putFile(photoUri);
            uploadTask.addOnSuccessListener(taskSnapshot -> photoRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                article.setPhoto(downloadUri.toString());
                articleRepository.updateArticle(article);

                Intent intent = new Intent(SettingArticleActivity.this, ArticleActivity.class);
                intent.putExtra("ARTICLE", article);
                startActivity(intent);
                finish();
            }));
        }
    }

    public void onClickBackActivity(View view) {
        finish();
    }

    public void addArticlePhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    public void onClickAddArticlePhoto(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, 1);
            } else {
                addArticlePhoto();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                addArticlePhoto();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            addArticlePhoto();
        }
    }
}