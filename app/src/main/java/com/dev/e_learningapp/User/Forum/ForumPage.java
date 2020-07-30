package com.dev.e_learningapp.User.Forum;

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
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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

    private Uri uri;
    private Bitmap imageBitmap;

    private EditText post;
    private RecyclerView recyclerView;
    private ForumAdapter forumAdapter;

    private ArrayList<ArrayList<String>> Listitems;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_page);

        //BottomNav Bar
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.forum);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){

                    case R.id.home:
                        Pair[] pairs = new Pair[3];
                        pairs[0] = new Pair <View,String>(post,"searchbartransition");
                        pairs[1] = new Pair <View,String>(bottomNavigationView,"bottomnavtransition");
                        pairs[2] = new Pair <View,String>(recyclerView,"recycletransition");

                        Intent intent = new Intent(getApplicationContext(), HomePage.class);
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ForumPage.this, pairs);
                        startActivity(intent,options.toBundle());
                        return true;

                    case R.id.forum:
                        return true;

                    case R.id.camera:
                        verifyPermissions();
                        return true;

                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(), ProfilePage.class));
                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        return true;
                }

                return false;
            }
        });

        //Recycler View
        recyclerView = findViewById(R.id.recyclerView);
        getPostdataFromDatabase();


        post = findViewById(R.id.post);

        post.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair <View,String>(post,"posttransition");
                pairs[1] = new Pair <View,String>(bottomNavigationView,"bottomnavtransition");

                Intent intent = new Intent(getApplicationContext(), ForumPost.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ForumPage.this, pairs);
                startActivity(intent,options.toBundle());
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair <View,String>(post,"posttransition");
                pairs[1] = new Pair <View,String>(bottomNavigationView,"bottomnavtransition");

                Intent intent = new Intent(getApplicationContext(), ForumPost.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ForumPage.this, pairs);
                startActivity(intent,options.toBundle());
            }
        });
    }

    private void getPostdataFromDatabase(){
        Query getPostData = FirebaseDatabase.getInstance().getReference("Posts");
        Listitems = new ArrayList<>();

        getPostData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                        String name = postSnapshot.child("name").getValue(String.class);
                        String content = postSnapshot.child("content").getValue(String.class);

                        ArrayList<String> localList = new ArrayList<>();
                        localList.add(name);
                        localList.add(content);

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
        forumAdapter = new ForumAdapter(this,data);
        recyclerView.setAdapter(forumAdapter);
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
                Toast.makeText(ForumPage.this,"Error " + e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayTextFromImage(FirebaseVisionText firebaseVisionText){

        List<FirebaseVisionText.TextBlock> blockList = firebaseVisionText.getTextBlocks();

        if(blockList.size() == 0){
            Toast.makeText(ForumPage.this,"Cannot detect any text",Toast.LENGTH_SHORT).show();
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
            CropImage.activity().start(ForumPage.this);
        }
        else{
            ActivityCompat.requestPermissions(ForumPage.this,permissions,1);
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
            CropImage.activity().start(ForumPage.this);
        }
        else {
            startActivity(new Intent(getApplicationContext(), ForumPage.class));
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        bottomNavigationView.setSelectedItemId(R.id.forum);
        if(imageBitmap != null){
            detectTextFromImage();
        }
    }
}
