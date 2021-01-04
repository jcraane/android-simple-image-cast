package nl.jcraane.simpleimagecast

import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ImageAdapter(private val images: List<String>) : RecyclerView.Adapter<ImageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(ImageView(parent.context).apply {
            layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                .apply {
                    val margin = 20f.toDip(parent.context)
                    setMargins(margin, margin, margin, margin)
                }
        })
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        Glide.with(holder.imageView)
            .load(images[position])
            .into(holder.imageView)
    }

    override fun getItemCount() = images.size

}

class ImageViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)