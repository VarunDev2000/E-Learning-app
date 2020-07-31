package com.dev.e_learningapp.SignUp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.e_learningapp.Database.UserHelperClass;
import com.dev.e_learningapp.User.HomePage;
import com.dev.e_learningapp.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GetUserDetails extends AppCompatActivity {

    public  static  final  String PREFS_NAME = "LocalStorage";

    private TextInputLayout name,email,new_pass,conf_pass;
    private TextView error;
    private Button submit;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_user_details);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        new_pass = findViewById(R.id.new_pass);
        conf_pass = findViewById(R.id.conf_pass);
        submit = findViewById(R.id.signup_btn);
        error = findViewById(R.id.error);
        progressBar = findViewById(R.id.progressBar);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                closeKeyboard();
                error.setText("");

                submit.setClickable(false);
                progressBar.setVisibility(View.VISIBLE);

                String s_name = name.getEditText().getText().toString().trim();
                String s_email = email.getEditText().getText().toString().trim();
                String s_new_pass = new_pass.getEditText().getText().toString().trim();
                String s_conf_pass = conf_pass.getEditText().getText().toString().trim();

                if(s_name.equals("")){
                    submit.setClickable(true);
                    progressBar.setVisibility(View.GONE);
                    name.setError("This field cannot be empty");
                }

                if(!s_name.equals("")){
                    name.setError(null);
                }

                if(s_email.equals("")){
                    submit.setClickable(true);
                    progressBar.setVisibility(View.GONE);
                    email.setError("This field cannot be empty");
                }

                if(!s_email.equals("")){
                    email.setError(null);
                }

                if(s_new_pass.equals("")){
                    submit.setClickable(true);
                    progressBar.setVisibility(View.GONE);
                    new_pass.setError("This field cannot be empty");
                }

                if(!s_new_pass.equals("")){
                    new_pass.setError(null);
                }

                if(s_conf_pass.equals("")){
                    submit.setClickable(true);
                    progressBar.setVisibility(View.GONE);
                    conf_pass.setError("This field cannot be empty");
                }

                if(!s_conf_pass.equals("")){
                    conf_pass.setError(null);
                }

                if(!s_new_pass.equals(s_conf_pass)){
                    //Toast.makeText(GetUserDetails.this, "Passwords dont match", Toast.LENGTH_SHORT).show();
                    submit.setClickable(true);
                    progressBar.setVisibility(View.GONE);
                    error.setText("Passwords doesnot match!!");
                }

                if(!s_name.equals("") && !s_email.equals("") && !s_new_pass.equals("") && !s_conf_pass.equals("") && s_new_pass.equals(s_conf_pass))
                {
                    error.setText("");
                    String s_phoneNo = getIntent().getStringExtra("phoneNo");
                    storeNewUserData(s_name,s_email,s_new_pass,s_phoneNo);

                    setUserLogin(true);
                    setUserDetails(s_name,s_email,s_phoneNo);

                    submit.setClickable(true);
                    progressBar.setVisibility(View.GONE);

                    Intent intent = new Intent(getApplicationContext(), HomePage.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

            }
        });

    }

    private void storeNewUserData(String s_name,String s_email, String s_new_pass, String s_phoneNo){

        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference("Users");

        UserHelperClass addNewUser = new UserHelperClass(s_name,s_email,s_new_pass,s_phoneNo);

        reference.child(s_phoneNo).setValue(addNewUser);
    }

    private void setUserLogin(boolean login){
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("login",true);
        editor.apply();
    }

    private void setUserDetails(String name,String email,String phoneNo){
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("name",name);
        editor.putString("email",email);
        editor.putString("phoneNo",phoneNo);
        editor.apply();
    }

    private void closeKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
