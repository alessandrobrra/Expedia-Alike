package hotelapp;

import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ThreadSafeInvertedIndex extends InvertedIndex {
  /** The lock */
  private ReentrantReadWriteLock lock;

  /** Constructor for the thread safe inverted index */
  public ThreadSafeInvertedIndex() {
    super();
    lock = new ReentrantReadWriteLock();
  }

  /**
   * Add a review to the inverted index
   *
   * @param word the word to add
   * @param review the review to add
   */
  @Override
  public void add(String word, Review review, int count) {
    lock.writeLock().lock();
    try {
      super.add(word, review, count);
    } finally {
      lock.writeLock().unlock();
    }
  }

  /**
   * Get the inverted index
   *
   * @return the inverted index
   */
  @Override
  public Map<String, Map<Review, Integer>> getInvertedIndex() {
    lock.readLock().lock();
    try {
      return super.getInvertedIndex();
    } finally {
      lock.readLock().unlock();
    }
  }
}
