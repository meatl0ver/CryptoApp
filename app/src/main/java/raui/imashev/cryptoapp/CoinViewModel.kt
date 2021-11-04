package raui.imashev.cryptoapp

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import raui.imashev.cryptoapp.api.ApiFactory
import raui.imashev.cryptoapp.database.AppDatabase
import raui.imashev.cryptoapp.pojo.CoinPriceInfo
import raui.imashev.cryptoapp.pojo.CoinPriceInfoRawData
import java.util.concurrent.TimeUnit


class CoinViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val compositeDisposable = CompositeDisposable()

    val priceList = db.coinPriceInfoDao().getPriceList()

    fun getDetailInfo(fSym: String): LiveData<CoinPriceInfo> {
        return db.coinPriceInfoDao().getPriceInfoAboutCoin(fSym)
    }

    init {
        loadData()
    }

    private fun loadData() {
        val disposable = ApiFactory.apiService.getTopCoinsInfo(limit = 50)
            .map { it.data?.map { it.coinInfo?.name }?.joinToString(",") }
            .flatMap { ApiFactory.apiService.getFullPriceList(fSyms = it) }
            .map { getPriceListFromRowData(it) }
            .delaySubscription(
                10, //частота обновлений
                TimeUnit.SECONDS //единица измерения для обновлений
            )
            .repeat() //Оператор rxjava, выполняющий загрузку бесконечно если загрузка успешна
            .retry() // Оператор rxjava, выполняющий загрузку бесконечно если загрузка неуспешна
            .subscribeOn(Schedulers.io())
            .subscribe({
                db.coinPriceInfoDao().insertData(it)
                Log.d("TEST_OF_LOADING_DATA", "SUCCESS: $it")
            }, {
                //если происходит ошибка repeat() останавливает загрузку без retry()
                Log.d("TEST_OF_LOADING_DATA", "Failure: ${it.message}")

            })
        compositeDisposable.add(disposable)
    }

    private fun getPriceListFromRowData(
        coinPriceInfoRawData: CoinPriceInfoRawData
    )
            : List<CoinPriceInfo> {
        val result = ArrayList<CoinPriceInfo>()
        val jsonObject = coinPriceInfoRawData.coinPriceInfoJsonObject ?: return result
        val jsonObjectKeySet = jsonObject.keySet()
        for (coinKey in jsonObjectKeySet) {
            val currencyJson = jsonObject.getAsJsonObject(coinKey)
            val currencyKeySet = currencyJson.keySet()
            for (currencyKey in currencyKeySet) {
                val priceInfo = Gson().fromJson(
                    currencyJson.getAsJsonObject(currencyKey),
                    CoinPriceInfo::class.java
                )
                result.add(priceInfo)
            }
        }
        return result
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}