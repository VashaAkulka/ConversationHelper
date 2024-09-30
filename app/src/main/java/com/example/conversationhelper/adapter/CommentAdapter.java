package com.example.conversationhelper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.conversationhelper.R;
import com.example.conversationhelper.db.model.Comment;
import com.example.conversationhelper.db.model.User;
import com.example.conversationhelper.db.repository.UserRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private final List<Comment> comments;
    private final Context context;
    private final UserRepository userRepository;

    public CommentAdapter(Context context, List<Comment> comments, FirebaseFirestore db) {
        this.context = context;
        this.comments = comments;
        userRepository = new UserRepository(db);
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        userRepository.getUserById(comment.getUserId())
                        .thenAccept(user -> holder.bind(comment, user));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        private final TextView userName;
        private final TextView content;
        private final TextView date;
        private final ImageView avatar;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name_comment);
            content = itemView.findViewById(R.id.comment_content);
            date = itemView.findViewById(R.id.date_comment);
            avatar = itemView.findViewById(R.id.comment_user_avatar);
        }

        public void bind(Comment comment, User user) {
            content.setText(comment.getContent());
            userName.setText(user.getName());

            Glide.with(itemView.getContext())
                    .load(user.getAvatar())
                    .into(avatar);


            Date createTime = comment.getCreateTime().toDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy   HH:mm", Locale.getDefault());
            String formattedDate = dateFormat.format(createTime);
            date.setText(formattedDate);
        }
    }
}
