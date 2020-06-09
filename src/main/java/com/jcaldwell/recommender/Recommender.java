package com.jcaldwell.recommender;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;

public class Recommender {

    public static void main(String[] args) {

        String inputFileName = null;
        String outputFileName = "./recommendations.csv";
        final int maxSimilaritiesOutput = 3;

        /*
         * Parse positional arguments
         * arg 1 = input CSV file name (full path)
         * arg 2 = output CSV file name (if not provided, defaults to "./recommendations.csv")
         */
        if (args.length == 0) {
            System.out.println("No input or output files specified. Exiting....");
            System.exit(0);
        }
        if (args.length == 1) {
            inputFileName = args[0];
            System.out.println("Input file provided..." + inputFileName);
            System.out.println("No output file provided...defaulting to recommendations.csv");
        }
        if (args.length == 2) {
            inputFileName = args[0];
            outputFileName = args[1];
        }

        // Parse user-watched videos into video mappings to user sets
        LinkedHashMap<String, HashSet<String>> watchedVideoToUsersMap = parseInputCSV(inputFileName);

        // Calculate Jacquard Coefficients of unique set of watched video pairs
        int numVideos = watchedVideoToUsersMap.size();
        Map.Entry<String, HashSet<String>>[] videoMapArray = new Map.Entry[watchedVideoToUsersMap.size()];
        videoMapArray = watchedVideoToUsersMap.entrySet().toArray(videoMapArray);
        LinkedHashMap<String, List<JacquardSimilarityPair>> videoSimilarityPairsMap = new LinkedHashMap<>(numVideos);

        // O(N(N-1)/2) unique pairs calculated
        for (int i = 0; i < videoMapArray.length; i++) {
            List<JacquardSimilarityPair> recommendedPairsForRow = new ArrayList<>(videoMapArray.length - i);
            for (int k = i+1; k < videoMapArray.length; k++) {
                JacquardSimilarityPair similarityPair = new JacquardSimilarityPair(
                        videoMapArray[i],
                        videoMapArray[k]);
                similarityPair.jacquardIndex();
                recommendedPairsForRow.add(similarityPair);
            }
            // Add ith row of (n-i) jacquard similarity pairs
            videoSimilarityPairsMap.put(videoMapArray[i].getKey(), recommendedPairsForRow);
        }
        System.out.println("Calculated Jacquard Coefficients of all watched video pairs");

        // Reduce similarities to 3 best recommendations for each source video,
        // filtering results where there is at least some viewer similarity -- ie, coefficient > 0)
        List<JacquardSimilarityPair> highestScoreRecommendations = new ArrayList<>(videoSimilarityPairsMap.size() * maxSimilaritiesOutput);
        videoSimilarityPairsMap.forEach( (video, pairs) ->
        {
            List<JacquardSimilarityPair> mostSimilarVideos = pairs.stream()
                    .sorted(Comparator.comparing(JacquardSimilarityPair::getCoefficient).reversed())
                    .limit(3)
                    .collect(Collectors.toList());
            highestScoreRecommendations.addAll(mostSimilarVideos);
        });

        System.out.println("Filtered 3 highest recommendations for each video, using Jacquard Coefficients");
        createRecommendationCSV(outputFileName, highestScoreRecommendations);
    }

    /**
      Parse CSV Input file, using Apache Commons CSV.
      Each CSV row is (user_id, watched_video_id) pair
      Parse rows into map of watched_video_id to set of user_id who watched the video.
        k = watched_video_id
        v = set of user_id (who watched video)
    */
    private static LinkedHashMap<String, HashSet<String>> parseInputCSV(String fileName) {
        LinkedHashMap<String, HashSet<String>> movies_watchedBy_users = new LinkedHashMap<>();
        System.out.println("Processing input file of user watched videos ...");
        try {
            Reader in = new FileReader(fileName);
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreEmptyLines(true)
                    .parse(in);

            for (CSVRecord record : records) {
                String userId = record.get("user_id");
                String videoId = record.get("watched_video_id");
                HashSet<String> userSet = movies_watchedBy_users.getOrDefault(videoId, new HashSet<>());
                userSet.add(userId);
                movies_watchedBy_users.put(videoId, userSet);
            }
            in.close();
            System.out.println("Parsed input file into map of watched_video_id to set of user_id");
        } catch (Exception e) {
            System.out.println("Exception encountered parsing input file: " + e.getMessage());
            System.exit(1);
        }
        return movies_watchedBy_users;
    }

    /**
     Create CSV Output file, using Apache Commons CSV.
     Each CSV output header is (video_id, recommended_video_id, score).
     highestScoreRecommendations - map of videos to highest score JacquardSimilarityPairs.
     For each recommendation output CSV row.
     */
    private static void createRecommendationCSV(String outputFileName,
                                                List<JacquardSimilarityPair> highestScoreRecommendations) {
        try {
            FileWriter out = new FileWriter(outputFileName);
            CSVPrinter printer = CSVFormat.DEFAULT
                    .withHeader("video_id", "recommended_video_id", "score")
                    .print(out);

            highestScoreRecommendations.forEach( simPair -> {
                        try {
                            printer.printRecord(simPair.getVideoA(), simPair.getVideoB(), simPair.getCoefficient());
                        } catch (IOException e) {
                            System.out.println("Exception encountered writing output file: " + e.getMessage());
                            System.exit(1);                        }
                    }
            );
            System.out.println("Created recommended videos CSV");
            out.close();
        } catch (Exception e) {
            System.out.println("Exception encountered writing output file: " + e.getMessage());
            System.exit(1);
        }
    }
}
