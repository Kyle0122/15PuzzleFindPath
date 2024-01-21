import java.io.File

fun solveSinglePuzzle(solver: SolverAStar) {
    // record starting time
    val sysDate = System.currentTimeMillis()

    var depth_Max: Byte = 0
    var ResultArray: Array<FifteenPuzzle>? = null
    
    while (ResultArray == null) {
		ResultArray = solver.searchClosest()
        val currentState = solver.openSet.peek()
        if (currentState != null && currentState.depth > depth_Max) {
            print("max Search depth: ${currentState.depth}")
            println("\tclose set size: ${solver.closeSet.size}\topen set size: ${solver.openSet.size()}")
            depth_Max = currentState.depth
        }
    }
    println("\nFound Solution in search depth: ${ResultArray[0].depth}")
    println("Total: ${ResultArray[0].depth + ResultArray[1].depth} steps")
    
    val timelaps = System.currentTimeMillis() - sysDate
    println(ResultArray[0].printSolutionRecursive(ResultArray[1]))
    println("Used time: ${timelaps * 0.001}s")
    println("Total Nodes: ${solver.openSet.size() + solver.closeSet.size + solver.targetSet.size}")
}

fun main(args: Array<String>) {
    
    // Check if the correct number of arguments is provided
    if (args.size != 2) {
        println("Usage: java -jar -Xms8g mainkt.jar <input_file> <target_file>")
        println("Usage: java -jar -Xms8g mainkt.jar batch <input_file>")
        return
    }
    
    if (args[0] == "batch" && args.size == 2) {
        // Run batch processing
        val batchInputFile = File(args[1])
        val batchInputLines = batchInputFile.useLines { it.toList() }
            .filterNot { it.startsWith("#") }

        var isTargetLine = true
        var end: FifteenPuzzle? = null

        for (line in batchInputLines) {
            val numbers = line.trim().split("\\s+".toRegex())
                .filterNot { it.isBlank() }
                .map { it.toInt() }

            if (isTargetLine && numbers.size == 16) {
                // This line is the target
                val targetBoard = numbers.toIntArray()
                end = FifteenPuzzle(targetBoard)
                isTargetLine = false
            } else if (end != null && numbers.size == 17) {
                // This line is a test case
                val startBoard = numbers.take(16).toIntArray()
                val start = FifteenPuzzle(startBoard)
                start.heuristics = start.getManhattan(end).toByte()

                // Add your solving logic here using 'start' and 'end'
                val solver = SolverAStar(start, end, 72)
                solveSinglePuzzle(solver)
            }
        }
    } else {
        // Read the input file
        val inputFile = File(args[0])
        val inputLines = inputFile.useLines { it.toList() }

        // Initialize the starting puzzle board
        val startBoard = inputLines
            .filterNot { it.startsWith("#") || it.isBlank() }
            .map { line -> line.trim().split("\\s+".toRegex()).map { it.toInt() }.toIntArray() }
            .toTypedArray()
        val start = FifteenPuzzle(startBoard)
        
        // Read the target file
        val targetFile = File(args[1])
        val targetLines = targetFile.useLines { it.toList() }
        
        // Initialize the target puzzle board
        val endBoard = targetLines
            .filterNot { it.startsWith("#") || it.isBlank() }
            .map { line -> line.trim().split("\\s+".toRegex()).map { it.toInt() }.toIntArray() }
            .toTypedArray()
        val end = FifteenPuzzle(endBoard)
        
        start.heuristics = start.getManhattan(end).toByte()
        println("start: \n${start}")
        println("start manhattan: " + start.heuristics)
        println("start parity: " + start.getParity())
        println("end parity: " + end.getParity())
        
        // parity check
        val parityDifference = start.getParity() - end.getParity()
        if (parityDifference % 2 == 0) {
            println("parity check passed.")
        } else {
            println("parity check shows unsolvable, exiting.")
            return
        }
        
        val solver = SolverAStar(start, end, 72)
        println("sample length   ${solver.targetArray.size}")
        solveSinglePuzzle(solver)
    }
    
}
