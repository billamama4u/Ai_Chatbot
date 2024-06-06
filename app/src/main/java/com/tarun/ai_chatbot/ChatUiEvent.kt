package com.tarun.ai_chatbot

import android.graphics.Bitmap

sealed class ChatUiEvent{

    data class Updateprompt(val newPrompt:String):ChatUiEvent()
    data class Sendprompt(val prompt:String,val bitmap: Bitmap?):ChatUiEvent()

}
