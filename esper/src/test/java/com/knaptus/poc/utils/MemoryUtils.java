package com.knaptus.poc.utils;

/**
 * TODO
 *
 * @author Aditya Bhardwaj
 */
public class MemoryUtils {

    public static Stats stats() {
        Runtime runtime = Runtime.getRuntime();
        return new Stats(runtime.maxMemory()/(1024*1024), runtime.freeMemory()/(1024*1024), runtime.totalMemory()/(1024*1024));
    }

    public static class Stats {
        private final long maxMemory;
        private final long freeMemory;
        private final long totalMemory;
        private final long allocatedMemory;


        public Stats(long maxMemory, long freeMemory, long totalMemory) {
            this.maxMemory = maxMemory;
            this.freeMemory = freeMemory;
            this.totalMemory = totalMemory;
            this.allocatedMemory = totalMemory - freeMemory;
        }

        public long getMaxMemory() {
            return maxMemory;
        }

        public long getFreeMemory() {
            return freeMemory;
        }

        public long getTotalMemory() {
            return totalMemory;
        }

        public long getAllocatedMemory() {
            return allocatedMemory;
        }

        @Override
        public String toString() {
            return "Stats{" +
                    "maxMemory=" + maxMemory +
                    ", freeMemory=" + freeMemory +
                    ", totalMemory=" + totalMemory +
                    ", allocatedMemory=" + allocatedMemory +
                    '}';
        }
    }
}
