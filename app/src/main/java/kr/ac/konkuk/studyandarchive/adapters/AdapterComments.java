package kr.ac.konkuk.studyandarchive.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import kr.ac.konkuk.studyandarchive.R;
import kr.ac.konkuk.studyandarchive.models.ModelComment;

public class AdapterComments extends RecyclerView.Adapter<AdapterComments.MyHolder> {

    Context context;
    List<ModelComment> commentList;
    String myUid, postId;

    public AdapterComments(Context context, List<ModelComment> commentList, String myUid, String postId) {
        this.context = context;
        this.commentList = commentList;
        this.myUid = myUid;
        this.postId = postId;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // row-comments.xml 레이아웃을 bid
        View view = LayoutInflater.from(context).inflate(R.layout.row_comments, viewGroup,false);

        return new MyHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        //get the data

        final String uid = commentList.get(i).getUid();
        String name = commentList.get(i).getuName();
        String field = commentList.get(i).getuField();
        String image = commentList.get(i).getuDp();
        final String cid = commentList.get(i).getcId();
        String comment = commentList.get(i).getComment();
        String timeStamp = commentList.get(i).getTimestamp();

        //날짜 포멧 string으로
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(timeStamp));
        String pTime = DateFormat.format("yyyy/MM/dd hh:mm aa", calendar).toString();

        // set the data
        myHolder.nameTv.setText(name);
        myHolder.commentTv.setText(comment);
        myHolder.timeTv.setText(pTime);
        myHolder.fieldTv.setText(field);
        //set user dp
        try{
            Picasso.get().load(image).placeholder(R.drawable.ic_default_img_purple).into(myHolder.avatarIv);
        }catch (Exception e){

        }

        //comment 클리리스너
        myHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // comment가 현재 로그인된 사용자가 남긴 거면
                if(myUid.equals(uid)){
                    // my comment
                    // 삭제하라는 다이얼로그 띄우기
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                    builder.setTitle("댓글삭제");
                    builder.setMessage("이 댓글을 삭제할까요?");
                    builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //댓글 삭제
                            deleteComment(cid);
                        }


                    });
                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //dismiss
                            dialog.dismiss();
                        }
                    });
                    //dialog show
                    builder.create().show();

                }else {
                    //no my comment
                    Toast.makeText(context, "다른사람이 작성한 댓글은 삭제할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
                // 이 메소드에서 이벤트에 대한 처리를 끝낸경우 true, 아닌경우 false
                return false;

            }

        });





    }

    private void deleteComment(String cid) {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId); // 해당 포스트 가져오기
        ref.child("Comments").child(cid).removeValue(); // 댓글 삭제

        //댓글 개수 업데이트
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String comments = ""+ snapshot.child("pComments").getValue();
                int newCommentVal = Integer.parseInt(comments) -1 ;
                ref.child("pComments").setValue(""+newCommentVal);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        //declare views from row_comments.xml
        ImageView avatarIv;
        TextView nameTv, commentTv, timeTv, fieldTv;


        public MyHolder(@NonNull View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.avatarIv);
            nameTv = itemView.findViewById(R.id.nameTv);
            commentTv = itemView.findViewById(R.id.commentTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            fieldTv = itemView.findViewById(R.id.fieldTv);

        }
    }

}
