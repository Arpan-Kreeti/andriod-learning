package com.example.android.trackmysleepquality.sleeptracker

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.convertDurationToFormatted
import com.example.android.trackmysleepquality.convertNumericQualityToString
import com.example.android.trackmysleepquality.database.SleepNight


// Here instead of using RecycleView adapter we extend ListAdapter which has two generic arguments
// The list type and the view holder, its constructor takes in a callback which is called when ever the list
// changes, here we use the DiffUtil class we implemented which efficiently tells  recycle view
// how to update the list when ever it changes
class SleepNightAdapter : ListAdapter<SleepNight, SleepNightAdapter.ViewHolder>(SleepNightDiffCallback()) {

// This code is no longer needed since we inherit List Adapter instead of Recycle view adapter
//    var data = listOf<SleepNight>()
//
//    // Here we define a custom setter for the data variable we do this so that we can call
//    // notifyDataSetChanged() which notify's recycle view that the data has changed
//    // and the recycle view can update its entire list of data immediately
//    set(value) {
//        field = value
//        notifyDataSetChanged()
//    }

// This is not required since List adapter can figure this out
// Returns the number of items in teh recycle view
//    override fun getItemCount(): Int {
//        return data.size
//    }

    // Set the view holder at position
    // A view holder describes an item view and has meta data about its position in the recycle view
    // IMPORTANT
    // Recycle view reuses any views that have gone out of screen as we scroll down, thus any properties
    // set for these older views reappear in the new views as they are recycled, we must reset these
    // recycled views to avoid this problem
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val res = holder.itemView.context.resources
        holder.bind(item, res)
    }


    // Tell recycle view how to create a view holder
    // This is called by recycler view when it needs a new view of a given type
    // A view is added to a view group before its displayed on screen
    // (A view group is also a type of view that contains a group of views)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }


    // We make the view holder class constructor private since we make its instances only via the
    // static function from() defined as a companion object
    class ViewHolder private constructor(itemView: View): RecyclerView.ViewHolder(itemView) {
        // By doing this we can now easily access the view properties using this view holder class
        val sleepLength: TextView = itemView.findViewById(R.id.sleep_length)
        val quality: TextView = itemView.findViewById(R.id.quality_string)
        val qualityImage: ImageView = itemView.findViewById(R.id.quality_image)

        fun bind(item: SleepNight, res: Resources) {

            // Here we set the sleepLength, quality and qualityImage properties of the view holder
            sleepLength.text = convertDurationToFormatted(item.startTimeMilli, item.endTimeMilli, res)
            quality.text = convertNumericQualityToString(item.sleepQuality, res)

            qualityImage.setImageResource(when (item.sleepQuality) {
                0 -> R.drawable.ic_sleep_0
                1 -> R.drawable.ic_sleep_1
                2 -> R.drawable.ic_sleep_2
                3 -> R.drawable.ic_sleep_3
                4 -> R.drawable.ic_sleep_4
                5 -> R.drawable.ic_sleep_5
                else -> R.drawable.ic_sleep_active
            })
        }

        // This is a static function of the ViewHolder so we keep it inside a companion object
        // This is static since we want to call this on the ViewHolder class and not on its instance
        //
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                // We inflate a view
                // The view holder knows which layout to inflate from parent.context
                val layoutInflator = LayoutInflater.from(parent.context)
                // When inflating recycle view first pass the view resource to inflate,
                // then we pass the parent view group and lastly we pass false.
                // By default the view is attached to the parent bu here we pass false
                // to specify that do not attach this view to its parent for now,
                // this is because recycle view does this for us when its time.
                // The below code will always be same(is the standard) whenever
                // we override this method in a recycler view adapter
                val view = layoutInflator.inflate(R.layout.list_item_sleep_night, parent, false)

                // Make an instance of view holder and wrap our view up in a holder and return it to recycler view
                return ViewHolder(view)
            }
        }
    }
}

// The diff util class (it uses is used by recycle view to efficiently update, add ,remove items from
// the list without redrawing the entire list. To make this work we make a own diff util class
// and extend DiffUtil and override the required methods.
// (DiffUtil is a utility class that can calculate the difference between two lists and output a
// list of update operations that converts the first list into the second one.
// DiffUtil uses Myers's difference algorithm to calculate the minimal number of updates to
// convert one list into another.
// It can be used to calculate updates for a RecyclerView Adapter.
class SleepNightDiffCallback : DiffUtil.ItemCallback<SleepNight>() {

    // This callback is used by diff util to check if two recycle view items
    // are the same or not. In our case two items are the same if they have same night id
    override fun areItemsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
        return oldItem.nightId == newItem.nightId
    }

    // Check if two items have same contents
    override fun areContentsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
        // This works since we have defined sleep night as a data class thus
        // data classes when compared using == checks if all properties are equal
       return oldItem == newItem


    }

}