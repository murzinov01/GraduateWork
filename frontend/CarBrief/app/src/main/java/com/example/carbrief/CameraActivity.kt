package com.example.carbrief

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.*
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.carbrief.ml.Model0
import com.example.carbrief.ml.Model2
import com.example.carbrief.ml.Model4
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.io.FileOutputStream
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.support.model.Model

class CameraActivity : AppCompatActivity() {
    private lateinit var sharedPref: SharedPreferences
    private lateinit var modelPreferencesKey: String
    private lateinit var useGpuKey: String
    private lateinit var modelType: String
    private lateinit var textureView: TextureView
    private lateinit var cameraManager: CameraManager
    private lateinit var handler: Handler
    private lateinit var cameraDevice: CameraDevice
    private lateinit var imageView: ImageView
    private lateinit var bitmap: Bitmap
    private var lastBitmap: Bitmap? = null
    private lateinit var model0: Model0
    private lateinit var model2: Model2
    private lateinit var model4: Model4
    private lateinit var imageProcessor: ImageProcessor
    private lateinit var getInstructionBtn: Button
    private val paint = Paint()
    private var labels = listOf(
        Labels.CLIMATE_CONTROL.text, Labels.MUSIC_SYSTEM.text, Labels.AIR_BLOWER.text,
        Labels.DISPLAY.text, Labels.LIGHT_CONTROLLER.text, Labels.STEERING_BUTTONS.text,
        Labels.GEAR_LEVER.text, Labels.DRIVE_SWITCHING.text,
    )
    private var labelsOnColors = mutableMapOf<String, Int>()

    private var colors = listOf(
        Color.BLUE, Color.GREEN, Color.RED, Color.CYAN, Color.WHITE,
        Color.DKGRAY, Color.MAGENTA, Color.YELLOW, Color.RED)
    private lateinit var options: Model.Options

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        sharedPref = getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        modelPreferencesKey = getString(R.string.preference_model)
        useGpuKey = getString(R.string.preference_use_gpu)
        modelType = sharedPref.getString(modelPreferencesKey, "").toString()

        imageView = findViewById(R.id.imageView)
        textureView = findViewById(R.id.textureView)

        getPermission()

        for (i in labels.indices) {
            labelsOnColors[labels[i]] = colors[i]
        }

        val compatList = CompatibilityList()

        options = if(sharedPref.getBoolean(useGpuKey, false) && compatList.isDelegateSupportedOnThisDevice) {
            Model.Options.Builder().setDevice(Model.Device.GPU).build()
        } else {
            Model.Options.Builder().setNumThreads(4).build()
        }


        getInstructionBtn = findViewById(R.id.getInstructionBtn)
        getInstructionBtn.setOnClickListener {
            bitmap = textureView.bitmap!!
            lastBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false)
            var image = TensorImage.fromBitmap(bitmap)
            image = imageProcessor.process(image)

            val location: FloatArray
            val score: FloatArray
            val classes: FloatArray

            when (modelType) {
                "", "model0" -> {
                    val outputs = model0.process(image)
                    location = outputs.locationAsTensorBuffer.floatArray
                    score = outputs.scoreAsTensorBuffer.floatArray
                    classes = outputs.categoryAsTensorBuffer.floatArray
                }
                "model2" -> {
                    val outputs = model2.process(image)
                    location = outputs.locationAsTensorBuffer.floatArray
                    score = outputs.scoreAsTensorBuffer.floatArray
                    classes = outputs.categoryAsTensorBuffer.floatArray
                }
                else -> {
                    val outputs = model4.process(image)
                    location = outputs.locationAsTensorBuffer.floatArray
                    score = outputs.scoreAsTensorBuffer.floatArray
                    classes = outputs.categoryAsTensorBuffer.floatArray
                }
            }

            val intent = Intent(this, InstructionActivity::class.java)
            intent.putExtra("location", location)
            intent.putExtra("score", score)
            intent.putExtra("classes", classes)
            intent.putExtra("labels", labels.toTypedArray())
            println(classes)

