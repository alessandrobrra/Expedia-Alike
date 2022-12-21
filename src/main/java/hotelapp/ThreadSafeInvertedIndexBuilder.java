package hotelapp;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ThreadSafeInvertedIndexBuilder extends InvertedIndexBuilder {
  /** The lock */
  private final ReentrantReadWriteLock lock;

  /**
   * Constructor for the thread safe inverted index builder
   *
   * @param invertedIndex the inverted index to build
   */
  public ThreadSafeInvertedIndexBuilder(InvertedIndex invertedIndex) {
    super(invertedIndex);
    lock = new ReentrantReadWriteLock();
  }

  @Override
  public void populateStopWords(String stopWordsFile) {
    lock.writeLock().lock();
    try {
      super.populateStopWords(stopWordsFile);
    } finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  public void addReviews(List<Review> reviews) {
    lock.writeLock().lock();
    try {
      super.addReviews(reviews);
    } finally {
      lock.writeLock().unlock();
    }
  }
}
