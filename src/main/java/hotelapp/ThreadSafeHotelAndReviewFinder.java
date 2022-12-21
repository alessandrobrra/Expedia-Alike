package hotelapp;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ThreadSafeHotelAndReviewFinder extends HotelAndReviewFinder {
  /** The lock */
  private ReentrantReadWriteLock lock;

  /** Constructor for the thread safe review finder */
  public ThreadSafeHotelAndReviewFinder() {
    super();
    lock = new ReentrantReadWriteLock();
  }

  /**
   * HotelAndReviewFinder for hotel using hotel id
   *
   * @param id the hotel id return the hotel
   * @return the hotel
   */
  @Override
  public Hotel findHotel(String id) {
    lock.readLock().lock();
    try {
      return super.findHotel(id);
    } finally {
      lock.readLock().unlock();
    }
  }

  /**
   * Find reviews for a hotel
   *
   * @param id of the hotel
   * @return list of reviews
   */
  @Override
  public List<Review> findReviews(String id) {
    lock.readLock().lock();
    try {
      return super.findReviews(id);
    } finally {
      lock.readLock().unlock();
    }
  }

  /**
   * Find all the reviews that contain the word
   *
   * @param word to hotelAndReviewFinder in reviews
   * @return List of reviews that contain the word
   */
  @Override
  public List<Review> findWord(String word) {
    lock.readLock().lock();
    try {
      return super.findWord(word);
    } finally {
      lock.readLock().unlock();
    }
  }

  /**
   * Add a hotel to the hotel map
   *
   * @param hotel the hotel to add
   */
  @Override
  public void addHotel(Hotel hotel) {
    lock.writeLock().lock();
    try {
      super.addHotel(hotel);
    } finally {
      lock.writeLock().unlock();
    }
  }

  /**
   * Adds hotels to hotel map
   *
   * @param hotels the hotels to add
   */
  @Override
  public void addHotels(Hotel[] hotels) {
    lock.writeLock().lock();
    try {
      super.addHotels(hotels);
    } finally {
      lock.writeLock().unlock();
    }
  }

  /**
   * Add a review to the review map
   *
   * @param review the review to add
   */
  @Override
  public void addReview(Review review) {
    lock.writeLock().lock();
    try {
      super.addReview(review);
    } finally {
      lock.writeLock().unlock();
    }
  }

  /**
   * Adds reviews to review map
   *
   * @param reviews the reviews to add
   */
  @Override
  public void addReviews(List<Review> reviews) {
    lock.writeLock().lock();
    try {
      super.addReviews(reviews);
    } finally {
      lock.writeLock().unlock();
    }
  }

  /**
   * Delete reviews for a hotel
   *
   * @param review review to delete
   */
  @Override
  public void deleteReview(Review review) {
    lock.writeLock().lock();
    try {
      super.deleteReview(review);
    } finally {
      lock.writeLock().unlock();
    }
  }

  /**
   * Get all the reviews
   *
   * @return list of reviews
   */
  @Override
  public List<Review> getReviews() {
    lock.readLock().lock();
    try {
      return super.getReviews();
    } finally {
      lock.readLock().unlock();
    }
  }

  /**
   * Sets the inverted index to use
   *
   * @param invertedIndex the inverted index
   */
  @Override
  public void setInvertedIndex(InvertedIndex invertedIndex) {
    lock.writeLock().lock();
    try {
      super.setInvertedIndex(invertedIndex);
    } finally {
      lock.writeLock().unlock();
    }
  }

  /**
   * Get the hotel map
   *
   * @return the hotel map
   */
  @Override
  public Map<String, Hotel> getHotelMap() {
    lock.readLock().lock();
    try {
      return super.getHotelMap();
    } finally {
      lock.readLock().unlock();
    }
  }

  /**
   * Get the review map
   *
   * @return the review map
   */
  @Override
  public Map<String, Set<Review>> getReviewMap() {
    lock.readLock().lock();
    try {
      return super.getReviewMap();
    } finally {
      lock.readLock().unlock();
    }
  }

  /**
   * Get the Hotels
   *
   * @return set of hotels
   */
  @Override
  public Set<Hotel> getHotels() {
    lock.readLock().lock();
    try {
      return super.getHotels();
    } finally {
      lock.readLock().unlock();
    }
  }

  /**
   * Find a specific review
   *
   * @param hotelId hotel id of the review
   * @param reviewId review id of the review
   * @return the review
   */
  @Override
  public Review findReview(String hotelId, String reviewId) {
    lock.readLock().lock();
    try {
      return super.findReview(hotelId, reviewId);
    } finally {
      lock.readLock().unlock();
    }
  }
}
