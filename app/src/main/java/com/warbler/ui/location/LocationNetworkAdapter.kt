package com.warbler.ui.location

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.warbler.data.model.location.LocationEntity
import com.warbler.databinding.LocationResultListItemBinding
import com.warbler.utilities.ClickListener

class LocationNetworkAdapter(
    private val clickListener: ClickListener<LocationEntity>
) : ListAdapter<LocationEntity, LocationNetworkAdapter.ViewHolder>(
    LocationNetworkDiffUtilCallback
) {

    private var locationList = emptyList<LocationEntity>()

    class ViewHolder(private val itemBinding: LocationResultListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(location: LocationEntity, clickListener: ClickListener<LocationEntity>) {
            itemBinding.location = location
            itemBinding.clickListener = clickListener
            itemBinding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return LocationResultListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).let {
            ViewHolder(it)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = locationList[position]
        holder.bind(currentItem, clickListener)
    }

    fun setItems(list: List<LocationEntity>) {
        locationList = list
        submitList(list)
    }
}

object LocationNetworkDiffUtilCallback : DiffUtil.ItemCallback<LocationEntity>() {
    override fun areItemsTheSame(oldItem: LocationEntity, newItem: LocationEntity): Boolean {
        return "${oldItem.lat}${oldItem.lon}" == "${newItem.lat}${newItem.lon}"
    }

    override fun areContentsTheSame(oldItem: LocationEntity, newItem: LocationEntity): Boolean {
        return oldItem == newItem
    }
}
