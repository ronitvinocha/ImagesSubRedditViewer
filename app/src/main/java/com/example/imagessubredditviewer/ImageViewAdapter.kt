package com.example.imagessubredditviewer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.imageloader.ImageLoader
import kotlinx.android.synthetic.main.recycleitem.view.*
import org.jetbrains.anko.find
import java.util.logging.Handler

class ImageViewAdapter(private val context:Context, private val values: List<String>): BaseAdapter()
{
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var viewHolder:ViewHolder
       var view=convertView
        if(view==null)
        {
            view=inflater.inflate(R.layout.recycleitem,null)
            viewHolder=ViewHolder()
            viewHolder.imageView=view.findViewById(R.id.recycleitemimageview)
            view.tag=viewHolder
        }
        else
        {
            viewHolder=view.tag as ViewHolder
             viewHolder.imageView?.setImageBitmap(null)
        }
        var imview=viewHolder.imageView
         var imageLoader= ImageLoader(context)
        imageLoader.DisplayImage(values[position],imview)
        return view!!
    }

    override fun getItem(position: Int): Any {
       return values[position]
    }

    override fun getItemId(position: Int): Long {
       return position.toLong()
    }

    override fun getCount(): Int {
        return values.size
    }
    inner class ViewHolder{
         var imageView:ImageView?=null
    }
}
