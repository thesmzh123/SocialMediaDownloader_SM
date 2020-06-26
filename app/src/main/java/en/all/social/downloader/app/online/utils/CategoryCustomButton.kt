package en.all.social.downloader.app.online.utils

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.cardview.widget.CardView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import en.all.social.downloader.app.online.R

@Suppress("DEPRECATION")
class CategoryCustomButton : CardView {
    private var cardView: MaterialCardView? = null

    constructor(context: Context) : super(context)
    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        View.inflate(context, R.layout.custom_layout, this)
        val btnText = findViewById<MaterialTextView>(R.id.btnText)
        cardView = findViewById(R.id.cardView)
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.CategoryCustomButton)
        btnText.text = typedArray.getString(R.styleable.CategoryCustomButton_btntext)
        cardView!!.setCardBackgroundColor(resources.getColor(android.R.color.darker_gray))
        typedArray.recycle()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    override fun setCardBackgroundColor(color: Int) {
        cardView!!.setCardBackgroundColor(color)
    }
}