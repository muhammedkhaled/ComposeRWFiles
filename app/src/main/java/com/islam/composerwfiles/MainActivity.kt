package com.islam.composerwfiles

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.islam.composerwfiles.ui.theme.ComposeRWFilesTheme
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeRWFilesTheme {
                RWFiles()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @Composable
    fun PermissionScreen(context: Context) {
        val activity = context as Activity
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE
                ),
                23
            )
            RWFiles()
        } else {
            if (Environment.isExternalStorageManager()) {
                RWFiles()
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse(String.format("package:%s", context.packageName))
                context.startActivity(intent)
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.R)
    @Composable
    fun RWFiles() {
        val context = LocalContext.current
        val activity = context as Activity
        val message = remember { mutableStateOf("") }
        val txtMsg = remember { mutableStateOf("") }
        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(
            key1 = lifecycleOwner,
            effect = {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_START) {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                            ActivityCompat.requestPermissions(
                                activity,
                                arrayOf(
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.MANAGE_EXTERNAL_STORAGE
                                ),
                                23
                            )
                        } else {
                            if (Environment.isExternalStorageManager()) {

                            } else {
                                val intent =
                                    Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                                intent.addCategory("android.intent.category.DEFAULT")
                                intent.data =
                                    Uri.parse(String.format("package:%s", context.packageName))
                                context.startActivity(intent)
                            }
                        }

                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)

                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = message.value,
                onValueChange = { message.value = it },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(
                        text = "Type Here.."
                    )
                })
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = txtMsg.value)
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Button(onClick = {
                    val folder: File =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

                    val file = File(folder, "Hassan.txt")
                    writeFiles(file, message.value, context)
                    Toast.makeText(context, "DATA SAVED", Toast.LENGTH_SHORT).show()

                }) {
                    Text(text = "Save")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = {
                    val folder =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    val file = File(folder, "Hassan.txt")
                    val data: String = getData(file)
                    txtMsg.value = data

                }) {
                    Text(text = "Load")
                }
            }
        }
    }


    private fun getData(myFile: File): String {
        var fileInPutStream: FileInputStream? = null
        try {
            fileInPutStream = FileInputStream(myFile)
            var i = -1
            val buffer = StringBuffer()
            while (fileInPutStream.read().also { i = it } != -1) {
                buffer.append(i.toChar())
            }
            return buffer.toString()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            if (fileInPutStream != null) {
                try {
                    fileInPutStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return ""
    }


    private fun writeFiles(file: File, data: String, context: Context) {
        var fileOutputStream: FileOutputStream? = null
        try {
            fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(data.toByteArray())
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @Preview(showBackground = true)
    @Composable
    fun RWFilesPreview() {
        RWFiles()
    }

}



