package hotelapp;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/** The review of a hotel */
public class Review {
  /** The word count map of a specific word in the review */
  public final Map<String, Integer> wordCountMap;
  /** The id of the hotel */
  private final String hotelId;
  /** The id of the user */
  private final String reviewId;
  /** The overall rating of the review */
  private int ratingOverall;
  /** The title of the review */
  private String title;
  /** The text of the review */
  private String reviewText;
  /** The username of the reviewer */
  @SerializedName("userNickname")
  private final String username;
  /** The date of the review */
  @SerializedName("reviewSubmissionTime")
  private final LocalDate date;
  /** The word count of the review */
  private int wordCount;

  /** Constructor for a review */
  public Review(
      String hotelId,
      String reviewId,
      int ratingOverall,
      String title,
      String reviewText,
      String username,
      LocalDate date) {
    this.hotelId = hotelId;
    this.reviewId = reviewId;
    this.ratingOverall = ratingOverall;
    this.title = title;
    this.reviewText = reviewText;
    this.username = username;
    this.date = date;
    wordCountMap = new HashMap<>();
    wordCount = 0;
  }

  /**
   * Get the id of the hotel
   *
   * @return the hotelId
   */
  public String getHotelId() {
    return hotelId;
  }

  /**
   * Get the title of the review
   *
   * @return the reviewTitle
   */
  public String getReviewTitle() {
    return title;
  }

  /*
   * Get the rating of the review
   */
  public int getRating() {
    return ratingOverall;
  }

  /**
   * Get the id of the review
   *
   * @return the review id
   */
  public String getReviewId() {
    return reviewId;
  }

  /**
   * Get the text of the review
   *
   * @return the reviewText
   */
  public String getReviewText() {
    return reviewText;
  }

  /**
   * Get the date of the review
   *
   * @return the date
   */
  public LocalDate getDate() {
    return date;
  }

  /** add a word to the wordCountMap */
  public void addWordCountToMap(String word) {
    wordCountMap.put(word, wordCountMap.getOrDefault(word, 0) + 1);
  }

  /**
   * Get the word count of the review's word
   *
   * @param word the word looking for
   * @return the wordCount
   */
  public int getWordCountFromMap(String word) {
    return wordCountMap.get(word);
  }

  /** set the wordCount */
  public void setWordCountToReview(Integer wordCount) {
    this.wordCount = wordCount;
  }

  /**
   * Get the word count of the review
   *
   * @return the wordCount
   */
  public int getWordCount() {
    return wordCount;
  }

  /**
   * Get the username of the reviewer
   *
   * @return the username
   */
  public String getReviewUsername() {
    return username;
  }

  /** Update title */
  public void updateTitle(String title) {
    this.title = title;
  }

  /** Update review text */
  public void updateReviewText(String reviewText) {
    this.reviewText = reviewText;
  }

  /** Update rating */
  public void updateRating(int rating) {
    ratingOverall = rating;
  }

  @Override
  public String toString() {
    return "--------------------"
        + System.lineSeparator()
        + "Review by "
        + username
        + " on "
        + date
        + System.lineSeparator()
        + "Rating: "
        + ratingOverall
        + System.lineSeparator()
        + "ReviewId: "
        + reviewId
        + System.lineSeparator()
        + ""
        + title
        + System.lineSeparator()
        + ""
        + reviewText;
  }

  @Override
  public int hashCode() {
    return reviewId.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Review other = (Review) obj;
    return this.reviewId.equals(other.reviewId);
  }

  /** Comparator for sorting reviews by date. If equal by review id */
  public static class ReviewDateComparator implements Comparator<Review> {
    @Override
    public int compare(Review r1, Review r2) {
      return r1.getDate().compareTo(r2.getDate());
    }
  }

  /** Comparator for sorting reviews by word count and then by date. */
  public static class ReviewWordCountComparator implements Comparator<Review> {
    @Override
    public int compare(Review r1, Review r2) {
      if (r1.getWordCount() == r2.getWordCount()) {
        return r1.getDate().compareTo(r2.getDate());
      }
      return r1.getWordCount() - r2.getWordCount();
    }
  }
}
