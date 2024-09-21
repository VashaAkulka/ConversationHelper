package com.example.conversationhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ListChatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_chats);
    }

    public void onClickAddChat(View view) {
        Intent intent = new Intent(ListChatsActivity.this, SettingChatActivity.class);
        startActivity(intent);
    }
}