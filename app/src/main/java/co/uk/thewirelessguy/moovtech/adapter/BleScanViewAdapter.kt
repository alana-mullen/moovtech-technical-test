package co.uk.thewirelessguy.moovtech.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.uk.thewirelessguy.moovtech.R
import co.uk.thewirelessguy.moovtech.adapter.BleScanViewAdapter.ViewHolder
import com.inuker.bluetooth.library.search.SearchResult

class BleScanViewAdapter(
    context: Context?,
    var itemData: List<SearchResult>, private val listener: (SearchResult) -> Unit
) : RecyclerView.Adapter<ViewHolder>() {
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.item_main, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemData[position]
        holder.mBleRssi.text = itemData[position].name + " - " + itemData[position]
            .address + " - RSSI: " + itemData[position].rssi
        holder.mBleRssi.setOnClickListener {
            listener(item)
        }
    }

    override fun getItemCount() = itemData.size

    class ViewHolder internal constructor(view: View) :
        RecyclerView.ViewHolder(view) {
        var mBleRssi: TextView = view.findViewById<View>(R.id.tv) as TextView

        init {
            /*view.setOnClickListener {
                Timber.d("onClick--> position = $position")
            }*/
        }
    }

}