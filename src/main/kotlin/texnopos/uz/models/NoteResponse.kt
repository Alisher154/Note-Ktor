package texnopos.uz.models

import kotlinx.serialization.Serializable


@Serializable
data class NoteResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T
)
