package com.dany.cameramission.ui.dest

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.dany.cameramission.R
import com.dany.cameramission.base.BaseFragment
import com.dany.cameramission.databinding.FragmentPhotoGridBinding
import com.dany.cameramission.ui.adapter.PhotoGridAdapter
import com.dany.cameramission.viewmodel.PhotoMissionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PhotoGridFragment : BaseFragment<FragmentPhotoGridBinding>(
  R.layout.fragment_photo_grid
) {
  private val cameraPermissionLauncher =
    registerForActivityResult(ActivityResultContracts.RequestPermission()) { grant ->
      if (!grant) return@registerForActivityResult
    }

  private val permissionGranted: Boolean
    get() = ContextCompat.checkSelfPermission(
      requireContext(),
      Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

  private val photoMissionVm by navGraphViewModels<PhotoMissionViewModel>(R.id.main)

  private val photoItemAdapter by lazy {
    PhotoGridAdapter(
      onClickAdd = {
        when {
          permissionGranted -> findNavController().navigate(PhotoGridFragmentDirections.actionToPhotoCapture())
          else -> cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
      },
      onClickPhoto = {
        photoMissionVm.selectPhoto(it)
        findNavController().navigate(PhotoGridFragmentDirections.actionToPhotoMission())
      }
    )
  }

  override fun FragmentPhotoGridBinding.initView() {
    if (!permissionGranted) {
      cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    viewPhotoGrid.apply {
      adapter = photoItemAdapter
    }
  }

  override fun FragmentPhotoGridBinding.bindingVM() {
    CoroutineScope(Dispatchers.Main).launch {
      photoMissionVm.photoItemListFlow.collect {
        photoItemAdapter.itemList = it
      }
    }
  }
}