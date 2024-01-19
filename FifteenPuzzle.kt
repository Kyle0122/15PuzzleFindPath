
class FifteenPuzzle : Comparable<FifteenPuzzle> {
	companion object {
        const val BOARDLENGTH: Int = 4
    }
    
    var board: ULong = 0UL
    var parent: FifteenPuzzle = this
    var x: Byte = 1
    var y: Byte = 1
    var heuristics: Byte = 127.toByte()
    var depth: Byte = 0

    constructor(board: Array<IntArray>) {
        for (i in 0 until BOARDLENGTH) {
            for (j in 0 until BOARDLENGTH) {
                if (board[i][j] == 0 || board[i][j] == 16) {
                    x = i.toByte()
                    y = j.toByte()
                } else {
                    setval(i, j, board[i][j].toByte())
                }
            }
        }
    }

    constructor(board: Array<IntArray>, parent: FifteenPuzzle) : this(board) {
        this.parent = parent
    }

    constructor(board: Array<IntArray>, depth: Int) : this(board) {
        this.depth = depth.toByte()
    }
    
    // Copy constructor
    constructor(original: FifteenPuzzle) {
        this.board = original.board
        this.parent = original
        this.x = original.x
        this.y = original.y
        this.heuristics = 127.toByte()
        this.depth = original.depth
    }

    fun setval(x: Int, y: Int, value: Byte) {
        val shift = 60 - (4 * x + y) * 4
        // Perform the left shift
        val shiftedValue: ULong = (0xFUL and value.toULong()) shl shift
        // Clear the bits at the specified position in the board
        val clearedBoard: ULong = board and (0xFFFFFFFFFFFFFFFFUL xor (0xFUL shl shift))
        board = clearedBoard or shiftedValue
    }

    fun getval(x: Int, y: Int): Int {
        val shift = 60 - (4 * x + y) * 4
        return ((board shr shift) and 0xfUL).toInt()
    }
    
    fun getx(): Int = this.x.toInt()
	
    fun gety(): Int = this.y.toInt()

    fun getManhattan(target: FifteenPuzzle): Byte {
        var manhattan = 0
        val targetBoardX = IntArray(16)
        val targetBoardY = IntArray(16)
        for (i in 0 until BOARDLENGTH) {
            for (j in 0 until BOARDLENGTH) {
                val index = target.getval(i, j)
                targetBoardX[index] = i
                targetBoardY[index] = j
            }
        }
        for (i in 0 until BOARDLENGTH) {
            for (j in 0 until BOARDLENGTH) {
                val index = getval(i, j)
                if (index == 0) continue
                val xx = targetBoardX[index]
                val yy = targetBoardY[index]
                manhattan += Math.abs(yy - j) + Math.abs(xx - i)
            }
        }
        return manhattan.toByte()
    }

    fun getParity(): Byte {
        var parity: Byte = 0
        // valueN stores the position(0 to 15) of the tile
        val valueN = IntArray(16)
        // boardN stores the tile in each position
        val boardN = IntArray(16)
        for (i in 0 until BOARDLENGTH) {
            for (j in 0 until BOARDLENGTH) {
                boardN[4 * i + j] = getval(i, j)
                valueN[getval(i, j)] = 4 * i + j
                if (getval(i, j) == 0) {
                    parity = (i + j).toByte()
                }
            }
        }
        for (i in 0 until 16) {
            if (boardN[i] != i) {
                boardN[valueN[i]] = boardN[i]
                valueN[boardN[i]] = valueN[i]
                boardN[i] = i
                valueN[i] = i
                parity++
            }
        }
        return parity
    }

    private fun moveZero(dx: Int, dy: Int) {
        var xNew = getx() + dx
        var yNew = gety() + dy

        setval(x.toInt(), y.toInt(), getval(xNew, yNew).toByte())
        setval(xNew, yNew, 0)

        x = xNew.toByte()
        y = yNew.toByte()
    }
    
    private fun shiftZeroRight(pos: Int, deltaPos: Int): ULong {

        // Calculate the bit mask for the shifted position
        val shiftedBitMask: ULong = 0xfUL shl (60 - pos * 4 - deltaPos * 4)

        // Shift the 0 bit to the right by deltaPos positions
        val shiftedValue: ULong = board or ((board and shiftedBitMask) shl deltaPos * 4) and shiftedBitMask.inv()
        return shiftedValue
    }

    private fun shiftZeroLeft(pos: Int, deltaPos: Int): ULong {

        // Calculate the bit mask for the shifted position
        val shiftedBitMask: ULong = 0xfUL shl (60 - pos * 4 + deltaPos * 4)

        // Shift the 0 bit to the left by deltaPos positions
        val shiftedValue: ULong = board or ((board and shiftedBitMask) shr deltaPos * 4) and shiftedBitMask.inv()
        return shiftedValue
    }

