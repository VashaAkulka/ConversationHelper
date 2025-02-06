package com.example.conversationhelper.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.conversationhelper.R;
import com.example.conversationhelper.db.MessageType;
import com.example.conversationhelper.db.model.Message;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private final List<Message> messages;
    private final Context context;

    public MessageAdapter(List<Message> messages, Context context) {
        this.messages = messages;
        this.context = context;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.bind(messages.get(position), messages.size());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public void onViewRecycled(@NonNull MessageViewHolder holder) {
        super.onViewRecycled(holder);
        holder.stopAnimation();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView messageText, messageTime;
        private final Handler handler = new Handler();

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
            messageTime = itemView.findViewById(R.id.message_time);
        }

        public void bind(Message message, int count) {
            if (message == null) return;

            String text = message.getContent();
            messageText.setText(text);
            messageTime.setText(message.getCreateTime().toDate().toString().substring(11, 16));

            LinearLayout.LayoutParams textParams = (LinearLayout.LayoutParams) messageText.getLayoutParams();
            LinearLayout.LayoutParams timeParams = (LinearLayout.LayoutParams) messageTime.getLayoutParams();

            float scale = itemView.getContext().getResources().getDisplayMetrics().density;
            int marginInPx = (int) (50 * scale + 0.5f);

            if (message.getType() == MessageType.user) {
                textParams.gravity = Gravity.END;
                textParams.setMargins(marginInPx, 0, 0, 0);
                messageText.setBackgroundResource(R.drawable.round_chatgpt_message);
                timeParams.gravity = Gravity.END;
            } else if (message.getType() == MessageType.assistant) {
                textParams.gravity = Gravity.START;
                textParams.setMargins(0, 0, marginInPx, 0);
                messageText.setBackgroundResource(R.drawable.round_user_message);
                timeParams.gravity = Gravity.START;

                if (getAdapterPosition() == count - 1) {
                    animateText(text);
                }
            } else {
                textParams.gravity = Gravity.END;
                textParams.setMargins(marginInPx, 0, 0, 0);
                messageText.setBackgroundResource(R.drawable.round_error_message);
                timeParams.gravity = Gravity.END;
            }

            messageText.setLayoutParams(textParams);
            messageTime.setLayoutParams(timeParams);

            messageText.setOnLongClickListener(view -> {
                copyToClipboard(text);
                showCustomToast();
                return true;
            });
        }

        private void animateText(String text) {
            messageText.setText("");
            handler.removeCallbacksAndMessages(null);
            AtomicInteger index = new AtomicInteger(0);
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (index.get() < text.length()) {
                        messageText.append(String.valueOf(text.charAt(index.getAndIncrement())));
                        handler.postDelayed(this, 50);
                    }
                }
            };
            handler.post(runnable);
        }


        private void copyToClipboard(String text) {
            ClipboardManager clipboard = (ClipboardManager) itemView.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Скопированный текст", text);
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
            }
        }

        private void showCustomToast() {
            Context context = itemView.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) itemView.getRootView(), false);

            TextView toastMessage = layout.findViewById(R.id.toast_message);
            toastMessage.setText("Текст скопирован");

            Toast toast = new Toast(context);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 200);
            toast.show();
        }

        public void stopAnimation() {
            handler.removeCallbacksAndMessages(null);
            messageText.setText(messageText.getText().toString());
        }
    }
}
