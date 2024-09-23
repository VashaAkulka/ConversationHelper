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
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.example.conversationhelper.adapter.MessageAdapter;
import com.example.conversationhelper.db.Database;
import com.example.conversationhelper.db.model.Message;
import com.example.conversationhelper.gpt.ChatGptCallback;
import com.example.conversationhelper.gpt.ChatGptClient;

import java.util.ArrayList;
import java.util.List;

public class MessengerActivity extends AppCompatActivity {

    private List<Message> messages;
    private MessageAdapter adapter;
    private EditText editMessage;
    private ListView messageHistory;
    private ActivityResultLauncher<Intent> speechRecognizerLauncher;
    private Database database;
    private int chatId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);

        Intent intent = getIntent();
        chatId = intent.getIntExtra("CHAT-ID", 0);
        database = Database.getInstance(getApplicationContext());

        messageHistory = findViewById(R.id.message_history);
        editMessage = findViewById(R.id.edit_message);

        messages = new ArrayList<>();
        messages.addAll(database.getMessageContentByChatId(chatId));
        adapter = new MessageAdapter(this, messages);
        messageHistory.setAdapter(adapter);


        speechRecognizerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        ArrayList<String> speechResult = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        if (speechResult != null && !speechResult.isEmpty()) {
                            editMessage.setText(speechResult.get(0));
                        }
                    }
                }
        );
    }

    public void onClickSenderButton(View view) {
        String messageContent = editMessage.getText().toString();
        if (messageContent.equals("")) return;
        editMessage.setEnabled(false);

        messages.add(database.addMessage(messageContent, chatId, "user"));

        adapter.notifyDataSetChanged();
        messageHistory.setSelection(adapter.getCount() - 1);

        ChatGptClient.send(messages, new ChatGptCallback() {
            @Override
            public void onSuccess(String result) {
                messages.add(database.addMessage(result, chatId, "assistant"));

                adapter.notifyDataSetChanged();
                messageHistory.setSelection(adapter.getCount() - 1);
                editMessage.setEnabled(true);
            }

            @Override
            public void onError(Exception e) {
                throw new RuntimeException(e.getCause());
            }
        });

        editMessage.setText("");
    }

    public void onClickSpeechButton(View view) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        } else {
            startSpeechToText();
        }
    }

    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ru-RU");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Говорите...");

        speechRecognizerLauncher.launch(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startSpeechToText();
        }
    }
}