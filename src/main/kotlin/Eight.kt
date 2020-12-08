fun main() {
    println(a())
    println(b())
}

private fun a() = calculateAccumulatorForEndCondition(EndCondition.DUPLICATE_INSTRUCTION, instructions)!!

private fun b() =
    flipInstructionVariants.firstNotNull { calculateAccumulatorForEndCondition(EndCondition.NO_MORE_INSTRUCTIONS, it) }

private val instructions: List<Instruction> by lazy { input.lines().map { Instruction.fromRaw(it) } }
private val flipInstructionVariants = generateFlipVariants()

enum class EndCondition {
    DUPLICATE_INSTRUCTION, NO_MORE_INSTRUCTIONS;
}

private fun calculateAccumulatorForEndCondition(endCondition: EndCondition, instructions: List<Instruction>): Int? {
    val seenInstructionIndicies = mutableListOf(0)
    var state = State()
    while (true) {
        // Try and get the next instruction to execute.
        val instruction: Instruction
        try {
            instruction = instructions[state.instructionIndex]
        } catch (e: IndexOutOfBoundsException) {
            // Program has terminated.
            return if (endCondition == EndCondition.NO_MORE_INSTRUCTIONS) state.accumulator else null
        }

        // Execute the instruction.
        state = instruction.perform(state)

        // Check if we've already seen the instruction already.
        if (state.instructionIndex in seenInstructionIndicies) {
            return if (endCondition == EndCondition.DUPLICATE_INSTRUCTION) state.accumulator else null
        } else {
            seenInstructionIndicies.add(state.instructionIndex)
        }
    }
}

private fun generateFlipVariants(): List<List<Instruction>> {
    // Work out indices of instructions that can be flipped.
    val indices = instructions.mapIndexedNotNull { index, instruction ->
        if (instruction !is Instruction.Acc) {
            index
        } else {
            null
        }
    }

    return indices.map { index ->
        instructions.toMutableList().also { copy -> copy[index] = copy[index].flip() }
    }
}

private data class State(val instructionIndex: Int = 0, val accumulator: Int = 0)

private sealed class Instruction(open val value: Int) {

    abstract fun perform(state: State): State
    abstract fun flip(): Instruction

    data class NoOp(override val value: Int) : Instruction(value) {
        override fun perform(state: State) = state.copy(instructionIndex = state.instructionIndex+1)

        override fun flip(): Instruction = Jump(value)
    }

    data class Acc(override val value: Int) : Instruction(value) {
        override fun perform(state: State) =
                state.copy(instructionIndex = state.instructionIndex+1, accumulator = state.accumulator + value)

        override fun flip(): Instruction = error("Can't flip Acc instruction")
    }

    data class Jump(override val value: Int) : Instruction(value) {
        override fun perform(state: State) = state.copy(instructionIndex = state.instructionIndex+value)

        override fun flip(): Instruction = NoOp(value)
    }

    companion object {
        fun fromRaw(s: String): Instruction {
            val parts = s.split(" ")
            val instructionCode = parts[0]
            val value = parts[1].toInt()
            return when (instructionCode) {
                "nop" -> NoOp(value)
                "jmp" -> Jump(value)
                "acc" -> Acc(value)
                else -> error("Invalid instruction: $instructionCode")
            }
        }
    }
}

