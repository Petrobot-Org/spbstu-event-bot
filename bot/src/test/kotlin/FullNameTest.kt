import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import ru.spbstu.eventbot.domain.entities.FullName

internal class FullNameTest {

    @Test
    fun invoke() {
        assertNotNull(FullName.valueOf("Душечкина Виктория Сергеевна"))
        assertNotNull(FullName.valueOf("Эзериня Марта"))
        assertNotNull(FullName.valueOf("Серена Ван Дер Вудсен"))
        assertNull(FullName.valueOf("Чак"))
    }
}
