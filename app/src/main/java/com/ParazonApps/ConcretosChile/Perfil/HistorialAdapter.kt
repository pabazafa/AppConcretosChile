package com.ParazonApps.ConcretosChile.Perfil

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ParazonApps.ConcretosChile.Perfil.OnItemClickListener

data class HistorialItem(val section: String, val nombre: String)

class HistorialAdapter(private val data: List<HistorialItem>, private val itemClickListener: OnItemClickListener) : RecyclerView.Adapter<HistorialAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]

        with(holder) {
            textView.text = item.nombre
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, EditarResultado::class.java).apply {
                    putExtra("section", item.section)
                    putExtra("nombre", item.nombre)
                }
                itemView.context.startActivity(intent)
            }
        }
    }

    fun getItem(position: Int): HistorialItem {
        return data[position]
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(android.R.id.text1)
    }
}
