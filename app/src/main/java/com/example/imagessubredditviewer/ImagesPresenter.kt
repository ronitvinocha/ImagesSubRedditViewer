package com.example.imagessubredditviewer

import org.json.JSONObject
import java.lang.Exception

class ImagesPresenter(private var iImageApiResult: IImageApiResult):OnApiResponse,IImagePresenter{
    private var IMAGESURLLIST:MutableList<String> = ArrayList()
    override fun onCallComplete(success: Boolean, response: String?, callerId: Int) {
        if(success)
        {
            try {
                val json= JSONObject(response)
                val children=json.getJSONObject("data").getJSONArray("children")
                for(i in 0 until children.length())
                {
                    val url=children.getJSONObject(i).getJSONObject("data").getString("url")
                    if(url.endsWith(".jpeg")|| url.endsWith(".jpg"))
                    {
                         IMAGESURLLIST.add(url)
                    }
                }
                iImageApiResult.onApiCallback(IMAGESURLLIST)
            }
            catch (ex:Exception)
            {
                println("❗️"+ex.localizedMessage)
            }
        }
    }

    override fun callApi() {
        val apiTask=APITask("https://www.reddit.com/r/ArchitecturePorn.json",this, SUB_REDDIT,null)
        apiTask.execute(null)
    }

    companion object {
        private const val SUB_REDDIT = 0x01
    }

}