package com.example.teacherschedule.presentation.common.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt

/**
 * 可收合的區塊容器，根據使用者的垂直滑動自動收起或展開上方的區塊。
 *
 * ## 功能與互動：
 * - 當使用者向上滑動時，`collapseContent` 會逐漸收合至隱藏。
 * - 向下滑動時會展開顯示。
 * - 若滑動後停下時收合高度超過一半，會自動完全收起；否則自動展開。
 *
 * @param collapseContent 要顯示於頂部、可被收合的內容。
 * @param bodyContent 主體內容，通常是可滾動的內容列表，會被推到 collapseContent 底下。
 * @param modifier 容器的 Modifier。
 */
@Composable
fun Collapse(
    collapseContent: @Composable BoxScope.() -> Unit,
    bodyContent: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
) {
    var collapseContentHeight by remember { mutableFloatStateOf(0f) }
    var rawOffset by remember { mutableFloatStateOf(0f) }

    // 用動畫過渡至目標位置（0f 或 -collapseContentHeight
    val animatedOffset by animateFloatAsState(
        targetValue = rawOffset,
        label = "AnimatedCollapseOffset"
    )

    fun updateOffset(delta: Float): Offset {
        val previous = rawOffset
        rawOffset = (rawOffset + delta).coerceIn(-collapseContentHeight, 0f)
        return Offset(0f, rawOffset - previous)
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // 向上滑（收起）
                return if (available.y < 0 && rawOffset > -collapseContentHeight) {
                    updateOffset(available.y)
                } else Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                // 向下滑（展開）
                return if (available.y > 0 && rawOffset < 0f) {
                    updateOffset(available.y)
                } else Offset.Zero
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                // 判斷目前 offset，決定要收起還是展開。收合的閾值為收合區一半
                val threshold = -collapseContentHeight / 2f
                rawOffset = if (rawOffset <= threshold) -collapseContentHeight else 0f
                return Velocity.Zero
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        Box(
            modifier = Modifier
                .onSizeChanged { collapseContentHeight = it.height.toFloat() }
                .offset { IntOffset(0, animatedOffset.roundToInt()) }
                .zIndex(1f),
            content = collapseContent
        )

        Box(
            modifier = Modifier
                .offset { IntOffset(0, (collapseContentHeight + animatedOffset).roundToInt()) },
            content = bodyContent
        )
    }
}
