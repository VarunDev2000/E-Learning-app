package com.dev.e_learningapp.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dev.e_learningapp.SignUp.GetPhoneNumber;
import com.dev.e_learningapp.User.HomePage;
import com.dev.e_learningapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    public  static  final  String PREFS_NAME = "LocalStorage";

    private EditText phonenumber,password;
    private Button login,signup;

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
        login = findViewById(R.id.login);
        signup = findViewById(R.id.signup);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String s_phonenumber = "+91" + phonenumber.getText().toString().trim();
                final String s_password = password.getText().toString().trim();

                if(s_phonenumber.equals("+91")){
                    phonenumber.setError("This field cannot be empty");
                }

                if(s_password.equals("")){
                    password.setError("This field cannot be empty");
                }

                if(!s_password.equals("+91") && !s_phonenumber.equals("")){

                    Query checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("phoneNo").equalTo(s_phonenumber);

                    checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                phonenumber.setError(null);

                                String user_pass = snapshot.child(s_phonenumber).child("password").getValue(String.class);

                                if(user_pass.equals(s_password)){

                                    String name = snapshot.child(s_phonenumber).child("name").getValue(String.class);
                                    String email = snapshot.child(s_phonenumber).child("email").getValue(String.class);
                                    String phoneNo = snapshot.child(s_phonenumber).child("phoneNo").getValue(String.class);

                                    setUserLogin(true);
                                    setUserDetails(name,email,phoneNo);

                                    Intent intent = new Intent(getApplicationContext(), HomePage.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                                else{
                                    Toast.makeText(LoginActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                                }
                            }

                            else{
                                Toast.makeText(LoginActivity.this, "No such user data exist", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(LoginActivity.this, "Some error..Please try again later!!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GetPhoneNumber.class);
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
