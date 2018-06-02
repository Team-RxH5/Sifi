package com.anagramsoftware.sifi.recyclerview

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

class MarginItemDecorator(private val space: Int): RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
        outRect?.bottom = space
        outRect?.left = space * 2
        outRect?.right = space * 2

        // Add top margin only for the first item to avoid double space between items
//        if (parent?.getChildLayoutPosition(view) == 0) {
            outRect?.top = 0
//        } else {
//            outRect?.top = 0
//        }
    }
}