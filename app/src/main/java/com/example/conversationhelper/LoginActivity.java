package com.example.conversationhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.conversationhelper.auth.Authentication;
import com.example.conversationhelper.db.Database;
import com.example.conversationhelper.db.model.User;

public class LoginActivity extends AppCompatActivity {

    Database database;
    EditText editName;
    EditText editPassword;
    TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        database = Database.getInstance(getApplicationContext());

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

        User user = database.getUserByName(name);
        if (user == null || !user.getPassword().equals(password)) {
            error.setText("Имя или пароль неправильные");
            return;
        }

        Authentication.setUser(user);
        Intent intent = new Intent(LoginActivity.this, ListChatsActivity.class);
        startActivity(intent);
        finish();
    }

    public void onClickGoToRegistration(View view) {
        Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
        startActivity(intent);
        finish();
    }
}