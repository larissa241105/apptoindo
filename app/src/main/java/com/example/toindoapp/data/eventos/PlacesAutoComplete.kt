// Em um novo arquivo, ex: PlacesAutocomplete.kt
import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode

// Esta data class é para retornar os dados que precisamos
data class PlaceData(
    val address: String,
    val latLng: LatLng
)

@Composable
fun PlacesAutocompleteTextField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onPlaceSelected: (PlaceData) -> Unit
) {
    val context = LocalContext.current

    val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)


    val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
        .setCountry("BR")
        .build(context)


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val place = Autocomplete.getPlaceFromIntent(result.data!!)
            val placeData = PlaceData(
                address = place.address ?: place.name ?: "Localização desconhecida",
                latLng = place.latLng!!
            )

            onPlaceSelected(placeData)

        } else if (result.resultCode == AutocompleteActivity.RESULT_ERROR) {
            val status = Autocomplete.getStatusFromIntent(result.data!!)
            Log.e("PlacesAutocomplete", "Erro: ${status.statusMessage}")
        }
    }

    OutlinedTextField(
        value = value,
        onValueChange = { }, // Não faz nada, pois o clique abre o launcher
        label = { Text(label) },
        readOnly = true, // Impede a digitação direta
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = { launcher.launch(intent) }), // <-- A MÁGICA ACONTECE AQUI
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = Color.Black, // Para parecer texto normal
            disabledLabelColor = Color.Gray
        ),
        enabled = false // Desabilita para forçar o clique
    )
}