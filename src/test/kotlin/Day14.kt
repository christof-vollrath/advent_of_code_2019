import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.data_driven.data
import org.jetbrains.spek.data_driven.on as onData
import kotlin.math.ceil

/*
--- Day 14: Space Stoichiometry ---

As you approach the rings of Saturn, your ship's low fuel indicator turns on.
There isn't any fuel here, but the rings have plenty of raw material.
Perhaps your ship's Inter-Stellar Refinery Union brand nanofactory can turn these raw materials into fuel.

You ask the nanofactory to produce a list of the reactions it can perform that are relevant to this process
(your puzzle input).
Every reaction turns some quantities of specific input chemicals into some quantity of an output chemical.
Almost every chemical is produced by exactly one reaction;
the only exception, ORE, is the raw material input to the entire process and is not produced by a reaction.

You just need to know how much ORE you'll need to collect before you can produce one unit of FUEL.

Each reaction gives specific quantities for its inputs and output;
reactions cannot be partially run, so only whole integer multiples of these quantities can be used.
(It's okay to have leftover chemicals when you're done, though.)
For example, the reaction 1 A, 2 B, 3 C => 2 D means that exactly 2 units of chemical D can be produced
by consuming exactly 1 A, 2 B and 3 C.

You can run the full reaction as many times as necessary;
for example, you could produce 10 D by consuming 5 A, 10 B, and 15 C.

Suppose your nanofactory produces the following list of reactions:

10 ORE => 10 A
1 ORE => 1 B
7 A, 1 B => 1 C
7 A, 1 C => 1 D
7 A, 1 D => 1 E
7 A, 1 E => 1 FUEL

The first two reactions use only ORE as inputs; they indicate that you can produce as much of chemical A as you want
(in increments of 10 units, each 10 costing 10 ORE) and as much of chemical B as you want (each costing 1 ORE).

To produce 1 FUEL, a total of 31 ORE is required: 1 ORE to produce 1 B,
then 30 more ORE to produce the 7 + 7 + 7 + 7 = 28 A (with 2 extra A wasted) required in the reactions
to convert the B into C, C into D, D into E, and finally E into FUEL.
(30 A is produced because its reaction requires that it is created in increments of 10.)

Or, suppose you have the following list of reactions:

9 ORE => 2 A
8 ORE => 3 B
7 ORE => 5 C
3 A, 4 B => 1 AB
5 B, 7 C => 1 BC
4 C, 1 A => 1 CA
2 AB, 3 BC, 4 CA => 1 FUEL

The above list of reactions requires 165 ORE to produce 1 FUEL:

Consume 45 ORE to produce 10 A.
Consume 64 ORE to produce 24 B.
Consume 56 ORE to produce 40 C.
Consume 6 A, 8 B to produce 2 AB.
Consume 15 B, 21 C to produce 3 BC.
Consume 16 C, 4 A to produce 4 CA.
Consume 2 AB, 3 BC, 4 CA to produce 1 FUEL.

Here are some larger examples:

13312 ORE for 1 FUEL:

157 ORE => 5 NZVS
165 ORE => 6 DCFZ
44 XJWVT, 5 KHKGT, 1 QDVJ, 29 NZVS, 9 GPVTF, 48 HKGWZ => 1 FUEL
12 HKGWZ, 1 GPVTF, 8 PSHF => 9 QDVJ
179 ORE => 7 PSHF
177 ORE => 5 HKGWZ
7 DCFZ, 7 PSHF => 2 XJWVT
165 ORE => 2 GPVTF
3 DCFZ, 7 NZVS, 5 HKGWZ, 10 PSHF => 8 KHKGT

180697 ORE for 1 FUEL:

2 VPVL, 7 FWMGM, 2 CXFTF, 11 MNCFX => 1 STKFG
17 NVRVD, 3 JNWZP => 8 VPVL
53 STKFG, 6 MNCFX, 46 VJHF, 81 HVMC, 68 CXFTF, 25 GNMV => 1 FUEL
22 VJHF, 37 MNCFX => 5 FWMGM
139 ORE => 4 NVRVD
144 ORE => 7 JNWZP
5 MNCFX, 7 RFSQX, 2 FWMGM, 2 VPVL, 19 CXFTF => 3 HVMC
5 VJHF, 7 MNCFX, 9 VPVL, 37 CXFTF => 6 GNMV
145 ORE => 6 MNCFX
1 NVRVD => 8 CXFTF
1 VJHF, 6 MNCFX => 4 RFSQX
176 ORE => 6 VJHF

2210736 ORE for 1 FUEL:

171 ORE => 8 CNZTR
7 ZLQW, 3 BMBT, 9 XCVML, 26 XMNCP, 1 WPTQ, 2 MZWV, 1 RJRHP => 4 PLWSL
114 ORE => 4 BHXH
14 VRPVC => 6 BMBT
6 BHXH, 18 KTJDG, 12 WPTQ, 7 PLWSL, 31 FHTLT, 37 ZDVW => 1 FUEL
6 WPTQ, 2 BMBT, 8 ZLQW, 18 KTJDG, 1 XMNCP, 6 MZWV, 1 RJRHP => 6 FHTLT
15 XDBXC, 2 LTCX, 1 VRPVC => 6 ZLQW
13 WPTQ, 10 LTCX, 3 RJRHP, 14 XMNCP, 2 MZWV, 1 ZLQW => 1 ZDVW
5 BMBT => 4 WPTQ
189 ORE => 9 KTJDG
1 MZWV, 17 XDBXC, 3 XCVML => 2 XMNCP
12 VRPVC, 27 CNZTR => 2 XDBXC
15 KTJDG, 12 BHXH => 5 XCVML
3 BHXH, 2 VRPVC => 7 MZWV
121 ORE => 7 VRPVC
7 XCVML => 6 RJRHP
5 BHXH, 4 VRPVC => 5 LTCX

Given the list of reactions in your puzzle input, what is the minimum amount of ORE required to produce exactly 1 FUEL?
 */

