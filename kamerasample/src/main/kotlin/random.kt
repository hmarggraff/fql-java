package org.funql.ri.test

import java.util.Random
import java.lang.Math.abs

/**
 */
class RandomGenerator
{
class object
{
    val rg = Random(4);
    val vokale = array('a', 'e', 'i', 'o', 'u')
    val konsonanten = array('b', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'p', 'r', 's', 't', 'v', 'w', 'x', 'z')
    val kombis = array("sh", "sl", "sm", "sp", "st", "lb", "lf", "lg", "lk", "lm", "lp", "ls", "lt", "ch", "bl", "br", "dr", "fl", "fr", "pf", "pl", "pr", "rt", "rl", "rn", "rm", "tr")
    val akombis = array("sh", "sk", "sl", "sp", "st", "bl", "br", "dr", "fl", "fr", "pf", "pl", "pr", "tr")
    val ekombis = array("sh", "st", "lb", "lk", "lm", "ls", "lt", "rt", "rg", "rn", "ng")
    val surNames = array("John", "Peter", "Charles", "Gilbert", "Thomas", "Martin", "Walter", "Susan", "Eva", "Ginger", "Rebecca", "Jennifer", "Caroline")

    fun nextInt(modulo: Int) = rg.nextInt(modulo);

    fun nextLong(modulo: Long) = abs(rg.nextLong()) % modulo;

    fun nextFloat() = rg.nextDouble();

    fun nextRand64() = rg.nextInt(64);

    /**
       * Generates a random String that is not entirely unlike a name.
       *
       * @param baLen int	the approximate length of the string
       * @return A random String that contains enough vocal charaters so that it can be pronounced.
       */
    fun rString(baLen: Int): String
    {
        val ba = StringBuffer(baLen);
        var ix = 0;
        if (nextInt(4) == 0)
        {
            ba.append(pick(akombis))
            ba.append(pick(vokale))
            ix += 3;
        }
        else if (nextInt(3) != 0)
        {
            ba.append(pick(konsonanten))
            ix += 1;
        }
        ba.append(pick(vokale))
        ix += 1;
        while (ix < baLen)
        {
            val vi = nextInt(konsonanten.size)
            if (nextInt(4) == 0)
            {
                ba.append(pick(kombis))
                ix += 1;
            }
            else
                ba.append(konsonanten[vi]);
            ba.append(pick(vokale))
            ix += 2;
        }
        if (rg.nextBoolean())
        {
            ba.append(pick(ekombis))
        }
        ba.setCharAt(0, Character.toUpperCase(ba.charAt(0)))
        return ba.toString()
    }

    fun string32() = rString(nextInt(16) + 17);

    fun string8(): String = rString(nextInt(4) + 5)

    fun string12() = rString(nextInt(7) + 6);

    val makeVorname: String get() = surNames[nextInt(surNames.size)]

    fun pick<T:Any>(src: Array<T>):T = src[nextInt(src.size)]

}
}
