package com.sudo248.ltm.ui.activity.main.fragment.friend

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.sudo248.ltm.R
import com.sudo248.ltm.api.model.profile.Profile
import com.sudo248.ltm.databinding.ItemNewGroupBinding


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 23:11 - 06/11/2022
 */
class NewGroupAdapter(
    private val onItemRemove: (Int) -> Unit
) : RecyclerView.Adapter<NewGroupAdapter.ViewHolder>() {

    private val listProfile: MutableList<Profile> = mutableListOf()
    private val listPosition: MutableList<Int> = mutableListOf()

    fun addNewItem(profile: Profile, position: Int) {
        Log.d("sudoo", "addNewItem: $position")
        listPosition.add(position)
        listProfile.add(profile)
        notifyItemInserted(listPosition.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemNewGroupBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(listProfile[position], listPosition[position], position)
    }

    override fun getItemCount(): Int = listProfile.size

    inner class ViewHolder(private val binding: ItemNewGroupBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(profile: Profile, oldPosition: Int, position: Int) {
            Glide
                .with(itemView.context)
                .load(profile.image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.ic_error)
                .into(binding.imgAvatar)

            binding.imgCancel.setOnClickListener {
                Log.d("sudoo", "onBind: old $oldPosition pos $position")
                listProfile.removeAt(position)
                listPosition.removeAt(position)
                notifyDataSetChanged()
                onItemRemove(oldPosition)
            }
        }
    }
}