class Chemical private constructor (val name: String) {
    companion object {
        val chemicalsMap = mutableMapOf<String, Chemical>()
        fun create(name: String): Chemical {
            val found = chemicalsMap[name]
            return if (found != null) found
                else {
                val chemical = Chemical(name)
                chemicalsMap[name] = chemical
                chemical
            }
        }
    }
    override fun toString(): String = "Chemical(name='$name')"
}

data class ChemicalQuantity(val quantity: Int, val chemical: Chemical)
data class MutableChemicalQuantity(var quantity: Int, val chemical: Chemical) {
    constructor(chemicalQuantity: ChemicalQuantity): this(chemicalQuantity.quantity, chemicalQuantity.chemical)
}
class Inventory : MutableMap<Chemical, MutableChemicalQuantity> by HashMap() {
    override fun toString() = values.toString()
    fun add(chemical: Chemical, surplus: Int) {
        val existing = get(chemical)
        if (existing != null) existing.quantity += surplus
        else put(chemical, MutableChemicalQuantity(surplus, chemical))
    }
}

data class Reaction(val input: List<ChemicalQuantity>, val output: ChemicalQuantity) {
    fun neededInput(quantity: Int): Pair<List<ChemicalQuantity>, Int>{
        val outputQuantity = output.quantity
        val reactionNr = ceil(quantity / outputQuantity.toDouble()).toInt()
        val inputChemicalQuantities = input.map {inputChemicalQuantity ->
            ChemicalQuantity(reactionNr * inputChemicalQuantity.quantity, inputChemicalQuantity.chemical)
        }
        val surplus = reactionNr * outputQuantity - quantity
        return inputChemicalQuantities to surplus
    }
}

val ORE = Chemical.create("ORE")

