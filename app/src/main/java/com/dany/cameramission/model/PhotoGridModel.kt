package com.dany.cameramission.model

import android.graphics.Bitmap

data class PhotoGridItem(
  val bitmap: Bitmap? = null,
  val type: PhotoGridType = PhotoGridType.ITEM,
) {
  companion object {
    const val IMAGE_WIDTH = 256
    const val IMAGE_HEIGHT = 256
  }
}

enum class PhotoGridType {
  ITEM,
  ADD,
  ;
}
