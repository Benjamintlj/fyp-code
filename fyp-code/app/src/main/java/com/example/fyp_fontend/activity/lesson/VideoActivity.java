package com.example.fyp_fontend.activity.lesson;

import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import android.content.Intent;
import android.media.browse.MediaBrowser;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.fragments.ContinueFragment;
import com.example.fyp_fontend.utils.ContentManager;

public class VideoActivity extends AppCompatActivity {
    private static final String TAG = "VideoActivity";


    private PlayerView playerView;
    private ExoPlayer exoPlayer;
    private String streamUrl;
    private boolean playerViewPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        streamUrl = getIntent().getStringExtra("videoUrl");

        initView();
        prepareVideo();
        initListeners();
    }

    @OptIn(markerClass = UnstableApi.class) private void initView() {
        playerView = findViewById(R.id.playerView);
        playerView.setUseController(false);

        ContinueFragment continueFragment = new ContinueFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, continueFragment).commit();
    }

    private void prepareVideo() {
        exoPlayer = new ExoPlayer.Builder(getApplicationContext()).build();
        playerView.setPlayer(exoPlayer);

        // Load video content
        if (streamUrl != null) {
            MediaItem mediaItem = MediaItem.fromUri(streamUrl);
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
        } else {
            Toast.makeText(getApplicationContext(), "Something went wrong while loading the video", Toast.LENGTH_LONG).show();
            finish();
        }

        // start the video
        exoPlayer.play();
    }

    @OptIn(markerClass = UnstableApi.class) private void initListeners() {
        playerView.setOnClickListener(view -> {
            if (!playerViewPressed) {
                playerView.setUseController(true);
                playerView.showController();
                playerViewPressed = true;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        exoPlayer.pause();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        exoPlayer.release();
        exoPlayer = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ContentManager.ContentManagerNewActivity.NEXT_ITEM.ordinal()) {
            finish();
        }
    }
}