class Day14Spec : Spek({

    describe("part 1") {
        describe("calculate ore") {
            given("first example") {
                val reactionList = listOf(
                    Reaction(
                        listOf(
                            ChemicalQuantity(10, Chemical.create("ORE"))
                        ),
                        ChemicalQuantity(10, Chemical.create("A"))
                    ),
                    Reaction(
                        listOf(
                            ChemicalQuantity(1, Chemical.create("ORE"))
                        ),
                        ChemicalQuantity(1, Chemical.create("B"))
                    ),
                    Reaction(
                        listOf(
                            ChemicalQuantity(7, Chemical.create("A")),
                            ChemicalQuantity(1, Chemical.create("B"))
                        ),
                        ChemicalQuantity(1, Chemical.create("C"))
                    ),
                    Reaction(
                        listOf(
                            ChemicalQuantity(7, Chemical.create("A")),
                            ChemicalQuantity(1, Chemical.create("C"))
                        ),
                        ChemicalQuantity(1, Chemical.create("D"))
                    ),
                    Reaction(
                        listOf(
                            ChemicalQuantity(7, Chemical.create("A")),
                            ChemicalQuantity(1, Chemical.create("D"))
                        ),
                        ChemicalQuantity(1, Chemical.create("E"))
                    ),
                    Reaction(
                        listOf(
                            ChemicalQuantity(7, Chemical.create("A")),
                            ChemicalQuantity(1, Chemical.create("E"))
                        ),
                        ChemicalQuantity(1, Chemical.create("FUEL"))
                    )
                )
                it("should calculate ore for a direct reaction") {
                    val reactions = Reactions(reactionList)
                    reactions.oreNeeded(10, "A") `should equal` 10
                }
                it("should calculate ore for a direct reaction when more is needed than one reaction") {
                    val reactions = Reactions(reactionList)
                    reactions.oreNeeded(11, "A") `should equal` 20
                }
                it("should calculate ore when more reactions are needed") {
                    val reactions = Reactions(reactionList)
                    reactions.oreNeeded(20, "C") `should equal` 160
                }
                it("should calculate ore for fuel") {
                    val reactions = Reactions(reactionList)
                    reactions.oreNeeded(1, "FUEL") `should equal` 31
                }

            }
            given("first example as string") {
                val reactionString = """
                    10 ORE => 10 A
                    1 ORE => 1 B
                    7 A, 1 B => 1 C
                    7 A, 1 C => 1 D
                    7 A, 1 D => 1 E
                    7 A, 1 E => 1 FUEL
                """.trimIndent()
                val reactionList = reactionString.parseReactions()
                it("should be parsed correctly") {
                    reactionList.size `should equal` 6
                }
                it("should calculate ore for fuel") {
                    val reactions = Reactions(reactionList)
                    reactions.oreNeeded(1, "FUEL") `should equal` 31
                }
            }
            describe("more examples") {
                // see https://www.mathsisfun.com/polar-cartesian-coordinates.html
                val testData = arrayOf(
                    data("""
                        9 ORE => 2 A
                        8 ORE => 3 B
                        7 ORE => 5 C
                        3 A, 4 B => 1 AB
                        5 B, 7 C => 1 BC
                        4 C, 1 A => 1 CA
                        2 AB, 3 BC, 4 CA => 1 FUEL 
                    """.trimIndent(), 165)
                )
                onData("reactions %s ", with = *testData) { reactionString, expected ->
                    it("should calculate expected $expected ore") {
                        val reactionList = reactionString.parseReactions()
                        val reactions = Reactions(reactionList)
                        reactions.oreNeeded(1, "FUEL") `should equal` expected
                    }
                }
            }
        }
    }
})

private fun String.parseReactions(): List<Reaction> = lines().map { line ->
    val (inputString, outputString) = line.split("=>")
    val inputs = inputString.split(",").map { input ->
        val (quantityString, chemicalString) = input.trim().split(" ")
        val quantity = quantityString.trim().toInt()
        val chemical = Chemical.create(chemicalString.trim())
        ChemicalQuantity(quantity, chemical)
    }
    val (outputQuantityString, outputChemicalString) = outputString.trim().split(" ")
    val outputQuantity = outputQuantityString.trim().toInt()
    val outputChemical = Chemical.create(outputChemicalString.trim())
    val output = ChemicalQuantity(outputQuantity, outputChemical)
    Reaction(inputs, output)
}

class Reactions(reactionsList: List<Reaction>) {
    val reactionMap: Map<Chemical, Reaction> = reactionsList.map { it.output.chemical to it }.toMap()

    fun oreNeeded(quantity: Int, name: String) = oreNeeded(ChemicalQuantity(quantity, Chemical.create(name)))
    fun oreNeeded(outputChemicalQuantity: ChemicalQuantity): Int {
        var oreQuantity = 0
        var needed = listOf(outputChemicalQuantity)
        val inventory = Inventory()
        while (needed.isNotEmpty()) {
            println("oreQuanitity=$oreQuantity")
            println("needed=$needed")
            println("inventory=$inventory")
            oreQuantity += needed.filter { it.chemical == ORE }.map { it.quantity }.sum()
            needed = neededInputs(needed.filter { it.chemical != ORE }, inventory)
        }
        return oreQuantity
    }
    fun neededInputs(outputs: List<ChemicalQuantity>, inventory: Inventory): List<ChemicalQuantity> {
        return outputs.flatMap {ouput ->
            val reaction = reactionMap[ouput.chemical] ?: error("No reaction found to produce ${ouput.quantity} of ${ouput.chemical.name}")
            val (neededChemicalQuanitities, surplus) = reaction.neededInput(ouput.quantity)
            if (surplus > 0) inventory.add(ouput.chemical, surplus)
            neededChemicalQuanitities.mapNotNull { neededChemicalQuantity ->
                val inventoryChemicalQuantity = inventory[neededChemicalQuantity.chemical]
                if (inventoryChemicalQuantity != null) {
                    // Take it from inventory
                    if (inventoryChemicalQuantity.quantity >= neededChemicalQuantity.quantity) {
                        inventoryChemicalQuantity.quantity -= neededChemicalQuantity.quantity
                        null // Need not to produce anything
                    } else {
                        val reducedQuantity = ChemicalQuantity(neededChemicalQuantity.quantity - inventoryChemicalQuantity.quantity, neededChemicalQuantity.chemical)
                        inventoryChemicalQuantity.quantity = 0
                        reducedQuantity
                    }
                } else neededChemicalQuantity
            }
        }
    }
}
