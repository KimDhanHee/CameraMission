package com.dany.cameramission.ui.dest

import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import com.dany.cameramission.R
import com.dany.cameramission.base.BaseFragment
import com.dany.cameramission.databinding.FragmentPhotoMissionBinding
import com.dany.cameramission.extensions.result
import com.dany.cameramission.extensions.toBitmap
import com.dany.cameramission.ml.ResnetModel
import com.dany.cameramission.model.PhotoGridItem
import com.dany.cameramission.utils.YuvToRgbConverter
import com.dany.cameramission.viewmodel.PhotoMissionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PhotoMissionFragment : BaseFragment<FragmentPhotoMissionBinding>(
  R.layout.fragment_photo_mission
) {
  private var cameraExecutor: ExecutorService? = null

  private val photoMissionVm by navGraphViewModels<PhotoMissionViewModel>(R.id.main)

  private val tfModel: ResnetModel by lazy {
    ResnetModel.newInstance(requireContext())
  }

  override fun FragmentPhotoMissionBinding.initView() {
    cameraSetting()
  }

  override fun FragmentPhotoMissionBinding.bindingVM() {
    CoroutineScope(Dispatchers.Main).launch {
      photoMissionVm.selectedPhotoFlow.collect {
        it ?: return@collect

        Glide.with(this@PhotoMissionFragment)
          .load(it)
          .into(viewAnswerImage)

        photoMissionVm.answerImageVector = tfModel.result(it)
      }
    }
  }

  private fun FragmentPhotoMissionBinding.cameraSetting() {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

    cameraExecutor = Executors.newSingleThreadExecutor()

    cameraProviderFuture.addListener({
      val cameraProvider = cameraProviderFuture.get()

      val preview = Preview.Builder()
        .build()
        .also {
          it.setSurfaceProvider(viewFinder.surfaceProvider)
        }

      val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

      val imageAnalyzer = ImageAnalysis.Builder()
        .setTargetResolution(Size(PhotoGridItem.IMAGE_WIDTH, PhotoGridItem.IMAGE_HEIGHT))
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()
        .also {
          it.setAnalyzer(cameraExecutor!!, ImageAnalyzer { similarity ->
            CoroutineScope(Dispatchers.Main).launch {
              viewSimilarity.text = "%.5f %%".format(similarity * 100)
              viewSimilarity.setTextColor(
                ContextCompat.getColor(requireContext(), when (similarity) {
                  in 0.7..1.0 -> R.color.green
                  in 0.3..0.7 -> R.color.orange
                  else -> R.color.red
                })
              )
            }
          })
        }

      try {
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(this@PhotoMissionFragment,
          cameraSelector,
          preview,
          imageAnalyzer
        )
      } catch (e: Exception) {
      }
    }, ContextCompat.getMainExecutor(requireContext()))
  }

  override fun onDestroyView() {
    cameraExecutor?.shutdown()
    tfModel.close()

    super.onDestroyView()
  }

  private inner class ImageAnalyzer(private val onAnalyze: (Float) -> Unit) :
    ImageAnalysis.Analyzer {
    private val converter by lazy { YuvToRgbConverter(requireContext()) }

    override fun analyze(image: ImageProxy) {
      try {
        image.toBitmap(converter)?.let {
          val vector = tfModel.result(it)
          onAnalyze(photoMissionVm.imageSimilarity(vector))
        }
        image.close()
      } catch (e: Exception) {}
    }
  }
}