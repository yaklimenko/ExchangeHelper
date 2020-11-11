package com.example.habitsexchangehelper.view

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.example.habitsexchangehelper.di.ActivityComponent
import com.example.habitsexchangehelper.entity.Currency
import com.example.habitsexchangehelper.repository.RatesRepository
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.math.BigDecimal

import javax.inject.Inject

class ExchangeViewModel : ViewModel() {

    @Inject lateinit var ratesRepository: RatesRepository


    var baseAmount: BigDecimal? = null
    var targetAmount: BigDecimal? = null

    val baseField: ObservableField<String> = ObservableField()
    val targetField: ObservableField<String> = ObservableField()

    private lateinit var rate: BigDecimal

    private val disposables: CompositeDisposable = CompositeDisposable()

    fun onViewStarted(component: ActivityComponent) {
        component.inject(this)
    }

    fun on

    fun onCurrencesSelected(base: Currency, target: Currency) {

        disposables.add(
            ratesRepository.getRate(base, target)
                .subscribe(
                    {it ->
                        rate = it

                    }
                )
        )

    }

    fun checkSavedBaseAmount() {
        disposables.add(
            ratesRepository.getSavedInputBaseAmount()
            .subscribe {
                    it -> if (!it.equals(BigDecimal.ZERO)) baseAmount.set(it)
            }
        )
    }


}