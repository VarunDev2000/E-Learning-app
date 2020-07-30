package com.dev.e_learningapp.SignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dev.e_learningapp.R;
import com.dev.e_learningapp.User.Forum.ForumPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;


public class OTPVerification extends AppCompatActivity {

    Button verify;
    TextInputLayout otp;
    CardView cardView;
    ProgressBar progressBar;

    String verificationCodeBySystem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);

        verify = findViewById(R.id.verify);
        otp = findViewById(R.id.otp);
        cardView = findViewById(R.id.cardView);
        progressBar = findViewById(R.id.progressBar);

        String phoneNo = getIntent().getStringExtra("phoneNo");

        sendVerificationCodeToUser(phoneNo);

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);
                verify.setClickable(false);

                String code = otp.getEditText().getText().toString();

                if (code.isEmpty() || code.length() < 6) {
                    progressBar.setVisibility(View.GONE);
                    verify.setClickable(true);

                    otp.setError("Wrong OTP");
                    otp.requestFocus();
                    return;
                }
                verifyOTP(code);
            }
        });
    }

    private void sendVerificationCodeToUser(String phoneNo) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNo,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            verificationCodeBySystem = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if(code != null)
            {
                verifyOTP(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            progressBar.setVisibility(View.GONE);
            verify.setClickable(true);
            Toast.makeText(OTPVerification.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyOTP(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem,code);
        signIn(credential);
    }

    private void signIn(PhoneAuthCredential credential){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(OTPVerification.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressBar.setVisibility(View.GONE);
                            verify.setClickable(true);

                            String phoneNo = getIntent().getStringExtra("phoneNo");
                            Intent intent = new Intent(getApplicationContext(), GetUserDetails.class);
                            intent.putExtra("phoneNo",phoneNo);
                            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                            startActivity(intent);

                        }

                        else{
                            progressBar.setVisibility(View.GONE);
                            verify.setClickable(true);
                            Toast.makeText(OTPVerification.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        progressBar.setVisibility(View.GONE);
        Intent intent = new Intent(getApplicationContext(), GetPhoneNumber.class);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        startActivity(intent);
    }
}