package com.iammert.tabscrollattacherlib

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

internal fun RecyclerView.scrollToPosition(position: Int, scrollMethod: ScrollMethod) {
    when (scrollMethod) {
        is ScrollMethod.Direct -> scrollToPosition(position)
        is ScrollMethod.Smooth -> smoothSnapToPosition(position)
        is ScrollMethod.LimitedSmooth -> smoothScrollToPosition(position, scrollMethod.limit)
    }
}

private fun RecyclerView.smoothScrollToPosition(position: Int, scrollLimit: Int) {
    layoutManager?.apply {
        when (this) {
            is LinearLayoutManager -> {
                val topItem = findFirstVisibleItemPosition()
                val distance = topItem - position
                val anchorItem = when {
                    distance > scrollLimit -> position + scrollLimit
                    distance < -scrollLimit -> position - scrollLimit
                    else -> topItem
                }
                if (anchorItem != topItem) scrollToPosition(anchorItem)
                post {
                    smoothSnapToPosition(position)
                }
            }
            else -> smoothSnapToPosition(position)
        }
    }
}

fun RecyclerView.smoothSnapToPosition(
    position: Int,
    snapMode: Int = LinearSmoothScroller.SNAP_TO_START
) {
    val smoothScroller = object : LinearSmoothScroller(context) {
        override fun getVerticalSnapPreference(): Int = snapMode
        override fun getHorizontalSnapPreference(): Int = snapMode
    }
    smoothScroller.targetPosition = position
    layoutManager?.startSmoothScroll(smoothScroller)
}