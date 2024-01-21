class HashSetL<T>: MutableSet<T> {
    private val loadFactor = 7
    private val initialCapacity = 100
    private var table: Array<MutableList<T>> = Array(initialCapacity) { mutableListOf() }
    private var sizeValue = 0

    override val size: Int
        get() = sizeValue

    override fun add(element: T): Boolean {
        if (needsResize()) {
            resize()
        }

        val index = getIndex(element)
        val bucket = table[index]

        if (bucket.none { it == element }) {
            bucket.add(element)
            sizeValue++
            return true
        }
        return false
    }
    
    fun addOrUpdate(element: T): Boolean {
        if (needsResize()) {
            resize()
        }

        val index = getIndex(element)
        val bucket = table[index]

        val indexOfElement = bucket.indexOfFirst { it == element }

        if (indexOfElement != -1) {
            // Update the existing element
            bucket[indexOfElement] = element
            return false // Element updated, return false
        } else {
            // Add the new element
            bucket.add(element)
            sizeValue++
            return true // Element added, return true
        }
    }

    override fun addAll(elements: Collection<T>): Boolean {
        var modified = false
        for (element in elements) {
            if (add(element)) {
                modified = true
            }
        }
        return modified
    }

    override fun clear() {
        table = Array(initialCapacity) { mutableListOf() }
        sizeValue = 0
    }

    override fun contains(element: T): Boolean {
        val index = getIndex(element)
        val bucket = table[index]
        return bucket.any { it == element }
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        return elements.all { contains(it) }
    }

    override fun isEmpty(): Boolean {
        return sizeValue == 0
    }

    override fun iterator(): MutableIterator<T> {
        return object : MutableIterator<T> {
            private var currentIndex = 0
            private var currentBucketIndex = 0

            override fun hasNext(): Boolean {
                while (currentBucketIndex < table.size && currentIndex >= table[currentBucketIndex].size) {
                    currentIndex = 0
                    currentBucketIndex++
                }
                return currentBucketIndex < table.size
            }

            override fun next(): T {
                if (!hasNext()) {
                    throw NoSuchElementException()
                }
                return table[currentBucketIndex][currentIndex++]
            }

            override fun remove() {
                throw UnsupportedOperationException("Remove operation is not supported.")
            }
        }
    }

    override fun remove(element: T): Boolean {
        val index = getIndex(element)
        val bucket = table[index]
        
        val indexOfElement = bucket.indexOfFirst { it == element }
        
        if (indexOfElement != -1) {
            bucket.removeAt(indexOfElement)
            sizeValue--
            return true
        }
        
        return false
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        val newTable = Array(table.size) { mutableListOf<T>() }

        for (bucket in table) {
            for (element in bucket) {
                if (element in elements) {
                    val newIndex = getIndex(element)
                    newTable[newIndex].add(element)
                }
            }
        }

        table = newTable
        val modified = sizeValue != newTable.sumOf { it.size }
        sizeValue = newTable.sumOf { it.size }

        return modified
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        var modified = false
        for (element in elements) {
            if (remove(element)) {
                modified = true
            }
        }
        return modified
    }
    
    fun calculateStandardDeviation(): Double {
        val averageBucketSize = size.toDouble() / table.size
        var squaredDeviationSum = 0.0

        for (bucket in table) {
            val deviation = bucket.size - averageBucketSize
            squaredDeviationSum += deviation * deviation
        }

        val meanSquaredDeviation = squaredDeviationSum / table.size
        val standardDeviation = Math.sqrt(meanSquaredDeviation)
        
        return standardDeviation
    }

    fun findObject(goal: T): T? {
        val index = getIndex(goal)
        val bucket = table[index]

        for (element in bucket) {
            if (element == goal) {
                return element
            }
        }
        return null
    }

    private fun getIndex(value: T): Int {
        val hashCode = value.hashCode() and Int.MAX_VALUE // Ensure non-negative hash code
        return hashCode % table.size
    }

    private fun needsResize(): Boolean {
        return sizeValue >= loadFactor * table.size
    }

    private fun resize() {
        val newCapacity = table.size * 5
        val newTable = Array(newCapacity) { mutableListOf<T>() }

        for (bucket in table) {
            for (element in bucket) {
                val hashCode = element.hashCode() and Int.MAX_VALUE // Ensure non-negative hash code
                val newIndex = hashCode % newCapacity
                newTable[newIndex].add(element)
            }
        }

        table = newTable
    }

}
