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

import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {
    Database database;
    EditText editName;
    EditText editPassword;
    EditText editPasswordRepeat;
    EditText editEmail;
    TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        database = Database.getInstance(getApplicationContext());

        editName = findViewById(R.id.edit_user_name_reg);
        editPassword = findViewById(R.id.edit_user_password_reg);
        editPasswordRepeat = findViewById(R.id.edit_user_password_repeat_reg);
        editEmail = findViewById(R.id.edit_user_email_reg);
        error = findViewById(R.id.error_text_registration_form);
    }

    public void onClickRegistration(View view) {
        String name = editName.getText().toString();
        String password = editPassword.getText().toString();
        String passwordRepeat = editPasswordRepeat.getText().toString();
        String email = editEmail.getText().toString();

        if (name.equals("") || password.equals("") || passwordRepeat.equals("") || email.equals("")) {
            error.setText("Все поля обязательны");
            return;
        }

        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        if (!Pattern.compile(emailPattern).matcher(email).matches()) {
            error.setText("Неправильныый формат электроной почты");
            return;
        }

        if (!password.equals(passwordRepeat)) {
            error.setText("Пароли не совпадают");
            return;
        }

        if (password.length() < 6) {
            error.setText("Пароль слишком легкий");
            return;
        }

        User user = database.getUserByName(name);
        if (user != null) {
            error.setText("Такой пользователь уже существует");
            return;
        }

        database.addUser(name, email, password);
        Authentication.setUser(database.getUserByName(name));
        Intent intent = new Intent(RegistrationActivity.this, ListChatsActivity.class);
        startActivity(intent);
        finish();
    }

    public void onClickGoToLogin(View view) {
        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}