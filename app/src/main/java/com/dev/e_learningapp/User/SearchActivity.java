package com.dev.e_learningapp.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dev.e_learningapp.Others.SearchActivityAdapter;
import com.dev.e_learningapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.regex.Pattern;


public class SearchActivity extends AppCompatActivity {

    private Uri uri;
    private String textToSearch;

    private RecyclerView recyclerView;
    private SearchActivityAdapter searchActivityAdapter;
    private String[] items;
    private ArrayList<ArrayList<String>> Listitems,finalItems;

    private EditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        //search in RecyclerView
        search = findViewById(R.id.search);

        textToSearch =  getIntent().getExtras().getString("textToSearch");
        search.setText(textToSearch);

        search.requestFocus();

        //Recycler View
        recyclerView = findViewById(R.id.recyclerView);
        getVideodataFromDatabase();



        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    initList();
                }

                else{
                    searchItem(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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
                    initList();
                    searchItem(textToSearch);
                    Log.d("TAG",""+Listitems);
                }

                else{
                    Toast.makeText(SearchActivity.this, "", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SearchActivity.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initList(){
        finalItems = new ArrayList<ArrayList<String>>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchActivityAdapter = new SearchActivityAdapter(this,finalItems);
        recyclerView.setAdapter(searchActivityAdapter);
    }

    private void searchItem(String s){

        if(!s.equals("")) {
            finalItems.clear();

            for (ArrayList<String> item : Listitems) {
                if (Pattern.compile(Pattern.quote(s), Pattern.CASE_INSENSITIVE).matcher(item.get(1)).find()) {
                    Log.d("TAG", "" + item);
                    finalItems.add(item);
                }
            }

            searchActivityAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {

        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair <View,String>(search,"searchbartransition");

        finish();
        Intent intent = new Intent(getApplicationContext(), HomePage.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SearchActivity.this, pairs);
        startActivity(intent,options.toBundle());
    }

}
