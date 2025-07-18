package com.warbler.ui.forecast.viewpager

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.warbler.data.model.weather.Conversion
import com.warbler.data.model.weather.Conversion.toDegrees
import com.warbler.data.model.weather.Forecast
import com.warbler.data.model.weather.WeatherDataSourceDto
import com.warbler.databinding.ForecastViewPagerItemBinding
import com.warbler.ui.MainWeatherDetailItemAdapter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.roundToInt

class ViewPagerAdapter(
    private val days: List<Forecast>,
    private val position: Int,
    private val location: String,
) : RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>() {
    inner class ViewPagerViewHolder(
        private val itemBinding: ForecastViewPagerItemBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(forecastItem: Forecast) {
            itemBinding.forecast = forecastItem
            itemBinding.hiLow.temperatureLowText.text =
                Conversion
                    .fromKelvinToProvidedUnit(
                        forecastItem.daily.temp.min,
                        forecastItem.temperature,
                    ).roundToInt()
                    .toDegrees

            itemBinding.hiLow.temperatureHiText.text =
                Conversion
                    .fromKelvinToProvidedUnit(
                        forecastItem.daily.temp.max,
                        forecastItem.temperature,
                    ).roundToInt()
                    .toDegrees

            val weatherDetailList =
                WeatherDataSourceDto.buildWeatherDetailList(
                    itemBinding.root.context,
                    forecastItem,
                )

            val weatherDetailItemAdapter = MainWeatherDetailItemAdapter()
            val layoutManager = GridLayoutManager(itemBinding.root.context, 3)
            itemBinding.weatherDetailRecyclerView.adapter = weatherDetailItemAdapter
            itemBinding.weatherDetailRecyclerView.layoutManager = layoutManager
            weatherDetailItemAdapter.setItems(weatherDetailList)

            itemBinding.shareIcon.setOnClickListener {
                val sharableView = itemBinding.headingConstraint
                val context = sharableView.context

                itemBinding.shareIcon.visibility = View.GONE

                val bitmap = Bitmap.createBitmap(sharableView.width, sharableView.height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                sharableView.draw(canvas)

                itemBinding.shareIcon.visibility = View.VISIBLE

                try {
                    val cachePath = File(context.cacheDir, "images")
                    cachePath.mkdirs() // don't forget to make the directory
                    val stream: FileOutputStream =
                        FileOutputStream("$cachePath/image.png") // overwrites this image every time
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    stream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                val imagePath = File(context.cacheDir, "images")
                val newFile = File(imagePath, "image.png")
                val contentUri =
                    FileProvider.getUriForFile(context, "com.warbler.fileprovider", newFile)

                val sendIntent: Intent =
                    Intent().apply {
                        action = Intent.ACTION_SEND
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        setDataAndType(contentUri, "image/*")
                        putExtra(Intent.EXTRA_STREAM, contentUri)
                    }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(context, shareIntent, null)
            }

            itemBinding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewPagerViewHolder =
        ForecastViewPagerItemBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ).let {
                ViewPagerViewHolder(it)
            }

    override fun getItemCount(): Int = days.size

    override fun onBindViewHolder(
        holder: ViewPagerViewHolder,
        position: Int,
    ) {
        val currentDay = days[position]
        holder.bind(currentDay)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.scrollToPosition(position)
    }
}
