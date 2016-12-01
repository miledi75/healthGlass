package com.example.mileto.healthglass;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.vuzix.hardware.GestureSensor;
import com.vuzix.speech.VoiceControl;

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
    private Button              addImagesButton;
    private GridView            imageGridView;
    private String              imagesDirectoryPath;
    private Uri                 imageUri;
    private List<String>        imagesPathList;
    private String              patientIdFromBarcode;
    private String              protocolId;

    private AlertDialog.Builder goHomeDilaogBuilder;
    private AlertDialog         goHomeDialog;


    //VoiceControl and GestureControl
    private VoiceControl    mVc;
    private GestureSensor   mGc;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures);

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
            //Toast.makeText(this,"Please turn on the gestureSensor",Toast.LENGTH_SHORT).show();
            //GestureSensor.On();
        }

        //get the protocolId from the PatientInfoActivity Intent
        Intent fromProtocolActivity         = getIntent();
        this.patientIdFromBarcode           = fromProtocolActivity.getStringExtra("patientId");
        this.protocolId                     = fromProtocolActivity.getStringExtra("protocolId");

        //initialize directory path
        imagesDirectoryPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES)+ DIRECTORY_NAME+"/"+this.patientIdFromBarcode+"/"+this.protocolId;
        Log.d("imageDirectory:","Path:"+imagesDirectoryPath);
        //initialize UI elements
        addImagesButton = (Button) findViewById(R.id.buttonAddPicture);

        imageGridView           = (GridView) findViewById(R.id.imagesGriedViewToDo);

        //check if the request is coming from a performed protocol
        if(fromProtocolActivity.getStringExtra("performed") != null)
        {
            //disable/hide the add image button
            addImagesButton.setVisibility(View.INVISIBLE);
        }

        //onclicklistener for the picturebutton
        addImagesButton.setOnClickListener(new View.OnClickListener()
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
            File pictureFile;

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


    private void moveDown()
    {
        //if button is selected, go to the imagesGridView
        if(addImagesButton.hasFocus())
        {
            imageGridView.requestFocus();
            //select first item
            imageGridView.setSelection(0);
        }
        else if(imageGridView.hasFocus())
        {
            //get the selected item position
            int position = imageGridView.getSelectedItemPosition();
            //if the last item is selected, move to the button
            if(position == imageGridView.getCount()-1)
            {
                addImagesButton.requestFocus();
            }
            else
            {
                //select next item
                imageGridView.setSelection(position+1);
            }
        }
    }

    private void moveUp()
    {
        //if button is selected, move to the last item of the gridview
        if(addImagesButton.hasFocus())
        {
            imageGridView.requestFocus();
            //move to the last item
            imageGridView.setSelection(imageGridView.getCount()-1);
        }
        else if(imageGridView.hasFocus())
        {
            //get the position of the selected item
            int position = imageGridView.getSelectedItemPosition();
            Toast.makeText(getApplicationContext(),Integer.toString(position),Toast.LENGTH_SHORT).show();
            //if first item is selected, move to the button
            if(position == 0)
            {
                addImagesButton.requestFocus();
            }
            else
            {
                //go to previous item
                imageGridView.setSelection(position - 1);
            }

        }
    }

    private void handleSelection()
    {
        //check if buttin is selected
        if(addImagesButton.hasFocus())
        {
            addImagesButton.performClick();
        }
        else if(imageGridView.hasFocus())
        {
            //get position of selected item
            int position = imageGridView.getSelectedItemPosition();
            //call itemClick of selected item
            imageGridView.performItemClick(imageGridView.getChildAt(position),position,imageGridView.getItemIdAtPosition(position));
        }
    }

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
                startActivity(in);
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

    @Override
    public void onResume()
    {
        super.onResume();
        loadGridviewWithImages();
        //register voice
        try
        {
            if (mVc != null)
            {
                mVc.on();
            }
        }
        catch(Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        //register gesture
        try
        {
            if(mGc != null)
            {
                mGc.register();
            }
        }
        catch(Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause()
    {
        //deactivate voice
        try
        {
            if(mVc != null)
            {
                mVc.off();
            }
        }
        catch(Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        //unregister gesture
        try
        {
            if(mGc != null)
            {
                mGc.unregister();
            }
        }
        catch(Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        super.onPause();
    }



    @Override
    public void onDestroy()
    {
        try
        {
            if(mVc != null)
            {
                mVc.destroy();
            }
        }
        catch(Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        //unregister gesture
        try
        {
            if(mGc != null)
            {
                mGc.unregister();
                mGc = null;
            }
        }
        catch(Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        super.onDestroy();
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

            if(this.command.equals("go up"))
            {
                moveUp();
            }
            if(this.command.equals("go down"))
            {
                moveDown();
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
            moveUp();
        }

        @Override
        protected void onForwardSwipe(int i)
        {
            moveDown();
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
