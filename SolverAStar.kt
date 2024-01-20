
class SolverAStar {
    val start: FifteenPuzzle
    val target: FifteenPuzzle
    val maxDepth: Int
    
    var openSet: PriorityQueueS<FifteenPuzzle>
    var closeSet: HashSetL<FifteenPuzzle>
    var targetSet: HashSetL<FifteenPuzzle>
    var targetArray: List<FifteenPuzzle>
    
    constructor(
        start: FifteenPuzzle,
        target: FifteenPuzzle,
        maxDepth: Int
    ) {
        this.start = start
        this.target = target
        this.maxDepth = maxDepth

        val targetOpenSet = PriorityQueueS<FifteenPuzzle>()
        targetSet = HashSetL<FifteenPuzzle>()
        targetArray = buildArray(target, start, targetOpenSet, targetSet,
                start.heuristics / 4, Math.max(0, start.heuristics /4 - 3))

        this.openSet = PriorityQueueS<FifteenPuzzle>()
        this.openSet.enqueue(start)
        
        this.closeSet = HashSetL<FifteenPuzzle>()
    }

    fun searchClosest(): Array<FifteenPuzzle>? {
        val current = openSet.dequeue() ?: throw NoSuchElementException("No solution found!")
        var children = current.getChildren()

        // check if any child node is in the target set
        for (puzzle in children) {
            val target_puzz = targetSet.findObject(puzzle)

            if (target_puzz != null) {
                return arrayOf(puzzle, target_puzz)
            }
        }

        // find in the closeSet if there are already a current object.
        // only find in depth lower than current depth
        /*var old: FifteenPuzzle? = null
        for (depth in 0 until current.depth) {
            old = closeSet[depth].findObject(current)
            if (old != null) { break }
        }*/
        val old = closeSet.findObject(current)
        
        if (old == null || current.depth < old.depth) {
            closeSet.addOrUpdate(current)
        } else {
            return null
        }

        val maxManhattanValue = maxDepth - current.depth - targetArray[0].depth

        if(current.depth <= start.heuristics / 2) {

            for (child in children) {
                val a = child.getManhattan(target)
                child.heuristics = a.toByte()
                openSet.enqueue(child)
            }
            
        } else {
            
            for (child in children) {
                for (puzz in targetArray) {
                    val a = child.getManhattan(puzz)
                    
                    if (a < child.heuristics) {
                        child.heuristics = a.toByte()
                    }
                }

                if (child.heuristics <= maxManhattanValue) {
                    openSet.enqueue(child)
                }
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
                    child.heuristics = child.getManhattan(end).toByte()
                    
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
