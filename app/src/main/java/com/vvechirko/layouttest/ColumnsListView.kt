package com.vvechirko.layouttest

import android.animation.LayoutTransition
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

class ColumnsListView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    private var spanCount = 3
    private var columns: List<LinearLayout> = listOf()
    private var adapter: Adapter<*>? = null

    init {
        orientation = HORIZONTAL
        isBaselineAligned = false
        weightSum = spanCount.toFloat()

        val childList = mutableListOf<LinearLayout>()
        for (i in 0 until spanCount) {
            val v = LinearLayout(context).apply {
                orientation = VERTICAL
                layoutTransition = LayoutTransition()
            }
            childList.add(v)
            addView(v, LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f))
        }
        columns = childList
    }

    fun setAdapter(adapter: Adapter<*>) {
        this.adapter = adapter.apply {
            init(spanCount, columns)
        }
    }

    abstract class Adapter<T> {
        private var spanCount = 2
        private var columns: List<LinearLayout> = listOf()
        private var columnItems: List<MutableList<T>> = listOf()

        abstract fun onCreateHolder(parent: ViewGroup): View
        abstract fun onBindHolder(view: View, item: T)

        fun init(spanCount: Int, columns: List<LinearLayout>) {
            this.spanCount = spanCount
            this.columns = columns

            val columnItems = mutableListOf<MutableList<T>>()
            for (i in 0 until spanCount) {
                columnItems.add(mutableListOf())
            }
            this.columnItems = columnItems
        }

        fun setItems(list: List<T>) {
            // split orders for columns
            list.forEachIndexed { index, item ->
                val span = index % spanCount
                columnItems[span].add(item)
            }

            // populate containers of columns
            for (i in 0 until spanCount) {
                columns[i].removeAllViews()
                columnItems[i].forEach { item ->
                    val v = onCreateHolder(columns[i])
                    onBindHolder(v, item)
                    columns[i].addView(v)
                }
            }
        }

        fun addItems(list: List<T>) {
            // split orders for columns
            list.forEachIndexed { index, item ->
                var isPresent = false
                // check if item is present
                for (i in 0 until spanCount) {
                    if (columnItems[i].contains(item)) {
                        isPresent = true
                        break
                    }
                }

                // add item to less full column
                if (!isPresent) {
                    var columnIndex = 0
                    var min = columnItems[0].size

                    // find less full column
                    for (i in 0 until spanCount) {
                        if (columnItems[i].size < min) {
                            columnIndex = i
                            min = columnItems[i].size
                        }
                    }

                    // add new item
                    columnItems[columnIndex].add(item)
                    val v = onCreateHolder(columns[columnIndex])
                    onBindHolder(v, item)
                    columns[columnIndex].addView(v)
                }
            }
        }

        fun removeItem(item: T) {
            for (i in 0 until spanCount) {
                val position = columnItems[i].indexOf(item)
                if (position != -1) {
                    columnItems[i].removeAt(position)
                    columns[i].removeViewAt(position)
                    return
                }
            }
        }

        fun updateItem(item: T) {
            for (i in 0 until spanCount) {
                val position = columnItems[i].indexOf(item)
                if (position != -1) {
                    columnItems[i].set(position, item)
                    onBindHolder(columns[i].getChildAt(position), item)
                    return
                }
            }
        }
    }
}