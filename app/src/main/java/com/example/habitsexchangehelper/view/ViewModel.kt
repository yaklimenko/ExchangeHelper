package com.example.habitsexchangehelper.view

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.habitsexchangehelper.di.ActivityComponent
import com.example.habitsexchangehelper.entity.Currency
import com.example.habitsexchangehelper.repository.RatesRepository
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.lang.NumberFormatException
import java.math.BigDecimal

import javax.inject.Inject

class ExchangeViewModel : ViewModel() {

    @Inject lateinit var ratesRepository: RatesRepository


    private var baseAmount: BigDecimal? = null
    private var targetAmount: BigDecimal? = null
    private var rate: BigDecimal? = null
    private var baseCurrency: Currency? = null
    private var targetCurrency: Currency? = null

    val baseField: MutableLiveData<String> = MutableLiveData()
    val targetField: MutableLiveData<String> = MutableLiveData()



    private val disposables: CompositeDisposable = CompositeDisposable()

    // This observer will invoke onBaseFieldChanged() when the user updates the base field
    private val emailObserver = Observer<String> { onBaseFieldChanged(it) }

    init {
        baseField.observeForever(emailObserver)
    }

    private fun onBaseFieldChanged(baseFieldInput: String?) {
        if (baseFieldInput == null) {
            targetField.value = ""
            return;
        }
        var amount: BigDecimal?
        try {
            amount = BigDecimal(baseFieldInput)
        } catch (e: NumberFormatException) {
            onError(e)
            amount = null;
            return
        }
        if (amount.equals(baseAmount)) {
            return;
        }
        refreshAmount(amount)
    }

    fun refreshAmount (base: BigDecimal? = baseAmount) {
        if (base == null) {
            return;
        }
        baseAmount = base;
        if (rate == null) {
            return
        }
        targetAmount = baseAmount?.multiply(rate)
        targetField.value = targetAmount.toString()
    }



    fun onViewStarted(component: ActivityComponent) {
        component.inject(this)
    }


    fun refreshCurrencies() {
        if (baseCurrency == null || targetCurrency == null) {
            return
        }
        if (baseCurrency!!.equals(targetCurrency)) {
            rate = BigDecimal.ONE
            refreshAmount()
            return
        }
        disposables.add(
            ratesRepository.getRate(baseCurrency!!, targetCurrency!!)
                .subscribe(
                    {
                        rate = it
                        refreshAmount()
                    }, { e -> onError(e) }
                )
        )
    }

    fun onBaseCurrencySet(bc: Currency) {
        if (bc == baseCurrency) {
            return
        }
        baseCurrency = bc
        refreshCurrencies()
    }

    fun onTargetCurrencySet(tc: Currency) {
        if (tc == targetCurrency) {
            return
        }
        targetCurrency = tc
        refreshCurrencies()
    }

    fun recoverSavedBaseAmount() {
        disposables.add(
            ratesRepository.getSavedInputBaseAmount()
            .subscribe ( { it -> refreshAmount(it)}, {e -> onError(e)} )
        )
    }

    fun onError (t: Throwable) {
        Log.e(TAG, "onError: ", t)
        //todo
    }

    companion object{
        private val TAG = ExchangeViewModel::class.java.simpleName
    }
}