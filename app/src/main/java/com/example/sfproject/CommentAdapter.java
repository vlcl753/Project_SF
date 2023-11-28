package com.example.sfproject;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private List<Comment> commentList;
    private Context context;

    // 생성자 수정
    public CommentAdapter(List<Comment> commentList, Context context) {
        this.commentList = (commentList != null) ? commentList : new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_comments, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = commentList.get(position);

        holder.tvUsername.setText(comment.getUname());
        holder.tvCommentText.setText(comment.getContent());

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String timestampString = dateFormat.format(comment.getTimestamp().toDate());
        holder.tvTimestamp.setText(timestampString);

        if (comment.getUimg() != null && !comment.getUimg().isEmpty()) {
            Glide.with(context)
                    .load(comment.getUimg())
                    .circleCrop()
                    .placeholder(R.drawable.default_profile_image)
                    .error(R.drawable.default_profile_image)
                    .into(holder.ivProfile);
        } else {
            holder.ivProfile.setImageResource(R.drawable.default_profile_image);
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername;
        TextView tvCommentText;
        TextView tvTimestamp;
        ImageView ivProfile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.comment_username);
            tvCommentText = itemView.findViewById(R.id.comment_content);
            tvTimestamp = itemView.findViewById(R.id.comment_date);
            ivProfile = itemView.findViewById(R.id.comment_user_img);
        }
    }

    // 이 부분에 setCommentList 메서드를 이동
    public void setCommentList(List<Comment> commentList) {
        if (commentList != null) {
            this.commentList = commentList;
        } else {
            this.commentList = new ArrayList<>();
        }
        notifyDataSetChanged(); // 데이터가 변경되었음을 알림

        Log.d("CommentAdapter", "setCommentList 호출됨. 데이터 크기: " + commentList.size());
    }
}
