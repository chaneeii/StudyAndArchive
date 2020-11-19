package kr.ac.konkuk.studyandarchive.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

    public AdapterComments(Context context, List<ModelComment> commentList) {
        this.context = context;
        this.commentList = commentList;
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

        String uid = commentList.get(i).getUid();
        String name = commentList.get(i).getuName();
        String field = commentList.get(i).getuField();
        String image = commentList.get(i).getuDp();
        String cid = commentList.get(i).getcId();
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
