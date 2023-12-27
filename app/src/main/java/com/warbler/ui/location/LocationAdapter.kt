package com.warbler.ui.location

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.warbler.data.model.location.LocationEntity
import com.warbler.databinding.LocationListItemBinding
import com.warbler.utilities.ClickListenerInterface

class LocationAdapter(
    private val clickListener: ClickListenerInterface<LocationEntity>,
) : ListAdapter<LocationEntity, LocationAdapter.ViewHolder>(
        LocationDiffUtilCallback,
    ) {
    private var locationList = emptyList<LocationEntity>()

    class ViewHolder(private val itemBinding: LocationListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(
            location: LocationEntity,
            clickListener: ClickListenerInterface<LocationEntity>,
            position: Int,
        ) {
            itemBinding.location = location
            itemBinding.clickListener = clickListener
            itemBinding.executePendingBindings()
            if (position == 0) itemBinding.locationIndicatorIcon.visibility = View.VISIBLE
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        return LocationListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        ).let {
            ViewHolder(it)
        }
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        val currentItem = locationList[position]
        if (position == 0) {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT)
        }
        holder.bind(currentItem, clickListener, position)
    }

    fun setItems(list: List<LocationEntity>) {
        locationList = list
        submitList(list)
    }
}

object LocationDiffUtilCallback : DiffUtil.ItemCallback<LocationEntity>() {
    override fun areItemsTheSame(
        oldItem: LocationEntity,
        newItem: LocationEntity,
    ): Boolean {
        return "${oldItem.lat}${oldItem.lon}" == "${newItem.lat}${newItem.lon}"
    }

    override fun areContentsTheSame(
        oldItem: LocationEntity,
        newItem: LocationEntity,
    ): Boolean {
        return oldItem == newItem
    }
}
