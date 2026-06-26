package com.biomech.core.resource

object AppResources {
    private var currentLocale: Locale = Locale.EN
    private var _strings: Strings = Strings(currentLocale)

    val strings: Strings get() = _strings

    fun setLocale(locale: Locale) {
        currentLocale = locale
        _strings = Strings(locale)
    }

    fun getLocale(): Locale = currentLocale
}
