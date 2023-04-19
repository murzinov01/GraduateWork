package com.example.carbrief

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Handler
import android.os.HandlerThread
import android.view.*
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.example.carbrief.ml.Model0
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import android.view.Surface
import android.view.TextureView
import android.widget.Button


class Camera : Fragment() {
    private lateinit var textureView: TextureView
    private lateinit var cameraManager: CameraManager
    private lateinit var handler: Handler
    private lateinit var cameraDevice: CameraDevice
    private lateinit var imageView: ImageView
    private lateinit var bitmap: Bitmap
    private lateinit var model0: Model0
    private lateinit var imageProcessor: ImageProcessor
    private lateinit var getInstructionBtn: Button
    private val paint = Paint()
    lateinit var labels:List<String>
    private var labelsOnColors = mutableMapOf<String, Int>()

    private var colors = listOf(
        Color.BLUE, Color.GREEN, Color.RED, Color.CYAN, Color.WHITE,
        Color.DKGRAY, Color.MAGENTA, Color.YELLOW, Color.RED)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageView = view.findViewById(R.id.imageView)
        textureView = view.findViewById(R.id.textureView)

        getPermission()
        labels = activity?.let { FileUtil.loadLabels(it, "labels.txt") } as List<String>

        for (i in labels.indices) {
            labelsOnColors[labels[i]] = colors[i]
        }
        getInstructionBtn = view.findViewById(R.id.getInstructionBtn)
        getInstructionBtn.setOnClickListener {

        }


        imageProcessor = ImageProcessor.Builder().add(ResizeOp(320, 320, ResizeOp.ResizeMethod.BILINEAR)).build()
        model0 =  Model0.newInstance(requireActivity())

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
                bitmap = textureView.bitmap!!
                var image = TensorImage.fromBitmap(bitmap)
                image = imageProcessor.process(image)

                val outputs = model0.process(image)
                val location = outputs.locationAsTensorBuffer.floatArray
                val score = outputs.scoreAsTensorBuffer.floatArray
                val classes = outputs.categoryAsTensorBuffer.floatArray

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
                        // canvas.drawText(labels.get(classes.get(index).toInt())+" "+fl.toString(), location.get(x+1)*w, location.get(x)*h, paint)
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

        cameraManager = requireActivity().getSystemService( Context.CAMERA_SERVICE) as CameraManager
    }

    override fun onDestroy() {
        super.onDestroy()
        model0!!.close()
        cameraDevice!!.close()
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
        if (activity?.let { ContextCompat.checkSelfPermission(it, android.Manifest.permission.CAMERA) } != PackageManager.PERMISSION_GRANTED) {
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