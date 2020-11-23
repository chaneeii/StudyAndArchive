package kr.ac.konkuk.studyandarchive.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import kr.ac.konkuk.studyandarchive.PostDetailActivity;
import kr.ac.konkuk.studyandarchive.R;
import kr.ac.konkuk.studyandarchive.models.ModelPost;

import static android.content.ContentValues.TAG;

public class AdapterThumbs extends RecyclerView.Adapter<AdapterThumbs.MyHolder> {

    private Context context;
    private List<ModelPost> mPosts;

    public AdapterThumbs(Context context, List<ModelPost> mPosts) {
        this.context = context;
        this.mPosts = mPosts;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_thumbs, viewGroup, false);
        return new AdapterThumbs.MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {

        ModelPost post = mPosts.get(i);


        long time = Long.parseLong(post.getpStudyTime());
        int h   = (int)(time /3600000);
        int m = (int)(time - h*3600000)/60000;
        int s= (int)(time - h*3600000- m*60000)/1000 ;
        String pStudyTime_s = " "+(h < 10 ? "0"+h: h)+":"+(m < 10 ? "0"+m: m)+":"+(s < 10 ? "0"+s: s);
        myHolder.post_s_time.setText(pStudyTime_s);

        String pImage =post.getpImage();
        final String pId =post.getpId();


        //포스트 이미지 설정
        try{
//            Picasso.get().load(post.getpImage()).placeholder(R.drawable.test_image).into(myHolder.post_image);
            Picasso.get().load(pImage).placeholder(R.drawable.test_image).into(myHolder.post_image);
        }catch (Exception e){

        }

        // button click handlers
        myHolder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이미지 선택하면 포스트 상세 페이지 postdetail activity
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("postId",pId); // 해당 포스트가 클릭될때 해당 id로 포스트 디테일을 가져옴
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public  class MyHolder extends RecyclerView.ViewHolder{

        public ImageView post_image;
        public TextView post_s_time;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            post_image = itemView.findViewById(R.id.post_image);
            post_s_time = itemView.findViewById(R.id.post_s_time);
        }


    }

}
