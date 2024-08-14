
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    private val _showDialog = mutableStateOf(false)
    val showDialog: State<Boolean> = _showDialog

    fun toggleDialog() {
        _showDialog.value = !_showDialog.value
    }

    fun setDialogState(state: Boolean) {
        _showDialog.value = state
    }
}