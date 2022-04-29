import org.junit.jupiter.api.Test
import org.koin.dsl.koinApplication
import org.koin.test.KoinTest
import org.koin.test.check.checkModules
import ru.spbstu.eventbot.mainModule

internal class ModulesTest : KoinTest {

    @Test
    fun `Verify Koin app`() {
        koinApplication {
            modules(mainModule)
            checkModules()
        }
    }
}
