package com.example.conversationhelper;

import static com.example.conversationhelper.db.repository.ChatRepository.getInstance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.example.conversationhelper.adapter.ChatAdapter;
import com.example.conversationhelper.auth.Authentication;
import com.example.conversationhelper.db.model.Chat;
import com.example.conversationhelper.db.repository.ChatRepository;

import java.util.List;

public class ListChatsActivity extends AppCompatActivity {

    private ChatRepository chatRepository;
    ChatAdapter adapter;
    List<Chat> chatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_chats);

        chatRepository = getInstance(getApplicationContext());
        ListView listChat = findViewById(R.id.list_chat);
        chatList = chatRepository.getAllChatsByUserId(Authentication.getUser().getId());
        adapter = new ChatAdapter(this, chatList);
        listChat.setAdapter(adapter);

        listChat.setOnItemClickListener((adapterView, view, i, l) -> {
            Chat selectedChat = adapter.getItem(i);
            if (selectedChat != null) {
                Intent intent = new Intent(ListChatsActivity.this, MessengerActivity.class);
                intent.putExtra("CHAT", selectedChat);
                startActivity(intent);
            }
        });
    }

    public void onClickAddChat(View view) {
        Intent intent = new Intent(ListChatsActivity.this, SettingChatActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        chatList.clear();
        chatList.addAll(chatRepository.getAllChatsByUserId(Authentication.getUser().getId()));
        adapter.notifyDataSetChanged();
    }
}