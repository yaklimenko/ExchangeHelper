package com.example.habitsexchangehelper.view

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.habitsexchangehelper.di.ActivityComponent
import com.example.habitsexchangehelper.entity.Currency
import com.example.habitsexchangehelper.repository.RatesRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import retrofit2.HttpException
import java.math.BigDecimal
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ExchangeViewModel : ViewModel() {

    @Inject
    lateinit var ratesRepository: RatesRepository


    private var baseAmount: BigDecimal = BigDecimal.ZERO
    private var targetAmount: BigDecimal? = null
    private var rate: BigDecimal? = null


    val baseAmountFieldDown: MutableLiveData<String> = MutableLiveData()
    val baseAmountFieldUp: MutableLiveData<String> = MutableLiveData()

    val targetAmountField: MutableLiveData<String> = MutableLiveData()
    val baseCurrencySelected: MutableLiveData<Currency> = MutableLiveData()

    val targetCurrencySelected: MutableLiveData<Currency> = MutableLiveData()
    val errorMsg: MutableLiveData<String> = MutableLiveData()

    private val disposables: CompositeDisposable = CompositeDisposable()

    // This observer will invoke onBaseFieldChanged() when the user updates the base field
    private val amountObserver = Observer<String> { onBaseFieldChanged(it) }
    private val observableAmount: PublishSubject<String> = PublishSubject.create()

    init {
        baseAmountFieldDown.observeForever(amountObserver)
    }

    fun onActivityStarted() {
        recoverCurrencies()
        recoverSavedBaseAmount()
        refreshCurrencies()
        subscribeAmountObservable()
    }

    private fun subscribeAmountObservable() {
        observableAmount
            .debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                handleAmount(it)
            }, { e -> onError(e) })

    }

    private fun handleAmount(it: String?) {
        val amount: BigDecimal?
        val tmp: String = if (it == null || it.isEmpty()) {
            "0"
        } else {
            it;
        }
        amount = try {
            BigDecimal(tmp)
        } catch (e: NumberFormatException) {
            onError(e)
            null
        }
        refreshAmount(amount)
    }

    private fun onBaseFieldChanged(baseFieldInput: String?) {
        observableAmount.onNext(baseFieldInput)
    }

    private fun refreshAmount(base: BigDecimal?) {
        if (base != baseAmount) {
            baseAmount = base ?: BigDecimal.ZERO
            refreshAmount()
        }
    }

    private fun refreshAmount() {
        if (rate == null) {
            return
        }
        targetAmount = baseAmount.multiply(rate)
        targetAmountField.value = targetAmount.toString()
        Log.d(TAG, "refreshAmount: $targetAmount")
    }



    fun inject(component: ActivityComponent) =
        component.inject(this)

    private fun refreshCurrencies() {
        Log.d(TAG, "refreshCurrencies: START")

        val baseVal = baseCurrencySelected.value
        val targetVal = targetCurrencySelected.value
        if (baseVal == null) {
            return
        }
        if (targetVal == null) {
            return
        }
        if (baseVal == targetVal) {
            rate = BigDecimal.ONE
            refreshAmount()
            return
        }

        disposables.add(
            ratesRepository.getRate(baseVal, targetVal)
                .onErrorResumeNext {
                    Log.d(TAG, "refreshCurrencies: ERROR")
                    if (it is HttpException || it is UnknownHostException) {
                        Log.e(TAG, "refreshCurrencies: ", it)
                        return@onErrorResumeNext ratesRepository.getSavedRate(baseVal, targetVal)
                    } else {
                        Single.error(it)
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        rate = it
                        refreshAmount()
                        Log.d(TAG, "refreshCurrencies: SUCCESS")
                    }, { e -> onError(e) }
                )
        )
    }

    fun onBaseCurrencySet(bc: Currency) {
        if (bc == baseCurrencySelected.value) {
            return
        }
        baseCurrencySelected.value = bc
        refreshCurrencies()
    }

    fun onTargetCurrencySet(tc: Currency) {
        if (tc == targetCurrencySelected.value) {
            return
        }
        targetCurrencySelected.value = tc
        refreshCurrencies()
    }

    private fun recoverSavedBaseAmount() {
        disposables.add(
            ratesRepository.getBaseAmount()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    baseAmountFieldUp.value = it.toEngineeringString()
                    refreshAmount(it)
                }, { e -> onError(e) })
        )
    }

    private fun recoverCurrencies() {
        disposables.add(
            ratesRepository.getSavedBaseCurrency()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    baseCurrencySelected.value = it
                    return@map Single.just(true)
                }
                .observeOn(Schedulers.io())
                .flatMap { ratesRepository.getSavedTargetCurrency() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    targetCurrencySelected.value = it
                    refreshCurrencies()
                }, { e -> onError(e) })
        )
    }

    private fun onError(t: Throwable) {
        Log.e(TAG, "onError: ", t)
        if (t is HttpException) {
            errorMsg.value = "" + t.code() + t.response().toString() + t.message()
            return
        }
        errorMsg.value = t.message
    }

    fun saveState() {
        Log.d(TAG, "saveState: amount:$baseAmount")
        ratesRepository.saveBaseAmount(baseAmount)
            .andThen(
                ratesRepository.saveCurrencies(
                    baseCurrencySelected.value!!,
                    targetCurrencySelected.value!!
                )
            )
            .subscribe()
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
        baseAmountFieldDown.removeObserver(amountObserver)
    }


    companion object {
        private val TAG = ExchangeViewModel::class.java.simpleName
    }
}