package com.jcaldwell.recommender;

import java.util.Collection;
import java.util.Set;

public interface JaccardSimilarity<T>  {

    double DEFAULT_COEFFICIENT = 0.0;

    static double jaccardIndex(int intersectionCount, int unionCount) {
        // Not really needed, if data sample includes only movies viewed
        if (unionCount == 0) {
            return DEFAULT_COEFFICIENT;
        }
        return (double) intersectionCount / (double) unionCount;
    }

    double jaccardIndex();

    double jaccardIndexFrom(Set<T> s1, Set<T> s2);
}
