package com.example.conversationhelper.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.conversationhelper.R;
import com.example.conversationhelper.db.model.Message;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {

    public MessageAdapter(Context context, List<Message> messages) {
        super(context, R.layout.list_item_message, messages);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        Message message = getItem(position);
        if (message != null) {
            if (message.getType().equals("system")) {
                return new View(getContext());
            }
            String text = message.getContent();

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_message, parent, false);
            }

            TextView messageText = convertView.findViewById(R.id.message_text);
            messageText.setText(text);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) messageText.getLayoutParams();
            float scale = getContext().getResources().getDisplayMetrics().density;
            int marginInPx = (int) (50 * scale + 0.5f);

            if (message.getType().equals("user")) {
                params.gravity = Gravity.START;
                params.setMargins(0, 0, marginInPx, 0);
                messageText.setBackgroundResource(R.drawable.round_user_message);
            } else {
                params.gravity = Gravity.END;
                params.setMargins(marginInPx, 0, 0, 0);
                messageText.setBackgroundResource(R.drawable.round_chatgpt_message);
            }

            messageText.setLayoutParams(params);
        }

        return convertView;
    }
}
