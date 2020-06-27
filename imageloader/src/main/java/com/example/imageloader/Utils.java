package com.example.imageloader;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
//Logic for writing output stream
public class Utils {
    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
              os.close();
              is.close();
            }
        }
        catch(Exception ex){
            Log.i("❗️",ex.getMessage());
            ex.printStackTrace();
        }
    }
}

