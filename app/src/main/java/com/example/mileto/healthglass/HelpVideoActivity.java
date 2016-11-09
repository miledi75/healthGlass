package com.example.mileto.healthglass;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class HelpVideoActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener
{

    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView myYoutubePlayer;
    private String youtubeLinkIdentifier;
    private String protocolItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_video);

        //get the protocolItemId from the sending intent
        Intent intent   = getIntent();
        protocolItemId  = intent.getStringExtra("protocolItemId");

        //@todo implement rest call to get the corresponding youtube link with the educational clip

        //for now use the testlink https://www.youtube.com/watch?v=W17Icz3o0Hk
        youtubeLinkIdentifier = "W17Icz3o0Hk";

        //if theres is no identifier display message
        Toast.makeText(getApplicationContext(),"No instructional video available",Toast.LENGTH_SHORT).show();
        //Go back to the calling intent
        finish();

        //intialize the player
        myYoutubePlayer = (YouTubePlayerView) findViewById(R.id.youtubeViewer);
        //Use the API key from google cloud to access and initialize the API
        myYoutubePlayer.initialize(Config.YOUTUBE_API_KEY,this);


    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b)
    {
        if (!b)
        {
            // https://www.youtube.com/watch?v=W17Icz3o0Hk
            youTubePlayer.cueVideo("fhWaJi1Hsfo"); // https://www.youtube.com/watch?v=W17Icz3o0Hk
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult)
    {
        if (youTubeInitializationResult.isUserRecoverableError())
        {
            youTubeInitializationResult.getErrorDialog(this, RECOVERY_REQUEST).show();
        }
        else
        {
            String error = youTubeInitializationResult.toString();
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
