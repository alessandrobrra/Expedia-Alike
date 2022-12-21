package hotelapp;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/** Modified from the example of Prof. Engle */
public class DatabaseHandler {

  private static DatabaseHandler dbHandler =
      new DatabaseHandler("database.properties"); // singleton pattern
  private Properties config; // a "map" of properties
  private String uri = null; // uri to connect to mysql using jdbc
  private Random random = new Random(); // used in password  generation

  /**
   * DataBaseHandler is a singleton, we want to prevent other classes from creating objects of this
   * class using the constructor
   */
  private DatabaseHandler(String propertiesFile) {
    this.config = loadConfigFile(propertiesFile);
    this.uri =
        "jdbc:mysql://"
            + config.getProperty("hostname")
            + "/"
            + config.getProperty("username")
            + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    System.out.println("uri = " + uri);
  }

  /**
   * Returns the instance of the database handler.
   *
   * @return instance of the database handler
   */
  public static DatabaseHandler getInstance() {
    return dbHandler;
  }

  // Load info from config file database.properties
  public Properties loadConfigFile(String propertyFile) {
    Properties config = new Properties();
    try (FileReader fr = new FileReader(propertyFile)) {
      config.load(fr);
    } catch (IOException e) {
      System.out.println(e);
    }

    return config;
  }

  public void createTable() {
    Statement statement;
    try (Connection dbConnection =
        DriverManager.getConnection(
            uri, config.getProperty("username"), config.getProperty("password"))) {
      System.out.println("dbConnection successful");
      statement = dbConnection.createStatement();
      statement.executeUpdate(PreparedStatements.CREATE_USER_TABLE);
    } catch (SQLException ex) {
      System.out.println(ex);
    }
  }

  /**
   * Returns the hex encoding of a byte array.
   *
   * @param bytes - byte array to encode
   * @param length - desired length of encoding
   * @return hex encoded byte array
   */
  public static String encodeHex(byte[] bytes, int length) {
    BigInteger bigint = new BigInteger(1, bytes);
    String hex = String.format("%0" + length + "X", bigint);

    assert hex.length() == length;
    return hex;
  }

  /**
   * Calculates the hash of a password and salt using SHA-256.
   *
   * @param password - password to hash
   * @param salt - salt associated with user
   * @return hashed password
   */
  public static String getHash(String password, String salt) {
    String salted = salt + password;
    String hashed = salted;

    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      md.update(salted.getBytes());
      hashed = encodeHex(md.digest(), 64);
    } catch (Exception ex) {
      System.out.println(ex);
    }

