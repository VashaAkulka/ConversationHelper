package com.example.conversationhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.example.conversationhelper.adapter.ChatAdapter;
import com.example.conversationhelper.auth.Authentication;
import com.example.conversationhelper.db.model.Chat;
import com.example.conversationhelper.db.repository.ChatRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ListChatsActivity extends AppCompatActivity {

    private ChatRepository chatRepository;
    private ChatAdapter adapter;
    private final List<Chat> chatList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_chats);

        chatRepository = new ChatRepository(FirebaseFirestore.getInstance());
        ListView listChat = findViewById(R.id.list_chat);
        adapter = new ChatAdapter(this, chatList);
        listChat.setAdapter(adapter);

        loadChats();

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
        loadChats();
    }

    private void loadChats() {
        chatRepository.getAllChatsByUserId(Authentication.getUser().getId())
                .thenAccept(list -> {
                    chatList.clear();
                    chatList.addAll(list);
                    adapter.notifyDataSetChanged();
                });
    }

    public void onClickToProfile(View view) {
        Intent intent = new Intent(ListChatsActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    public void onClickToArticle(View view) {
        Intent intent = new Intent(ListChatsActivity.this, ArticleListActivity.class);
        startActivity(intent);
    }
}