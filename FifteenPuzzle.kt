
class FifteenPuzzle {
	companion object {
        const val BOARDLENGTH: Byte = 4
    }
    
    var board: ULong = 0UL
    var parent: FifteenPuzzle = this
    var x: Byte = 0
    var y: Byte = 0
    var manhattan: Byte = 127.toByte()
    var depth: Byte = 0

    constructor(data: Array<IntArray>) {
        for (i in 0 until BOARDLENGTH) {
            for (j in 0 until BOARDLENGTH) {
                if (data[i][j] == 0) {
                    x = i.toByte()
                    y = j.toByte()
                } else {
                    setval(i, j, data[i][j].toByte())
                }
            }
        }
    }

    constructor(data: Array<IntArray>, parent: FifteenPuzzle) : this(data) {
        this.parent = parent
    }
    
    constructor(data: Array<IntArray>, depth: Int) : this(data) {
        this.depth = depth.toByte()
    }

    fun setval(x: Int, y: Int, value: Byte) {
        val shift = (4 * x + y) * 4
        val mask = (value.toULong() shl (60 - shift))
        board = board and ((0xfUL shl (60 - shift)).inv())
        board = board or mask
    }

    fun getval(x: Int, y: Int): Int {
        val shift = (4 * x + y) * 4
        return ((board shr (60 - shift)) and 0xfUL).toInt()
    }
    
    inline fun FifteenPuzzle.getx(): Int = this.x.toInt()
	
    inline fun FifteenPuzzle.gety(): Int = this.y.toInt()

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
        var lastnum = ((board shr 60) and 0xfUL).toInt()
        for (shift in 4..60 step 4) {
            val num = ((board shr (60 - shift)) and 0xfUL).toInt()
            if (num < lastnum) {
                parity++
            }
            lastnum = num
        }
        return parity
    }

    private fun moveZero(rx: Int, ry: Int) {
        var rxNew = rx + x
        var ryNew = ry + y

        setval(x.toInt(), y.toInt(), getval(rxNew, ryNew).toByte())
        setval(rxNew, ryNew, 0)

        x = rxNew.toByte()
        y = ryNew.toByte()
    }

    fun getChildren(): Array<FifteenPuzzle> {
        val children = arrayOfNulls<FifteenPuzzle>(4)
        var index = 0
        val newboard = Array(4) { IntArray(4) }
        for (i in 0 until BOARDLENGTH) {
            for (j in 0 until BOARDLENGTH) {
                newboard[i][j] = getval(i, j)
            }
        }
        if (getx() != 0 && parent.getx() != x - 1) {
            children[index] = FifteenPuzzle(newboard, this)
            children[index]?.moveZero(-1, 0)
            children[index]?.depth = (this.depth + 1).toByte()
            index++
        }
        if (getx() != BOARDLENGTH - 1 && parent.getx() != x + 1) {
            children[index] = FifteenPuzzle(newboard, this)
            children[index]?.moveZero(1, 0)
            children[index]?.depth = (this.depth + 1).toByte()
            index++
        }
        if (gety() != 0 && parent.gety() != y - 1) {
            children[index] = FifteenPuzzle(newboard, this)
            children[index]?.moveZero(0, -1)
            children[index]?.depth = (this.depth + 1).toByte()
            index++
        }
        if (gety() != BOARDLENGTH - 1 && parent.gety() != y + 1) {
            children[index] = FifteenPuzzle(newboard, this)
            children[index]?.moveZero(0, 1)
            children[index]?.depth = (this.depth + 1).toByte()
            index++
        }
        return (children.copyOf(index) as Array<FifteenPuzzle>)
    }

    fun printSolutionRecursive(forward: Boolean): String {
        val mergedResult = StringBuilder()
        if (this.parent == this) {
            return this.toString()
        } else {
            val A = this.parent.printSolutionRecursive(forward).split("\n").dropLastWhile { it == "" }
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
        }
        return mergedResult.toString()
    }

    fun printSolutionForward() {
        println(this)
        if (this.parent != this) {
            this.parent.printSolutionForward()
        }
    }

    override fun equals(other: Any?): Boolean {
        val puzz = other as FifteenPuzzle
        return this.board == puzz.board
    }

    override fun toString(): String {
        var ans = ""
        for (i in 0 until BOARDLENGTH) {
            for (j in 0 until BOARDLENGTH) {
                ans += String.format("%x ", getval(i, j))
            }
            ans += "\n"
        }
        return ans + String.format("%8d", this.depth) + "\n"
    }

    override fun hashCode(): Int {
        return (board + (board shr 32)).toInt()
    }
}
