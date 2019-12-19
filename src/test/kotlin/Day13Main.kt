import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking

var score = 0
val screen = GameScreen()
var terminated = false

fun main(args: Array<String>) {
    val intCodesString = readResource("day13Input.txt")!!
    val intCodes = parseIntCodes09(intCodesString)
    val patchedIntCodes = listOf(2L) + intCodes.drop(1) // Patch so that no coins must be inserted
    println("To move left press [a] [RETURN], for right [f] [RETURN], to finish [x]")
    println("Now press [RETURN] to play")
    System.`in`.read()
    runBlocking {
        val inputChannel = Channel<Long>()
        val outputChannel = Channel<Long>()
        async {
            patchedIntCodes.executeExtendedIntCodes09Async(inputChannel, outputChannel)
            terminated = true
        }
        async {
            while(!terminated) {
                val x = outputChannel.receive().toInt()
                val y = outputChannel.receive().toInt()
                val code = outputChannel.receive().toInt()
                if (x == -1 && y == 0) {
                    score = code
                } else screen.draw(Coord2(x, y), code)
            }
        }
        while(true) {
            println()
            println("Score $score")
            println()
            println(screen)
            println()

            val c = System.`in`.read()
            if (c.toChar() == 'x') break
            when(c.toChar()) {
                'a' -> inputChannel.send(-1L)
                ' ' -> inputChannel.send(0L)
                'f' -> inputChannel.send(1L)
                else -> {}
            }

        }
    }
}

suspend fun GameScreen.printScreen(outputChannel: Channel<Long>) {
    while(true) {
        val x = outputChannel.receive().toInt()
        val y = outputChannel.receive().toInt()
        val code = outputChannel.receive().toInt()
        if (x == -1 && y == 0) {
            println()
            println("Score $code")
            println()
            println(this)
            println()
        } else draw(Coord2(x, y), code)
    }
}