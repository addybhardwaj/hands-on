package com.intelladept.poc.utils;

/**
 * TODO
 *
 * @author Aditya Bhardwaj
 */
public class MemoryUtils {

    public static Stats stats() {
        Runtime runtime = Runtime.getRuntime();
        return new Stats(runtime.maxMemory(), runtime.freeMemory(), runtime.totalMemory());
    }

    public static class Stats {
        private final long maxMemory;
        private final long freeMemory;
        private final long totalMemory;


        public Stats(long maxMemory, long freeMemory, long totalMemory) {
            this.maxMemory = maxMemory;
            this.freeMemory = freeMemory;
            this.totalMemory = totalMemory;
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

        @Override
        public String toString() {
            return "Stats{" +
                    "maxMemory=" + maxMemory +
                    ", freeMemory=" + freeMemory +
                    ", totalMemory=" + totalMemory +
                    '}';
        }
    }
}
