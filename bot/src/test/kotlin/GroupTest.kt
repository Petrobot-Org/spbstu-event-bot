import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import ru.spbstu.eventbot.domain.entities.Group

internal class GroupTest {

    @Test
    fun invoke() {
        assertNotNull(Group.valueOf("3530904/00004"))
        assertNotNull(Group.valueOf("з3530904/00004"))
        assertNotNull(Group.valueOf("3530902/00001"))
        assertNull(Group.valueOf("3430902/00001"))
        assertNull(Group.valueOf("3530456/00001"))
        assertNull(Group.valueOf("п3530902/00001"))
    }
}
