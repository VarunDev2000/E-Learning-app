package com.dev.e_learningapp.SignUp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dev.e_learningapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class GetPhoneNumber extends AppCompatActivity {

    FirebaseAuth fAuth;
    EditText phoneNumber;
    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_phone_number);

        phoneNumber = findViewById(R.id.phonenumber);
        next = findViewById(R.id.next);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!phoneNumber.getText().toString().isEmpty() && phoneNumber.getText().toString().length() == 10){
                    String phoneNum = "+91" + phoneNumber.getText().toString();
                    Log.d("TAG", "onClick: Phone NO -> " + phoneNum);

                    Intent intent = new Intent(getApplicationContext(),OTPVerification.class);
                    intent.putExtra("phoneNo",phoneNum);
                    startActivity(intent);
                }

                else{
                    phoneNumber.setError("Phone number is not valid");
                }
            }
        });
    }
}
