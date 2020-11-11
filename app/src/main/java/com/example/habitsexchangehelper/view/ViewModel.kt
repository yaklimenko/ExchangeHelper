package com.example.habitsexchangehelper.view

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.example.habitsexchangehelper.di.ActivityComponent
import com.example.habitsexchangehelper.repository.RatesRepository
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

class ExchangeViewModel : ViewModel() {

    @Inject lateinit var ratesRepository: RatesRepository

    val baseAmount: ObservableField<BigDecimal> = ObservableField()
    val targetAmount: ObservableField<BigDecimal> = ObservableField()


    fun onViewStarted(component: ActivityComponent) {
        component.inject(this)
    }

    fun checkSavedBaseAmount(): BigDecimal? {
        ratesRepository.getSavedInputBaseAmount()
            .subscribe {
                    it -> if (!it.equals(BigDecimal.ZERO)) baseAmount.set(it)
            }

    }
}