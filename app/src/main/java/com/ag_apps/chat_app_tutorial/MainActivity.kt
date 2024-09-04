package com.ag_apps.chat_app_tutorial

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import com.ag_apps.chat_app_tutorial.ui.theme.ChatAppTutorialTheme
import com.zegocloud.zimkit.common.ZIMKitRouter
import com.zegocloud.zimkit.common.enums.ZIMKitConversationType
import com.zegocloud.zimkit.components.conversation.ui.ZIMKitConversationFragment
import com.zegocloud.zimkit.services.ZIMKit
import com.zegocloud.zimkit.services.ZIMKitConfig
import im.zego.zim.enums.ZIMErrorCode

class MainActivity : FragmentActivity() {

    private var openConversations by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        init()
        setContent {
            ChatAppTutorialTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Screen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    @Composable
    fun Screen(modifier: Modifier = Modifier) {
        if (openConversations) {
            ConversationsScreen()
        } else {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        connectUser()
                    }
                ) {
                    Text(text = "Open Conversations")
                }
            }
        }
    }

    @Composable
    fun ConversationsScreen(modifier: Modifier = Modifier) {
        val fragmentManager = remember {
            this@MainActivity.supportFragmentManager
        }

        val fragment = remember {
            ZIMKitConversationFragment()
        }

        AndroidView(
             modifier = modifier,
            factory = {
                FrameLayout(it).apply {
                    id = View.generateViewId()
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            update = {
                fragmentManager.beginTransaction().replace(
                    it.id, fragment
                ).commit()
            }
        )
    }

    private fun connectUser() {
        val useId = "user2"
        val useName = "user2"
        val userImage = "https://storage.zego.im/IMKit/avatar/avatar-0.png"

        ZIMKit.connectUser(useId, useName, userImage) { info ->
            if (info.code == ZIMErrorCode.SUCCESS) {
                openConversations = true
                startChat("user1")
            } else {
                Toast.makeText(
                    this,
                    "Connect user failed: ${info.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    private fun startChat(userId: String) {
        ZIMKitRouter.toMessageActivity(
            this,
            userId,
            ZIMKitConversationType.ZIMKitConversationTypePeer
        )
    }

    private fun init() {
        val appID = 0L
        val appSign = "xxx"
        ZIMKit.initWith(application, appID, appSign, ZIMKitConfig())
        ZIMKit.initNotifications()
    }
}


