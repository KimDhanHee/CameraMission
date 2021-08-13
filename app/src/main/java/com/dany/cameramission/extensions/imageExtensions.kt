package com.dany.cameramission.extensions

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.camera.core.ImageProxy
import com.dany.cameramission.ml.ResnetModel
import com.dany.cameramission.model.PhotoGridItem
import com.dany.cameramission.utils.YuvToRgbConverter
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.nio.ByteBuffer

@SuppressLint("UnsafeExperimentalUsageError")
fun ImageProxy.toBitmap(converter: YuvToRgbConverter): Bitmap? {
  val image = image ?: return null
  val rotationMatrix = Matrix().apply {
    postRotate(imageInfo.rotationDegrees.toFloat())
  }
  val bitmapBuffer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
  converter.yuvToRgb(image, bitmapBuffer)

  return Bitmap.createBitmap(
    bitmapBuffer,
    0,
    0,
    bitmapBuffer.width,
    bitmapBuffer.height,
    rotationMatrix,
    false
  )
}

fun ImageProxy.toBitmap(): Bitmap? {
  val bytes = this.planes[0].buffer.toByteArray()
  return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

private fun ByteBuffer.toByteArray(): ByteArray {
  rewind()
  val data = ByteArray(remaining())
  get(data)
  return data
}

fun ResnetModel.result(image: Bitmap): FloatArray {
  val imageProcessor = ImageProcessor.Builder()
    .add(ResizeOp(PhotoGridItem.IMAGE_HEIGHT,
      PhotoGridItem.IMAGE_WIDTH,
      ResizeOp.ResizeMethod.BILINEAR))
    .build()

  val tImage = imageProcessor.process(
    TensorImage(DataType.FLOAT32).apply {
      load(image)
    }
  )

  val output = process(tImage.tensorBuffer).outputFeature0AsTensorBuffer

  return output.floatArray
}