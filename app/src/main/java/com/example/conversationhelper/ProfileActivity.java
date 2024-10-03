package com.example.conversationhelper;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.conversationhelper.auth.Authentication;
import com.example.conversationhelper.auth.SharedPreferencesUtil;
import com.example.conversationhelper.db.model.User;
import com.example.conversationhelper.db.repository.ChatRepository;
import com.example.conversationhelper.db.repository.ResultRepository;
import com.example.conversationhelper.db.repository.UserRepository;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class ProfileActivity extends AppCompatActivity {

    private ImageView avatar;
    private TextView labelName, labelEmail;
    private UserRepository userRepository;
    private ResultRepository resultRepository;
    private ChatRepository chatRepository;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        userRepository = new UserRepository(FirebaseFirestore.getInstance());
        resultRepository = new ResultRepository(FirebaseFirestore.getInstance());
        chatRepository = new ChatRepository(FirebaseFirestore.getInstance());

        avatar = findViewById(R.id.image_avatar);
        labelName = findViewById(R.id.profile_name);
        labelEmail = findViewById(R.id.profile_email);

        labelName.setText(Authentication.getUser().getName());
        labelEmail.setText(Authentication.getUser().getEmail());

        if (Authentication.getUser().getAvatar() != null) {
            String avatarUrlString = Authentication.getUser().getAvatar();

            Glide.with(this)
                    .load(avatarUrlString)
                    .apply(new RequestOptions()
                            .centerCrop()
                            .circleCrop())
                    .into(avatar);
        }

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            Glide.with(this)
                                    .load(imageUri)
                                    .apply(new RequestOptions()
                                            .centerCrop()
                                            .circleCrop())
                                    .into(avatar);

                            String userId = Authentication.getUser().getId();
                            StorageReference avatarRef = storageReference.child("avatars/" + userId + ".jpg");

                            UploadTask uploadTask = avatarRef.putFile(imageUri);
                            uploadTask.addOnSuccessListener(taskSnapshot -> avatarRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                                String imageUrlString = downloadUri.toString();

                                Authentication.getUser().setAvatar(imageUrlString);
                                userRepository.updateUser(Authentication.getUser());
                            }));
                        }
                    }
                });

        createPieDiagram();
        createBarDiagram();
    }

    private void createBarDiagram() {
        BarChart barChart = findViewById(R.id.time_stats);
        ArrayList<BarEntry> barEntries = new ArrayList<>();

        resultRepository.getTimeByUserId(Authentication.getUser().getId())
                        .thenAccept(timeList -> {
                            for (int i = 0; i < timeList.size(); i++) {
                                barEntries.add(new BarEntry(i + 1, timeList.get(i)));
                            }
                            bindBarDiagram(barEntries, barChart);
                        });
    }

    private void bindBarDiagram(ArrayList<BarEntry> barEntries, BarChart barChart) {
        if (barEntries.isEmpty()) return;

        int barColor = ContextCompat.getColor(this, R.color.message_background_color);
        int textColor = ContextCompat.getColor(this, R.color.text_color);

        BarDataSet barDataSet = new BarDataSet(barEntries, "Sample Data");
        barDataSet.setColor(barColor);
        barDataSet.setValueTextColor(textColor);
        barDataSet.setValueTextSize(14f);

        BarData barData = new BarData(barDataSet);
        barData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.getDefault(), "%.1f", value);
            }
        });
        barChart.setData(barData);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setTextSize(12f);
        leftAxis.setTextColor(textColor);

        barChart.setDescription(null);
        barChart.getLegend().setEnabled(false);
        barChart.setScaleEnabled(false);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getXAxis().setDrawAxisLine(false);
        barChart.getAxisLeft().setDrawAxisLine(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getXAxis().setEnabled(false);
        barChart.animateY(3000);
        barChart.invalidate();

        barChart.setVisibility(View.VISIBLE);
        findViewById(R.id.bar_chart_title).setVisibility(View.VISIBLE);
        findViewById(R.id.profile_place_holder).setVisibility(View.GONE);
    }

    private void createPieDiagram() {
        PieChart answerDiagram = findViewById(R.id.answer_stats);
        ArrayList<PieEntry> answerEntries = new ArrayList<>();
        resultRepository.getCountRightAnswerByUserId(Authentication.getUser().getId())
                        .thenAccept(countRight -> {
                            answerEntries.add(new PieEntry(countRight, "Правильно"));

                            chatRepository.getCountQuestionByUserId(Authentication.getUser().getId())
                                    .thenAccept(countQuestion -> {
                                        answerEntries.add(new PieEntry(countQuestion, "Неправильно"));
                                        bindPieDiagram(answerDiagram, answerEntries, "Ответы");
                                    });
                        });


        PieChart chatDiagram = findViewById(R.id.complete_chat_stats);
        ArrayList<PieEntry> chatEntries = new ArrayList<>();
        chatRepository.getCountCompleteChat(Authentication.getUser().getId())
                .thenAccept(pair -> {
                    chatEntries.add(new PieEntry(pair.first, "Завершены"));
                    chatEntries.add(new PieEntry(pair.second, "В процессе"));

                    bindPieDiagram(chatDiagram, chatEntries, "Чаты");
                });

    }

    private void bindPieDiagram(PieChart pieChart, List<PieEntry> entries, String text) {
        if (entries.get(0).getValue() == 0 && entries.get(1).getValue() == 0) return;

        PieDataSet dataSet = new PieDataSet(entries, "Labels");
        int correctColor = ContextCompat.getColor(this, R.color.correct);
        int incorrectColor = ContextCompat.getColor(this, R.color.incorrect);
        int textColor = ContextCompat.getColor(this, R.color.text_color);
        dataSet.setColors(correctColor, incorrectColor);
        dataSet.setValueTextColor(textColor);
        dataSet.setValueTextSize(14f);

        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.getDefault(), "%.0f", value);
            }
        });

        pieChart.setDescription(null);
        pieChart.setCenterText(text);
        pieChart.setCenterTextColor(textColor);
        pieChart.setCenterTextSize(14f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setTransparentCircleRadius(0f);
        pieChart.getLegend().setEnabled(false);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setData(pieData);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setEntryLabelColor(textColor);
        pieChart.invalidate();

        findViewById(R.id.profile_place_holder).setVisibility(View.GONE);
        pieChart.setVisibility(View.VISIBLE);
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
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        }
    }

    public void profileButton(String text) {
        new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
                .setTitle("Подтверждение")
                .setMessage("Вы уверены, что хотите " + text + " пользователя?")
                .setPositiveButton("Да", (dialogInterface, i) -> {
                    if (text.equals("удалить")) userRepository.deleteUserById(Authentication.getUser().getId(), FirebaseStorage.getInstance());

                    Intent intent = new Intent(ProfileActivity.this, RegistrationActivity.class);

                    SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(this);
                    sharedPreferencesUtil.deleteUser();

                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Нет", (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }


    public void updateUserData() {
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
                String newName = editTextName.getText().toString();
                String newEmail = editTextEmail.getText().toString();
                String newPassword = editTextPassword.getText().toString();

                String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
                if (!Pattern.compile(emailPattern).matcher(newEmail).matches()) {
                    errorText.setText("Неправильныый формат электроной почты");
                    return;
                }

                if (newPassword.length() < 6) {
                    errorText.setText("Слишком легкий пароль");
                    return;
                }

                User user = Authentication.getUser();
                User newUser = new User(user.getId(), user.getRole(), newName, newPassword, newEmail, user.getAvatar());

                userRepository.updateUser(newUser)
                        .thenAccept(aBoolean -> {
                            if (aBoolean || newUser.getName().equals(user.getName())) {
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
        });

        dialog.show();
    }

    public void onClickPopupMenu(View view) {
        Context wrapper = new ContextThemeWrapper(this, R.style.CustomPopupMenu);
        PopupMenu popupMenu = new PopupMenu(wrapper, view);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.option1) {
                profileButton("сменить");
            } else if (item.getItemId() == R.id.option2) {
                updateUserData();
            } else if (item.getItemId() == R.id.option3) {
                profileButton("удалить");
            }
            return true;
        });

        popupMenu.show();
    }

    public void onClickBackActivity(View view) {
        finish();
    }
}