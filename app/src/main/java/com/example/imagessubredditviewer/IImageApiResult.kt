package com.example.imagessubredditviewer

interface IImageApiResult{
    fun onApiCallback(imagesUrlList: MutableList<String>)
}