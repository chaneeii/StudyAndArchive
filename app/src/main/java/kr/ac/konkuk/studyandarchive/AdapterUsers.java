package kr.ac.konkuk.studyandarchive;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.ContentValues.TAG;


// modeluser 내용 리사이클러뷰에 뿌려쥼

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder>{

    Context context;
    List<ModelUser> userList;

    //constructor
    public AdapterUsers(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) { // i = viewType
        // inflate layout(row_user.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.row_users, viewGroup,false);


        return new MyHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) { //i = position
        //get data
        String userImage = userList.get(i).getImage();
        final String userName = userList.get(i).getName();
        final String userField = userList.get(i).getField();

        if(userField.equals("디자인")){
            myHolder.mFieldTv.setBackground(ContextCompat.getDrawable(context, R.drawable.radius_yellow));
            myHolder.mFieldTv.setTextColor(Color.parseColor("#ffffff"));
        }



        //setData
        myHolder.mNameTv.setText(userName);
        myHolder.mFieldTv.setText(userField);

        try{
            Picasso.get().load(userImage).
                    placeholder(R.drawable.ic_default_img_purple).
                    into(myHolder.mAvatarIv);
        }catch (Exception e){

        }
        
        //handle item click
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, ""+userName, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {

//        Log.d(TAG, "getItemCount: "+userList.size());
        return userList.size();

    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder{

        public ImageView mAvatarIv;
        public TextView mNameTv, mFieldTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            mAvatarIv =itemView.findViewById(R.id.avatarIv);
            mNameTv =itemView.findViewById(R.id.nameTv);
            mFieldTv =itemView.findViewById(R.id.fieldTv);


        }
    }

}
