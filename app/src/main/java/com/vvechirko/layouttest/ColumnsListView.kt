package com.vvechirko.layouttest

import android.animation.LayoutTransition
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

class ColumnsListView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    val spanCount = 3
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
    }

    fun setAdapter(adapter: Adapter<*>) {
        this.adapter = adapter.also {
            it.init(this)
        }
    }

    abstract class Holder<T>(val view: View) {
        abstract fun bind(item: T)
    }

    abstract class Adapter<T> {
        lateinit var listView: ColumnsListView
        private var columnItems: List<MutableList<T>> = listOf()

        abstract fun onCreateHolder(parent: ViewGroup): Holder<T>
        abstract fun onBindHolder(holder: Holder<T>, item: T)

        fun init(parent: ColumnsListView) {
            this.listView = parent

            this.columnItems = mutableListOf<MutableList<T>>().apply {
                for (i in 0 until listView.spanCount) {
                    add(mutableListOf())
                }
            }
        }

        fun addItems(list: List<T>) {
            // split orders for columns
            list.forEachIndexed { index, item ->
                var isPresent = false
                // check if item is present
                for (i in 0 until listView.spanCount) {
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
                    for (i in 0 until listView.spanCount) {
                        if (columnItems[i].size < min) {
                            columnIndex = i
                            min = columnItems[i].size
                        }
                    }

                    // add new item
                    columnItems[columnIndex].add(item)
                    val holder = onCreateHolder(column(columnIndex))
                    holder.view.tag = holder
                    onBindHolder(holder, item)
                    column(columnIndex).addView(holder.view)
                }
            }
        }

        fun removeItem(item: T) {
            for (i in 0 until listView.spanCount) {
                val position = columnItems[i].indexOf(item)
                if (position != -1) {
                    columnItems[i].removeAt(position)
                    column(i).removeViewAt(position)
                    return
                }
            }
        }

        fun updateItem(item: T) {
            for (i in 0 until listView.spanCount) {
                val position = columnItems[i].indexOf(item)
                if (position != -1) {
                    columnItems[i].set(position, item)
                    val holder = column(i).getChildAt(position).tag as Holder<T>
                    onBindHolder(holder, item)
                    return
                }
            }
        }

        private fun column(index: Int) = listView.getChildAt(index) as LinearLayout
    }
}