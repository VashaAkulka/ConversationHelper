package com.example.conversationhelper;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.conversationhelper.auth.Authentication;
import com.example.conversationhelper.db.model.User;
import com.example.conversationhelper.db.repository.UserRepository;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ProfileActivity extends AppCompatActivity {

    ImageView avatar;
    TextView labelName;
    TextView labelEmail;
    UserRepository userRepository;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userRepository = UserRepository.getInstance(getApplicationContext());

        avatar = findViewById(R.id.image_avatar);
        labelName = findViewById(R.id.profile_name);
        labelEmail = findViewById(R.id.profile_email);

        labelName.setText(Authentication.getUser().getName());
        labelEmail.setText(Authentication.getUser().getEmail());

        if (Authentication.getUser().getAvatar() != null) {
            byte[] avatarBytes = Authentication.getUser().getAvatar();
            Bitmap avatarBitmap = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.length);
            avatar.setImageBitmap(avatarBitmap);
        }

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream imageStream = getContentResolver().openInputStream(imageUri);
                            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                            avatar.setImageBitmap(selectedImage);

                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            selectedImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                            byte[] imageBytes = byteArrayOutputStream.toByteArray();

                            Authentication.getUser().setAvatar(imageBytes);
                            userRepository.updateUser(Authentication.getUser());
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });


    }

    public void onClickOpenGallery(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 1);
            } else {
                openGallery();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                openGallery();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        }
    }

    public void onClickProfileButton(View view) {
        if (R.id.delete_user_button == view.getId()) userRepository.deleteUserById(Authentication.getUser().getId());
        Intent intent = new Intent(ProfileActivity.this, RegistrationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    public void onClickUpdateUserData(View view) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_update_user, null);

        EditText editTextName = dialogView.findViewById(R.id.editTextName);
        EditText editTextEmail = dialogView.findViewById(R.id.editTextEmail);
        EditText editTextPassword = dialogView.findViewById(R.id.editTextPass);

        TextView errorText = dialogView.findViewById(R.id.dialog_error);

        editTextName.setText(Authentication.getUser().getName());
        editTextEmail.setText(Authentication.getUser().getEmail());
        editTextPassword.setText(Authentication.getUser().getPassword());

        AlertDialog dialog = new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
                .setTitle("Смена личных данных")
                .setView(dialogView)
                .setPositiveButton("Обновить", null)
                .setNegativeButton("Отмена", (dialogInterface, which) -> dialogInterface.dismiss())
                .create();

        dialog.setOnShowListener(d -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(newView -> {
                String newName = editTextName.getText().toString().trim();
                String newEmail = editTextEmail.getText().toString().trim();
                String newPassword = editTextPassword.getText().toString().trim();

                User user = Authentication.getUser();
                User newUser = new User(user.getId(), user.getRole(), newName, newPassword, newEmail, user.getAvatar());

                if (userRepository.updateUser(newUser)) {
                    user.setName(newName);
                    user.setEmail(newEmail);
                    user.setPassword(newPassword);

                    labelName.setText(Authentication.getUser().getName());
                    labelEmail.setText(Authentication.getUser().getEmail());
                    dialog.dismiss();
                } else {
                    errorText.setText("Такой пользователь уже существует");
                }
            });
        });

        dialog.show();
    }
}