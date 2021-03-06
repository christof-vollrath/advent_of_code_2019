import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.data_driven.data
import org.jetbrains.spek.data_driven.on as onData

/*
--- Day 4: Secure Container ---

You arrive at the Venus fuel depot only to discover it's protected by a password.
The Elves had written the password on a sticky note, but someone threw it out.

However, they do remember a few key facts about the password:

It is a six-digit number.
The value is within the range given in your puzzle input.
Two adjacent digits are the same (like 22 in 122345).
Going from left to right, the digits never decrease; they only ever increase or stay the same (like 111123 or 135679).
Other than the range rule, the following are true:

111111 meets these criteria (double 11, never decreases).
223450 does not meet these criteria (decreasing pair of digits 50).
123789 does not meet these criteria (no double).
How many different passwords within the range given in your puzzle input meet these criteria?

Your puzzle input is 264793-803935.

--- Part Two ---

An Elf just remembered one more important detail:
the two adjacent matching digits are not part of a larger group of matching digits.

Given this additional criterion, but still ignoring the range rule, the following are now true:

112233 meets these criteria because the digits never decrease and all repeated digits are exactly two digits long.
123444 no longer meets the criteria (the repeated 44 is part of a larger group of 444).
111122 meets the criteria (even though 1 is repeated more than twice, it still contains a double 22).
How many different passwords within the range given in your puzzle input meet all of the criteria?

 */

fun fallowsPasswordRules(password: Int): Boolean {
    val chars = password.toString().toList()
    return sixDigits(chars) && containsTwins(chars) && neverDecreasingDigits(chars)
}

fun fallowsExtendedPasswordRules(password: Int): Boolean {
    val chars = password.toString().toList()
    return sixDigits(chars) && containsTwins(chars) && neverDecreasingDigits(chars) && containsExactTwin(chars)
}

fun containsExactTwin(chars: List<Char>): Boolean {
    var sameCharCount = 0
    var recentChar: Char? = null
    chars.forEach { c ->
        if (c == recentChar) {
            sameCharCount++
        } else {
            if (sameCharCount == 1) // found exact twin fallowd by another char
                return true
            sameCharCount = 0
        }
        recentChar = c
    }
    return sameCharCount == 1 // eventually exact twin at the end
}

fun neverDecreasingDigits(chars: List<Char>): Boolean {
    chars.drop(1).fold(chars.first()) { prev, current ->
        if (prev > current) return false
        current
    }
    return true
}

fun containsTwins(chars: List<Char>): Boolean {
    chars.drop(1).fold(chars.first()) { prev, current ->
        if (prev == current) return true
        current
    }
    return false
}

fun sixDigits(chars: List<Char>) = chars.size == 6

fun candidates(): Sequence<Int> {
    return sequence {
        for (i in 264793..803935) {
            yield(i)
        }
    }
}

class Day04Spec : Spek({

    describe("part 1") {
        describe("check password rules") {
            val testData = arrayOf(
                data(111111, true),
                data(223450, false),
                data(123789, false)
            )
            onData("fallows password rules %s", with = *testData) { password, expected ->
                it("should return $expected") {
                    fallowsPasswordRules(password) `should equal` expected
                }
            }
        }
        describe("count passwords") {
            val count = candidates().filter {fallowsPasswordRules(it) }.count()
            count `should equal` 966
        }
    }
    describe("part 2") {
        describe("check extended password rules") {
            val testData = arrayOf(
                data(112233, true),
                data(123444, false),
                data(111122, true)
            )
            onData("fallows password rules %s", with = *testData) { password, expected ->
                it("should return $expected") {
                    fallowsExtendedPasswordRules(password) `should equal` expected
                }
            }
        }
        describe("count passwords") {
            val count = candidates().filter {fallowsExtendedPasswordRules(it) }.count()
            count `should equal` 628
        }
    }
})
