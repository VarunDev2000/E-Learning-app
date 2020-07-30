package com.dev.e_learningapp.SignUp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dev.e_learningapp.R;
import com.dev.e_learningapp.User.Forum.ForumPage;
import com.dev.e_learningapp.User.HomePage;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class GetPhoneNumber extends AppCompatActivity {

    FirebaseAuth fAuth;
    TextInputLayout phoneNumber;
    CardView cardView;
    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_phone_number);

        phoneNumber = findViewById(R.id.phonenumber);
        next = findViewById(R.id.next);
        cardView = findViewById(R.id.cardView);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!phoneNumber.getEditText().getText().toString().isEmpty() && phoneNumber.getEditText().getText().toString().length() == 10){
                    String phoneNum = "+91" + phoneNumber.getEditText().getText().toString();
                    //Log.d("TAG", "onClick: Phone NO -> " + phoneNum);

                    Intent intent = new Intent(getApplicationContext(), OTPVerification.class);
                    intent.putExtra("phoneNo",phoneNum);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    startActivity(intent);
                }

                else{
                    phoneNumber.setError("Phone number is not valid");
                }
            }
        });
    }
}
