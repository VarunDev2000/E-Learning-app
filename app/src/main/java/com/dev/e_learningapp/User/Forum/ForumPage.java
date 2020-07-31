package com.dev.e_learningapp.User.Forum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.dev.e_learningapp.Others.ForumAdapter;
import com.dev.e_learningapp.R;
import com.dev.e_learningapp.User.HomePage;
import com.dev.e_learningapp.User.ProfilePage;
import com.dev.e_learningapp.User.SearchActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ForumPage extends AppCompatActivity {

    public  static  final  String PREFS_NAME = "LocalStorage";

    private Uri uri;
    private Bitmap imageBitmap;

    private EditText post;
    private ImageButton back;
    private RecyclerView recyclerView;
    public SwipeRefreshLayout swipeRefreshLayout;
    private ForumAdapter forumAdapter;

    private ArrayList<ArrayList<String>> Listitems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_page);

        post = findViewById(R.id.post);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setRefreshing(true);
        getPostdataFromDatabase();

        //Recycler View
        recyclerView = findViewById(R.id.recyclerView);

        //RefreshCheck
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setEmptyList();
                //swipeRefreshLayout.setRefreshing(false);
            }
        });


        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair <View,String>(recyclerView,"recycletransition");

                Intent intent = new Intent(getApplicationContext(), HomePage.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ForumPage.this, pairs);
                startActivity(intent,options.toBundle());
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair <View,String>(post,"posttransition");

                Intent intent = new Intent(getApplicationContext(), ForumPost.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ForumPage.this, pairs);
                startActivity(intent,options.toBundle());
            }
        });

    }

    public void getPostdataFromDatabase(){
        Query getPostData = FirebaseDatabase.getInstance().getReference("Posts");
        Listitems = new ArrayList<>();

        getPostData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                        String key = postSnapshot.getKey();
                        String name = postSnapshot.child("name").getValue(String.class);
                        String content = postSnapshot.child("content").getValue(String.class);
                        String phoneNo = postSnapshot.child("phoneNo").getValue(String.class);

                        ArrayList<String> localList = new ArrayList<>();
                        localList.add(key);
                        localList.add(name);
                        localList.add(content);
                        localList.add(phoneNo);

                        Listitems.add(localList);
                    }
                    initList(Listitems);
                }

                else{
                    Toast.makeText(ForumPage.this, "", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ForumPage.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initList(ArrayList<ArrayList<String>>  data){
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Collections.reverse(data);
        forumAdapter = new ForumAdapter(this,data,getPhoneNo());
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                swipeRefreshLayout.setRefreshing(false);
                                recyclerView.setAdapter(forumAdapter);
                            }
                        });
                    }
                },
                2000
        );
    }

    public void setEmptyList(){
        ArrayList<ArrayList<String>>  data = new ArrayList<ArrayList<String>>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Collections.reverse(data);
        forumAdapter = new ForumAdapter(this,data,getPhoneNo());
        recyclerView.setAdapter(forumAdapter);
        getPostdataFromDatabase();
    }

    private String getPhoneNo(){
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);

        String phno = sharedPreferences.getString("phoneNo","");
        return phno;
    }

    @Override
    public void onBackPressed() {
        //finish();
        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair <View,String>(recyclerView,"recycletransition");

        Intent intent = new Intent(getApplicationContext(), HomePage.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ForumPage.this, pairs);
        startActivity(intent,options.toBundle());
    }


    @Override
    protected void onResume() {
        super.onResume();
        post.requestFocus();
    }
}
