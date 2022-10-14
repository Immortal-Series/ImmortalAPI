package net.immortalapi.utils;

import java.util.HashMap;

public final class Benchmark {

    /**
     * All sections.
     */
    private static final HashMap<String, Long> benchmarks = new HashMap<>();

    /**
     * @param section Section name.
     */
    public static void start(String section) {
        benchmarks.put(section, System.nanoTime());
    }

    /**
     * @param section Must be the same as the start section name.
     */
    public static void end(String section) {
        Long calculatedTime = calculate(benchmarks.remove(section));
        System.out.println(section + " took " + calculatedTime + "ms.");
    }

    /**
     * @param section Section name.
     * @param cycles  Amount of time to execute this code.
     * @param code    Code to run.
     */
    public static void testCycles(String section, int cycles, Runnable code) {
        for (int i = 0; i < cycles; i++) {
            start(section);
            code.run();
            end(section);
        }
    }

    /**
     * @param time Nano section time.
     * @return Return MS time.
     */
    private static long calculate(Long time) {
        return (long) ((System.nanoTime() - time) / 1000000D);
    }

}
