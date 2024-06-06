package com.tarun.ai_chatbot

import android.graphics.Bitmap

data class ChatState(
    val chatlist:MutableList<Chat> = mutableListOf(),
    val prompt:String="",
    val bitmap:Bitmap?=null
)
