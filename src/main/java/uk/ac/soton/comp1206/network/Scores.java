package uk.ac.soton.comp1206.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.*;

/**
 * The Scores class handles the score list outputted on the Score scene
 */
public class Scores {

    private static final Logger logger = LogManager.getLogger(Scores.class);

    /**
     * A hashmap of the names and scores
     */
    private static HashMap<String, Integer> scoresMap = new HashMap<>();

    /**
     * Method called to add a new name and score to the list
     * @param name
     * @param value
     */
    public static void addNewScore(String name, int value) {
        saveScores(name, value);
        readScores();
    }

    /**
     * Saves the information to a file
     * @param name
     * @param value
     */
    private static void saveScores(String name, int value) {
        try {
            File file = new File("Scores.txt");
            FileWriter myWriter = new FileWriter(file, true);
            myWriter.write(name + ":" + value + "\r\n");
            myWriter.close();
            logger.info("Successfully added {}:{} to scores", name, value);
        } catch (IOException e) {
            System.out.println("Couldnt add any score sis");
            e.printStackTrace();
        }
    }

    /**
     * Reads the scores from a file everytime they need to  be used
     */
    private static void readScores() {
        try {
            File file = new File("Scores.txt");
            if (!file.exists()) {
                whenEmptyList();
                return;
            }
            Scanner myReader = new Scanner(file);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] words = data.split(":");
                scoresMap.put(words[0], Integer.parseInt(words[1]));
                logger.info("Read {}, ", data);
            }
            scoresMap=sortMap();
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /**
     * If there is no previous game, the list is initialised with 2 pseudo scores
     */
    private static void whenEmptyList() {
        saveScores("Try and beat this!", 1000);
        saveScores("A good challenge", 5000);
        readScores();
        logger.info("Created fake list");
    }

    /**
     * Get highest score
     * @return
     */
    private static Integer findHighScore() {
        readScores();
        if (scoresMap.size() == 0)
            return 0;
        return Collections.max(scoresMap.values());
    }

    /**
     * Get the score map
     * @return
     */
    public static HashMap<String, Integer> getScores() {
        return  scoresMap;
    }

    /**
     * Get the highest score
     * @return
     */
    public static Integer getHighScore() {
        return findHighScore();
    }

    /**
     * Sorts the hashmap considering the value
     * @return
     */
    public static HashMap<String, Integer> sortMap() {
        HashMap<String, Integer> mapToUse = sortByValue(getScores());
        for (Map.Entry<String, Integer> en : mapToUse.entrySet()) {
            System.out.println("Key = " + en.getKey() +
                    ", Value = " + en.getValue());
        }
        return mapToUse;
    }

    // function to sort hashmap by values
    public static HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm) {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            if(temp.size()>9) break;
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }
}

