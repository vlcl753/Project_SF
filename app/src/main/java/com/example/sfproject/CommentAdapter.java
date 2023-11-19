package com.example.sfproject;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sfproject.Comment;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private List<Comment> commentList;
    private Context context;

    public CommentAdapter(List<Comment> commentList, Context context) {
        this.commentList = commentList;
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

        // Timestamp를 날짜 형식으로 변환 (24시간 형식)
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String timestampString = dateFormat.format(comment.getTimestamp().toDate());
        holder.tvTimestamp.setText(timestampString);

        // 프로필 사진 표시 (Glide 라이브러리 사용)
        if (comment.getUimg() != null && !comment.getUimg().isEmpty()) {
            // 파이어베이스 스토리지에서 이미지 다운로드 및 표시
            Glide.with(context)
                    .load(comment.getUimg()) // 이미지 다운로드 URL
                    .circleCrop()
                    .placeholder(R.drawable.default_profile_image)
                    .error(R.drawable.default_profile_image)
                    .into(holder.ivProfile);
        } else {
            // 프로필 이미지가 없는 경우 기본 이미지 표시
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
}

