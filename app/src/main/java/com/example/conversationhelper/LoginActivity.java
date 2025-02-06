package com.example.conversationhelper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.conversationhelper.auth.Authentication;
import com.example.conversationhelper.auth.MailSender;
import com.example.conversationhelper.auth.SharedPreferencesUtil;
import com.example.conversationhelper.db.repository.UserRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private UserRepository userRepository;
    private EditText editName, editPassword;
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

        findViewById(R.id.login_button).setEnabled(false);
        userRepository.getUserByName(name)
                .thenAccept(user -> {
                    findViewById(R.id.login_button).setEnabled(true);
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

    public void onClickGoToForgotPass(View view) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_forgot_password, null);

        EditText editTextEmail = dialogView.findViewById(R.id.editTextEmail);
        TextView errorText = dialogView.findViewById(R.id.dialog_error);

        AlertDialog dialog = new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
                .setTitle("Восстановление пароля")
                .setView(dialogView)
                .setPositiveButton("Ок", null)
                .setNegativeButton("Отмена", (dialogInterface, which) -> dialogInterface.dismiss())
                .create();

        dialog.setOnShowListener(d -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(newView -> {
                String email = editTextEmail.getText().toString();

                String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
                if (!Pattern.compile(emailPattern).matcher(email).matches()) {
                    errorText.setText("Неправильныый формат электроной почты");
                    return;
                }

                userRepository.getUserByEmail(email)
                        .thenAccept(user -> {
                            if (user != null) {
                                MailSender mailSender = new MailSender();
                                mailSender.sendEmail(user.getEmail(), "Восстановление пароля", "Ваш пароль " + user.getPassword());

                                dialog.dismiss();
                                showCustomToast((ViewGroup) view.getRootView());
                            } else {
                                errorText.setText("Такой пользователь уже существует");
                            }
                        });
            });
        });

        dialog.show();
    }

    private void showCustomToast(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout = inflater.inflate(R.layout.custom_toast, parent, false);

        TextView toastMessage = layout.findViewById(R.id.toast_message);
        toastMessage.setText("Проверьте вашу почту");

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 200);
        toast.show();
    }
}