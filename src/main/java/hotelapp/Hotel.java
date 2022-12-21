package hotelapp;

import com.google.gson.annotations.SerializedName;

public class Hotel {

  /** The name of the hotel */
  @SerializedName("f")
  private final String name;

  /** The id of the hotel */
  private final int id;

  /** The coordinates of the hotel */
  @SerializedName("ll")
  private final Coordinates coordinates;

  /** The address of the hotel */
  @SerializedName("ad")
  private final String address;

  /** The city of the hotel */
  @SerializedName("ci")
  private final String city;

  /** The state of the hotel */
  @SerializedName("pr")
  private final String state;

  /** The country of the hotel */
  @SerializedName("c")
  private final String country;


  /** Constructor for a hotel */
  public Hotel(
      String name,
      int id,
      Coordinates coordinates,
      String address,
      String city,
      String state,
      String country) {
    this.name = name;
    this.id = id;
    this.coordinates = coordinates;
    this.address = address;
    this.city = city;
    this.state = state;
    this.country = country;
  }

  /**
   * Get the id of the hotel
   *
   * @return the id
   */
  public int getId() {
    return id;
  }

  /**
   * Get the name of the hotel
   *
   * @return the name
   */
  public String getHotelName() {
    return name;
  }

  /**
   * Get the address of the hotel
   *
   * @return the address
   */
  public String getHotelAddress() {
    return address;
  }

  /**
   * Get the city of the hotel
   *
   * @return the city
   */
  public String getHotelCity() {
    return city;
  }

  /**
   * Get the state of the hotel
   *
   * @return the state
   */
  public String getHotelState() {
    return state;
  }

  /**
   * Get the latitude of the hotel
   *
   * @return the country
   */
  public String getHotelLat() {
    return Double.toString(coordinates.getLatitude());
  }

  /**
   * Get the longitude of the hotel
   *
   * @return the country
   */
  public String getHotelLon() {
    return Double.toString(coordinates.getLongitude());
  }

  /**
   * Get the country of the hotel
   *
   * @return the country
   */
  public String getHotelCountry() {
    return country;
  }

  @Override
  public String toString() {
    return "********************"
        + System.lineSeparator()
        + name
        + ": "
        + id
        + ""
        + System.lineSeparator()
        + address
        + ""
        + System.lineSeparator()
        + city
        + ", "
        + state;
  }

  public String expediaString() {
    //    https://www.expedia.com/San-Francisco-Hotels-Hotel-Kabuki.h1003.Hotel-Information
    String[] citySplit = city.split(" ");
    String[] nameSplit = name.split(" ");
    StringBuilder sb = new StringBuilder();
    sb.append("https://www.expedia.com/");
    for (String s : citySplit) {
      sb.append(s);
      sb.append("-");
    }
    sb.append("Hotels-");
    for (int i = 0; i < nameSplit.length; i++) {
      sb.append(nameSplit[i]);
      if (i != nameSplit.length - 1) {
        sb.append("-");
      }
    }
    sb.append(".h");
    sb.append(id);
    sb.append(".Hotel-Information");
    return sb.toString();
  }

  /** The coordinates of a hotel */
  public static class Coordinates {
    /** The latitude of the hotel */
    @SerializedName("lat")
    private final double latitude;

    /** The longitude of the hotel */
    @SerializedName("lng")
    private final double longitude;

    /**
     * Constructor for the coordinates of a hotel
     *
     * @param latitude the latitude of the hotel
     * @param longitude the longitude of the hotel
     */
    public Coordinates(double latitude, double longitude) {
      this.latitude = latitude;
      this.longitude = longitude;
    }

    public double getLatitude() {
      return latitude;
    }

    public double getLongitude() {
      return longitude;
    }

    @Override
    public String toString() {
      return "latitude: " + latitude + ", longitude: " + longitude;
    }
  }
}
