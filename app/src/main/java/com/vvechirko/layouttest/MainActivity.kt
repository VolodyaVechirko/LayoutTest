package com.vvechirko.layouttest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rv = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = Adapter()
        rv.adapter = adapter
        rv.layoutManager = GridLayoutManager(this, 2)
//        rv.layoutManager = StaggeredGridLayoutManager(
//                2, StaggeredGridLayoutManager.VERTICAL
//        ).apply {
//            gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
//        }

        val mList = mutableListOf<Any>()
        for (i in 0 until 10) {
            mList.add(Order((i + 1).toString()))
        }
        adapter.setOrders(mList)
    }
}

data class Order(val id: String, var status: String = "new") {
    override fun toString(): String = id
    override fun hashCode(): Int = id.hashCode()
    override fun equals(other: Any?): Boolean = if (other is Order) other.id == id else false
}

class Adapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val orders = mutableListOf<Any>()

    fun setOrders(orders: List<Any>) {
        this.orders.apply {
            clear()
            addAll(orders)
        }
        notifyDataSetChanged()
    }

    val spanCount = 2

    fun updateOrder(order: Order) {
        val position = orders.indexOf(order)
        if (position != -1) {
            if (order.status == "done") {
                val size = orders.size
                Log.d("OrdersLogs", orders.toString())
                for (i in position until size step spanCount) {
                    if (size - i <= spanCount) {
                        // last item
                        orders.set(i, Any())
                        Log.d("OrdersLogs", "set $i null")
                    } else {
                        orders.set(i, orders.get(i + spanCount))
                        Log.d("OrdersLogs", "set $i from ${i + spanCount}")
                    }
                }
                Log.d("OrdersLogs", orders.toString())
                notifyDataSetChanged()
            } else {
                orders.set(position, order)
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (orders.get(position) is Order) 1 else 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) Holder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_order, parent, false))
        else EmptyHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_empty, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = orders[position]
        if (item is Order) {
            (holder as? Holder)?.let {
                (it.itemView as Button).text = item.id
                it.itemView.setOnClickListener {
                    item.status = "done"
                    updateOrder(item)
                }
            }
        }
    }

    override fun getItemCount() = orders.size

    class Holder(view: View) : RecyclerView.ViewHolder(view) {

        init {
            view.layoutParams.height = 3 * Random.nextInt(100, 200)
        }
    }

    class EmptyHolder(view: View) : RecyclerView.ViewHolder(view)
}