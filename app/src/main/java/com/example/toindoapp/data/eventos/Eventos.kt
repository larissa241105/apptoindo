// Você pode criar este arquivo, por exemplo, como Evento.kt
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Evento(
    val id: String = "", // <-- CAMPO NOVO ADICIONADO
    val nome: String = "",
    val data: String = "",
    val horario: String = "",
    val local: String = "",
    val preco: Double = 0.0,
    val isGratuito: Boolean = true,
    val categoria: String = "",
    val isPublico: Boolean = true,
    val imagemUrl: String? = null, // URL da imagem no Firebase Storage,
    val creatorId: String = "", // <-- CAMPO ADICIONADO,
    //val participants: List<String> = listOf(), // campo adicionado
    //val participantLimit: Int?=null, //campo adicionado
    @ServerTimestamp
    val criadoEm: Date? = null, // Data de criação automática pelo Firestore
)