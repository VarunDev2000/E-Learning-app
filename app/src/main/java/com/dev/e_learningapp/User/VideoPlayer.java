package com.dev.e_learningapp.User;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.dev.e_learningapp.R;

public class VideoPlayer extends AppCompatActivity{

    private VideoView videoView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        String videoUri = getIntent().getStringExtra("videoUri");

        videoView = findViewById(R.id.videoView);
        progressBar = findViewById(R.id.progressBar);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);

        Uri uri = Uri.parse(videoUri);

        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);

        progressBar.setVisibility(View.VISIBLE);


        final MediaPlayer.OnInfoListener onInfoToPlayStateListener = new MediaPlayer.OnInfoListener() {

            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {

                if (MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START == what) {
                    progressBar.setVisibility(View.GONE);
                }
                return false;
            }
        };

        videoView.setOnInfoListener(onInfoToPlayStateListener);
        videoView.start();
    }

    @Override
    public void onBackPressed() {

        finish();
        Intent intent = new Intent(getApplicationContext(), HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
