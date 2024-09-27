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
import com.example.conversationhelper.db.model.Chat;
import com.example.conversationhelper.db.model.Message;
import com.example.conversationhelper.db.repository.MessageRepository;
import com.example.conversationhelper.gpt.ChatGptCallback;
import com.example.conversationhelper.gpt.ChatGptClient;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MessengerActivity extends AppCompatActivity {

    private List<Message> messages;
    private MessageAdapter adapter;
    private EditText editMessage;
    private ListView messageHistory;
    private ActivityResultLauncher<Intent> speechRecognizerLauncher;
    private MessageRepository messageRepository;
    private Chat chat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);

        Intent intent = getIntent();
        chat = (Chat) intent.getSerializableExtra("CHAT");
        messageRepository = new MessageRepository(FirebaseFirestore.getInstance());

        messageHistory = findViewById(R.id.message_history);
        editMessage = findViewById(R.id.edit_message);

        messages = new ArrayList<>();
        messageRepository.getMessageByChatId(chat.getId())
                        .thenAccept(list -> {
                            messages.addAll(list);
                            adapter = new MessageAdapter(this, messages);
                            messageHistory.setAdapter(adapter);
                        });

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
        editMessage.setText("");

        messages.add(messageRepository.addMessage(messageContent, chat.getId(), "user"));

        adapter.notifyDataSetChanged();
        messageHistory.setSelection(adapter.getCount() - 1);

        sendMessageWithRetries(chat, messages, messageContent, 3);
    }

    private void sendMessageWithRetries(Chat chat, List<Message> messages, String messageContent, int retries) {
        ChatGptClient.send(chat, messages, new ChatGptCallback() {
            @Override
            public void onSuccess(String result) {
                messages.add(messageRepository.addMessage(result, chat.getId(), "assistant"));

                adapter.notifyDataSetChanged();
                messageHistory.setSelection(adapter.getCount() - 1);
                editMessage.setEnabled(true);
            }

            @Override
            public void onError(Exception e) {
                if (retries > 0) {
                    sendMessageWithRetries(chat, messages, messageContent, retries - 1);
                } else {
                    editMessage.setText("Error: Соединение устройства не устойчивое, повторите попытку");
                    editMessage.setEnabled(true);
                }
            }
        });
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