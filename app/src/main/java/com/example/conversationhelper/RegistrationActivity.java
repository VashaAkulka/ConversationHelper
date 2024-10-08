package com.example.conversationhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.conversationhelper.auth.Authentication;
import com.example.conversationhelper.auth.SharedPreferencesUtil;
import com.example.conversationhelper.db.model.User;
import com.example.conversationhelper.db.repository.UserRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {
    private UserRepository userRepository;
    private EditText editName, editPassword, editPasswordRepeat, editEmail;
    private TextView error;
    private SharedPreferencesUtil sharedPreferencesUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        userRepository = new UserRepository(FirebaseFirestore.getInstance());
        sharedPreferencesUtil = new SharedPreferencesUtil(this);

        User authUser = sharedPreferencesUtil.loadUser();
        if (authUser != null) {
            Authentication.setUser(authUser);

            Intent intent = new Intent(RegistrationActivity.this, ListChatsActivity.class);
            startActivity(intent);
            finish();
        }

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

        userRepository.getUserByName(name)
                .thenAccept(user -> {
                    if (user != null) {
                        error.setText("Такой пользователь уже существует");
                        return;
                    }

                    User authUser = userRepository.addUser(name, email, password);
                    Authentication.setUser(authUser);

                    sharedPreferencesUtil.saveUser(authUser);

                    Intent intent = new Intent(RegistrationActivity.this, ListChatsActivity.class);
                    startActivity(intent);
                    finish();
                });
    }

    public void onClickGoToLogin(View view) {
        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void OnClickShowPassword(View view) {
        CheckBox checkBox = (CheckBox) view;
        if (checkBox.isChecked()) {
            editPassword.setTransformationMethod(null);
            editPasswordRepeat.setTransformationMethod(null);
        } else {
            editPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            editPasswordRepeat.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }

        editPassword.setSelection(editPassword.getText().length());
        editPasswordRepeat.setSelection(editPasswordRepeat.getText().length());
    }
}