package com.dany.cameramission.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.dany.cameramission.extensions.cosineSimilarity
import com.dany.cameramission.model.PhotoGridItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PhotoMissionViewModel : ViewModel() {
  private val _selectedPhotoFlow = MutableStateFlow<Bitmap?>(null)
  val selectedPhotoFlow: StateFlow<Bitmap?>
    get() = _selectedPhotoFlow

  fun selectPhoto(photo: Bitmap?) {
    _selectedPhotoFlow.value = photo
  }

  private val _photoItemListFlow = MutableStateFlow(listOf<PhotoGridItem>())
  val photoItemListFlow: StateFlow<List<PhotoGridItem>>
    get() = _photoItemListFlow

  fun addPhotoItem(item: PhotoGridItem) {
    _photoItemListFlow.value = _photoItemListFlow.value.plus(item)
  }

  var answerImageVector: FloatArray? = null

  fun imageSimilarity(imageVector: FloatArray): Float {
    var sim = answerImageVector?.cosineSimilarity(imageVector) ?: 0f

    sim = (sim + 1) / 2
    sim = 5 * sim - 3.5f
    sim = when {
      sim > 1 -> 1f
      sim < 0 -> 0f
      else -> sim
    }

    return sim
  }
}