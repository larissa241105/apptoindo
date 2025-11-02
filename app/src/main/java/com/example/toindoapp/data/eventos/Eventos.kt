
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Evento(
    val id: String = "",
    val nome: String = "",
    val data: String = "",
    val horario: String = "",
    val descricao: String =  "",
    val local: String = "",
    val preco: Double = 0.0,
    val isGratuito: Boolean = true,
    val categoria: String = "",
    val creatorName: String = "Desconhecido",
    val publico: Boolean = true,
    val participantesCount: Int = 0,
    val imagemUrl: String? = null,
    val creatorId: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,


    @ServerTimestamp
    val criadoEm: Date? = null,
)