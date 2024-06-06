package com.tarun.ai_chatbot

import android.graphics.Bitmap

data class Chat(val prompt:String,
    val bitmap: Bitmap?,
    val isfromuser:Boolean)
