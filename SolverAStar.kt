
class SolverAStar {
    val start: FifteenPuzzle
    val target: FifteenPuzzle
    
    var openSet: PriorityQueueS<FifteenPuzzle>
    var closeSet: HashSetL<FifteenPuzzle>
    var targetSet: HashSetL<FifteenPuzzle>
    var targetArray: List<FifteenPuzzle>
    
    constructor(
        start: FifteenPuzzle,
        target: FifteenPuzzle
    ) {
        this.start = start
        this.target = target

        val targetOpenSet = PriorityQueueS<FifteenPuzzle>()
        targetSet = HashSetL<FifteenPuzzle>()
        targetArray = buildArray(target, start, targetOpenSet, targetSet,
                start.heuristics / 4, Math.max(0, start.heuristics /4 - 3))
        
        openSet = PriorityQueueS<FifteenPuzzle>()
        openSet.enqueue(start)
        closeSet = HashSetL<FifteenPuzzle>()
        buildArray(start, target, openSet, closeSet, start.heuristics / 2, 0)
    }

    fun searchClosest(maxdepth: Int): Array<FifteenPuzzle>? {
        val current = openSet.dequeue() ?: throw NoSuchElementException("No solution found!")
        var children = current.getChildren()
        
        for (puzzle in children) {
            val target_puzz = targetSet.findObject(puzzle)

            if (target_puzz != null) {
                return arrayOf(puzzle, target_puzz)
            }
        }

        val old = closeSet.findObject(current)
        if (old == null || current.depth < old.depth) {
            closeSet.addOrUpdate(current)
        } else {
            return null
        }
        
        val maxManhattanValue = maxdepth - current.depth - targetArray[0].depth

        for (child in children) {
            for (puzz in targetArray) {
                val a = child.getManhattan(puzz)
                
                if (a < child.heuristics) {
                    child.heuristics = a
                }
            }

            if (child.heuristics <= maxManhattanValue) {
                openSet.enqueue(child)
            }
        }

        return null
    }

    fun buildArray(
        start: FifteenPuzzle,
        end: FifteenPuzzle,
        openSet: PriorityQueueS<FifteenPuzzle>,
        closeSet: HashSetL<FifteenPuzzle>,
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

                for (child in children) {
                    child.heuristics = child.getManhattan(end)
                    
                    if (!closeSet.contains(child)) {
                        nextLevel.enqueue(child)
                    }
                }

                closeSet.add(puzz)
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
