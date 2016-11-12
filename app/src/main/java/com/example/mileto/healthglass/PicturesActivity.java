package com.example.mileto.healthglass;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.graphics.Bitmap.createBitmap;

public class PicturesActivity extends AppCompatActivity
{

    private static final int    REQUEST_IMAGE_CAPTURE = 20;
    private static final String DIRECTORY_NAME="/woundCare";
    private Button              AddimagesButton;
    private GridView            imageGridView;
    private String              imagesDirectoryPath;
    private Uri                 imageUri;
    private List<String>        imagesPathList;
    private String              patientIdFromBarcode;
    private String              protocolId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures);

        //get the protocolId from the PatientInfoActivity Intent
        Intent fromProtocolActivity         = getIntent();
        this.patientIdFromBarcode           = fromProtocolActivity.getStringExtra("patientId");
        this.protocolId                     = fromProtocolActivity.getStringExtra("protocolId");

        //initialize directory path
        imagesDirectoryPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES)+ DIRECTORY_NAME+"/"+this.patientIdFromBarcode+"/"+this.protocolId;
        Log.d("imageDirectory:","Path:"+imagesDirectoryPath);
        //initialize UI elements
        AddimagesButton = (Button) findViewById(R.id.buttonAddPicture);

        imageGridView           = (GridView) findViewById(R.id.imagesGriedViewToDo);

        //check if the request is coming from a performed protocol
        if(fromProtocolActivity.getStringExtra("performed") != null)
        {
            //disable/hide the add image button
            AddimagesButton.setVisibility(View.INVISIBLE);
        }

        //onclicklistener for the picturebutton
        AddimagesButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                activateCameraIntent();
            }
        });

        //gridview clicklistener
        imageGridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override

            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                ImageAdapter imageAdapter = (ImageAdapter) parent.getAdapter();
                String imagePath = imageAdapter.getItem(position).toString();
                //Call an intent to display the fullblown picture along with delete button
                //Add the path to the picture to the intent using PutExtra() method
                Intent fullSizeImageActivity = new Intent(getApplicationContext(), ViewImageFullsizeActivity.class);
                fullSizeImageActivity.putExtra("imagePath", imagePath);
                startActivity(fullSizeImageActivity);
                //Log.d("GridViewClickListener:","Path:"+imageAdapter.getImagePath());
            }
        });
    }

    private void loadGridviewWithImages()
    {
        //fill the list with the images paths
        imagesPathList = retrieveCapturedImagesPath();
        if(imagesPathList!=null)
        {
            imageGridView.setAdapter(new ImageAdapter(this,imagesPathList));
        }
    }

    private void activateCameraIntent()
    {
        //call the camera intent

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            File pictureFile = null;

            pictureFile = getFileForPicture();

            if(pictureFile != null)
            {
                this.imageUri = Uri.fromFile(pictureFile);

                //Log.d("FileUri:",Uri.fromFile(pictureFile).getPath());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,this.imageUri);
                //perform an extra check to see if the android camerapermissions are set in the manifest
                //from android m+
                //manifest permission should suffice for the M100 as it's running android 15
                startActivityForResult(takePictureIntent, 1);
            }
            else
            {
                Log.d("CameraIntent:","PictureFile is null!:"+pictureFile.getAbsolutePath());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {

            imagesPathList = null;
            imagesPathList = retrieveCapturedImagesPath();
            if (imagesPathList != null)
            {
                imageGridView.setAdapter(new ImageAdapter(this, imagesPathList));
            }
        }
    }

    private List<String> retrieveCapturedImagesPath()
    {
        List<String> listOfImagesInDirectory = new ArrayList<String>();
        File filesInImageDirectory = new File(imagesDirectoryPath);
        if(filesInImageDirectory.exists())
        {
            File[] imageFiles=filesInImageDirectory.listFiles();
            for(int i=0;i<imageFiles.length;i++)
            {
                File tempFile = imageFiles[i];
                if(tempFile.isDirectory())
                {
                    continue;
                }
                if(tempFile == null)
                {
                    Log.d("ArrayIamges:","File null:"+tempFile.getPath());
                }
                listOfImagesInDirectory.add(tempFile.getPath());
                //Log.d("ImageListDirectory",tempFile.getPath());
            }
        }
        else
        {
            Log.d("ImageListDirectory","No files in directory!");
        }
        return listOfImagesInDirectory;
    }

    private File getFileForPicture()
    {
        //Create directory if it does not exist
        File imageDirectory = new File(imagesDirectoryPath);
        if(!imageDirectory.exists())
        {
            imageDirectory.mkdirs();
        }

        DateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String imgcurTime = date.format(new Date());
        String _path = imageDirectory.getPath() + File.separator + imgcurTime.replace(" ","_")+"_patientID_"+ this.patientIdFromBarcode + ".jpg";

        File pictureFile = new File(_path);
        //this.imagePath = pictureFile.getAbsolutePath();
        return pictureFile;
    }

    private  Uri getOutputMediaFileUri()
    {
        return Uri.fromFile(getOutputMediaFile());
    }

    private File getOutputMediaFile()
    {
        File mediaStorageDir = new File(getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");

        // check if the storage directory exists
        if (! mediaStorageDir.exists())
        {
            if (! mediaStorageDir.mkdirs())
            {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");

        return mediaFile;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        loadGridviewWithImages();
    }
}
