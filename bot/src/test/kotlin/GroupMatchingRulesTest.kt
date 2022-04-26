import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import ru.spbstu.eventbot.domain.entities.GroupMatchingRules
import java.time.LocalDate
import java.time.Month

typealias Year = GroupMatchingRules.Year
typealias Speciality = GroupMatchingRules.Speciality

internal class GroupMatchingRulesTest {

    @Test
    fun `empty rules`() {
        val matcher = GroupMatchingRules().toRegex(LocalDate.MIN)

        assertTrue(matcher.matches("3530904/00004"))
        assertTrue(matcher.matches("в3530504/90004"))
        assertTrue(matcher.matches("з3530901/80004"))
        assertTrue(matcher.matches("3535904/70004"))
        assertTrue(matcher.matches("3536044/60004"))
        assertTrue(matcher.matches("3530704/50004"))
        assertTrue(matcher.matches("3532344/40004"))
        assertTrue(matcher.matches("3530234/30004"))
        assertTrue(matcher.matches("3533455/20004"))
        assertTrue(matcher.matches("3533455/10004"))
    }

    @Test
    fun `first half of year`() {
        val date = LocalDate.of(2022, Month.APRIL, 26)
        val years = setOf(Year.valueOf(1)!!)
        val matcher = GroupMatchingRules(years = years).toRegex(date)

        assertTrue(matcher.matches("3530904/10004"))
        assertFalse(matcher.matches("3530904/20004"))
        assertFalse(matcher.matches("3530904/00004"))
    }

    @Test
    fun `second half of year`() {
        val date = LocalDate.of(2022, Month.DECEMBER, 26)
        val years = setOf(Year.valueOf(2)!!)
        val matcher = GroupMatchingRules(years = years).toRegex(date)

        assertTrue(matcher.matches("3530904/10004"))
        assertFalse(matcher.matches("3530904/20004"))
        assertFalse(matcher.matches("3530904/00004"))
    }

    @Test
    fun `only speciality`() {
        val specialities = setOf(Speciality.valueOf("0904")!!)
        val matcher = GroupMatchingRules(specialities = specialities).toRegex(LocalDate.MIN)

        assertTrue(matcher.matches("3530904/00004"))
        assertTrue(matcher.matches("в3530904/30001"))
        assertFalse(matcher.matches("3531904/00004"))
    }

    @Test
    fun everything() {
        val date = LocalDate.of(2022, Month.APRIL, 26)
        val specialities = setOf(Speciality.valueOf("0904")!!)
        val years = setOf(Year.valueOf(2)!!)
        val matcher = GroupMatchingRules(years, specialities).toRegex(date)

        assertTrue(matcher.matches("3530904/00004"))
        assertTrue(matcher.matches("в3530904/00003"))
        assertFalse(matcher.matches("3531904/00004"))
        assertFalse(matcher.matches("3530904/10004"))
    }

    @Test
    fun `several choices`() {
        val date = LocalDate.of(2022, Month.APRIL, 26)
        val specialities = setOf(Speciality.valueOf("0904")!!, Speciality.valueOf("1111")!!)
        val years = setOf(Year.valueOf(2)!!, Year.valueOf(1)!!)
        val matcher = GroupMatchingRules(years, specialities).toRegex(date)

        assertTrue(matcher.matches("3530904/00004"))
        assertTrue(matcher.matches("3531111/00002"))
        assertTrue(matcher.matches("3530904/10003"))
        assertTrue(matcher.matches("3531111/10001"))
        assertFalse(matcher.matches("3530904/20001"))
        assertFalse(matcher.matches("3531904/00004"))
    }
}
