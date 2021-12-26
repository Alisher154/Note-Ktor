package texnopos.uz.models

import kotlinx.serialization.Serializable

@Serializable
data class NoteRequest(
    val note: String
)
