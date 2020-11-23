package kr.ac.konkuk.studyandarchive.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kr.ac.konkuk.studyandarchive.R;
import kr.ac.konkuk.studyandarchive.models.ModelPost;

import static android.content.ContentValues.TAG;

public class AdapterStudyRecord extends RecyclerView.Adapter<AdapterStudyRecord.MyHolder>{

    private Context context;
    private List<ModelPost> mPosts;

    public AdapterStudyRecord(Context context, List<ModelPost> mPosts) {
        this.context = context;
        this.mPosts = mPosts;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_records, viewGroup, false);
        return new AdapterStudyRecord.MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {


        ModelPost post = mPosts.get(i);

        //포스트 등록날짜
        //timestamp 변환
        Long timeStamp = Long.parseLong(post.getpTime());
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(timeStamp);

        // 기록이등록된날짜
        String pDate = DateFormat.format("yyyy/MM/dd", calendar).toString();
        String pMonth = DateFormat.format("MM", calendar).toString();

        //이번달
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat thisMonthF = new SimpleDateFormat("MM", Locale.getDefault());
        String thisMonth = thisMonthF.format(currentTime);

        if(thisMonth.equals(pMonth)){

            myHolder.record_date.setText(pDate);

            //월
            Long time = Long.parseLong(post.getpStudyTime());
            int h   = (int)(time /3600000);
            int m = (int)(time - h*3600000)/60000;
            int s= (int)(time - h*3600000- m*60000)/1000 ;
            String pStudyTime_s = " "+(h < 10 ? "0"+h: h)+":"+(m < 10 ? "0"+m: m)+":"+(s < 10 ? "0"+s: s);
            myHolder.record_time.setText(pStudyTime_s);

        }

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }


    public class MyHolder extends RecyclerView.ViewHolder{

        public TextView record_date,record_time;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            record_date = itemView.findViewById(R.id.record_date);
            record_time = itemView.findViewById(R.id.record_time);
        }


    }

    public String thisMonthStudy() {

        Long result = null;
/*
        for(ModelPost post : mPosts){

            //포스트 등록날짜
            //timestamp 변환
            Long timeStamp = Long.parseLong(post.getpTime());
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            calendar.setTimeInMillis(timeStamp);

            // 기록이등록된날짜
            String pDate = DateFormat.format("yyyy/MM/dd", calendar).toString();
            String pMonth = DateFormat.format("MM", calendar).toString();

            //이번달
            Date currentTime = Calendar.getInstance().getTime();
            SimpleDateFormat thisMonthF = new SimpleDateFormat("MM", Locale.getDefault());
            String thisMonth = thisMonthF.format(currentTime);



            if(thisMonth.equals(pMonth)){

                //월
                Long time = Long.parseLong(post.getpStudyTime());
                result += time;

            }

        }

*/
        int h   = (int)(result/3600000);
        int m = (int)(result - h*3600000)/60000;
        int s= (int)(result- h*3600000- m*60000)/1000 ;
        String pStudyTime_s = " "+(h < 10 ? "0"+h: h)+":"+(m < 10 ? "0"+m: m)+":"+(s < 10 ? "0"+s: s);



        if(result!=null){
//            return pStudyTime_s;
            return "공부시간";
        }else{
            return "이달 기록 없음";
        }



    }




}
