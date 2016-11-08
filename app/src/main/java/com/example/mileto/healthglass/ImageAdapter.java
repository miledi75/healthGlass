package com.example.mileto.healthglass;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by mileto.di.marco on 10/10/2016.
 */

public class ImageAdapter extends BaseAdapter
{
    private Context myContext;
    private List<String> pathToImages;
    private String imagePath;

    public String getImagePath()
    {
        return imagePath;
    }



    public ImageAdapter(Context mC, List<String> images)
    {
        this.myContext      = mC;
        this.pathToImages   = images;
    }

    @Override
    public int getCount()
    {
        return pathToImages.toArray().length;
    }

    @Override
    public Object getItem(int position)
    {
        return pathToImages.toArray()[position];
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ImageView imageView;
        if (convertView == null)
        {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(myContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(5, 5, 5, 5);
        }
        else
        {
            imageView = (ImageView) convertView;
        }
        FileInputStream fs  = null;
        Bitmap          bm;
        try
        {
            Log.d("FileDirectory:", pathToImages.get(position));
            fs = new FileInputStream(new File(pathToImages.get(position)));
            File imageFile = new File(pathToImages.get(position));
            this.imagePath = pathToImages.get(position);
            if(fs!=null)
            {
                Picasso.with(myContext).load(imageFile).centerCrop().fit().into(imageView);
            }
            else
            {
                Log.d("GridViewFile:","Cannot find file:"+ pathToImages.get(position));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(fs!=null)
            {
                try
                {
                    fs.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return imageView;
    }
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
    {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(FileDescriptor fd, int reqWidth, int reqHeight)
    {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd,null,options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fd,null,options);
    }
}
