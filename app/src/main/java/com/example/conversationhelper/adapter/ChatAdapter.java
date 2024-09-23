package com.example.conversationhelper.adapter;

import static com.example.conversationhelper.db.repository.ChatRepository.getInstance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.conversationhelper.R;
import com.example.conversationhelper.db.model.Chat;
import com.example.conversationhelper.db.repository.ChatRepository;

import java.util.List;
import java.util.Locale;

public class ChatAdapter extends ArrayAdapter<Chat> {

    ChatRepository chatRepository;

    public ChatAdapter(Context context, List<Chat> chats) {
        super(context, R.layout.list_item_chat, chats);
        chatRepository = getInstance(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Chat chat = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_chat, parent, false);
        }

        TextView name = convertView.findViewById(R.id.chat_name);
        TextView description = convertView.findViewById(R.id.chat_description);

        if (chat != null) {
            name.setText(String.format("%s %s", chat.getDifficulty(), chat.getSpecialization()));
            description.setText(String.format(Locale.getDefault(), "Количество вопросов: %d\nСтатус: %s\nЯзык общения: %s",
                    chat.getNumberQuestions(),
                    chat.getStatus() == 0 ? "в процессе" : "завершен",
                    chat.getLanguage()));
        }

        convertView.findViewById(R.id.delete_button).setOnClickListener(view -> {
            if (chat != null) {
                chatRepository.deleteChatById(chat.getId());
                remove(chat);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }
}