            val filename = "bitmap.png"
            val stream: FileOutputStream = openFileOutput(filename, MODE_PRIVATE)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)

            //Cleanup
            stream.close()
            bitmap.recycle()

            intent.putExtra("image", filename)

            startActivity(intent)
            finish()
        }

        val toolbar: Toolbar = findViewById<View>(R.id.toolbarCamera) as Toolbar

        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, BottomNavigationActivity::class.java)
            startActivity(intent)
            finish()
        }


        imageProcessor = ImageProcessor.Builder().add(ResizeOp(320, 320, ResizeOp.ResizeMethod.BILINEAR)).build()

        when (modelType) {
            "", "model0" -> {
                model0 = Model0.newInstance(this, options)
            }
            "model2" -> {
                model2 = Model2.newInstance(this, options)
            }
            else -> {
                model4 = Model4.newInstance(this, options)
            }
        }

        val handlerThread = HandlerThread("videoThread")
        handlerThread.start()
        handler = Handler(handlerThread.looper)

        textureView.surfaceTextureListener = object:TextureView.SurfaceTextureListener{
            override fun onSurfaceTextureAvailable(p0: SurfaceTexture, p1: Int, p2: Int) {
                openCamera()
            }

            override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture, p1: Int, p2: Int) {
            }

            override fun onSurfaceTextureDestroyed(p0: SurfaceTexture): Boolean {
                return false
            }

            override fun onSurfaceTextureUpdated(p0: SurfaceTexture) {
                bitmap = if (lastBitmap == null) {
                    textureView.bitmap!!
                } else {
                    lastBitmap!!.copy(Bitmap.Config.ARGB_8888, false)
                }
                var image = TensorImage.fromBitmap(bitmap)
                image = imageProcessor.process(image)

                val location: FloatArray
                val score: FloatArray
                val classes: FloatArray

                when (modelType) {
                    "", "model0" -> {
                        val outputs = model0.process(image)
                        location = outputs.locationAsTensorBuffer.floatArray
                        score = outputs.scoreAsTensorBuffer.floatArray
                        classes = outputs.categoryAsTensorBuffer.floatArray
                    }
                    "model2" -> {
                        val outputs = model2.process(image)
                        location = outputs.locationAsTensorBuffer.floatArray
                        score = outputs.scoreAsTensorBuffer.floatArray
                        classes = outputs.categoryAsTensorBuffer.floatArray
                    }
                    else -> {
                        val outputs = model4.process(image)
                        location = outputs.locationAsTensorBuffer.floatArray
                        score = outputs.scoreAsTensorBuffer.floatArray
                        classes = outputs.categoryAsTensorBuffer.floatArray
                    }
                }

                val mutableBitMap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                val canvas = Canvas(mutableBitMap)

                val h = mutableBitMap.height
                val w = mutableBitMap.width
                paint.textSize = h/30f
                paint.strokeWidth = h/250f
                var x: Int
                score.forEachIndexed { index, fl ->
                    x = index
                    x *= 4
                    if(fl >= 0.45){
                        val label = labels[classes[index].toInt()]
                        labelsOnColors[label]?.let { paint.color = it }
                        paint.style = Paint.Style.STROKE
                        canvas.drawRect(
                            RectF(
                                location[x + 1] * w,
                                location[x] * h,
                                location[x + 3] * w,
                                location[x + 2] * h
                            ), paint
                        )
                        paint.style = Paint.Style.FILL
                        canvas.drawText(
                            labels[classes[index].toInt()],
                            location[x + 1] * w,
                            location[x] * h,
                            paint
                        )
                    }
                }

                imageView.setImageBitmap(mutableBitMap)
            }
        }

        cameraManager = getSystemService( Context.CAMERA_SERVICE) as CameraManager
    }

    override fun onDestroy() {
        super.onDestroy()
        when (modelType) {
            "", "model0" -> {
                model0.close()
            }
            "model2" -> {
                model2.close()
            }
            else -> {
                model4.close()
            }
        }
        cameraDevice.close()
    }

    @SuppressLint("MissingPermission")
    private fun openCamera() {
        cameraManager.openCamera(cameraManager.cameraIdList[0], object:CameraDevice.StateCallback(){
            override fun onOpened(p0: CameraDevice) {
                cameraDevice = p0

                val surfaceTexture = textureView.surfaceTexture
                val surface = Surface(surfaceTexture)

                val captureRequest = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                captureRequest.addTarget(surface)

                cameraDevice.createCaptureSession(listOf(surface), object: CameraCaptureSession.StateCallback(){
                    override fun onConfigured(p0: CameraCaptureSession) {
                        p0.setRepeatingRequest(captureRequest.build(), null, null)
                    }

                    override fun onConfigureFailed(p0: CameraCaptureSession) {

                    }
                }, handler)
            }

            override fun onDisconnected(p0: CameraDevice) {
                p0.close()
            }

            override fun onError(p0: CameraDevice, p1: Int) {

            }
        }, handler)
    }

    private fun getPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 101)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            getPermission()
        }
    }
}