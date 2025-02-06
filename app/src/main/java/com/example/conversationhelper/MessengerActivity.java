package com.example.conversationhelper;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.conversationhelper.adapter.MessageAdapter;
import com.example.conversationhelper.auth.Authentication;
import com.example.conversationhelper.db.MessageType;
import com.example.conversationhelper.db.model.Chat;
import com.example.conversationhelper.db.model.Message;
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
    private RecyclerView messageHistory;
    private ActivityResultLauncher<Intent> speechRecognizerLauncher;
    private MessageRepository messageRepository;
    private ResultRepository resultRepository;
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

        messageHistory = findViewById(R.id.message_history);
        editMessage = findViewById(R.id.edit_message);
        speechButton = findViewById(R.id.speech_button);
        sendButton = findViewById(R.id.send_button);

        resultRepository.getSuccessByChatId(chat.getId()).thenAccept(aBoolean -> {
            if (aBoolean != null) {
                editMessage.setVisibility(View.GONE);
                speechButton.setVisibility(View.GONE);
                sendButton.setVisibility(View.GONE);
            }
        });

        messageRepository.getMessageByChatId(chat.getId())
                        .thenAccept(list -> {
                            messages.addAll(list);
                            adapter = new MessageAdapter(messages, this);
                            messageHistory.setLayoutManager(new LinearLayoutManager(this));
                            messageHistory.setAdapter(adapter);
                            messageHistory.scrollToPosition(adapter.getItemCount() - 1);
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

        messages.add(messageRepository.addMessage(messageContent, chat.getId(), MessageType.user));

        adapter.notifyItemInserted(messages.size() - 1);
        messageHistory.scrollToPosition(adapter.getItemCount() - 1);

        sendMessageWithRetries(chat, messages, 3);
    }

    private void sendMessageWithRetries(Chat chat, List<Message> messages, int retries) {
        ChatGptClient.send(chat, messages, new ChatGptCallback() {
            @Override
            public void onSuccess(String result) {
                String regexResult = "(?<=Ваш результат: |Your result: )\\d+";
                String regexStatus = "(Пройдено успешно|Пройдено неудачно|Passed successfully|Passed unsuccessfully)";

                Pattern patternResult = Pattern.compile(regexResult);
                Matcher matcherResult = patternResult.matcher(result);

                Pattern patternStatus = Pattern.compile(regexStatus);
                Matcher matcherStatus = patternStatus.matcher(result);

                messages.add(messageRepository.addMessage(result, chat.getId(), MessageType.assistant));

                adapter.notifyItemInserted(messages.size() - 1);
                messageHistory.scrollToPosition(adapter.getItemCount() - 1);

                if (matcherResult.find() && matcherStatus.find()) {
                    String numberString = matcherResult.group();
                    int number = Integer.parseInt(numberString);

                    String status = matcherStatus.group();
                    boolean isSuccessful = status.equals("Пройдено успешно") || status.equals("Passed successfully");

                    resultRepository.addResult(Authentication.getUser().getId(), chat.getId(), number, isSuccessful);

                    editMessage.setVisibility(View.GONE);
                    speechButton.setVisibility(View.GONE);
                    sendButton.setVisibility(View.GONE);
                } else editMessage.setEnabled(true);
            }

            @Override
            public void onError(Throwable e) {
                if (retries > 0) {
                    sendMessageWithRetries(chat, messages, retries - 1);
                } else {
                    int size = messages.size() - 1;
                    messages.get(size).setContent("Ошибка соединения, пожалуйста повторите попытку чуть позже.");
                    messages.get(size).setType(MessageType.error);
                    messageRepository.updateMessage(messages.get(size));
                    editMessage.setEnabled(true);
                    adapter.notifyItemInserted(messages.size() - 1);
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

    public void onClickBackActivity(View view) {
        finish();
    }
}