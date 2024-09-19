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
        super(context, R.layout.list_item, messages);
        this.context = context;
        this.messages = messages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item, parent, false);

        TextView messageText = rowView.findViewById(R.id.message_text);
        messageText.setText(messages.get(position));

        if (position % 2 == 0) {
            messageText.setGravity(Gravity.START);
        } else {
            messageText.setGravity(Gravity.END);
        }

        return rowView;
    }


}
