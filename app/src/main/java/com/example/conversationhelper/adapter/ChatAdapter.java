package com.example.conversationhelper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.conversationhelper.R;
import com.example.conversationhelper.db.model.Chat;
import com.example.conversationhelper.db.repository.ChatRepository;
import com.example.conversationhelper.db.repository.ResultRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Locale;

public class ChatAdapter extends ArrayAdapter<Chat> {

    private final ChatRepository chatRepository;
    private final ResultRepository resultRepository;

    public ChatAdapter(Context context, List<Chat> chats) {
        super(context, R.layout.list_item_chat, chats);
        chatRepository = new ChatRepository(FirebaseFirestore.getInstance());
        resultRepository = new ResultRepository(FirebaseFirestore.getInstance());
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Chat chat = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_chat, parent, false);
        }

        final View finalConvertView = convertView;

        TextView name = finalConvertView.findViewById(R.id.chat_name);
        TextView description = finalConvertView.findViewById(R.id.chat_description);

        if (chat != null) {
            if (chat.isStatus()) {
                resultRepository.getSuccessByChatId(chat.getId())
                        .thenAccept(aBoolean -> {
                            ImageView status = finalConvertView.findViewById(R.id.chat_status);
                            if (aBoolean) status.setImageResource(R.drawable.baseline_done_24);
                            else status.setImageResource(R.drawable.baseline_do_disturb_24);
                        });
            }

            name.setText(String.format("%s %s", chat.getDifficulty(), chat.getSpecialization()));
            description.setText(String.format(Locale.getDefault(), "Количество вопросов: %d\nЯзык общения: %s",
                    chat.getNumberQuestions(),
                    chat.getLanguage()));

            finalConvertView.findViewById(R.id.delete_button).setOnClickListener(view -> {
                chatRepository.deleteChatById(chat.getId());
                remove(chat);
                notifyDataSetChanged();
            });
        }

        return finalConvertView;
    }

}