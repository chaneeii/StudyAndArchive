package kr.ac.konkuk.studyandarchive;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;

import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;

import static android.service.controls.ControlsProviderService.TAG;


public class RecordFragment extends Fragment {

    Chronometer chronometer;
    Button btnReset, btnStart, btnPause, btnFinish;
    SimpleDateFormat format;
    private boolean running;
    private long pauseOffset;
    long time;



    public RecordFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true); // to show menu option in framgent
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_record, container, false);


        // assign variable
        chronometer = view.findViewById(R.id.chronometer);
        btnStart =view.findViewById(R.id.bt_start);
        btnPause =view.findViewById(R.id.bt_pause);
        btnReset =view.findViewById(R.id.bt_reset);
        btnFinish = view.findViewById(R.id.bt_finish);

        //Initialize simple date format
        //Get current time
        format = new SimpleDateFormat("hh:mm:ss aa");

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!running){
                    chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset );
                    chronometer.start();
                    running = true;
                }
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(running){
                    chronometer.stop();
                    pauseOffset = SystemClock.elapsedRealtime() -chronometer.getBase();
                    running = false;
                }
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chronometer.setBase(SystemClock.elapsedRealtime());
                pauseOffset=0;
            }
        });

        final Intent myIntent = new Intent(getContext(), AddPostActivity.class);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!running){ //일시정지상태
                    time = pauseOffset;
                }else{ //시간 측정도중
                    time = SystemClock.elapsedRealtime()-chronometer.getBase();
                }
                int elapsed = (int)(time);
                int h   = (int)(time /3600000);
                int m = (int)(time - h*3600000)/60000;
                int s= (int)(time - h*3600000- m*60000)/1000 ;
                String t = (h < 10 ? "0"+h: h)+":"+(m < 10 ? "0"+m: m)+":"+ (s < 10 ? "0"+s: s);
                myIntent.putExtra("study_time", t);
                myIntent.putExtra("study_time_long",time);
                Log.d(TAG, "찬희다"+ t);
                Log.d(TAG, "찬희다"+ time);
                Log.d(TAG, "찬희다"+ elapsed);

                startActivity(myIntent);
            }
        });


        return view;
    }

    /*inflate option menu*/
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        menu.clear();

        //inflating menu
//        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }


}









//    public void startChronometer(View view) {
//        if(!running){
//            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset );
//            chronometer.start();
//            running = true;
//        }
//
//    }

//    public void pauseChronometer(View view) {
//        if(running){
//            chronometer.stop();
//            pauseOffset = SystemClock.elapsedRealtime() -chronometer.getBase();
//            running = false;
//        }
//    }
//
//    public void resetChronometer(View view) {
//        chronometer.setBase(SystemClock.elapsedRealtime());
//        pauseOffset=0;
//    }