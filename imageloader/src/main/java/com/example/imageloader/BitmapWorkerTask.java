package com.example.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

interface BitworkerTaskRespone {
     void onCallComplete(Bitmap bitmap);
}

public class BitmapWorkerTask  extends AsyncTask<Integer, Void, Bitmap> {
    FileCache fileCache;
    BitworkerTaskRespone bitworkerTaskRespone;
    String url;
    BitmapWorkerTask(BitworkerTaskRespone bitworkerTaskRespone, Context context,String url)
    {
        fileCache=new FileCache(context);
        this.bitworkerTaskRespone=bitworkerTaskRespone;
        this.url=url;
    }
    @Override
    protected Bitmap doInBackground(Integer... integers) {
        Bitmap bitmap=null;
        try{
            File f=fileCache.getFile(url);
            if(f.exists())
            {
                Log.i("from disk",f.getPath());
                bitmap=decodeFile(f);
            }
            else
            {
                URL imageUrl = new URL(url);
                Log.i("from url",url);
                HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
                conn.setConnectTimeout(30000);
                conn.setReadTimeout(30000);
                conn.setInstanceFollowRedirects(true);
                InputStream is=conn.getInputStream();
                OutputStream os = new FileOutputStream(f);
                //storing file in disk
                Utils.CopyStream(is, os);
                os.close();
                bitmap = decodeFile(f);
            }
            return bitmap;
        }
        catch (Exception ex)
        {
            Log.i("❗️",ex.getMessage());
            ex.printStackTrace();
            return bitmap;
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        bitworkerTaskRespone.onCallComplete(bitmap);
    }
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
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
    private Bitmap decodeFile(File f){
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inPreferredConfig = Bitmap.Config.RGB_565;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);
            o.inSampleSize=calculateInSampleSize(o,300,300);
            o.inJustDecodeBounds=false;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o);
        } catch (FileNotFoundException e) {
            Log.i("❗️",e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
