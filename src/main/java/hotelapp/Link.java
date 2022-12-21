package hotelapp;

public class Link {
  private final String hotelName;
  private final String expediaLink;

  public Link(String hotelName, String expediaLink) {
    this.hotelName = hotelName;
    this.expediaLink = expediaLink;
  }

  public String getHotelName() {
    return hotelName;
  }

  public String getExpediaLink() {
    return expediaLink;
  }

  @Override
  public String toString() {
    return hotelName + " " + expediaLink;
  }
}
