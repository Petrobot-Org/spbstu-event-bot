import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import ru.spbstu.eventbot.domain.usecases.IsFullNameValidUseCase
import ru.spbstu.eventbot.domain.usecases.IsGroupValidUseCase

internal class IsGroupValidUseCaseTest {

    @org.junit.jupiter.api.Test
    fun invoke() {
        assertTrue(IsGroupValidUseCase("3530904/00004"))
        assertTrue(IsGroupValidUseCase("з3530904/00004"))
        assertTrue(IsGroupValidUseCase("3530902/00001"))
        assertFalse(IsGroupValidUseCase("3430902/00001"))
        assertFalse(IsGroupValidUseCase("3530456/00001"))
        assertFalse(IsGroupValidUseCase("п3530902/00001"))
    }
}