@file:Suppress("UNUSED_PARAMETER")

package lesson11.task1
import kotlin.math.max
import kotlin.math.pow

/**
 * Класс "полином с вещественными коэффициентами".
 *
 * Общая сложность задания -- средняя, общая ценность в баллах -- 16.
 * Объект класса -- полином от одной переменной (x) вида 7x^4+3x^3-6x^2+x-8.
 * Количество слагаемых неограничено.
 *
 * Полиномы можно складывать -- (x^2+3x+2) + (x^3-2x^2-x+4) = x^3-x^2+2x+6,
 * вычитать -- (x^3-2x^2-x+4) - (x^2+3x+2) = x^3-3x^2-4x+2,
 * умножать -- (x^2+3x+2) * (x^3-2x^2-x+4) = x^5+x^4-5x^3-3x^2+10x+8,
 * делить с остатком -- (x^3-2x^2-x+4) / (x^2+3x+2) = x-5, остаток 12x+16
 * вычислять значение при заданном x: при x=5 (x^2+3x+2) = 42.
 *
 * В конструктор полинома передаются его коэффициенты, начиная со старшего.
 * Нули в середине и в конце пропускаться не должны, например: x^3+2x+1 --> Polynom(1.0, 2.0, 0.0, 1.0)
 * Старшие коэффициенты, равные нулю, игнорировать, например Polynom(0.0, 0.0, 5.0, 3.0) соответствует 5x+3
 */
class Polynom(vararg coeffs: Double) {

    private val firstNonNullIndex = coeffs.indexOfFirst { it != 0.0 }

    private val coeffsRevers = coeffs.reversed().take(coeffs.size - firstNonNullIndex)

    /**
     * Геттер: вернуть значение коэффициента при x^i
     */
    fun coeff(i: Int): Double = coeffsRevers[i]


    /**
     * Расчёт значения при заданном x
     */
    fun getValue(x: Double): Double = coeffsRevers.withIndex().map { (index, value) -> value * x.pow(index) }.sum()


    /**
     * Степень (максимальная степень x при ненулевом слагаемом, например 2 для x^2+x+1).
     *
     * Степень полинома с нулевыми коэффициентами считать равной 0.
     * Слагаемые с нулевыми коэффициентами игнорировать, т.е.
     * степень 0x^2+0x+2 также равна 0.
     */
    fun degree(): Int = coeffsRevers.size - 1

    /**
     * Сложение
     */
    operator fun plus(other: Polynom): Polynom {
        val highDegree = max(this.degree(), other.degree())
        val result = mutableListOf<Double>()

        for (i in 0..highDegree) {

            val thisElement = if (i <= degree()) coeffsRevers[i] else 0.0
            val otherElement = if (i <= other.degree()) other.coeffsRevers[i] else 0.0

            result.add( thisElement + otherElement )
        }
        return Polynom(*result.reversed().toDoubleArray())
    }

    /**
     * Смена знака (при всех слагаемых)
     */
    operator fun unaryMinus(): Polynom = Polynom(*coeffsRevers.map { -it }.reversed().toDoubleArray())

    /**
     * Вычитание
     */
    operator fun minus(other: Polynom): Polynom = this + -other

    /**
     * Умножение
     */
    operator fun times(other: Polynom): Polynom {
        val result = Array(this.degree() + other.degree() + 1 ){ 0.0 }.toMutableList()

        for (i in 0..this.degree())
            for (j in 0..other.degree())
                result[i + j] += this.coeffsRevers[i] * other.coeffsRevers[j]

        return Polynom(*result.reversed().toDoubleArray())
    }

    /**
     * Деление
     *
     * Про операции деления и взятия остатка см. статью Википедии
     * "Деление многочленов столбиком". Основные свойства:
     *
     * Если A / B = C и A % B = D, то A = B * C + D и степень D меньше степени B
     */
    operator fun div(other: Polynom): Polynom = divideWithRemainder(other)[0]!!

    /**
     * Взятие остатка
     */
    operator fun rem(other: Polynom): Polynom = divideWithRemainder(other)[1]!!

    private fun divideWithRemainder(p: Polynom): Array<Polynom?> {
        val answer: Array<Polynom?> = arrayOfNulls(2)
        val m = degree()
        val n: Int = p.degree()
        if (m < n) {
            val q = doubleArrayOf(0.0)
            answer[0] = Polynom(*q)
            answer[1] = p
            return answer
        }
        val quotient = DoubleArray(m - n + 1)
        val coef = DoubleArray(m + 1)
        for (k in 0..m) {
            coef[k] = coeffsRevers[k]
        }
        val norm: Double = 1 / p.coeff(n)
        for (k in m - n downTo 0) {
            quotient[k] = coef[n + k] * norm
            for (j in n + k - 1 downTo k) {
                coef[j] -= quotient[k] * p.coeff(j - k)
            }
        }
        val remainder = DoubleArray(n)
        for (k in 0 until n) {
            remainder[k] = coef[k]
        }
        answer[0] = Polynom(*quotient.reversed().toDoubleArray())
        answer[1] = Polynom(*remainder.reversed().toDoubleArray())
        return answer
    }

    /**
     * Сравнение на равенство
     */
    override fun equals(other: Any?): Boolean {
        return other is Polynom && (this === other || this.coeffsRevers == other.coeffsRevers)
    }

    /**
     * Получение хеш-кода
     */
    override fun hashCode(): Int = coeffsRevers.hashCode()
    
}
