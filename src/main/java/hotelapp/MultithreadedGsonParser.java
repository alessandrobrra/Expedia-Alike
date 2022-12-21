package hotelapp;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

public class MultithreadedGsonParser extends GsonParser {
  /** The executor service used */
  private final ExecutorService executorService;

  /** The phaser */
  private final Phaser phaser;

  /** The logger object */
  private final Logger logger = LogManager.getLogger();

  /**
   * Constructor for the MultithreadedGsonParser
   *
   * @param hotelAndReviewFinder the review finder
   * @param numThreads the number of threads to use
   */
  public MultithreadedGsonParser(HotelAndReviewFinder hotelAndReviewFinder, int numThreads) {
    super(hotelAndReviewFinder);
    executorService = Executors.newFixedThreadPool(numThreads);
    phaser = new Phaser();
  }

  @Override
  public void parseHotel(String hotelFile) throws FileNotFoundException {
    super.parseHotel(hotelFile);
  }

  @Override
  public void parseReviewDirectory(Path reviewDirectory) {
    try (DirectoryStream<Path> pathsInDirectory = Files.newDirectoryStream(reviewDirectory)) {
      for (Path filePath : pathsInDirectory) {
        if (Files.isDirectory(filePath)) {
          logger.debug("Directory found: " + filePath);
          parseReviewDirectory(filePath);
        } else if (filePath.toString().endsWith(".json")) {
          logger.debug("Starting thread for file " + filePath);
          Worker worker = new Worker(filePath, phaser);
          phaser.register();
          executorService.submit(worker);
        }
      }
    } catch (Exception e) {
      logger.error("Error parsing the file: " + e);
    } finally {
      logger.debug("Awaiting termination of threads for file " + reviewDirectory);
    }
  }

  /*
   Method to shut down the executor service
  */
  public void shutdown() {
    System.out.println("Shutting down the executor service");
    logger.debug("Shutting down executor service");
    phaser.awaitAdvance(phaser.getPhase());
    executorService.shutdown();
    try {
      executorService.awaitTermination(2, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /*
   Worker class to parse the reviews
  */
  public class Worker implements Runnable {
    // The file to parse
    private final Path reviewDir;

    public Worker(Path reviewDir, Phaser phaser) {
      this.reviewDir = reviewDir;
    }

    @Override
    public void run() {
      try (FileReader br = new FileReader(reviewDir.toString())) {
        JsonObject jsonObject =
            JsonParser.parseReader(br)
                .getAsJsonObject()
                .getAsJsonObject("reviewDetails")
                .getAsJsonObject("reviewCollection");
        JsonArray jsonArray = jsonObject.getAsJsonArray("review");

        List<Review> reviews = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        for (JsonElement element : jsonArray) {
          String hotelId = element.getAsJsonObject().get("hotelId").getAsString();
          String reviewId = element.getAsJsonObject().get("reviewId").getAsString();
          int ratingOverall = element.getAsJsonObject().get("ratingOverall").getAsInt();
          String title = element.getAsJsonObject().get("title").getAsString();
          String reviewText = element.getAsJsonObject().get("reviewText").getAsString();
          String username = element.getAsJsonObject().get("userNickname").getAsString();
          if (username.isBlank()) {
            username = "Anonymous";
          }
          String date = element.getAsJsonObject().get("reviewSubmissionTime").getAsString();
          LocalDate submissionDate = LocalDate.parse(date, formatter);

          Review review =
              new Review(
                  hotelId, reviewId, ratingOverall, title, reviewText, username, submissionDate);
          reviews.add(review);
        }
        hotelAndReviewFinder.addReviews(reviews);

      } catch (Exception e) {
        throw new IllegalArgumentException("Cannot findHotel the review file " + e.getMessage());
      } finally {
        phaser.arriveAndDeregister();
      }
    }
  }
}
