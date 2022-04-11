import org.junit.jupiter.api.Assertions.*
import ru.spbstu.eventbot.domain.usecases.IsFullNameValidUseCase

internal class IsFullNameValidUseCaseTest {

    @org.junit.jupiter.api.Test
    fun invoke() {
        assertTrue(IsFullNameValidUseCase("Душечкина Виктория Сергеевна"))
        assertTrue(IsFullNameValidUseCase("Эзериня Марта"))
        assertTrue(IsFullNameValidUseCase("Серена Ван Дер Вудсен"))
        assertFalse(IsFullNameValidUseCase("Чак"))
    }
}