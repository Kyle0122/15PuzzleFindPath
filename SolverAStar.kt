
class SolverAStar {
    
    val start: FifteenPuzzle
    val target: FifteenPuzzle
    
    var openSet: PriorityQueueS<FifteenPuzzle>
    var closeSet: HashMap<ULong, FifteenPuzzle>
    var targetSet: HashMap<ULong, FifteenPuzzle>
    var targetArray: List<FifteenPuzzle>
    
    constructor(
        start: FifteenPuzzle,
        target: FifteenPuzzle
    ) {
        this.start = start
        this.target = target

        val targetOpenSet = PriorityQueueS<FifteenPuzzle>()
        targetSet = HashMap<ULong, FifteenPuzzle>()
        targetArray = buildArray(target, start, targetOpenSet, targetSet,
                start.heuristics / 4, Math.max(0, start.heuristics /4 - 3))
        
        openSet = PriorityQueueS<FifteenPuzzle>()
        closeSet = HashMap<ULong, FifteenPuzzle>()
        buildArray(start, target, openSet, closeSet, start.heuristics / 4, 0)
    }

    fun searchClosest(maxdepth: Int): Array<FifteenPuzzle>? {
        val current = openSet.dequeue()
        if(current == null) {
            println("No solution found!")
            return null
        }
        
        var children: Array<FifteenPuzzle>? = current.getChildrenGen2()
        //var children: Array<FifteenPuzzle>? = current.getChildren()
        if (children == null || current.heuristics <=2) {
            children = current.getChildren()
        }
        
        for (puzzle in children) {
            val target_puzz = targetSet[puzzle.board]
            if (target_puzz != null) {
                val ResultArray = arrayOf(puzzle, target_puzz)
                return ResultArray
            }
        }

        val old = closeSet[current.board]
        if (old == null) {
            closeSet.put(current.board, current)
        } else if (current.depth < old.depth) {
            closeSet.put(current.board, current)
        } else {
            return null
        }
        
        var maxManhattanValue = maxdepth - current.depth - targetArray[0].depth
        var childrenNumber = 0
        for (i in 0 until children.size) {
            for (puzz in targetArray) {
                val a = children[i].getManhattan(puzz)
                if (a < children[i].heuristics) {
                    children[i].heuristics = a
                }
            }
            if (children[i].heuristics < maxManhattanValue) {
                openSet.enqueue(children[i])
                childrenNumber++
            }
        }

        return null
    }

    fun buildArray(
        start: FifteenPuzzle,
        end: FifteenPuzzle,
        openSet: PriorityQueueS<FifteenPuzzle>,
        closeSet: HashMap<ULong, FifteenPuzzle>,
        depth: Int,
        sampleDeep: Int
    ): List<FifteenPuzzle> {
        var sample: List<FifteenPuzzle> = listOf(end)
        var currentLevel = PriorityQueueS<FifteenPuzzle>()
        currentLevel.enqueue(start)
        var level = 1
        while (level <= depth) {
            val nextLevel = PriorityQueueS<FifteenPuzzle>()
            for (puzz in currentLevel) {
                val children = puzz.getChildren()
                for (i in 0 until children.size) {
                    children[i].heuristics = children[i].getManhattan(end)
                    if (!closeSet.containsKey(children[i].board)) {
                        nextLevel.enqueue(children[i])
                    }
                }
                closeSet.put(puzz.board, puzz)
            }
            currentLevel = nextLevel
            if (level == sampleDeep) {
                sample = currentLevel.toList()
            }
            level++
        }
        openSet.addAll(currentLevel)
        return sample
    }
    
}
