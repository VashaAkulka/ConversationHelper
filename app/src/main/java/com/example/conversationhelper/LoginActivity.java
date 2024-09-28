package com.example.conversationhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.conversationhelper.auth.Authentication;
import com.example.conversationhelper.auth.SharedPreferencesUtil;
import com.example.conversationhelper.db.repository.UserRepository;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private UserRepository userRepository;
    private EditText editName;
    private EditText editPassword;
    private TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userRepository = new UserRepository(FirebaseFirestore.getInstance());

        editName = findViewById(R.id.edit_user_name_log);
        editPassword = findViewById(R.id.edit_user_password_log);
        error = findViewById(R.id.error_text_login_form);
    }

    public void onClickLogin(View view) {
        String name = editName.getText().toString();
        String password = editPassword.getText().toString();

        if (name.equals("") || password.equals("")) {
            error.setText("Все поля обязательны");
            return;
        }

        userRepository.getUserByName(name)
                .thenAccept(user -> {
                    if (user == null || !user.getPassword().equals(password)) {
                        error.setText("Имя или пароль неправильные");
                        return;
                    }

                    Authentication.setUser(user);

                    SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(this);
                    sharedPreferencesUtil.saveUser(user);

                    Intent intent = new Intent(LoginActivity.this, ListChatsActivity.class);
                    startActivity(intent);
                    finish();
                });
    }

    public void onClickGoToRegistration(View view) {
        Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
        startActivity(intent);
        finish();
    }
}