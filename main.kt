import java.util.PriorityQueue
import java.util.HashMap

fun searchClosest(
    openSet: PriorityQueue<FifteenPuzzle>,
    closeSet: HashMap<ULong, FifteenPuzzle>,
    maxdepth: Int,
    target: Array<FifteenPuzzle>,
    targetSet: HashMap<ULong, FifteenPuzzle>
): Array<FifteenPuzzle>? {
    val current = openSet.poll()
    val children = current.getChildren()

    for (puzzle in children) {
		val target_puzz = targetSet[puzzle.board]
        if (target_puzz != null) {
            val ResultArray = arrayOf(puzzle, target_puzz)

            println("Found Solution in search depth: ${puzzle.depth}")
            println("Total: ${puzzle.depth + target_puzz.depth} steps")
            return ResultArray
        }
    }

    val old = closeSet[current.board]
    if (old == null) {
        closeSet.put(current.board, current)
    } else {
        if (current.depth < old.depth) {
            closeSet.put(current.board, current)
        } else {
            return null
        }
    }
    var maxManhattanValue = maxdepth - current.depth - target[0].depth
    var childrenNumber = 0
    for (i in 0 until children.size) {
        for (puzz in target) {
            val a = children[i].getManhattan(puzz)
            if (a < children[i].manhattan) {
                children[i].manhattan = a
            }
        }
        if (children[i].manhattan < maxManhattanValue) {
            openSet.add(children[i])
            childrenNumber++
        }
    }

    return null
}

val comp = Comparator<FifteenPuzzle> { a, b ->
    (a.depth * 9 + a.manhattan * 9 + a.getParity()) - (b.depth * 9 + b.manhattan * 9 + b.getParity())
}

fun buildArray(
    start: FifteenPuzzle,
    end: FifteenPuzzle,
    openSet: PriorityQueue<FifteenPuzzle>,
    closeSet: HashMap<ULong, FifteenPuzzle>,
    depth: Int,
    sampleDeep: Int
): Array<FifteenPuzzle> {
    var sample: Array<FifteenPuzzle> = arrayOf(end)
    var currentLevel = PriorityQueue<FifteenPuzzle>(comp)
    currentLevel.add(start)
    var level = 1
    while (level <= depth) {
        val nextLevel = PriorityQueue<FifteenPuzzle>(comp)
        for (puzz in currentLevel) {
            val children = puzz.getChildren()
            for (i in 0 until children.size) {
                children[i].manhattan = children[i].getManhattan(end)
                if (!closeSet.containsKey(children[i].board)) {
                    nextLevel.add(children[i])
                }
            }
            closeSet.put(puzz.board, puzz)
        }
        currentLevel = nextLevel
        if (level == sampleDeep) {
            sample = currentLevel.toArray(sample)
        }
        level++
    }
    openSet.addAll(currentLevel)
    return sample
}

fun main(args: Array<String>) {
    /*val startBoard = arrayOf(
        intArrayOf(14, 10, 2, 1),
        intArrayOf(13, 9, 8, 11),
        intArrayOf(7, 3, 6, 12),
        intArrayOf(15, 5, 4, 0)
    )*/
    
    /*val startBoard = arrayOf(
        intArrayOf(14, 10, 2, 1),
        intArrayOf(13, 9, 8, 0),
        intArrayOf(7, 3, 6, 11),
        intArrayOf(15, 5, 4, 12)
    )*/
    
    val startBoard = arrayOf(
        intArrayOf(14, 13, 15, 7),
        intArrayOf(11, 12, 9, 5),
        intArrayOf(6, 0, 2, 1),
        intArrayOf(4, 8, 10, 3)
    )
    
    val start = FifteenPuzzle(startBoard)

    val endBoard = arrayOf(
        intArrayOf(0, 1, 2, 3),
        intArrayOf(4, 5, 6, 7),
        intArrayOf(8, 9, 10, 11),
        intArrayOf(12, 13, 14, 15)
    )
    val end = FifteenPuzzle(endBoard)
    start.manhattan = start.getManhattan(end)
    end.manhattan = end.getManhattan(start)
    println("start: \n$start")
    println("start parity: " + start.getParity())
    println("start ham: " + start.manhattan)
    val sysDate = System.currentTimeMillis()

    val openSet = PriorityQueue<FifteenPuzzle>(comp)
    val closeSet = HashMap<ULong, FifteenPuzzle>()
    buildArray(start, end, openSet, closeSet, start.manhattan / 4, 0)
    println("Build Array level ${openSet.peek().depth}, size: ${openSet.size}")

    val endOpenSet = PriorityQueue<FifteenPuzzle>(comp)
    val endCloseSet = HashMap<ULong, FifteenPuzzle>()
    val endArray = buildArray(
        end,
        start,
        endOpenSet,
        endCloseSet,
        start.manhattan / 4,
        Math.max(0, start.manhattan / 4 - 3)
    )
    println("Build Array level ${endOpenSet.peek().depth}, size: ${endOpenSet.size}")
    println("sample length   ${endArray.size}")


    var depth_Max: Byte = 0
    var ResultArray: Array<FifteenPuzzle>? = null
    
    while (ResultArray == null) {
		ResultArray = searchClosest(openSet, closeSet, 80, endArray, endCloseSet)
        val currentState = openSet.peek()
        if (currentState.depth > depth_Max) {
            print("max Search depth: ${currentState.depth}")
            println("\tclose set size: ${closeSet.size}\topen set size: ${openSet.size}")
            depth_Max = currentState.depth
        }
    }
    
    val timelaps = System.currentTimeMillis() - sysDate
    println(ResultArray[0].printSolutionRecursive(true))
    ResultArray[1].parent.printSolutionForward()
    println("Used time: ${timelaps * 0.001}s")
    println("Total Nodes: ${openSet.size + closeSet.size + endCloseSet.size + endOpenSet.size}")

}