    return hashed;
  }

  /**
   * Registers a new user, placing the username, password hash, and salt into the database.
   *
   * @param newuser - username of new user
   * @param newpass - password of new user
   */
  public boolean registerUser(String newuser, String newpass) {
    // Generate salt
    byte[] saltBytes = new byte[16];
    random.nextBytes(saltBytes);

    String usersalt = encodeHex(saltBytes, 32); // salt
    String passhash = getHash(newpass, usersalt); // hashed password

    PreparedStatement statement;
    try (Connection connection =
        DriverManager.getConnection(
            uri, config.getProperty("username"), config.getProperty("password"))) {
      System.out.println("dbConnection successful");
      try {
        statement = connection.prepareStatement(PreparedStatements.USER_SQL);
        statement.setString(1, newuser);
        ResultSet rs = statement.executeQuery();
        if (rs.next()) {
          return false;
        }

        statement = connection.prepareStatement(PreparedStatements.REGISTER_SQL);
        statement.setString(1, newuser);
        statement.setString(2, passhash);
        statement.setString(3, usersalt);
        statement.executeUpdate();
        statement.close();
        return true;
      } catch (SQLException e) {
        System.out.println(e);
      }
    } catch (SQLException ex) {
      System.out.println(ex);
    }
    return false;
  }

  public boolean checkUser(String username, String password) {
    PreparedStatement statement;
    try (Connection connection =
        DriverManager.getConnection(
            uri, config.getProperty("username"), config.getProperty("password"))) {
      System.out.println("dbConnection successful");
      try {
        statement = connection.prepareStatement(PreparedStatements.USER_SQL);
        statement.setString(1, username);
        ResultSet rs = statement.executeQuery();
        if (rs.next()) {
          statement = connection.prepareStatement(PreparedStatements.SALT_SQL);
          statement.setString(1, username);
          rs = statement.executeQuery();
          if (rs.next()) {
            String salt = rs.getString("usersalt");
            String hashed = getHash(password, salt);
            statement = connection.prepareStatement(PreparedStatements.AUTH_SQL);
            statement.setString(1, username);
            statement.setString(2, hashed);
            rs = statement.executeQuery();
            if (rs.next()) {
              statement.close();
              rs.close();
              return true;
            }
          }
        }
        statement.close();
        rs.close();
      } catch (SQLException e) {
        System.out.println(e);
      }
    } catch (SQLException ex) {
      System.out.println(ex);
    }
    return false;
  }

  public boolean updateLogin(String username, String loginTime) {
    PreparedStatement statement;
    try (Connection connection =
        DriverManager.getConnection(
            uri, config.getProperty("username"), config.getProperty("password"))) {
      System.out.println("dbConnection successful");
      try {
        statement = connection.prepareStatement(PreparedStatements.UPDATE_LOGIN_SQL);
        statement.setString(1, loginTime);
        statement.setString(2, username);
        statement.executeUpdate();
        statement.close();
        return true;
      } catch (SQLException e) {
        System.out.println(e);
      }
    } catch (SQLException ex) {
      System.out.println(ex);
    }
    return false;
  }

  public String getLastLogin(String username) {
    PreparedStatement statement;
    try (Connection connection =
        DriverManager.getConnection(
            uri, config.getProperty("username"), config.getProperty("password"))) {
      System.out.println("dbConnection successful");
      try {
        statement = connection.prepareStatement(PreparedStatements.GET_LOGIN_SQL);
        statement.setString(1, username);
        ResultSet rs = statement.executeQuery();
        if (rs.next()) {
          String lastLogin = rs.getString("lastLogin");
          statement.close();
          rs.close();
          return lastLogin;
        }
        statement.close();
        rs.close();
      } catch (SQLException e) {
        System.out.println(e);
      }
    } catch (SQLException ex) {
      System.out.println(ex);
    }
    return null;
  }

  public boolean addExpediaLink(String username, String hotelName, String link) {
    PreparedStatement statement;
    try (Connection connection =
        DriverManager.getConnection(
            uri, config.getProperty("username"), config.getProperty("password"))) {
      System.out.println("dbConnection successful");
      try {
        String userid = getUserId(username);
        statement = connection.prepareStatement(PreparedStatements.ADD_EXPEDIA_SQL);
        statement.setString(1, userid);
        statement.setString(2, username);
        statement.setString(3, hotelName);
        statement.setString(4, link);
        statement.executeUpdate();
        statement.close();
        return true;
      } catch (SQLException e) {
        System.out.println(e);
      }
    } catch (SQLException ex) {
      System.out.println(ex);
    }
    return false;
  }

  private String getUserId(String username) {
    PreparedStatement statement;
    try (Connection connection =
        DriverManager.getConnection(
            uri, config.getProperty("username"), config.getProperty("password"))) {
      System.out.println("dbConnection successful");
      try {
        statement = connection.prepareStatement(PreparedStatements.GET_USERID_SQL);
        statement.setString(1, username);
        ResultSet rs = statement.executeQuery();
        if (rs.next()) {
          String userid = rs.getString("userid");
          statement.close();
          rs.close();
          return userid;
        }
        statement.close();
        rs.close();
      } catch (SQLException e) {
        System.out.println(e);
      }
    } catch (SQLException ex) {
      System.out.println(ex);
    }
    return null;
  }

  public Set<Link> getExpediaLinks(String username) {
    PreparedStatement statement;
    Set<Link> links = new HashSet<>();
    try (Connection connection =
        DriverManager.getConnection(
            uri, config.getProperty("username"), config.getProperty("password"))) {
      System.out.println("dbConnection successful");
      try {
        statement = connection.prepareStatement(PreparedStatements.GET_EXPEDIA_SQL);
        statement.setString(1, username);
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
          String hotelName = rs.getString("hotelName");
          String link = rs.getString("link");
          links.add(new Link(hotelName, link));
        }
        statement.close();
        rs.close();
        return links;
      } catch (SQLException e) {
        System.out.println(e);
      }
    } catch (SQLException ex) {
      System.out.println(ex);
    }
    return null;
  }

  public boolean clearExpediaLinks(String username) {
    PreparedStatement statement;
    try (Connection connection =
        DriverManager.getConnection(
            uri, config.getProperty("username"), config.getProperty("password"))) {
      System.out.println("dbConnection successful");
      try {
        statement = connection.prepareStatement(PreparedStatements.CLEAR_EXPEDIA_SQL);
        statement.setString(1, username);
        statement.executeUpdate();
        statement.close();
        return true;
      } catch (SQLException e) {
        System.out.println(e);
      }
    } catch (SQLException ex) {
      System.out.println(ex);
    }
    return false;
  }

  public boolean addReview(Review review) {
    PreparedStatement statement;
    try (Connection connection =
        DriverManager.getConnection(
            uri, config.getProperty("username"), config.getProperty("password"))) {
      try {
        statement = connection.prepareStatement(PreparedStatements.ADD_REVIEW_SQL);
        statement.setString(1, review.getHotelId());
        statement.setString(2, review.getReviewId());
        statement.setInt(3, review.getRating());
        statement.setString(4, review.getReviewTitle());
        statement.setString(5, review.getReviewText());
        statement.setString(6, review.getReviewUsername());
        statement.setString(7, review.getDate().toString());
        statement.executeUpdate();
        statement.close();
        return true;
      } catch (SQLException e) {
        System.out.println(e);
      }
    } catch (SQLException ex) {
      System.out.println(ex);
    }
    return false;
  }

  public List<Review> getReviews(String hotelId) {
    PreparedStatement statement;
    List<Review> reviews = new ArrayList<>();
    try (Connection connection =
        DriverManager.getConnection(
            uri, config.getProperty("username"), config.getProperty("password"))) {
      System.out.println("dbConnection successful");
      try {
        statement = connection.prepareStatement(PreparedStatements.GET_REVIEWS_SQL);
        System.out.println(hotelId);
        statement.setString(1, hotelId);
        ResultSet rs = statement.executeQuery();
        int count = 0;
        while (rs.next()) {
          String reviewId = rs.getString("reviewId");
          int ratingOverall = rs.getInt("ratingOverall");
          String title = rs.getString("title");
          String text = rs.getString("reviewText");
          String username = rs.getString("username");
          String date = rs.getString("date");
          reviews.add(
              new Review(
                  hotelId, reviewId, ratingOverall, title, text, username, LocalDate.parse(date)));
          count++;
        }
        statement.close();
        rs.close();
        reviews.sort(new Review.ReviewDateComparator());
        return reviews;
      } catch (SQLException e) {
        System.out.println(e.getMessage());
      }
    } catch (SQLException ex) {
      System.out.println(ex);
    }
    return null;
  }

  public Review getReview(String hotelId, String reviewId) {
    PreparedStatement statement;
    try (Connection connection =
        DriverManager.getConnection(
            uri, config.getProperty("username"), config.getProperty("password"))) {
      System.out.println("dbConnection successful");
      try {
        statement = connection.prepareStatement(PreparedStatements.GET_REVIEW_SQL);
        statement.setString(1, hotelId);
        statement.setString(2, reviewId);
        ResultSet rs = statement.executeQuery();
        if (rs.next()) {
          int ratingOverall = rs.getInt("ratingOverall");
          String title = rs.getString("title");
          String text = rs.getString("reviewText");
          String username = rs.getString("username");
          String date = rs.getString("date");
          statement.close();
          rs.close();
          return new Review(
              hotelId, reviewId, ratingOverall, title, text, username, LocalDate.parse(date));
        }
        statement.close();
        rs.close();
      } catch (SQLException e) {
        System.out.println(e);
      }
    } catch (SQLException ex) {
      System.out.println(ex);
    }
    return null;
  }

  public void deleteReview(Review review) {
    PreparedStatement statement;
    try (Connection connection =
        DriverManager.getConnection(
            uri, config.getProperty("username"), config.getProperty("password"))) {
      try {
        statement = connection.prepareStatement(PreparedStatements.DELETE_REVIEW_SQL);
        statement.setString(1, review.getHotelId());
        statement.setString(2, review.getReviewId());
        statement.executeUpdate();
        statement.close();
      } catch (SQLException e) {
        System.out.println(e);
      }
    } catch (SQLException ex) {
      System.out.println(ex);
    }
  }

  public void updateReview(Review review) {
    PreparedStatement statement;
    try (Connection connection =
        DriverManager.getConnection(
            uri, config.getProperty("username"), config.getProperty("password"))) {
      try {
        statement = connection.prepareStatement(PreparedStatements.EDIT_REVIEW_SQL);
        statement.setString(1, review.getReviewTitle());
        statement.setInt(2, review.getRating());
        statement.setString(3, review.getReviewText());
        statement.setString(4, review.getHotelId());
        statement.setString(5, review.getReviewId());
        statement.executeUpdate();
        statement.close();
      } catch (SQLException e) {
        System.out.println(e);
      }
    } catch (SQLException ex) {
      System.out.println(ex);
    }
  }

  public void addHotel(Hotel hotel) {
    PreparedStatement statement;
    try (Connection connection =
        DriverManager.getConnection(
            uri, config.getProperty("username"), config.getProperty("password"))) {
      try {
        statement = connection.prepareStatement(PreparedStatements.ADD_HOTEL_SQL);
        statement.setString(1, hotel.getHotelName());
        statement.setInt(2, hotel.getId());
        statement.setString(3, hotel.getHotelLat());
        statement.setString(4, hotel.getHotelLon());
        statement.setString(5, hotel.getHotelAddress());
        statement.setString(6, hotel.getHotelCity());
        statement.setString(7, hotel.getHotelState());
        statement.setString(8, hotel.getHotelCountry());
        statement.executeUpdate();
        statement.close();
      } catch (SQLException e) {
        System.out.println(e);
      }
    } catch (SQLException ex) {
      System.out.println(ex);
    }
  }

  public Set<Hotel> getHotels() {
    PreparedStatement statement;
    Set<Hotel> hotels = new TreeSet<>(Comparator.comparing(Hotel::getHotelName));
    try (Connection connection =
        DriverManager.getConnection(
            uri, config.getProperty("username"), config.getProperty("password"))) {
      try {
        statement = connection.prepareStatement(PreparedStatements.GET_HOTELS_SQL);
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
          String hotelName = rs.getString("name");
          int hotelId = rs.getInt("id");
          String hotelLat = rs.getString("latitude");
          String hotelLon = rs.getString("longitude");
          String hotelAddress = rs.getString("address");
          String hotelCity = rs.getString("city");
          String hotelState = rs.getString("state");
          String hotelCountry = rs.getString("country");
          hotels.add(
              new Hotel(
                  hotelName,
                  hotelId,
                  new Hotel.Coordinates(Double.parseDouble(hotelLat), Double.parseDouble(hotelLon)),
                  hotelAddress,
                  hotelCity,
                  hotelState,
                  hotelCountry));
        }
        statement.close();
        rs.close();
        return hotels;
      } catch (SQLException e) {
        System.out.println(e);
      }
    } catch (SQLException ex) {
      System.out.println(ex);
    }
    return null;
  }
}