private const val input = """acc -1
jmp +1
jmp +117
jmp +70
jmp +473
acc +11
acc +48
acc -10
acc -2
jmp +253
jmp +597
acc -19
acc -5
jmp +328
acc +41
acc +23
acc +35
acc +24
jmp +465
acc +20
nop +338
acc +17
jmp +1
jmp +549
acc +15
jmp +131
acc +35
jmp +63
acc +29
jmp +81
acc +30
acc +16
jmp +75
jmp +28
acc +20
acc -13
jmp +1
jmp +79
acc +16
acc -5
jmp -36
nop +361
acc +33
acc +46
jmp +270
acc +12
acc +13
acc +2
jmp +254
acc +37
acc -10
acc +2
acc +27
jmp +535
acc +18
acc +32
jmp +252
acc -2
jmp +1
acc -6
jmp +565
acc +40
jmp +200
acc -15
nop +419
nop +265
jmp +340
acc +46
acc +23
nop +527
acc +32
jmp +202
jmp +47
acc +12
jmp +493
jmp -71
nop +491
acc +18
jmp +38
jmp +5
nop +311
jmp -43
nop +197
jmp +275
acc +40
acc +27
jmp +440
acc +7
acc +44
jmp +131
acc +2
jmp +283
jmp +415
acc -12
acc -4
acc -8
acc +40
jmp -30
acc +43
nop -15
acc -2
jmp +74
jmp +421
jmp +1
acc +16
acc +23
jmp -96
acc -9
jmp +181
jmp +1
jmp +482
acc -5
acc +15
acc +8
acc +12
jmp +323
acc -16
jmp +417
jmp +483
acc +0
acc +31
acc +11
acc -9
jmp +46
acc +50
acc +41
jmp +153
acc +12
acc +23
acc -6
acc +38
jmp +191
acc +28
nop +75
jmp +462
acc +40
nop -52
jmp +404
acc +16
nop +273
jmp +105
acc +8
acc -4
jmp -59
acc +47
acc +0
jmp +1
jmp -45
acc +15
acc -14
nop +373
acc +25
jmp -50
acc +43
nop +154
jmp +368
acc +32
acc -2
acc +28
jmp +273
acc -9
jmp +1
nop -69
jmp +82
acc +20
jmp +1
jmp +282
acc +14
jmp +296
nop +61
jmp +1
acc +9
jmp +202
acc -2
jmp +441
acc +23
acc +16
acc +12
nop -117
jmp +247
acc +19
acc -10
acc -15
acc +39
jmp +213
acc -18
acc +29
jmp -80
acc +43
acc +10
acc +47
jmp +98
acc +49
acc +17
acc +24
jmp +430
acc -11
acc +36
acc -5
acc +23
jmp +108
acc -14
jmp +10
acc +31
acc +1
acc +3
acc +5
jmp +184
acc +48
jmp +70
acc +13
jmp +130
acc +26
acc +29
acc +25
jmp +165
nop +112
acc +11
acc +6
jmp +103
jmp +1
acc -2
acc +31
acc +0
jmp +123
acc -12
acc -5
acc -11
nop -161
jmp +112
acc +10
acc +19
jmp +132
jmp -157
nop +80
jmp +101
acc +12
acc +39
acc -11
jmp -75
nop -10
jmp +179
acc +37
acc -14
jmp +204
nop +380
acc +1
acc +43
acc +18
jmp -41
acc -8
acc +32
acc +50
nop -45
jmp +233
nop -182
acc +16
acc +16
jmp -131
acc +17
acc +21
jmp +92
jmp +179
acc -14
jmp +296
acc +44
acc -5
nop +12
jmp +134
acc +39
nop -30
acc +31
jmp -131
acc +7
acc +46
acc +14
nop +82
jmp -187
jmp +242
nop +178
acc -17
acc -8
nop +267
jmp +101
nop -50
jmp +1
acc -19
acc +10
jmp -172
acc +20
acc +39
acc +46
acc +41
jmp +340
jmp -266
acc +46
nop +45
acc +15
acc +16
jmp +158
acc +21
jmp +300
acc +35
jmp +190
nop -70
acc +49
acc +31
jmp +184
jmp +275
acc +8
nop -76
acc +30
acc +9
jmp -5
acc +28
acc +19
jmp +37
acc +5
acc +5
nop -200
nop +281
jmp -202
acc -4
acc -7
acc +0
jmp -252
acc +7
jmp +298
jmp +279
jmp +7
acc +30
acc -6
acc -19
jmp +201
acc +33
jmp +12
acc +47
acc +20
acc +43
jmp +268
jmp +124
acc +17
acc +0
acc +21
acc +34
jmp +227
jmp -313
acc -6
acc +43
jmp +1
jmp -22
nop +162
jmp +161
jmp -339
acc -12
nop +109
acc +21
jmp -149
nop -128
nop +199
jmp -327
nop +165
acc +19
jmp -302
acc +26
nop +67
jmp -52
nop -353
acc -4
jmp -200
jmp +245
acc +38
acc +3
jmp -232
acc +36
jmp +1
acc +20
jmp -157
acc -14
jmp -114
jmp -66
nop -59
acc +44
jmp -42
acc +40
nop -90
jmp -306
acc -7
nop -24
acc -17
jmp -226
acc +8
acc +39
jmp +106
jmp +1
acc +27
jmp -60
acc -10
jmp +1
jmp -366
acc +29
jmp -325
jmp -28
acc +34
acc +35
jmp -3
acc +30
acc -9
acc +33
acc -5
jmp +62
jmp +147
acc +43
acc +37
nop +120
acc +21
jmp +172
acc +22
acc +36
jmp -105
nop +37
acc +40
nop +26
jmp -288
acc +6
jmp +98
nop -278
acc +17
acc -11
jmp -99
nop -416
jmp -364
jmp +145
acc +23
acc -18
acc -13
acc +8
jmp +112
acc +34
jmp -411
jmp -255
acc +23
acc +1
acc +30
jmp +152
nop -225
acc +3
jmp -217
acc +37
acc -10
acc -1
nop -81
jmp +12
acc -3
acc +41
acc +12
jmp +151
acc +20
acc +10
jmp -229
acc +0
acc +12
acc +0
jmp +24
acc +35
acc +21
acc -13
jmp +40
acc +48
acc -9
acc +4
jmp -30
acc +1
nop -440
acc +36
jmp -241
jmp +15
acc +5
nop +147
acc +37
acc +12
jmp -457
jmp +85
jmp -308
jmp +1
acc +33
jmp -221
jmp +114
acc +29
nop -142
jmp -42
jmp -415
jmp -328
jmp -345
acc +23
acc +3
acc +13
jmp +1
jmp -233
acc +9
acc -9
jmp +59
nop -15
acc +17
acc +32
acc +39
jmp -251
acc +24
acc +26
acc +0
acc +27
jmp -319
jmp +46
acc +35
nop -134
acc +37
jmp -104
acc +5
acc +21
jmp +48
acc -2
jmp -83
jmp -415
acc +0
jmp -154
jmp -285
acc +5
acc -2
acc +27
jmp -519
acc -16
acc +31
acc +20
jmp -34
acc +3
acc +20
jmp -344
acc +0
acc -19
acc -5
jmp -310
acc +17
acc +42
acc +11
jmp -490
acc +47
acc +44
acc -1
jmp +12
acc +34
nop -512
acc +33
jmp -61
acc +43
acc -2
acc -16
acc +10
jmp -327
jmp -45
jmp -147
acc +33
jmp -203
acc +17
acc -8
nop +19
acc +20
jmp -164
acc +11
acc +18
acc +50
jmp -330
jmp -326
acc +43
acc +42
acc +22
jmp -310
jmp -248
nop -219
acc -7
acc +21
jmp -183
acc +0
jmp +6
acc +13
jmp -339
acc -17
acc -7
jmp -471
jmp -387
acc -4
acc +50
jmp -250
jmp -407
nop -1
jmp +1
jmp -157
acc +45
nop -456
acc +28
jmp -41
jmp -370
jmp -482
acc -17
acc +11
acc +18
acc +15
jmp -29
acc +31
acc +48
jmp -564
acc +47
jmp +1
acc +8
acc +27
jmp -26
acc +23
acc +0
acc +12
acc +17
jmp -364
acc -4
acc +50
acc -10
nop -393
jmp -531
nop -118
nop -256
nop -551
acc +36
jmp -536
acc -6
acc +44
jmp -530
acc -5
acc +16
acc +19
jmp +1
jmp +1"""