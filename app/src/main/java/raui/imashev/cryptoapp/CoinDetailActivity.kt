package raui.imashev.cryptoapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_coin_detail.*

class CoinDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: CoinViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coin_detail)
        if (!intent.hasExtra(GET_FROM_SYMBOL)) {
            finish()
            return
        }
        val fSym = intent.getStringExtra(GET_FROM_SYMBOL).toString()
        viewModel = ViewModelProvider(this).get(CoinViewModel::class.java)
        viewModel.getDetailInfo(fSym).observe(this, {

            val lastUpdateTemplate = this.resources.getString(R.string.last_update_template)

            tvPrice.text = it.price.toString()
            tvMinPrice.text = it.lowDay.toString()
            tvMaxPrice.text = it.highDay.toString()
            tvLastMarket.text = it.lastMarket.toString()
            tvLastUpdate.text = String.format(lastUpdateTemplate, it.getFormattedTime())
            tvFromSymbol.text = it.fromSymbol
            tvToSymbol.text = it.toSymbol
            Picasso.get().load(it.getFullImageUrl()).into(ivLogoCoin)
        })
    }

    companion object {
        const val GET_FROM_SYMBOL = "fSym"

        fun newIntent(context: Context, fSym: String): Intent {
            val intent = Intent(context, CoinDetailActivity::class.java)
            intent.putExtra(GET_FROM_SYMBOL, fSym)
            return intent
        }
    }
}