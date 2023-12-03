package com.example.sfproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

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

            // 댓글을 롱클릭했을 때 호출될 메서드를 정의
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // 롱클릭 이벤트 처리
                    showDeleteCommentDialog(commentList.get(getBindingAdapterPosition())); // 변경된 부분
                    return true; // 이벤트 소비 여부 반환
                }
            });
        }

        // 댓글 삭제 다이얼로그를 표시하는 메서드
        private void showDeleteCommentDialog(Comment comment) {
            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
            builder.setTitle("댓글 삭제")
                    .setMessage("이 댓글을 삭제하시겠습니까?")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 댓글 삭제 로직을 여기에 추가
                            deleteComment(comment);
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }

        // 댓글 삭제 로직을 처리하는 메서드
        private void deleteComment(Comment comment) {
            // 여기에 실제 댓글 삭제 로직을 추가해야 합니다.
            // 아래는 예시로 Firebase를 사용하는 경우의 코드입니다. 실제로 사용 중인 데이터베이스에 맞게 수정이 필요합니다.
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            String commentId = commentList.get(getBindingAdapterPosition()).getCommentId();
            firestore.collection("comments")
                    .document(commentId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        // 삭제 성공 시
                        Log.d("CommentAdapter", "댓글 삭제 성공");

                        // 시간 지연 후 UI 업데이트 및 RecyclerView 갱신
                        new Handler().postDelayed(() -> {
                            // UI 업데이트 등의 작업을 여기에서 수행하면 됩니다.
                            // commentList에서 삭제한 댓글을 제거
                            commentList.remove(getBindingAdapterPosition());
                            // RecyclerView 갱신
                            notifyItemRemoved(getBindingAdapterPosition());
                        }, 1000); // 1초 후에 UI 업데이트 진행
                    })
                    .addOnFailureListener(e -> {
                        // 삭제 실패 시
                        Log.e("CommentAdapter", "댓글 삭제 실패: " + e.getMessage());
                    });
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