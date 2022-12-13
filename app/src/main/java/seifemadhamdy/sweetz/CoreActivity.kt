package seifemadhamdy.sweetz

import android.content.ActivityNotFoundException
import android.graphics.Outline
import android.os.Bundle
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleObserver
import seifemadhamdy.sweetz.databinding.ActivityCoreBinding
import seifemadhamdy.sweetz.extension.initialize


class CoreActivity : AppCompatActivity(), LifecycleObserver {
    private lateinit var binding: ActivityCoreBinding
    private var revenue = 0
    private var dessertsSold = 0
    private val dessertTimer by lazy { DessertTimer(lifecycle) }
    private var currentDessert = allDesserts[0]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        WindowCompat.getInsetsController(window, window.decorView).apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            hide(WindowInsetsCompat.Type.systemBars())
        }

        // Use Data Binding to get reference to the views
        binding = DataBindingUtil.setContentView(this, R.layout.activity_core)

        savedInstanceState?.let {
            revenue = it.getInt(KEY_REVENUE)
            dessertsSold = it.getInt(KEY_DESSERTS_SOLD)
            dessertTimer.secondsCount = it.getInt(KEY_DESSERT_TIMER_SECONDS_COUNT)
        }

        // Set the TextViews to the right values
        binding.revenue = revenue
        binding.amountSold = dessertsSold

        showCurrentDessert()

        binding.dessertImageButton.setOnClickListener {
            // Update the score
            revenue += currentDessert.price
            dessertsSold++

            binding.revenue = revenue
            binding.amountSold = dessertsSold

            // Show the next dessert
            showCurrentDessert()
        }

        binding.shareFloatingActionButton.setOnClickListener {
            try {
                startActivity(
                    ShareCompat.IntentBuilder(this)
                        .setText(getString(R.string.share_text, dessertsSold, revenue))
                        .setType("text/plain")
                        .intent
                )
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(
                    this, getString(R.string.sharing_not_available),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        binding.blurView.apply {
            initialize(viewGroup = binding.constraintLayout)

            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    (view.height / 4).apply {
                        outline.setRoundRect(
                            0, 0, view.width, view.height + this, toFloat()
                        )
                    }
                }
            }

            clipToOutline = true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_REVENUE, revenue)
        outState.putInt(KEY_DESSERTS_SOLD, dessertsSold)
        outState.putInt(KEY_DESSERT_TIMER_SECONDS_COUNT, dessertTimer.secondsCount)
    }

    /**
     * Determine which dessert to show.
     */
    private fun showCurrentDessert() {
        var newDessert = allDesserts[0]

        for (dessert in allDesserts) {
            if (dessertsSold >= dessert.startProductionAmount) {
                newDessert = dessert
            }
            // The list of desserts is sorted by startProductionAmount. As you sell more desserts,
            // you'll start producing more expensive desserts as determined by startProductionAmount
            // We know to break as soon as we see a dessert who's "startProductionAmount" is greater
            // than the amount sold.
            else break
        }

        // If the new dessert is actually different than the current dessert, update the image
        if (newDessert != currentDessert) {
            currentDessert = newDessert
            binding.dessertImageButton.setImageResource(newDessert.imageId)
        }
    }

    companion object {
        const val KEY_REVENUE = "key_revenue"
        const val KEY_DESSERTS_SOLD = "key_desserts_sold"
        const val KEY_DESSERT_TIMER_SECONDS_COUNT = "key_dessert_timer_seconds_count"

        /**
         * Simple data class that represents a dessert. Includes the resource id integer associated with
         * the image, the price it's sold for, and the startProductionAmount, which determines when
         * the dessert starts to be produced.
         */
        data class Dessert(val imageId: Int, val price: Int, val startProductionAmount: Int)

        // Create a list of all desserts, in order of when they start being produced
        private val allDesserts = listOf(
            Dessert(R.drawable.cupcake, 5, 0),
            Dessert(R.drawable.donut, 10, 5),
            Dessert(R.drawable.eclair, 15, 20),
            Dessert(R.drawable.froyo, 30, 50),
            Dessert(R.drawable.gingerbread, 50, 100),
            Dessert(R.drawable.honeycomb, 100, 200),
            Dessert(R.drawable.icecreamsandwich, 500, 500),
            Dessert(R.drawable.jellybean, 1000, 1000),
            Dessert(R.drawable.kitkat, 2000, 2000),
            Dessert(R.drawable.lollipop, 3000, 4000),
            Dessert(R.drawable.marshmallow, 4000, 8000),
            Dessert(R.drawable.nougat, 5000, 16000),
            Dessert(R.drawable.oreo, 6000, 20000)
        )
    }
}