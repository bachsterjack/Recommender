package com.jcaldwell.recommender;

import java.util.*;

public class JaccardSimilarityPair implements JaccardSimilarity<String> {
    public Map.Entry<String, HashSet<String>> videoAUsersWatched;
    public Map.Entry<String, HashSet<String>> videoBUsersWatched;
    public double coefficient = JaccardSimilarity.DEFAULT_COEFFICIENT;

    public JaccardSimilarityPair(Map.Entry<String, HashSet<String>> videoAUsersWatched,
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
    public double jaccardIndex() {
        this.coefficient = this.jaccardIndexFrom(this.videoAUsersWatched.getValue(), this.videoBUsersWatched.getValue());
        return this.coefficient;
    }

    @Override
    public double jaccardIndexFrom(Set<String> s1, Set<String> s2) {
        HashSet<String> intersection = new HashSet<>(s1);
        intersection.retainAll(s2);
        HashSet<String> union = new HashSet<>(s1);
        union.addAll(s2);
        if (union.size() == 0) {
            return DEFAULT_COEFFICIENT;
        }
        return JaccardSimilarity.jaccardIndex(intersection.size(), union.size());
    }
}
