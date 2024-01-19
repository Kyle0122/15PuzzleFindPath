class PriorityQueueS<T : Comparable<T>> : Iterable<T> {

    private val elements: MutableList<T> = mutableListOf()

    fun enqueue(element: T) {
        elements.add(element)
        heapifyUp()
    }

    fun dequeue(): T? {
        if (isEmpty()) return null

        // Swap the root with the last element
        swap(0, elements.size - 1)

        // Remove the last element (previously the root)
        val dequeuedElement = elements.removeAt(elements.size - 1)

        // Restore the heap property
        heapifyDown()

        return dequeuedElement
    }
    
    fun addAll(otherQueue: PriorityQueueS<T>) {
        for (element in otherQueue.elements) {
            enqueue(element)
        }
    }
    
    fun addAll(otherArray: Iterable<T>) {
        for (element in otherArray) {
            enqueue(element)
        }
    }

    fun peek(): T? {
        return elements.firstOrNull()
    }

    fun isEmpty(): Boolean {
        return elements.isEmpty()
    }

    fun size(): Int {
        return elements.size
    }
    
    fun toList(): List<T> {
        return elements.toList()
    }
    
    override fun iterator(): Iterator<T> {
        return elements.iterator()
    }

    private fun heapifyUp() {
        var index = elements.size - 1

        while (index > 0) {
            val parentIndex = (index - 1) / 2

            if (elements[index] < elements[parentIndex]) {
                swap(index, parentIndex)
                index = parentIndex
            } else {
                break
            }
        }
    }

    private fun heapifyDown() {
        var index = 0

        while (true) {
            val leftChildIndex = 2 * index + 1
            val rightChildIndex = 2 * index + 2
            var smallestChildIndex = index

            if (leftChildIndex < elements.size && elements[leftChildIndex] < elements[smallestChildIndex]) {
                smallestChildIndex = leftChildIndex
            }

            if (rightChildIndex < elements.size && elements[rightChildIndex] < elements[smallestChildIndex]) {
                smallestChildIndex = rightChildIndex
            }

            if (smallestChildIndex != index) {
                swap(index, smallestChildIndex)
                index = smallestChildIndex
            } else {
                break
            }
        }
    }

    private inline fun swap(i: Int, j: Int) {
        val temp = elements[i]
        elements[i] = elements[j]
        elements[j] = temp
    }
}
