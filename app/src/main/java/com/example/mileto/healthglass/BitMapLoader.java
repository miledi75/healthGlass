package com.example.mileto.healthglass;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileDescriptor;

/**
 * Created by mileto.di.marco on 19/10/2016.
 */

public class BitMapLoader
{
    private BitmapFactory.Options bmfOptions;
    private int requestedWidth;
    private int requestedHeight;
    private FileDescriptor fd;

    public BitMapLoader(FileDescriptor fd, int requestedWidth, int requestedHeight)
    {
        this.bmfOptions         = new BitmapFactory.Options();
        this.fd                 = fd;
        this.requestedHeight    = requestedHeight;
        this.requestedWidth     = requestedWidth;
    }

    public int calculateInSampleSize()
    {
        // Raw height and width of image
        final int height = bmfOptions.outHeight;
        final int width = bmfOptions.outWidth;
        int inSampleSize = 1;

        if (height > requestedHeight || width > requestedWidth)
        {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= requestedHeight
                    && (halfWidth / inSampleSize) >= requestedWidth)
            {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public Bitmap decodeSampledBitmapFromResource()
    {

        // First decode with inJustDecodeBounds=true to check dimensions
        bmfOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd,null,bmfOptions);

        // Calculate inSampleSize
        bmfOptions.inSampleSize = calculateInSampleSize();

        // Decode bitmap with inSampleSize set
        bmfOptions.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fd,null,bmfOptions);
    }

}