    fun getChildren(): List<FifteenPuzzle> {
        val children: Array<FifteenPuzzle> = 
                arrayOf(FifteenPuzzle(this), FifteenPuzzle(this), FifteenPuzzle(this), FifteenPuzzle(this))
        var index = 0
        
        if (getx() != 0 && parent.getx() != x - 1) {
            children[index].moveZero(-1, 0)
            children[index].depth = (this.depth + 1).toByte()
            index++
        }
        if (getx() != BOARDLENGTH - 1 && parent.getx() != x + 1) {
            children[index].moveZero(1, 0)
            children[index].depth = (this.depth + 1).toByte()
            index++
        }
        if (gety() != 0 && parent.gety() != y - 1) {
            children[index].moveZero(0, -1)
            children[index].depth = (this.depth + 1).toByte()
            index++
        }
        if (gety() != BOARDLENGTH - 1 && parent.gety() != y + 1) {
            children[index].moveZero(0, 1)
            children[index].depth = (this.depth + 1).toByte()
            index++
        }
        return children.slice(0 until index)
    }
    
    fun getChildrenGen2(): Array<FifteenPuzzle>? {
        val xy = (getx() shl 4) or gety()
        val depth = this.depth.toByte()
        when (xy) {
            0x11, 0x12, 0x21, 0x22 -> return null
            0x00 -> {
                if (this.parent.getx() - this.getx() == 1) {
                    val childGen1 = FifteenPuzzle(this)
                    childGen1.moveZero(0, 1)
                    childGen1.depth = (depth + 1).toByte()
                    val childrenGen2 = arrayOf(FifteenPuzzle(childGen1), FifteenPuzzle(childGen1))
                    childrenGen2[0].parent = childGen1
                    childrenGen2[0].moveZero(0, 1)
                    childrenGen2[0].depth = (depth + 2).toByte()
                    childrenGen2[1].parent = childGen1
                    childrenGen2[1].moveZero(1, 0)
                    childrenGen2[1].depth = (depth + 2).toByte()
                    return childrenGen2
                } else {
                    val childGen1 = FifteenPuzzle(this)
                    childGen1.moveZero(1, 0)
                    childGen1.depth = (depth + 1).toByte()
                    val childrenGen2 = arrayOf(FifteenPuzzle(childGen1), FifteenPuzzle(childGen1))
                    childrenGen2[0].parent = childGen1
                    childrenGen2[0].moveZero(1, 0)
                    childrenGen2[0].depth = (depth + 2).toByte()
                    childrenGen2[1].parent = childGen1
                    childrenGen2[1].moveZero(0, 1)
                    childrenGen2[1].depth = (depth + 2).toByte()
                    return childrenGen2
                }
            }
            0x03 -> {
                if (this.parent.getx() - this.getx() == 1) {
                    val childGen1 = FifteenPuzzle(this)
                    childGen1.moveZero(0, -1)
                    childGen1.depth = (depth + 1).toByte()
                    val childrenGen2 = arrayOf(FifteenPuzzle(childGen1), FifteenPuzzle(childGen1))
                    childrenGen2[0].parent = childGen1
                    childrenGen2[0].moveZero(0, -1)
                    childrenGen2[0].depth = (depth + 2).toByte()
                    childrenGen2[1].parent = childGen1
                    childrenGen2[1].moveZero(1, 0)
                    childrenGen2[1].depth = (depth + 2).toByte()
                    return childrenGen2
                } else {
                    val childGen1 = FifteenPuzzle(this)
                    childGen1.moveZero(1, 0)
                    childGen1.depth = (depth + 1).toByte()
                    val childrenGen2 = arrayOf(FifteenPuzzle(childGen1), FifteenPuzzle(childGen1))
                    childrenGen2[0].parent = childGen1
                    childrenGen2[0].moveZero(1, 0)
                    childrenGen2[0].depth = (depth + 2).toByte()
                    childrenGen2[1].parent = childGen1
                    childrenGen2[1].moveZero(0, -1)
                    childrenGen2[1].depth = (depth + 2).toByte()
                    return childrenGen2
                }
            }
            0x30 -> {
                if (this.parent.getx() - this.getx() == -1) {
                    val childGen1 = FifteenPuzzle(this)
                    childGen1.moveZero(0, 1)
                    childGen1.depth = (depth + 1).toByte()
                    val childrenGen2 = arrayOf(FifteenPuzzle(childGen1), FifteenPuzzle(childGen1))
                    childrenGen2[0].parent = childGen1
                    childrenGen2[0].moveZero(0, 1)
                    childrenGen2[0].depth = (depth + 2).toByte()
                    childrenGen2[1].parent = childGen1
                    childrenGen2[1].moveZero(-1, 0)
                    childrenGen2[1].depth = (depth + 2).toByte()
                    return childrenGen2
                } else {
                    val childGen1 = FifteenPuzzle(this)
                    childGen1.moveZero(-1, 0)
                    childGen1.depth = (depth + 1).toByte()
                    val childrenGen2 = arrayOf(FifteenPuzzle(childGen1), FifteenPuzzle(childGen1))
                    childrenGen2[0].parent = childGen1
                    childrenGen2[0].moveZero(-1, 0)
                    childrenGen2[0].depth = (depth + 2).toByte()
                    childrenGen2[1].parent = childGen1
                    childrenGen2[1].moveZero(0, 1)
                    childrenGen2[1].depth = (depth + 2).toByte()
                    return childrenGen2
                }
            }
            0x33 -> {
                if (this.parent.getx() - this.getx() == -1) {
                    val childGen1 = FifteenPuzzle(this)
                    childGen1.moveZero(0, -1)
                    childGen1.depth = (depth + 1).toByte()
                    val childrenGen2 = arrayOf(FifteenPuzzle(childGen1), FifteenPuzzle(childGen1))
                    childrenGen2[0].parent = childGen1
                    childrenGen2[0].moveZero(0, -1)
                    childrenGen2[0].depth = (depth + 2).toByte()
                    childrenGen2[1].parent = childGen1
                    childrenGen2[1].moveZero(-1, 0)
                    childrenGen2[1].depth = (depth + 2).toByte()
                    return childrenGen2
                } else {
                    val childGen1 = FifteenPuzzle(this)
                    childGen1.moveZero(-1, 0)
                    childGen1.depth = (depth + 1).toByte()
                    val childrenGen2 = arrayOf(FifteenPuzzle(childGen1), FifteenPuzzle(childGen1))
                    childrenGen2[0].parent = childGen1
                    childrenGen2[0].moveZero(-1, 0)
                    childrenGen2[0].depth = (depth + 2).toByte()
                    childrenGen2[1].parent = childGen1
                    childrenGen2[1].moveZero(0, -1)
                    childrenGen2[1].depth = (depth + 2).toByte()
                    return childrenGen2
                }
            }
        }
        return null
    }

