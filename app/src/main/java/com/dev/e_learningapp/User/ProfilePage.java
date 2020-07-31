package com.dev.e_learningapp.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dev.e_learningapp.Login.LoginActivity;
import com.dev.e_learningapp.R;
import com.dev.e_learningapp.User.Forum.ForumPage;
import com.dev.e_learningapp.User.Forum.ForumPost;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfilePage extends AppCompatActivity {

    public  static  final  String PREFS_NAME = "LocalStorage";

    private TextView name,mail,phoneNo;
    private Button logout;
    private ImageButton back;
    ProgressBar logout_progressbar;
    ConstraintLayout main_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        name = (TextView)findViewById(R.id.name);
        mail = (TextView)findViewById(R.id.mail);
        phoneNo = (TextView)findViewById(R.id.phoneNo);
        back = findViewById(R.id.back);
        logout = findViewById(R.id.logout);
        logout_progressbar = findViewById(R.id.logout_progressbar);
        main_layout = findViewById(R.id.main_layout);

        logout_progressbar.setVisibility(View.GONE);

        name.setText(getName());
        mail.setText(getMail());
        phoneNo.setText(getPhoneNo());

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomePage.class);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main_layout.setAlpha((float) 0.8);
                logout_progressbar.setVisibility(View.VISIBLE);
                back.setClickable(false);
                logout.setClickable(false);

                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        logout();
                                        back.setClickable(true);
                                        logout.setClickable(true);
                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                });
                            }
                        },
                        2000
                );
            }
        });

    }

    private String getName(){
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);

        String name = sharedPreferences.getString("name","");
        String final_name = name.substring(0, 1).toUpperCase() + name.substring(1);

        return final_name;
    }

    private String getMail(){
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);

        String email = sharedPreferences.getString("email","");
        return email;
    }

    private String getPhoneNo(){
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);

        String phno = sharedPreferences.getString("phoneNo","");
        String final_phoneNo = phno.substring(3);

        return final_phoneNo;
    }

    public void logout(){
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("name","");
        editor.putString("email","");
        editor.putString("phoneNo","");
        editor.putBoolean("login",false);
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), HomePage.class);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        startActivity(intent);
    }

}
