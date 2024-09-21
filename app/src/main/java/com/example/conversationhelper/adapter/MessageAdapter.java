package com.example.conversationhelper.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.conversationhelper.R;

import java.util.ArrayList;

public class MessageAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> messages;
    public MessageAdapter(Context context, ArrayList<String> messages) {
        super(context, R.layout.list_item_message, messages);
        this.context = context;
        this.messages = messages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_message, parent, false);

        TextView messageText = rowView.findViewById(R.id.message_text);
        messageText.setText(messages.get(position));

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) messageText.getLayoutParams();
        float scale = context.getResources().getDisplayMetrics().density;
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
        return rowView;
    }




}
