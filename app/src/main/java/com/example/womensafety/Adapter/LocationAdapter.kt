package com.example.womensafety.Adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.womensafety.Model.LocationModel
import com.example.womensafety.databinding.LocationLayoutBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


open class LocationAdapter(options: FirebaseRecyclerOptions<LocationModel>) : FirebaseRecyclerAdapter<LocationModel, LocationAdapter.onViewHolder>(
    options
) {
    class onViewHolder(val binding : LocationLayoutBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): onViewHolder {
        val view = LocationLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return onViewHolder(view)
    }

    override fun onBindViewHolder(holder: onViewHolder, p1: Int, model: LocationModel) {

            holder.binding.latitude.text = model.latitude.toString()
            holder.binding.longitude.text = model.longitude.toString()
            holder.binding.maps.setOnClickListener {
                    val mapLink = "http://maps.google.com/maps?q=${model.latitude},${model.longitude}"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mapLink))
                    holder.itemView.context.startActivity(intent)
                }
    }
}