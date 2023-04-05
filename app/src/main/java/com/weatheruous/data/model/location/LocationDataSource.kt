package com.weatheruous.data.model.location
import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Keep
@Serializable
data class LocationDataSource(
    @SerialName("country")
    val country: String,
    @SerialName("lat")
    val lat: Double,
    @SerialName("local_names")
    val localNames: LocalNames? = null,
    @SerialName("lon")
    val lon: Double,
    @SerialName("name")
    val name: String,
    @SerialName("state")
    val state: String? = null
)

@Keep
@Serializable
data class LocalNames(
    @SerialName("af")
    val af: String? = null,
    @SerialName("ar")
    val ar: String? = null,
    @SerialName("ascii")
    val ascii: String? = null,
    @SerialName("az")
    val az: String? = null,
    @SerialName("bg")
    val bg: String? = null,
    @SerialName("ca")
    val ca: String? = null,
    @SerialName("da")
    val da: String? = null,
    @SerialName("de")
    val de: String? = null,
    @SerialName("el")
    val el: String? = null,
    @SerialName("en")
    val en: String? = null,
    @SerialName("et")
    val et: String? = null,
    @SerialName("eu")
    val eu: String? = null,
    @SerialName("fa")
    val fa: String? = null,
    @SerialName("feature_name")
    val featureName: String? = null,
    @SerialName("fi")
    val fi: String? = null,
    @SerialName("fr")
    val fr: String? = null,
    @SerialName("gl")
    val gl: String? = null,
    @SerialName("he")
    val he: String? = null,
    @SerialName("hi")
    val hi: String? = null,
    @SerialName("hr")
    val hr: String? = null,
    @SerialName("hu")
    val hu: String? = null,
    @SerialName("hy")
    val hy: String? = null,
    @SerialName("id")
    val id: String? = null,
    @SerialName("it")
    val itx: String? = null,
    @SerialName("ja")
    val ja: String? = null,
    @SerialName("ko")
    val ko: String? = null,
    @SerialName("ku")
    val ku: String? = null,
    @SerialName("la")
    val la: String? = null,
    @SerialName("lt")
    val lt: String? = null,
    @SerialName("mk")
    val mk: String? = null,
    @SerialName("nl")
    val nl: String? = null,
    @SerialName("no")
    val no: String? = null,
    @SerialName("pl")
    val pl: String? = null,
    @SerialName("pt")
    val pt: String? = null,
    @SerialName("ro")
    val ro: String? = null,
    @SerialName("ru")
    val ru: String? = null,
    @SerialName("sc")
    val sc: String? = null,
    @SerialName("sk")
    val sk: String? = null,
    @SerialName("sl")
    val sl: String? = null,
    @SerialName("sr")
    val sr: String? = null,
    @SerialName("th")
    val th: String? = null,
    @SerialName("tr")
    val tr: String? = null,
    @SerialName("uk")
    val uk: String? = null,
    @SerialName("vi")
    val vi: String? = null,
    @SerialName("zh")
    val zh: String? = null,
    @SerialName("zu")
    val zu: String? = null
)
