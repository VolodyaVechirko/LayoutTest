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
            init(columns)
        }
    }

    abstract class Holder<T>(val view: View) {

        var column: Int = 0
        var position: Int = 0

        abstract fun bind(item: T)
    }

    abstract class Adapter<T> {
        private var spanCount = 0
        private var columns: List<LinearLayout> = listOf()
        private var columnItems: List<MutableList<T>> = listOf()

//        private var holders: List<Holder<T>> = listOf()

        abstract fun onCreateHolder(parent: ViewGroup): Holder<T>
        abstract fun onBindHolder(holder: Holder<T>, item: T)

        fun init(columns: List<LinearLayout>) {
            this.spanCount = columns.size
            this.columns = columns

            this.columnItems = mutableListOf<MutableList<T>>().apply {
                for (i in 0 until spanCount) {
                    add(mutableListOf())
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
                    val holder = onCreateHolder(columns[columnIndex])
                    holder.view.tag = holder
                    onBindHolder(holder, item)
                    columns[columnIndex].addView(holder.view)
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
                    val holder = columns[i].getChildAt(position).tag as Holder<T>
                    onBindHolder(holder, item)
                    return
                }
            }
        }
    }
}