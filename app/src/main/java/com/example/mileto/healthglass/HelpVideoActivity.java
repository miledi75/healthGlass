package com.example.mileto.healthglass;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;


public class HelpVideoActivity extends AppCompatActivity
//{

    private static final int RECOVERY_REQUEST = 1;
    private String youtubeLinkIdentifier;
    private String protocolItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_video);

        //get the protocolItemId from the sending intent
        Intent intent   = getIntent();
        //protocolItemId  = intent.getStringExtra("protocolItemId").toString();

        //@todo implement rest call to get the corresponding youtube link with the educational clip

        //for now use the testlink https://www.youtube.com/watch?v=W17Icz3o0Hk
        youtubeLinkIdentifier = "W17Icz3o0Hk";

        if(youtubeLinkIdentifier == "")
        {
            //if theres is no identifier display message
            Toast.makeText(getApplicationContext(),"No instructional video available",Toast.LENGTH_SHORT).show();
            //Go back to the calling intent
            finish();
        }

        String SrcPath = "http://chamilo.arteveldehs.be/index.php?go=document_downloader&object=1411819&security_code=c95f82d406888a297500036c22168c6419d28300&application=repository&display=1";
        VideoView myVideoView = (VideoView)findViewById(R.id.myvideoview);
        myVideoView.setVideoURI(Uri.parse(SrcPath));

        myVideoView.setMediaController(new MediaController(this));
        myVideoView.requestFocus();
        myVideoView.start();
    }
}
