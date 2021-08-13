package com.dany.cameramission.ui.dest

import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.dany.cameramission.R
import com.dany.cameramission.base.BaseFragment
import com.dany.cameramission.databinding.FragmentPhotoCaptureBinding
import com.dany.cameramission.extensions.toBitmap
import com.dany.cameramission.model.PhotoGridItem
import com.dany.cameramission.viewmodel.PhotoMissionViewModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PhotoCaptureFragment : BaseFragment<FragmentPhotoCaptureBinding>(
  R.layout.fragment_photo_capture
) {
  private var cameraExecutor: ExecutorService? = null
  private var imageCapture: ImageCapture? = null

  private val photoMissionVm by navGraphViewModels<PhotoMissionViewModel>(R.id.main)

  override fun FragmentPhotoCaptureBinding.initView() {
    cameraSetting()
  }

  override fun FragmentPhotoCaptureBinding.setEventListener() {
    viewCapture.setOnClickListener {
      imageCapture?.takePicture(
        ContextCompat.getMainExecutor(requireContext()),
        object : ImageCapture.OnImageCapturedCallback() {
          override fun onCaptureSuccess(image: ImageProxy) {
            photoMissionVm.addPhotoItem(PhotoGridItem(image.toBitmap()))
            findNavController().navigateUp()
            image.close()
          }
        }
      )
    }
  }

  private fun FragmentPhotoCaptureBinding.cameraSetting() {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

    cameraExecutor = Executors.newSingleThreadExecutor()

    cameraProviderFuture.addListener({
      val cameraProvider = cameraProviderFuture.get()

      val preview = Preview.Builder()
        .build()
        .also {
          it.setSurfaceProvider(viewFinder.surfaceProvider)
        }

      imageCapture = ImageCapture.Builder()
        .setTargetResolution(Size(PhotoGridItem.IMAGE_WIDTH, PhotoGridItem.IMAGE_HEIGHT))
        .build()

      val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

      try {
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(this@PhotoCaptureFragment,
          cameraSelector,
          preview,
          imageCapture
        )
      } catch (e: Exception) {
      }
    }, ContextCompat.getMainExecutor(requireContext()))
  }

  override fun onDestroyView() {
    cameraExecutor?.shutdown()

    super.onDestroyView()
  }
}