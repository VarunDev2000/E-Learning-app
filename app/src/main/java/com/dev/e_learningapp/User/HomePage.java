package com.dev.e_learningapp.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dev.e_learningapp.Others.HomePageAdapter;
import com.dev.e_learningapp.Login.LoginActivity;
import com.dev.e_learningapp.R;
import com.dev.e_learningapp.User.Forum.ForumPage;
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
import java.util.List;
import java.util.regex.Pattern;


public class HomePage extends AppCompatActivity {

    private Uri uri;

    private RecyclerView recyclerView;
    private HomePageAdapter homePageAdapter;
    private String[] items;
    private ArrayList<ArrayList<String>> Listitems;

    private EditText search;
    private ImageButton camera_btn;
    private ProgressBar progressBar;

    private Bitmap imageBitmap;

    public  static  final  String PREFS_NAME = "LocalStorage";

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        if(getUserLogin() == false){
            startActivity(new Intent(HomePage.this, LoginActivity.class));
            finish();
        }

        camera_btn = findViewById(R.id.camera_btn);

        camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Card Effects
                final Animation anim = AnimationUtils.loadAnimation(v.getContext(), R.anim.bounce);
                camera_btn.startAnimation(anim);
                verifyPermissions();
            }
        });

        //BottomNav Bar
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){

                    case R.id.home:
                        return true;

                    case R.id.forum:
                        Pair[] pairs = new Pair[2];
                        pairs[0] = new Pair <View,String>(bottomNavigationView,"bottomnavtransition");
                        pairs[1] = new Pair <View,String>(recyclerView,"recycletransition");
                        //pairs[2] = new Pair <View,String>(search,"searchbartransition");

                        Intent intent = new Intent(getApplicationContext(), ForumPage.class);
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(HomePage.this, pairs);
                        startActivity(intent,options.toBundle());
                        return true;

                    case R.id.camera:
                        verifyPermissions();
                        return true;

                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(),ProfilePage.class));
                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        return true;
                }

                return false;
            }
        });

        //ProgressBar
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        //Recycler View
        recyclerView = findViewById(R.id.recyclerView);
        //getVideodataFromDatabase();


        //search in RecyclerView
        search = findViewById(R.id.search);

        search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair <View,String>(search,"searchbartransition");

                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                intent.putExtra("textToSearch","");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(HomePage.this, pairs);
                startActivity(intent,options.toBundle());
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair <View,String>(search,"searchbartransition");

                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                intent.putExtra("textToSearch","");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(HomePage.this, pairs);
                startActivity(intent,options.toBundle());
            }
        });

    }

    private void getVideodataFromDatabase(){
        Query getVideoData = FirebaseDatabase.getInstance().getReference("Videos");
        Listitems = new ArrayList<>();

        getVideoData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                        String category = postSnapshot.child("category").getValue(String.class);
                        String title = postSnapshot.child("title").getValue(String.class);
                        String url = postSnapshot.child("url").getValue(String.class);
                        ArrayList<String> localList = new ArrayList<>();
                        localList.add(category);
                        localList.add(title);
                        localList.add(url);

                        Listitems.add(localList);
                    }
                    initList(Listitems);
                    Log.d("TAG",""+Listitems);
                }

                else{
                    Toast.makeText(HomePage.this, "", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomePage.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initList(ArrayList<ArrayList<String>>  data){
        progressBar.setVisibility(View.GONE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        homePageAdapter = new HomePageAdapter(this,data);
        recyclerView.setAdapter(homePageAdapter);
    }

    private void searchItem(String s){

        for(String item : items){
            if(!Pattern.compile(Pattern.quote(s), Pattern.CASE_INSENSITIVE).matcher(item).find()){
                Listitems.remove(item);
            }
        }

        homePageAdapter.notifyDataSetChanged();
    }

    private Boolean getUserLogin(){
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);

        Boolean login = sharedPreferences.getBoolean("login",false);
        return login;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageuri = CropImage.getPickImageResultUri(this,data);

            if(CropImage.isReadExternalStoragePermissionsRequired(this,imageuri)){
                uri = imageuri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
            }
            else{
                startCrop(imageuri);
            }
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                try {
                    imageBitmap =  MediaStore.Images.Media.getBitmap(this.getContentResolver(), result.getUri());
                } catch (Exception e) {
                    Log.d("ERROR",e.getMessage());
                }
            }
        }
    }

    private void startCrop(Uri imageuri) {
        CropImage.activity(imageuri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    private void detectTextFromImage(){
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        detector.processImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                displayTextFromImage(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HomePage.this,"Error " + e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayTextFromImage(FirebaseVisionText firebaseVisionText){

        List<FirebaseVisionText.TextBlock> blockList = firebaseVisionText.getTextBlocks();

        if(blockList.size() == 0){
            Toast.makeText(HomePage.this,"Cannot detect any text",Toast.LENGTH_SHORT).show();
        }
        else{
            for(FirebaseVisionText.TextBlock block: firebaseVisionText.getTextBlocks()){
                String text = block.getText();

                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                intent.putExtra("textToSearch",text);
                startActivity(intent);
                //search.setText(text);
            }
        }
    }

    //Verify Permissions
    private void verifyPermissions(){
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[1]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[2]) == PackageManager.PERMISSION_GRANTED)
        {
            CropImage.activity().start(HomePage.this);
        }
        else{
            ActivityCompat.requestPermissions(HomePage.this,permissions,1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length == 3
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
                && grantResults[2] == PackageManager.PERMISSION_GRANTED)
        {
            bottomNavigationView.setSelectedItemId(R.id.camera_btn);
            CropImage.activity().start(HomePage.this);
        }
        else {
            startActivity(new Intent(getApplicationContext(), HomePage.class));
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        bottomNavigationView.setSelectedItemId(R.id.home);
        getVideodataFromDatabase();

        //Camera button Effects
        final Animation anim = AnimationUtils.loadAnimation(HomePage.this, R.anim.camera_bounce);
        camera_btn.startAnimation(anim);

        if(imageBitmap != null){
            detectTextFromImage();
        }
    }
}
