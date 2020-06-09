package com.jcaldwell.recommender;

import java.util.*;

public class JacquardSimilarityPair implements JacquardSimilarity<String> {
    public Map.Entry<String, HashSet<String>> videoAUsersWatched;
    public Map.Entry<String, HashSet<String>> videoBUsersWatched;
    public double coefficient = JacquardSimilarity.DEFAULT_COEFFICIENT;

    public JacquardSimilarityPair(Map.Entry<String, HashSet<String>> videoAUsersWatched,
                                  Map.Entry<String, HashSet<String>> videoBUsersWatched)
    {
        Objects.requireNonNull(videoAUsersWatched, "Sets for comparison must not be null");
        Objects.requireNonNull(videoBUsersWatched, "Sets for comparison must not be null");
        this.videoAUsersWatched = videoAUsersWatched;
        this.videoBUsersWatched = videoBUsersWatched;
    }

    public double getCoefficient() {
        return this.coefficient;
    }

    public String getVideoA() {
        return this.videoAUsersWatched.getKey();
    }

    public String getVideoB() {
        return this.videoBUsersWatched.getKey();
    }

    @Override
    public double jacquardIndex() {
        this.coefficient = this.jacquardIndexFrom(this.videoAUsersWatched.getValue(), this.videoBUsersWatched.getValue());
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
