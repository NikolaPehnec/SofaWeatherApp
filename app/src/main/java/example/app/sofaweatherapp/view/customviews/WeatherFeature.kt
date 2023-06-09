package example.app.sofaweatherapp.view.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import example.app.sofaweatherapp.R
import example.app.sofaweatherapp.databinding.WeatherFeatureDetailBinding

class WeatherFeature @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: WeatherFeatureDetailBinding = WeatherFeatureDetailBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.WeatherFeature, 0, 0).apply {
            try {
                binding.name.text = getString(R.styleable.WeatherFeature_name)
                binding.value.text = getString(R.styleable.WeatherFeature_value)
                binding.image.setImageDrawable(getDrawable(R.styleable.WeatherFeature_imgRes))
            } finally {
                recycle()
            }
        }
    }

    fun setValue(value: String) {
        binding.value.text = value
    }
}
