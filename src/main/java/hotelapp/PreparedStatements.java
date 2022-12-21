package hotelapp;

public class PreparedStatements {
  /** Prepared Statements */
  /** For creating the users table */
  public static final String CREATE_USER_TABLE =
      "CREATE TABLE users ("
          + "userid INTEGER AUTO_INCREMENT PRIMARY KEY, "
          + "username VARCHAR(32) NOT NULL UNIQUE, "
          + "password CHAR(64) NOT NULL, "
          + "usersalt CHAR(32) NOT NULL);";

  /** Used to insert a new user into the database. */
  public static final String REGISTER_SQL =
      "INSERT INTO users (username, password, usersalt) " + "VALUES (?, ?, ?);";

  /** Used to determine if a username already exists. */
  public static final String USER_SQL = "SELECT username FROM users WHERE username = ?";

  /** Used to retrieve the salt associated with a specific user. */
  public static final String SALT_SQL = "SELECT usersalt FROM users WHERE username = ?";

  /** Used to authenticate a user. */
  public static final String AUTH_SQL =
      "SELECT username FROM users " + "WHERE username = ? AND password = ?";

  /** Used to update the last login of a user */
  public static final String UPDATE_LOGIN_SQL = "UPDATE users SET lastLogin = ? WHERE username = ?";

  /** Used to retrieve the salt associated with a specific user. */
  public static final String GET_LOGIN_SQL = "SELECT lastLogin FROM users WHERE username = ?";

  public static final String ADD_EXPEDIA_SQL =
      "INSERT INTO expedia_history (userid, username, hotelName,link) VALUES (?, ?, ?, ?);";

  public static final String GET_USERID_SQL = "SELECT userid FROM users WHERE username = ?";

  public static final String GET_EXPEDIA_SQL =
      "SELECT hotelName,link FROM expedia_history WHERE username = ?";

  public static final String CLEAR_EXPEDIA_SQL = "DELETE FROM expedia_history WHERE username = ?";

  public static final String ADD_REVIEW_SQL =
      "INSERT INTO reviews (hotelId, reviewId, ratingOverall, title, reviewText, username, date) VALUES (?, ?, ?, ?, ?, ?, ?);";

  public static final String GET_REVIEWS_SQL = "SELECT * FROM reviews WHERE hotelId = ?";

  public static final String GET_REVIEW_SQL =
      "SELECT * FROM reviews WHERE hotelId = ? AND reviewId = ?";

  public static final String DELETE_REVIEW_SQL =
      "DELETE FROM reviews WHERE hotelId = ? AND reviewId = ?";

  public static final String EDIT_REVIEW_SQL =
      "UPDATE reviews SET title = ?, ratingOverall = ?, reviewText = ? WHERE hotelId = ? AND reviewId = ?";

  public static final String ADD_HOTEL_SQL =
      "INSERT INTO hotels (name, id, latitude, longitude, address, city, state, country) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

  public static final String GET_HOTELS_SQL = "SELECT * FROM hotels";
}
