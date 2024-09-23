package com.example.conversationhelper;

import static com.example.conversationhelper.db.repository.ChatRepository.getInstance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.conversationhelper.auth.Authentication;
import com.example.conversationhelper.db.repository.ChatRepository;

public class SettingChatActivity extends AppCompatActivity {

    private RadioGroup radioGroupDiff;
    private RadioGroup radioGroupLanguage;
    private TextView errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_chat);

        radioGroupDiff = findViewById(R.id.radio_group_diff);
        radioGroupLanguage = findViewById(R.id.radio_group_language);
        errorMessage = findViewById(R.id.error_text);
    }

    public void onClickContinue(View view) {
        String spec = ((EditText)findViewById(R.id.edit_spec)).getText().toString();
        String questions = ((EditText)findViewById(R.id.edit_number_question)).getText().toString();
        int checkedIdDiff = radioGroupDiff.getCheckedRadioButtonId();
        int checkedIdLanguage = radioGroupLanguage.getCheckedRadioButtonId();

        if (checkedIdDiff == -1 || checkedIdLanguage == -1 || spec.isEmpty() || questions.isEmpty()) {
            errorMessage.setText("Все поля обязательны");
            return;
        }

        String diff = ((RadioButton)findViewById(checkedIdDiff)).getText().toString();
        String language = ((RadioButton)findViewById(checkedIdLanguage)).getText().toString();

        Intent intent = new Intent(SettingChatActivity.this, MessengerActivity.class);
        ChatRepository chatRepository = getInstance(getApplicationContext());

        intent.putExtra("CHAT-ID", chatRepository.addChat(diff,spec,language,Integer.parseInt(questions), Authentication.getUser().getId()).getId());

        startActivity(intent);
        finish();
    }

    public void onClickComeBack(View view) {
        finish();
    }
}