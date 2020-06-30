package com.example.imageloader;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;


public class ImageLoader implements BitworkerTaskRespone{

    MemoryCache memoryCache=new MemoryCache();
    PhotoToLoad photoToLoad;
    Context context;

    public ImageLoader(Context context){
        this.context=context;

    }
    public void DisplayImage(String url,ImageView imageView)
    {
        photoToLoad=new PhotoToLoad(url,imageView,context);
         Log.i("😂🏻",photoToLoad.url);
         Log.i("😂🏻",String.valueOf(photoToLoad.imageView));
        Bitmap bitmap=memoryCache.get(photoToLoad.url);
        if(bitmap!=null)
        {
             photoToLoad.imageView.setImageBitmap(bitmap);
             Log.i("😘","from memory");
        }
        else
        {
            getBitmap(photoToLoad);
        }
    }
//
//    private void queuePhoto(String url, ImageView imageView)
//    {
//        PhotoToLoad p=new PhotoToLoad(url, imageView,imageView.getContext());
//        executorService.submit(new PhotosLoader(p));
//        //loading bitmap from either disk or network so using threadpool of 5 at a time
//    }

    private void getBitmap(PhotoToLoad photoToLoad)
    {

        BitmapWorkerTask bitmapWorkerTask=new BitmapWorkerTask(this,photoToLoad.context,photoToLoad.url);
        bitmapWorkerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    @Override
    public void onCallComplete(final Bitmap bitmap) {
         if(bitmap!=null)
         {
             memoryCache.put(String.valueOf(photoToLoad.url.hashCode()),bitmap);
             if(photoToLoad.imageView!=null)
             {
                 Log.i("✌️",bitmap.toString());
                 try{
                     photoToLoad.imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                      photoToLoad.imageView.setImageBitmap(bitmap);
                 }
                 catch (Exception e)
                 {
                     Log.i("❗️",e.getMessage());
                     e.printStackTrace();
                 }
             }
             else
             {
                 Log.i("❗️","imageview coming null ");
             }
         }
         else
         {
             Log.i("❗️","bitmap came null from bitmaptask");
         }
         //laod bitmap from memory cache as its quick no  threa
    }

    //decodes image and scales it to reduce memory consumption


    //Task for the queue
    private class PhotoToLoad
    {
        public String url;
        public ImageView imageView;
        public Context context;
        public PhotoToLoad(String u, ImageView i,Context c){
            context=c;
            url=u;
            imageView=i;
        }
    }

    public void clearCache() {
        memoryCache.clear();
    }

}