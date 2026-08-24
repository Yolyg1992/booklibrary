package com.tustockpro.booklibrary.domain.model

enum class FontScaleOption(
    val label: String,
    val scale: Float
) {
    NORMAL("Normal", 1.0f),
    LARGE("Grande", 1.1f),
    EXTRA_LARGE("Extra grande", 1.25f);

    companion object {
        fun fromStorage(value: String?): FontScaleOption {
            return entries.firstOrNull {
                it.name == value
            } ?: NORMAL
        }
    }
}
