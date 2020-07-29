package com.dev.e_learningapp.User.Forum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.e_learningapp.Database.PostHelperClass;
import com.dev.e_learningapp.Database.UserHelperClass;
import com.dev.e_learningapp.Login.LoginActivity;
import com.dev.e_learningapp.R;
import com.dev.e_learningapp.SignUp.GetUserDetails;
import com.dev.e_learningapp.User.HomePage;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

public class ForumPost extends AppCompatActivity {

    private EditText content;
    private Button post_btn;
    private ProgressBar progressBar;
    private TextView posting;

    private int counter = 0;

    public  static  final  String PREFS_NAME = "LocalStorage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_post);

        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        post_btn = findViewById(R.id.post_btn);
        content = findViewById(R.id.content);
        progressBar = findViewById(R.id.progressBar);
        posting = findViewById(R.id.posting);

        content.requestFocus();


        post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String s_content = content.getText().toString().trim();

                if(s_content.equals("")){
                    content.setError("Type Something!!");
                }

                else{
                    storeNewUserData(s_content);

                    content.setVisibility(View.GONE);
                    post_btn.setVisibility(View.GONE);

                    progressBar.setVisibility(View.VISIBLE);
                    posting.setVisibility(View.VISIBLE);

                    final Timer t = new Timer();
                    TimerTask tt = new TimerTask() {
                        @Override
                        public void run() {
                            counter++;
                            progressBar.setProgress(counter);

                            if(counter == 100){
                                t.cancel();

                                Intent intent = new Intent(getApplicationContext(), ForumPage.class);
                                //overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                startActivity(intent);
                            }
                        }
                    };

                    t.schedule(tt,0,20);
                }

            }
        });


    }

    private void storeNewUserData(String s_content){

        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        final DatabaseReference reference = rootNode.getReference("Posts");

        String s_name = getUsername();
        String s_email = getEmail();
        String s_phoneNo = getPhoneNo();

        final PostHelperClass addPost = new PostHelperClass(s_name,s_content,s_email,s_phoneNo);


        Query getTotalData = FirebaseDatabase.getInstance().getReference("Posts");
        getTotalData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                if(snapshot.exists()){
                    reference.child(Long.toString(snapshot.getChildrenCount() + 1)).setValue(addPost);
                }

                else{
                    reference.child(Long.toString(1)).setValue(addPost);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                return;
            }
        });

    }

    private String getUsername(){
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);

        String name = sharedPreferences.getString("name","");
        return name;
    }

    private String getEmail(){
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);

        String email = sharedPreferences.getString("email","");
        return email;
    }

    private String getPhoneNo(){
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);

        String phno = sharedPreferences.getString("phoneNo","");
        return phno;
    }
}
