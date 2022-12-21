package hotelapp;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/** The inverted index */
public class InvertedIndex {
  /** The index */
  private final Map<String, Map<Review, Integer>> invertedIndex;

  /** Constructor for the inverted index */
  public InvertedIndex() {
    invertedIndex = new TreeMap<>();
  }

  /**
   * Add a review to the inverted index
   *
   * @param word the word to add
   * @param review the review to add
   */
  public void add(String word, Review review, int count) {
    invertedIndex.putIfAbsent(word, new HashMap<>());
    invertedIndex.get(word).put(review, count);
  }

  /**
   * Get the inverted index
   *
   * @return the inverted index
   */
  public Map<String, Map<Review, Integer>> getInvertedIndex() {
    return Collections.unmodifiableMap(invertedIndex);
  }
}
