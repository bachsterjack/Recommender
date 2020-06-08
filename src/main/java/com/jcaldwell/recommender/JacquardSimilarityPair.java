package com.jcaldwell.recommender;

import jdk.nashorn.internal.objects.annotations.Getter;

import java.util.*;

public class JacquardSimilarityPair implements JacquardSimilarity<String> {
    public Map.Entry<String, HashSet<String>> sampleA;
    public Map.Entry<String, HashSet<String>> sampleB;
    public double coefficient = JacquardSimilarity.DEFAULT_COEFFICIENT;

    public JacquardSimilarityPair(Map.Entry<String, HashSet<String>> sampleA, Map.Entry<String, HashSet<String>> sampleB)
    {
        Objects.requireNonNull(sampleA, "Sets for comparison must not be null");
        Objects.requireNonNull(sampleB, "Sets for comparison must not be null");
        this.sampleA = sampleA;
        this.sampleB = sampleB;
    }

    public double getCoefficient() {
        return this.coefficient;
    }

    public String getVideoA() {
        return this.sampleA.getKey();
    }

    public String getVideoB() {
        return this.sampleB.getKey();
    }

    @Override
    public double jacquardIndex() {
        this.coefficient = this.jacquardIndexFrom(this.sampleA.getValue(), this.sampleB.getValue());
        return this.coefficient;
    }

    @Override
    public double jacquardIndexFrom(Set<String> s1, Set<String> s2) {
        HashSet<String> intersection = new HashSet<>(s1);
        intersection.retainAll(s2);
        HashSet<String> union = new HashSet<>(s1);
        union.addAll(s2);
        if (union.size() == 0) {
            return DEFAULT_COEFFICIENT;
        }
        return JacquardSimilarity.jaccardIndex(intersection.size(), union.size());
    }
}
