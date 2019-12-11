import java.lang.IllegalArgumentException
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.pow
import kotlin.math.sqrt

fun readResource(name: String) = ClassLoader.getSystemClassLoader().getResource(name)?.readText()

fun <T> List<List<T>>.transpose(): List<List<T>> {
    val result = mutableListOf<List<T>>()
    val n = get(0).size
    for (i in  0 until n) {
        val col = mutableListOf<T>()
        for (row in this) {
            col.add(row[i])
        }
        result.add(col)
    }
    return result
}

fun <E> List<E>.permute():List<List<E>> {
    if (size == 1) return listOf(this)
    val perms = mutableListOf<List<E>>()
    val sub = get(0)
    for(perm in drop(1).permute())
        for (i in 0..perm.size){
            val newPerm=perm.toMutableList()
            newPerm.add(i, sub)
            perms.add(newPerm)
        }
    return perms
}


fun gcd(a: Int, b: Int): Int = // Greatest Common Divisor (Euclid)
    when {
        a == 0 -> b
        b == 0 -> a
        a > b -> gcd(a-b, b)
        else -> gcd(a, b-a)
    }

// see https://www.mathsisfun.com/polar-cartesian-coordinates.html
data class PolarCoordinate(val dist: Double, val angle: Double)

data class CartesianCoordinate(val x: Double, val y: Double) {
    fun toPolar(): PolarCoordinate {
        val dist = sqrt(x.pow(2) + y.pow(2))
        val h = atan(y / x)
        val angle = when {
            x >= 0 && y >= 0 -> h // Quadrant I
            x < 0 && y >= 0 -> h + PI // Quadrant II
            x < 0 && y < 0 -> h + PI // Quadrant III
            x >= 0 && y < 0 -> h + PI * 2.0// Quadrant III
            else -> throw IllegalArgumentException("Unkown quadrant for x=$x y=$y")
        }
        return PolarCoordinate(dist, angle)
    }
}
