package hotelapp;

import java.util.*;

public class HotelAndReviewFinder {
  /** The hotel map */
  private final Map<String, Hotel> hotelMap;
  /** The review map */
  private final Map<String, TreeSet<Review>> reviewMap;
  /** The inverted index */
  private InvertedIndex invertedIndex;
  /** The review's id map for non duplicates */
  private final Map<String, Boolean> reviewsIdMap;

  private DatabaseHandler dbHandler = DatabaseHandler.getInstance();

  /** Constructor for HotelAndReviewFinder */
  public HotelAndReviewFinder() {
    hotelMap = new TreeMap<>();
    reviewMap = new TreeMap<>();
    reviewsIdMap = new TreeMap<>();
  }

  /**
   * HotelAndReviewFinder for hotel using hotel id
   *
   * @param id the hotel id return the hotel
   * @return the hotel
   */
  public Hotel findHotel(String id) {
    if (hotelMap.containsKey(id)) {
      return hotelMap.get(id);
    }
    return null;
  }

  /**
   * Find reviews for a hotel
   *
   * @param id of the hotel
   * @return list of reviews
   */
  public List<Review> findReviews(String id) {
    return dbHandler.getReviews(id);
    //    if (reviewMap.containsKey(id)) {
    //      return reviewMap.get(id);
    //    }
    //    return null;
  }

  /**
   * Find all the reviews that contain the word
   *
   * @param word to hotelAndReviewFinder in reviews
   * @return List of reviews that contain the word
   */
  public List<Review> findWord(String word) {
    List<Review> reviews = new ArrayList<>();
    if (invertedIndex.getInvertedIndex().containsKey(word)) {
      for (Review review : invertedIndex.getInvertedIndex().get(word).keySet()) {
        review.setWordCountToReview(invertedIndex.getInvertedIndex().get(word).get(review));
        reviews.add(review);
      }
    }
    reviews.sort(new Review.ReviewWordCountComparator().reversed());
    return reviews;
  }

  /**
   * Add a hotel to the hotel map
   *
   * @param hotel the hotel to add
   */
  public void addHotel(Hotel hotel) {
    hotelMap.put(String.valueOf(hotel.getId()), hotel);
  }

  /**
   * Adds hotels to hotel map
   *
   * @param hotels the hotels to add
   */
  public void addHotels(Hotel[] hotels) {
    for (Hotel hotel : hotels) {
      addHotel(hotel);
    }
  }

  /**
   * Add a review to the review map
   *
   * @param review the review to add
   */
  public void addReview(Review review) {
    if (reviewsIdMap.containsKey(review.getReviewId())) {
      return;
    }

    reviewMap.putIfAbsent(
        review.getHotelId(), new TreeSet<>(new Review.ReviewDateComparator().reversed()));
    reviewMap.get(review.getHotelId()).add(review);
    reviewsIdMap.put(review.getReviewId(), true);
  }

  public void addNewReview(Review review) {
    dbHandler.addReview(review);
  }

  /**
   * Adds reviews to review map
   *
   * @param reviews the reviews to add
   */
  public void addReviews(List<Review> reviews) {
    for (Review review : reviews) {
      addReview(review);
    }
  }

  public void deleteReview(Review review) {
    dbHandler.deleteReview(review);
    //    if (reviewMap.containsKey(review.getHotelId())) {
    //      reviewMap.get(review.getHotelId()).remove(review);
    //    }
  }

  /**
   * Get all the reviews
   *
   * @return list of reviews
   */
  public List<Review> getReviews() {
    List<Review> reviews = new ArrayList<>();
    for (Set<Review> reviewList : reviewMap.values()) {
      reviews.addAll(reviewList);
    }
    return Collections.unmodifiableList(reviews);
  }

  /**
   * Set the inverted index
   *
   * @param invertedIndex the inverted index
   */
  public void setInvertedIndex(InvertedIndex invertedIndex) {
    this.invertedIndex = invertedIndex;
  }

  /**
   * Check if the string is an integer
   *
   * @param string the string to check
   * @return true if the string is an integer, false otherwise
   */
  public static boolean isInteger(String string) {
    try {
      Integer.parseInt(string);
    } catch (NumberFormatException e) {
      return false;
    }
    return true;
  }

  /**
   * Get hotel map
   *
   * @return Hotel map
   */
  public Map<String, Hotel> getHotelMap() {
    return Collections.unmodifiableMap(hotelMap);
  }

  /**
   * Get review map
   *
   * @return Review map
   */
  public Map<String, Set<Review>> getReviewMap() {
    return Collections.unmodifiableMap(reviewMap);
  }

  /**
   * Get Hotels
   *
   * @return Set of hotels
   */
  public Set<Hotel> getHotels() {
    return dbHandler.getHotels();
  }

  /**
   * Find reviews
   *
   * @return A single review
   */
  public Review findReview(String hotelId, String reviewId) {
    return dbHandler.getReview(hotelId, reviewId);
  }
}
