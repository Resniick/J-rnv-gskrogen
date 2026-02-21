package se.jarnvagskrogen.lunchmeny.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import se.jarnvagskrogen.lunchmeny.data.model.DayMenu
import se.jarnvagskrogen.lunchmeny.data.repository.MenuRepository
import java.time.DayOfWeek
import java.time.LocalDate

data class MenuUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val weekMenu: List<DayMenu> = emptyList(),
    val selectedDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    val isWeekend: Boolean = false,
    val errorMessage: String? = null,
)

class LunchMenuViewModel(
    private val repository: MenuRepository = MenuRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    init {
        val today = LocalDate.now()
        val dayOfWeek = today.dayOfWeek
        val isWeekend = dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY

        _uiState.update {
            it.copy(
                selectedDayOfWeek = if (isWeekend) DayOfWeek.MONDAY else dayOfWeek,
                isWeekend = isWeekend,
            )
        }

        loadMenu()
    }

    fun selectDay(dayOfWeek: DayOfWeek) {
        _uiState.update { it.copy(selectedDayOfWeek = dayOfWeek) }
    }

    fun refresh() {
        _uiState.update { it.copy(isRefreshing = true) }
        loadMenu()
    }

    fun retry() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        loadMenu()
    }

    private fun loadMenu() {
        viewModelScope.launch {
            val result = repository.fetchWeekMenu()

            result.fold(
                onSuccess = { menus ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            weekMenu = menus,
                            errorMessage = null,
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = error.message ?: "Ett oväntat fel uppstod",
                        )
                    }
                }
            )
        }
    }
}
