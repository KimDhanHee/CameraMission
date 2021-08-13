package com.dany.cameramission.extensions

import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

fun FloatArray.tsSS(vector: FloatArray): Float {
  var result = triangle(vector) * sector(vector)
  result = when {
    result <= 40 -> 40f
    result >= 90 -> 90f
    else -> result
  }
  result = (90 - result) / (90 - 40)

  return result
}

fun FloatArray.sector(vector: FloatArray): Float =
  (Math.PI * (euclidean(vector) + maginitudeDifference(vector)).pow(2) * theta(vector) / 360).toFloat()

fun FloatArray.maginitudeDifference(vector: FloatArray): Float =
  abs(norm() - vector.norm())

fun FloatArray.euclidean(vector: FloatArray): Float =
  sqrt(zip(vector).map { (it.first - it.second).pow(2) }.sum())

fun FloatArray.triangle(vector: FloatArray): Float =
  (norm() * vector.norm() * sin(Math.toRadians(theta(vector)).toFloat())) / 2

fun FloatArray.theta(vector: FloatArray): Double =
  acos(cosineSimilarity(vector)) + Math.toRadians(10.0)

fun FloatArray.cosineSimilarity(vector: FloatArray) =
  dot(vector) / (norm() * vector.norm())

fun FloatArray.dot(vector: FloatArray) = zip(vector).map { it.first * it.second }.sum()

fun FloatArray.norm(): Float = sqrt(map { it * it }.sum())