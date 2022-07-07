package com.example.camera

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener


public const val Request_Code = 42
class MainActivity : AppCompatActivity() {

        private lateinit var resultLauncher: ActivityResultLauncher<Intent>
        private var currentImagePath: String? = null
    private var currentImageUri: Uri? = null

        private lateinit var btnOpenCamera: Button
        private lateinit var ivPhoto: ImageView

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            btnOpenCamera = findViewById(R.id.btnOpenCamera)
            ivPhoto = findViewById(R.id.ivImage)

            //result of open camera
            resultLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        handleCameraImage(result.data)
                    }
                }


            btnOpenCamera.setOnClickListener {
                openCamera()

            }


        }

    private fun openCamera() {

        //ask camera and read storage permission
        val permissionRequests = mutableListOf<String>()
        permissionRequests.add(Manifest.permission.READ_EXTERNAL_STORAGE)

        //ask write external storage permission when sdk is less than 28
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            permissionRequests.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        Dexter.withContext(this)
            .withPermissions(
                permissionRequests
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) { /* ... */

                    if (report.areAllPermissionsGranted()) {
                        Log.d(TAG, "onPermissionsChecked: all granted")

                        val values = ContentValues()
                        values.put(MediaStore.Images.Media.TITLE, "New Picture")
                        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")
                        currentImageUri = contentResolver.insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
                        )
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, currentImageUri)
                        resultLauncher.launch(intent)


                    } else {
                        Log.d(TAG, "onPermissionsChecked: not granted")
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) { /* ... */
                }
            }).check()


    }
    private fun handleCameraImage(data: Intent?) {
        // we are using glide because it prevent image rotation after image click

            Glide.with(this).load(currentImageUri).into(ivPhoto)


    }


    }




