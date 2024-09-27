package com.example.conversationhelper.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_message, parent, false);
        }

        TextView messageText = convertView.findViewById(R.id.message_text);
        TextView messageTime = convertView.findViewById(R.id.message_time);

        if (message != null) {
            String text = message.getContent();

            messageText.setText(text);
            messageTime.setText(message.getCreateTime().toDate().toString().substring(11, 16));

            LinearLayout.LayoutParams textParams = (LinearLayout.LayoutParams) messageText.getLayoutParams();
            LinearLayout.LayoutParams timeParams = (LinearLayout.LayoutParams) messageTime.getLayoutParams();

            float scale = getContext().getResources().getDisplayMetrics().density;
            int marginInPx = (int) (50 * scale + 0.5f);

            if (message.getType().equals("user")) {
                textParams.gravity = Gravity.END;
                textParams.setMargins(marginInPx, 0, 0, 0);
                messageText.setBackgroundResource(R.drawable.round_chatgpt_message);

                timeParams.gravity = Gravity.END;
            } else {
                textParams.gravity = Gravity.START;
                textParams.setMargins(0, 0, marginInPx, 0);
                messageText.setBackgroundResource(R.drawable.round_user_message);

                timeParams.gravity = Gravity.START;
            }

            messageText.setLayoutParams(textParams);
            messageTime.setLayoutParams(timeParams);

            messageText.setOnLongClickListener(view -> {
                copyToClipboard(text);
                showCustomToast(parent);
                return true;
            });
        }

        return convertView;
    }

    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Скопированный текст", text);
        clipboard.setPrimaryClip(clip);
    }

    private void showCustomToast(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View layout = inflater.inflate(R.layout.custom_toast, parent);

        TextView toastMessage = layout.findViewById(R.id.toast_message);
        toastMessage.setText("Текст скопирован");

        Toast toast = new Toast(getContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 200);
        toast.show();
    }
}
