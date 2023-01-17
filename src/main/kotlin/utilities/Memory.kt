package utilities

class Memory {
    companion object {
        // Everything is in bytes
        val totalMemory: Long get() = Runtime.getRuntime().totalMemory()
        val freeMemory: Long get() = Runtime.getRuntime().freeMemory()
        val maxMemory: Long get() = Runtime.getRuntime().maxMemory()
        val allocatedMemory: Long get() = totalMemory - freeMemory
        val freeMemoryPresumable: Long get() = maxMemory - allocatedMemory

        fun gcBite() {
            System.gc()
            Runtime.getRuntime().gc()
        }
    }
}