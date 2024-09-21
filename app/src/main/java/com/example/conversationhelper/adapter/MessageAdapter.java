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

import java.util.List;

public class MessageAdapter extends ArrayAdapter<String> {

    public MessageAdapter(Context context, List<String> messages) {
        super(context, R.layout.list_item_message, messages);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        String text = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_message, parent, false);
        }

        TextView messageText = convertView.findViewById(R.id.message_text);
        messageText.setText(text);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) messageText.getLayoutParams();
        float scale = getContext().getResources().getDisplayMetrics().density;
        int marginInPx = (int) (50 * scale + 0.5f);

        if (position % 2 == 0) {
            params.gravity = Gravity.START;
            params.setMargins(0, 0, marginInPx, 0);
            messageText.setBackgroundResource(R.drawable.round_user_message);
        } else {
            params.gravity = Gravity.END;
            params.setMargins(marginInPx, 0, 0, 0);
            messageText.setBackgroundResource(R.drawable.round_chatgpt_message);
        }

        messageText.setLayoutParams(params);
        return convertView;
    }
}
