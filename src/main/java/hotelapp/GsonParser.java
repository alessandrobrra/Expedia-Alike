package hotelapp;

import com.google.gson.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** The class that parses JSON files */
public class GsonParser {

  /** The gson object */
  private final Gson gson;

  /** HotelAndReviewFinder object */
  protected final HotelAndReviewFinder hotelAndReviewFinder;

  /** Constructor for the GsonCreator */
  public GsonParser(HotelAndReviewFinder hotelAndReviewFinder) {
    gson = new Gson();
    this.hotelAndReviewFinder = hotelAndReviewFinder;
  }

  /**
   * Parse the hotel JSON file
   *
   * @param hotelFile the path to the hotel JSON file
   */
  public void parseHotel(String hotelFile) throws FileNotFoundException {
    try (FileReader br = new FileReader(hotelFile)) {
      JsonObject jsonObject = JsonParser.parseReader(br).getAsJsonObject();
      JsonArray jsonArray = jsonObject.getAsJsonArray("sr");

      Hotel[] hotels = gson.fromJson(jsonArray, Hotel[].class);
      hotelAndReviewFinder.addHotels(hotels);

    } catch (Exception e) {
      throw new FileNotFoundException("Cannot findHotel the hotel file " + hotelFile);
    }
  }

  /**
   * Parse the review JSON files in the given file
   *
   * @param reviewFile the JSON files containing reviews
   */
  public void parseReview(String reviewFile) {
    try (FileReader br = new FileReader(reviewFile)) {
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
    }
  }

  /**
   * Parse the review JSON directory
   *
   * @param reviewDirectory the path to the review JSON directory
   */
  public void parseReviewDirectory(Path reviewDirectory) throws NotDirectoryException {
    try (DirectoryStream<Path> pathsInDirectory = Files.newDirectoryStream(reviewDirectory)) {
      for (Path filePath : pathsInDirectory) {
        if (Files.isDirectory(filePath)) {
          parseReviewDirectory(filePath);
        } else if (filePath.toString().endsWith(".json")) {
          parseReview(filePath.toString());
        }
      }
    } catch (Exception e) {
      throw new NotDirectoryException("Cannot findHotel the review directory " + e.getMessage());
    }
  }
}
