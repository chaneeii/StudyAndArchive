package kr.ac.konkuk.studyandarchive;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class AddPostActivity extends AppCompatActivity {

    ActionBar actionBar;


    //timer
    TextView timeView2;
    long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Archive");
        //액션바에서 뒤로가기 버튼 활성화
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        //timer---------------------------
        Intent archiveIntent = getIntent();
        time = archiveIntent.getExtras().getLong("study_time_long");

        int h   = (int)(time /3600000);
        int m = (int)(time - h*3600000)/60000;
        int s= (int)(time - h*3600000- m*60000)/1000 ;
        String t = (h < 10 ? "0"+h: h)+":"+(m < 10 ? "0"+m: m)+":"+ (s < 10 ? "0"+s: s);

        timeView2 = findViewById(R.id.timeView2);
        timeView2.setText(t);
        //timer--------------------------



    }


    //액션바에서 뒤로가기 버튼 활성화
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // 이전 엑티비티로 돌아가기
        return super.onSupportNavigateUp();
    }

}