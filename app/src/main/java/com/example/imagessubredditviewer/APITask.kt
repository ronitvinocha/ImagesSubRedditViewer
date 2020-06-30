package com.example.imagessubredditviewer


import android.os.AsyncTask

import java.io.IOException
import java.util.ArrayList
import java.io.OutputStreamWriter
import java.io.BufferedWriter
import java.io.UnsupportedEncodingException
import java.io.InputStreamReader
import java.io.BufferedReader
import java.net.*


interface OnApiResponse {
    fun onCallComplete(success: Boolean, response: String?, callerId: Int)
}

interface OnApiTimeout {
    fun onTimeout()
}

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
class APITask(
    private val mUrl: String,
    private val responseCallback: OnApiResponse, reqId: Int,
    private val postData: ArrayList<Pair<String, String>>?
) : AsyncTask<Void, Void, Boolean>() {
    private var response: String? = null
    private var callerId: Int = reqId
    private var isTimeout = false
    private var timeoutCallback: OnApiTimeout? = null

    init {
        response = ""
    }

    @Throws(UnsupportedEncodingException::class)
    private fun getQuery(params: ArrayList<Pair<String, String>>): String {
        val result = StringBuilder()
        var first = true

        for (pair in params) {
            if (first)
                first = false
            else
                result.append("&")

            result.append(URLEncoder.encode(pair.first, "UTF-8"))
            result.append("=")
            result.append(URLEncoder.encode(pair.second, "UTF-8"))
        }

        return result.toString()
    }

    override fun doInBackground(vararg params: Void): Boolean? {
        try {
            val aUrl = URL( mUrl)

            val conn = aUrl.openConnection() as HttpURLConnection
            //conn.setRequestProperty("Authorization", authHeader)
            if (postData != null) {
                conn.requestMethod = "POST"
                conn.readTimeout = 10000
                conn.connectTimeout = 15000
                conn.requestMethod = "POST"
                conn.doInput = true
                conn.doOutput = true

                conn.setRequestProperty("charset", "utf-8")
                val os = conn.outputStream
                val writer = BufferedWriter(
                    OutputStreamWriter(os, "UTF-8")
                )
                writer.write(getQuery(postData))
                writer.flush()
                writer.close()
                os.close()
            } else {
                conn.requestMethod = "GET"
            }
            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                var line: String?
                val br = BufferedReader(InputStreamReader(conn.inputStream))
                do {
                    line = br.readLine()
                    if(line!=null) {
                        response += line
                    }
                } while (line != null)
            } else {
                response = ""
            }
        } catch(e:SocketTimeoutException) {
            isTimeout = true
            return false
        }catch (e: MalformedURLException) {
            e.printStackTrace()
            return false
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
        return true
    }

    override fun onPostExecute(success: Boolean) {
//        val runnable = Runnable {
            if(isTimeout && timeoutCallback != null) {
                timeoutCallback!!.onTimeout()
            } else {
                responseCallback.onCallComplete(success, response, callerId)
            }
//        }
//        val handler = Handler()
//        handler.postDelayed(runnable, 2000)
    }

    override fun onCancelled() {
        responseCallback.onCallComplete(false, response, callerId)
    }
}

