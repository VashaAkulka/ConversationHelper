package com.example.conversationhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.example.conversationhelper.adapter.MessageAdapter;
import com.example.conversationhelper.gpt.ChatGptCallback;
import com.example.conversationhelper.gpt.ChatGptClient;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> messages;
    private MessageAdapter adapter;
    private EditText editMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView messageHistory = findViewById(R.id.message_history);
        editMessage = findViewById(R.id.edit_message);
        messages = new ArrayList<>();
        adapter = new MessageAdapter(this, messages);
        messageHistory.setAdapter(adapter);
    }

    public void onCLick(View view) {
        String message = editMessage.getText().toString();
        messages.add(message);
        adapter.notifyDataSetChanged();

        ChatGptClient.send(message, new ChatGptCallback() {
            @Override
            public void onSuccess(String result) {
                messages.add(result);
                runOnUiThread(() -> adapter.notifyDataSetChanged());
            }

            @Override
            public void onError(Exception e) {
                messages.add("Error: " + e.toString());
                runOnUiThread(() -> adapter.notifyDataSetChanged());
            }
        });

        editMessage.setText("");
    }
}