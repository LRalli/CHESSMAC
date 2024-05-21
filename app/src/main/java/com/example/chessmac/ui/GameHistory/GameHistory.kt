package com.example.chessmac.ui.GameHistory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chessmac.R
import com.example.chessmac.customFontFamily
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun GameHistory(
    history: ImmutableList<String>,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = modifier
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        item {
            Text(
                text = stringResource(R.string.moves_title),
                fontSize = 24.sp,
                style = TextStyle(fontSize = 16.sp, fontFamily = customFontFamily)
            )
        }
        items(history) { item ->
            GameHistoryItem(item)
        }
    }

    LaunchedEffect(history) {
        if (history.isNotEmpty()) {
            listState.scrollToItem(history.lastIndex)
        }
    }
}

@Composable
fun GameHistoryItem(
    historyItem: String,
    modifier: Modifier = Modifier
) {
    val moves = historyItem.split(" ")
    Row(modifier = modifier) {
        moves.forEachIndexed { index, move ->
            val textColor = if (index == 1 || index==2) Color.White else Color.Black
            Text(
                text = move,
                color = textColor,
                fontSize = 24.sp
            )
            if (index < moves.size - 1) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}