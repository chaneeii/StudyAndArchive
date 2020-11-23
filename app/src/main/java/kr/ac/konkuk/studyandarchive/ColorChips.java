package kr.ac.konkuk.studyandarchive;

import android.content.Context;
import android.view.View;

import androidx.core.content.ContextCompat;

public class ColorChips {


    public ColorChips(String field, View view, Context context){
        if(field.equals("고등학생")||field.equals("N수") ){ //수능

            view.setBackground(ContextCompat.getDrawable(context, R.drawable.chip_d_pink));

        }else if(field.equals("간호")||field.equals("보건")||field.equals("약학")||field.equals("의료")){ //의료

            view.setBackground(ContextCompat.getDrawable(context, R.drawable.chip_blue));

        }else if(field.equals("경영")||field.equals("문과")||field.equals("법률")||field.equals("사회")||field.equals("언어")||field.equals("인문")){
            //인문상경계
            view.setBackground(ContextCompat.getDrawable(context, R.drawable.chip_orange));

        }else if(field.equals("공무원")||field.equals("자격증")){

            view.setBackground(ContextCompat.getDrawable(context, R.drawable.chip_green));

        }else if(field.equals("공학")||field.equals("컴퓨터/통신")){

            view.setBackground(ContextCompat.getDrawable(context, R.drawable.chip_red));

        }else if(field.equals("교육")){

            view.setBackground(ContextCompat.getDrawable(context, R.drawable.chip_pink));

        }else if(field.equals("디자인")||field.equals("미술")||field.equals("연극/영화")||field.equals("예체능")||field.equals("음악")){

            view.setBackground(ContextCompat.getDrawable(context, R.drawable.chip_purple));

        }else if(field.equals("이과")||field.equals("자연")){

            view.setBackground(ContextCompat.getDrawable(context, R.drawable.chip_navy));

        }else{
            view.setBackground(ContextCompat.getDrawable(context, R.drawable.chip_gray));
        }


    }


}
