package com.tarun.ai_chatbot

import android.graphics.Bitmap
import android.graphics.Paint.Align
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Send

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.res.stringResource

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.size.Size

import com.tarun.ai_chatbot.ui.theme.Ai_chatbotTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class MainActivity : ComponentActivity() {

    private val uriState = MutableStateFlow("")

    private val selectedImageUri =
        registerForActivityResult<PickVisualMediaRequest, Uri>(
            ActivityResultContracts.PickVisualMedia()
        ){uri ->
            uri?.let{
                uriState.update { uri.toString() }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Ai_chatbotTheme {
                Surface (modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background){
                    Scaffold (topBar = {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primary)
                            .height(55.dp)
                            .padding(horizontal = 16.dp)){
                            Text(text =  stringResource(id = R.string.app_name), fontSize = 19.sp,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.align(Alignment.CenterStart))
                        }
                    }){ innerpadding ->
                        ChatScreen(paddingValues = innerpadding)
                    }

                }

            }
        }
    }
    @Composable
    fun ChatScreen(paddingValues: PaddingValues){
        val chatViewModel = viewModel<ChatViewModel>()
        val chatState = chatViewModel.chilstate.collectAsState().value
        val bitmap = getBitmap()
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(top = paddingValues.calculateTopPadding()), verticalArrangement = Arrangement.Bottom ) {
            LazyColumn(modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
                reverseLayout = true){
                        itemsIndexed(chatState.chatlist){index, chat ->
                            if(chat.isfromuser){
                                UserChatItem(promp = chat.prompt, bitmap =chat.bitmap )
                            }else{
                                ModelChatItem(response = chat.prompt)
                            }
                        }
            }
            Row (modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp), verticalAlignment = Alignment.CenterVertically){
                Column {
                    bitmap?.let {
                        Image(modifier = Modifier
                            .size(40.dp)
                            .padding(bottom = 2.dp)
                            .clip(RoundedCornerShape(6.dp)), contentDescription = "picked image",
                            contentScale = ContentScale.Crop,
                            bitmap = it.asImageBitmap())
                    }
                    Icon(modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            selectedImageUri.launch(
                                PickVisualMediaRequest
                                    .Builder()
                                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    .build()
                            )
                        }, imageVector = Icons.Rounded.AddCircle, contentDescription ="Add Photo", tint = MaterialTheme.colorScheme.primary )
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextField(modifier = Modifier.weight(1f),
                    value = chatState.prompt, onValueChange ={
                    chatViewModel.onEvent(ChatUiEvent.Updateprompt(it))
                }, placeholder = {
                    Text(text = "Type a prompt")
                }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        chatViewModel.onEvent(ChatUiEvent.Sendprompt(chatState.prompt,bitmap
                        ))
                    }, imageVector = Icons.Rounded.Send, contentDescription ="Send Prompt"
                    , tint = MaterialTheme.colorScheme.primary )


            }
        }
    }

    @Composable
    fun UserChatItem(promp: String,bitmap: Bitmap?){
        Column(modifier = Modifier.padding(start = 100.dp, bottom = 22.dp)) {
            bitmap?.let {
                Image(modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(bottom = 2.dp)
                    .clip(RoundedCornerShape(12.dp)), contentDescription = "image", contentScale = ContentScale.Crop,
                    bitmap = it.asImageBitmap())
            }
            Text(modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp), text = promp, fontSize = 17.sp, color = MaterialTheme.colorScheme.onPrimary)
        }
    }

    @Composable
    fun ModelChatItem(response: String){
        Column(modifier = Modifier.padding(end = 100.dp, bottom = 22.dp)) {

            Text(modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.tertiary)
                .padding(16.dp), text = response,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onTertiary)
        }
    }

    @Composable
    private fun getBitmap(): Bitmap? {
        val uri = uriState.collectAsState().value

        val imageState : AsyncImagePainter.State= rememberAsyncImagePainter(model = ImageRequest
            .Builder(LocalContext.current).data(uri).size(Size.ORIGINAL).build()).state
        if(imageState is AsyncImagePainter.State.Success){
            return imageState.result.drawable.toBitmap()
        }
        return null
    }
}

