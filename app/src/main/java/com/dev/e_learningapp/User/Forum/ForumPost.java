package com.dev.e_learningapp.User.Forum;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.dev.e_learningapp.R;

public class ForumPost extends AppCompatActivity {

    private EditText content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_post);

        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        content = findViewById(R.id.content);

        content.requestFocus();


        
    }
}
