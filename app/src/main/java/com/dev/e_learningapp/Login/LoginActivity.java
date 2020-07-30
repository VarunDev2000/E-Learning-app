package com.dev.e_learningapp.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.e_learningapp.SignUp.GetPhoneNumber;
import com.dev.e_learningapp.User.HomePage;
import com.dev.e_learningapp.R;
import com.dev.e_learningapp.User.SearchActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    public  static  final  String PREFS_NAME = "LocalStorage";

    private TextInputLayout phonenumber,password;
    private Button login;
    private TextView signup,error;
    private ProgressBar progressBar;
    private CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(getUserLogin() == true){
            startActivity(new Intent(LoginActivity.this, HomePage.class));
            finish();
            //Intent intent = new Intent(getApplicationContext(), HomePage.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //startActivity(intent);
        }

        phonenumber = findViewById(R.id.phonenumber);
        password = findViewById(R.id.password);
        error = findViewById(R.id.error);
        login = findViewById(R.id.login);
        signup = findViewById(R.id.signup);
        progressBar = findViewById(R.id.progressBar);
        cardView = findViewById(R.id.cardView);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                error.setText("");

                final String s_phonenumber = "+91" + phonenumber.getEditText().getText().toString().trim();
                final String s_password = password.getEditText().getText().toString().trim();

                login.setClickable(false);
                progressBar.setVisibility(View.VISIBLE);

                if(s_phonenumber.equals("+91")){
                    progressBar.setVisibility(View.GONE);
                    login.setClickable(true);
                    phonenumber.setError("This field cannot be empty");
                }

                if(!s_phonenumber.equals("+91")){
                    phonenumber.setError(null);
                }

                if(!s_password.equals("")){
                    password.setError(null);
                }

                if(s_password.equals("")){
                    progressBar.setVisibility(View.GONE);
                    login.setClickable(true);
                    password.setError("This field cannot be empty");
                }

                if(!s_password.equals("") && !s_phonenumber.equals("+91")){

                    Query checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("phoneNo").equalTo(s_phonenumber);

                    checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){

                                String user_pass = snapshot.child(s_phonenumber).child("password").getValue(String.class);

                                if(user_pass.equals(s_password)){

                                    String name = snapshot.child(s_phonenumber).child("name").getValue(String.class);
                                    String email = snapshot.child(s_phonenumber).child("email").getValue(String.class);
                                    String phoneNo = snapshot.child(s_phonenumber).child("phoneNo").getValue(String.class);

                                    setUserLogin(true);
                                    setUserDetails(name,email,phoneNo);
                                    progressBar.setVisibility(View.GONE);
                                    login.setClickable(true);

                                    Intent intent = new Intent(getApplicationContext(), HomePage.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);

                                }
                                else{
                                    progressBar.setVisibility(View.GONE);
                                    login.setClickable(true);
                                    error.setText("Invalid Password");
                                    //Toast.makeText(LoginActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                                }
                            }

                            else{
                                progressBar.setVisibility(View.GONE);
                                login.setClickable(true);
                                error.setText("No such user exist");
                                //Toast.makeText(LoginActivity.this, "No such user exist", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressBar.setVisibility(View.GONE);
                            login.setClickable(true);
                            Toast.makeText(LoginActivity.this, "Please try again later", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GetPhoneNumber.class);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                startActivity(intent);
            }
        });
    }

    private void setUserLogin(boolean login){
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("login",true);
        editor.apply();
    }

    private Boolean getUserLogin(){
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);

        Boolean login = sharedPreferences.getBoolean("login",false);
        return login;
    }

    private void setUserDetails(String name,String email,String phoneNo){
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("name",name);
        editor.putString("email",email);
        editor.putString("phoneNo",phoneNo);
        editor.apply();
    }
}