    fun printSolutionRecursive(LowerResult: FifteenPuzzle?): String {
        var result: String
        if (this.parent == this) {
            result = this.toString()
        } else {
            val R = this.parent.printSolutionRecursive(null)
            result = this.printToString(R)
        }
        var LowerPuzzle = LowerResult
        if (LowerPuzzle != null) {
            result = LowerPuzzle.printToString(result)
            while (LowerPuzzle != null && LowerPuzzle.parent != LowerPuzzle) {
                result = LowerPuzzle.parent.printToString(result)
                LowerPuzzle = LowerPuzzle.parent
            }
        }
        return result
    }

    fun printToString(Result: String): String{
        val mergedResult = StringBuilder()
        val A = Result.split("\n").dropLastWhile { it == "" }
        val B = this.toString().split("\n").dropLastWhile { it == "" }
        if (A[A.size - 1].length + 10 >= 82) {
            for (i in A.indices) {
                mergedResult.append(A[i] + "\n")
            }
            for (i in B.indices) {
                mergedResult.append(B[i] + "\n")
            }
        } else {
            for (i in 0 until A.size - B.size) {
                mergedResult.append(A[i] + "\n")
            }
            for (i in B.size downTo 1) {
                mergedResult.append(A[A.size - i] + " > " + B[B.size - i] + "\n")
            }
        }
        return mergedResult.toString()
    }

    override fun equals(other: Any?): Boolean {
        val puzz = other as FifteenPuzzle
        return this.board == puzz.board
    }

    override fun compareTo(other: FifteenPuzzle): Int {
        // Compare based on heuristics
        val deltaHeuristic = (this.depth + heuristics) - (other.depth + other.heuristics)
        if(deltaHeuristic == 0) {
            return this.heuristics - other.heuristics
        } else {
            return deltaHeuristic
        }
    }

    override fun toString(): String {
        var ans = ""
        for (i in 0 until BOARDLENGTH) {
            for (j in 0 until BOARDLENGTH) {
                ans += String.format("%x ", getval(i, j))
            }
            ans += "\n"
        }
        val xy = (getx() shl 4) or gety()
        return ans + String.format("%02x", xy) + String.format("%6d", this.depth) + "\n"
    }

    override fun hashCode(): Int {
        //return (board + (board shr 32)).toInt()
        return (board xor (board shr 32)).toInt() + (x.toInt() shl 16) + y.toInt()
    }
}
