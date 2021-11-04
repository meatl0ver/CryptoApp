package raui.imashev.cryptoapp.api

import raui.imashev.cryptoapp.pojo.CoinInfoListOfData
import io.reactivex.Single
import raui.imashev.cryptoapp.pojo.CoinPriceInfoRawData
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("top/totalvolfull")
    fun getTopCoinsInfo(
        @Query(QUERY_PARAM_API_KEY) apiKey: String = API_KEY_VALUE,
        @Query(QUERY_PARAM_LIMIT) limit: Int = LIMIT_DEFAULT_VALUE,
        @Query(QUERY_PARAM_TO_SYMBOL) tSym: String = CURRENCY,
    ): Single<CoinInfoListOfData>

    @GET("pricemultifull")
    fun getFullPriceList(
        @Query(QUERY_PARAM_API_KEY) apiKey: String = API_KEY_VALUE,
        @Query(QUERY_PARAM_FROM_SYMBOLS) fSyms: String,
        @Query(QUERY_PARAM_TO_SYMBOLS) tSyms: String = CURRENCY,
    ): Single<CoinPriceInfoRawData>

    companion object {
        private const val QUERY_PARAM_API_KEY = "api_key"
        private const val API_KEY_VALUE =
            "106cc72d4428c181cbe5b23eaec0fc66eec726801fffd146688dc2b986f639ac"
        private const val QUERY_PARAM_LIMIT = "limit"
        private const val LIMIT_DEFAULT_VALUE = 10
        private const val QUERY_PARAM_TO_SYMBOL = "tsym"
        private const val CURRENCY = "USD"

        private const val QUERY_PARAM_FROM_SYMBOLS = "fsyms"
        private const val QUERY_PARAM_TO_SYMBOLS = "tsyms"

    }
}