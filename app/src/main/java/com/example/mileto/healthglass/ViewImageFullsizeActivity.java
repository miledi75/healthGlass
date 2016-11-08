package com.example.mileto.healthglass;

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

import java.io.File;


public class ViewImageFullsizeActivity extends AppCompatActivity {

    private ImageView fullSizeImage;
    private Button deleteImageButton;
    private Button rotateImageButton;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image_fullsize);
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

}
