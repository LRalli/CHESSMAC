package com.example.chessmac.ui.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

//Extension function to convert an Offset to an IntOffset.
fun Offset.toIntOffset() = IntOffset(x.roundToInt(), y.roundToInt())