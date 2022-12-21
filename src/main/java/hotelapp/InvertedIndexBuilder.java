package hotelapp;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/** The inverted index builder */
public class InvertedIndexBuilder {
  /** The inverted index */
  private final InvertedIndex invertedIndex;
  /** The list of stop words */
  private final List<String> stopWords;

  /**
   * Constructor for the inverted index builder
   *
   * @param invertedIndex the inverted index to build
   */
  public InvertedIndexBuilder(InvertedIndex invertedIndex) {
    this.invertedIndex = invertedIndex;
    stopWords = new ArrayList<>();
  }

  /**
   * Populate the list of stop words
   *
   * @param file the file containing the stop words
   */
  public void populateStopWords(String file) {
    Path path = Path.of(file);
    try (Scanner scanner = new Scanner(path)) {
      while (scanner.hasNextLine()) {
        stopWords.add(scanner.nextLine());
      }
    } catch (Exception e) {
      System.out.println("Could not read the file:" + e);
    }
  }

  /**
   * Add a review to the inverted index
   *
   * @param reviews list of the reviews to add
   */
  public void addReviews(List<Review> reviews) {
    for (Review review : reviews) {
      String[] words =
          review.getReviewText().toLowerCase().replaceAll("[-+.^:,;!()]", "").split("\\s+");
      for (String word : words) {
        if (!stopWords.contains(word)) {
          review.addWordCountToMap(word);
          invertedIndex.add(word, review, review.getWordCountFromMap(word));
        }
      }
    }
  }
}
