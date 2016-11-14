package com.example.mileto.healthglass;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.vuzix.hardware.GestureSensor;
import com.vuzix.speech.VoiceControl;

import java.io.File;


public class ViewImageFullsizeActivity extends AppCompatActivity {

    private ImageView           fullSizeImage;
    private Button              deleteImageButton;
    private Button              rotateImageButton;
    private String              imagePath;
    private VoiceControl        mVc;
    private GestureSensor       mGc;
    private AlertDialog.Builder goHomeDilaogBuilder;
    private AlertDialog         goHomeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image_fullsize);

        //initialize/activate voicecontrol and gesturecontrol
        mVc = new MyVoiceControl(this);
        mGc = new MyGestureControl(this);

        //Voicecontrol and gesturecontrol
        //activate voice control
        try
        {
            mVc = new MyVoiceControl(getApplicationContext());
            if(mVc != null)
            {
                mVc.on();
            }
        }
        catch(Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG);
        }

        //activate gestureControl
        //check if gesturesensor is on
        if(GestureSensor.isOn())
        {
            try
            {
                mGc = new MyGestureControl(getApplicationContext());
                if (mGc == null) {
                    Toast.makeText(this, "Cannot create gestureSensor", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //activate gesturesensor
                    mGc.register();
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            Toast.makeText(this,"Please turn on the gestureSensor",Toast.LENGTH_SHORT).show();
            //GestureSensor.On();
        }

        //get the file path from the intent that is sending it
        Intent gridViewImages   = getIntent();
        this.imagePath          = gridViewImages.getStringExtra("imagePath");

        //initialize imgView and button
        deleteImageButton   = (Button) findViewById(R.id.buttonDeleteImage);
        rotateImageButton   = (Button) findViewById(R.id.buttonRotateImage);
        fullSizeImage       = (ImageView) findViewById(R.id.fullSizeImageView);

        //load the picure into the imageview
        File imageFile = new File(this.imagePath);
        if(imageFile.exists())
        {
            Picasso.with(this).load(imageFile).centerCrop().fit().into(fullSizeImage);
        }


        //click event for the image rotation
        rotateImageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                rotateImage(imagePath);
            }
        });
        //Click event for the image deletion
        deleteImageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //call the picture delete function
                //implement dialog to query user on deletion
                deletePicture(imagePath);
            }
        });


    }

    private void rotateImage(String pathToPicture)
    {
        File imageFile = new File(pathToPicture);
        if (imageFile != null)
        {
            Picasso.with(this).load(imageFile).rotate(180f).centerCrop().fit().into(fullSizeImage);
        }
    }

    private void deletePicture(String pathToPicture)
    {
        //boolean to check success on picture deletion
        boolean deleted;
        final String _picToDelete = pathToPicture;
        //build a user dialog
        AlertDialog.Builder deletePictureDialogBuilder = new AlertDialog.Builder(this);
        deletePictureDialogBuilder.setMessage("Delete picture?");
        //positive clicklistener
        Log.d("DeletPic:",pathToPicture);
        deletePictureDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                File imageFile = new File(_picToDelete);
                if(imageFile.exists())
                {
                    if(imageFile.delete())
                    {
                        Toast.makeText(getApplicationContext(),"Picture deleted",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Deletion failed!",Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Deletion failed!",Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });

        //negative clickListener
        deletePictureDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        //create the dialog and show it
        AlertDialog deletePictureDialog = deletePictureDialogBuilder.create();
        deletePictureDialog.show();
    }

    /**
     * handle movement for voiceControl and gestureControl
     */
    private void move()
    {
        if(rotateImageButton.hasFocus())
        {
            deleteImageButton.requestFocus();
        }
        else
        {
            rotateImageButton.requestFocus();
        }
    }

    /**
     * takes user back to homepage
     */
    private void goHome()
    {
        //present a dialog to query user
        //build a user dialog
        goHomeDilaogBuilder = new AlertDialog.Builder(this);
        goHomeDilaogBuilder.setMessage("Go to scan page?");
        //positive clicklistener
        goHomeDilaogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //go back to the home page
                Intent in = new Intent(getApplicationContext(),ScanActivity.class);
            }
        });

        //negative clickListener
        goHomeDilaogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        //create the dialog and show it
        goHomeDialog = goHomeDilaogBuilder.create();
        goHomeDialog.show();
    }

    private void handleSelection()
    {
        //check if buttons are selected/have focus
        if(deleteImageButton.hasFocus())
        {
            deleteImageButton.callOnClick();
        }
        else if(rotateImageButton.hasFocus())
        {
            rotateImageButton.callOnClick();
        }
    }


    //INNER CLASSESS
    //inner class myvoicecontrol
    public class MyVoiceControl extends VoiceControl
    {
        private String command;
        private Context activityContext;

        public MyVoiceControl(Context context)
        {
            super(context);
            this.activityContext = context;
        }


        protected void onRecognition(String result)
        {
            this.command = result;
            if(this.command.equals("select"))
            {
                handleSelection();
            }

            if(this.command.equals("go left") || this.command.equals("go right"))
            {
                move();
            }

            if(this.command.equals("stop"))
            {
                //call the dialog to query user to go home
                goHome();
            }

            if(this.command.equals("cancel"))
            {
                //check if dialog is activated
                if (goHomeDialog.isShowing())
                {
                    goHomeDialog.dismiss();
                }

            }
            if (this.command.equals("go"))
            {
                //check if dialog is activated
                if (goHomeDialog.isShowing())
                {
                    //activate the click event of the yes button
                    goHomeDialog.getButton(DialogInterface.BUTTON_POSITIVE).callOnClick();
                }
            }

            if(this.command.equals("go back"))
            {
                finish();
            }
        }
        @Override
        public String toString()
        {
            return command;
        }
    }



    //end of Vuzix voice control class

    //inner class myGestureControl
    public class MyGestureControl extends GestureSensor
    {

        public MyGestureControl(Context context)
        {
            super(context);
        }

        @Override
        protected void onBackSwipe(int i)
        {
            move();
        }

        @Override
        protected void onForwardSwipe(int i)
        {
            move();
        }

        @Override
        protected void onNear()
        {
            handleSelection();
        }

        @Override
        protected void onFar()
        {
            finish();
        }

        @Override
        public String toString()
        {
            return super.toString();
        }
    }

   //end inner class myGestureControl


}
