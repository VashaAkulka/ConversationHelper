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
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.conversationhelper.adapter.MessageAdapter;
import com.example.conversationhelper.auth.Authentication;
import com.example.conversationhelper.db.model.Chat;
import com.example.conversationhelper.db.model.Message;
import com.example.conversationhelper.db.repository.ChatRepository;
import com.example.conversationhelper.db.repository.MessageRepository;
import com.example.conversationhelper.db.repository.ResultRepository;
import com.example.conversationhelper.gpt.ChatGptCallback;
import com.example.conversationhelper.gpt.ChatGptClient;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessengerActivity extends AppCompatActivity {

    private final List<Message> messages = new ArrayList<>();
    private MessageAdapter adapter;
    private EditText editMessage;
    private ListView messageHistory;
    private ActivityResultLauncher<Intent> speechRecognizerLauncher;
    private MessageRepository messageRepository;
    private ResultRepository resultRepository;
    private ChatRepository chatRepository;
    private Chat chat;
    private ImageButton speechButton, sendButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);

        Intent intent = getIntent();
        chat = (Chat) intent.getSerializableExtra("CHAT");
        messageRepository = new MessageRepository(FirebaseFirestore.getInstance());
        resultRepository = new ResultRepository(FirebaseFirestore.getInstance());
        chatRepository = new ChatRepository(FirebaseFirestore.getInstance());

        messageHistory = findViewById(R.id.message_history);
        editMessage = findViewById(R.id.edit_message);
        speechButton = findViewById(R.id.speech_button);
        sendButton = findViewById(R.id.send_button);

        if (chat.isStatus()) {
            editMessage.setVisibility(View.GONE);
            speechButton.setVisibility(View.GONE);
            sendButton.setVisibility(View.GONE);
        }

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

        sendMessageWithRetries(chat, messages, 3);
    }

    private void sendMessageWithRetries(Chat chat, List<Message> messages, int retries) {
        ChatGptClient.send(chat, messages, new ChatGptCallback() {
            @Override
            public void onSuccess(String result) {
                String regex = "(?<=Ваш результат: |Your result: )\\d+";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(result);

                messages.add(messageRepository.addMessage(result, chat.getId(), "assistant"));

                adapter.notifyDataSetChanged();
                messageHistory.setSelection(adapter.getCount() - 1);

                if (matcher.find()) {
                    String numberString = matcher.group();
                    int number = Integer.parseInt(numberString);
                    resultRepository.addResult(Authentication.getUser().getId(), chat.getId(), number);

                    chat.setStatus(true);
                    chatRepository.updateChatStatusById(chat.getId());

                    editMessage.setVisibility(View.GONE);
                    speechButton.setVisibility(View.GONE);
                    sendButton.setVisibility(View.GONE);
                } else editMessage.setEnabled(true);
            }

            @Override
            public void onError(Exception e) {
                if (retries > 0) {
                    sendMessageWithRetries(chat, messages, retries - 1);
                } else {
                    editMessage.setText("Ошибка: Соединение устройства не устойчивое, повторите попытку");
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