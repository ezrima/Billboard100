import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class BillboardNumberOnes {

  private final String serverName = "localhost";

  private final int portNumber = 3306;

  private final String dbName = "billboardnumberones";

  private Connection getConnection() throws SQLException {
    Connection conn = null;
    Properties connectionProps = new Properties();
    Scanner scan = new Scanner(System.in);
    System.out.println("Enter your username: ");
    String input = scan.next();
    connectionProps.put("user", input);
    System.out.println("Enter your password: ");
    input = scan.next();
    connectionProps.put("password", input);
    try {
      conn = DriverManager.getConnection("jdbc:mysql://" + this.serverName + ":"
              + this.portNumber + "/" + this.dbName
              + "?characterEncoding=UTF-8&useSSL=false", connectionProps);
    } catch (SQLException e) {
      System.out.println("Invalid login.");
    }
    return conn;
  }

  private void run() {
    Connection conn = null;
    try {
      conn = this.getConnection();
      System.out.println("Connected to database");
      conn.setSchema("billboardnumberones");
      this.promptForCRUD(conn);
    } catch (SQLException e) {
      System.out.println("ERROR: Could not connect to the database");
      e.printStackTrace();
    }
  }

  private void promptForCRUD(Connection conn) throws SQLException {
    Scanner scan = new Scanner(System.in);
    System.out.println("Choose an operation:\ncreate | adds a new entry to the database\n" +
            "read | renders a read-only view of the database\n" +
            "update | modifies an existing entry in the database\n" +
            "delete | removes an existing entry from the database\n" +
            "exit | exits the program");
    String operation = scan.next();
    switch (operation) {
      case "create":
        this.createInDatabase(conn);
        break;
      case "read":
        this.readDatabase(conn);
        break;
      case "update":
        this.modifyDatabase(conn);
        break;
      case "delete":
        this.deleteFromDatabase(conn);
        break;
      case "exit":
        conn.close();
        System.out.println("Disconnected from database.");
        break;
      default:
        System.out.println("Invalid operation: " + operation + "\n");
        this.promptForCRUD(conn);
        break;
    }
  }

  private void readDatabase(Connection conn) throws SQLException {
    Scanner scan = new Scanner(System.in);
    System.out.println("Choose a view:\nsingles | view the list of singles\n" +
            "artists | view the list of artists\n" +
            "record labels | view the list of record labels");
    String operation = scan.nextLine();
    switch (operation) {
      case "singles":
        this.readSingles(conn);
        break;
      case "artists":
        this.readArtists(conn);
        break;
      case "record labels":
        this.readRecordLabels(conn);
        break;
      default:
        System.out.println("Invalid view: " + operation + "\n");
        this.readDatabase(conn);
        break;
    }
  }

  private void readSingles(Connection conn) throws SQLException {
    Scanner scan = new Scanner(System.in);
    String sql = ("CALL get_singles");
    CallableStatement callableStatement = conn.prepareCall(sql);
    ResultSet rs = callableStatement.executeQuery();
    StringBuilder output = new StringBuilder();
    while (rs.next()) {
      output.append("Year: ").append(rs.getString("year")).append(" | ")
              .append("Artist: ").append(rs.getString("artist")).append(" | ")
              .append("A-Side: ").append(rs.getString("aSide")).append(" | ")
              .append("B-Side: ").append(rs.getString("bSide")).append(" | ")
              .append("Record Label: ").append(rs.getString("recordLabel"))
              .append("\n\n");
    }
    System.out.println(output);
    callableStatement.close();
    System.out.println("Choose an operation:\nfilter | apply a filter to the list of singles\n" +
            "play | plays a specified song in Spotify (Must login in browser window)");
    String operation = scan.next();
    switch (operation) {
      case "filter":
        this.filterSingles(conn);
        break;
      case "play":
        this.playSingle(conn);
        break;
      default:
        System.out.println("Invalid operation: " + operation + "\n");
        this.readSingles(conn);
        break;
    }
  }

  private void filterSingles(Connection conn) throws SQLException {
    Scanner scan = new Scanner(System.in);
    System.out.println("Choose an operation:\nartist | show singles by a specified artist\n" +
            "record label | show singles with a specified record label\n");
    String filterType = scan.nextLine();
    switch (filterType) {
      case "artist":
        System.out.println("Choose an artist:\n");
        String artist = scan.nextLine();

        String sql = ("CALL filter_singles_artist(?)");
        CallableStatement callableStatement = conn.prepareCall(sql);
        callableStatement.setString(1, artist);
        ResultSet rs = callableStatement.executeQuery();
        StringBuilder output = new StringBuilder();
        while (rs.next()) {
          output.append("Year: ").append(rs.getString("year")).append(" | ")
                  .append("Artist: ").append(rs.getString("artist")).append(" | ")
                  .append("A-Side: ").append(rs.getString("aSide")).append(" | ")
                  .append("B-Side: ").append(rs.getString("bSide")).append(" | ")
                  .append("Record Label: ").append(rs.getString("recordLabel"))
                  .append("\n\n");
        }
        System.out.println(output);
        callableStatement.close();
        System.out.println("Choose an operation:\nfilter | apply a filter to the list of singles\n" +
                "play | plays a specified song in Spotify (Must login in browser window)");
        String operation = scan.next();
        switch (operation) {
          case "filter":
            this.filterSingles(conn);
            break;
          case "play":
            this.playSingle(conn);
            break;
          default:
            System.out.println("Invalid operation: " + operation + "\n");
            this.readSingles(conn);
            break;
        }
        break;
      case "record label":
        scan = new Scanner(System.in);
        System.out.println("Choose a record label:\n");
        String recordLabel = scan.nextLine();
        sql = ("CALL filter_singles_record_label(?)");
        callableStatement = conn.prepareCall(sql);
        callableStatement.setString(1, recordLabel);
        rs = callableStatement.executeQuery();
        output = new StringBuilder();
        while (rs.next()) {
          output.append("Year: ").append(rs.getString("year")).append(" | ")
                  .append("Artist: ").append(rs.getString("artist")).append(" | ")
                  .append("A-Side: ").append(rs.getString("aSide")).append(" | ")
                  .append("B-Side: ").append(rs.getString("bSide")).append(" | ")
                  .append("Record Label: ").append(rs.getString("recordLabel"))
                  .append("\n\n");
        }
        System.out.println(output);
        callableStatement.close();
        System.out.println("Choose an operation:\nfilter | apply a filter to the list of singles\n" +
                "play | plays a specified song in Spotify (Must login in browser window)");
        String operation2 = scan.next();
        switch (operation2) {
          case "filter":
            this.filterSingles(conn);
            break;
          case "play":
            this.playSingle(conn);
            break;
          default:
            System.out.println("Invalid operation: " + operation2 + "\n");
            this.readSingles(conn);
            break;
        }
        break;
      default:
        System.out.println("Invalid operation.\n");
        this.readSingles(conn);
        break;
    }

  }

  private void playSingle(Connection conn) throws SQLException {
    Scanner scan = new Scanner(System.in);
    String sql = ("SELECT title FROM song");
    Statement statement = conn.createStatement();
    ResultSet rs = statement.executeQuery(sql);
    List<String> songs = new ArrayList<>();
    while (rs.next()) {
      songs.add(rs.getString("title"));
    }
    System.out.println("Enter a song title:");
    StringBuilder userSong = new StringBuilder();
    userSong.append(scan.nextLine());
    statement.close();
    if (songs.contains(userSong.toString())) {
      sql = ("SELECT spotifyLink FROM song WHERE title = ?");
      CallableStatement callableStatement = conn.prepareCall(sql);
      callableStatement.setString(1, userSong.toString());
      rs = callableStatement.executeQuery();
      rs.next();
      String link = rs.getString(1);
      System.out.println(link);
      callableStatement.close();
      try {
        Runtime rt = Runtime.getRuntime();
        rt.exec("rundll32 url.dll,FileProtocolHandler " + link);
      } catch (Exception E) {
        System.out.println("Spotify link is broken.");
      }
    } else {
      System.out.println("Invalid song title.");
      this.playSingle(conn);
    }
    this.promptForCRUD(conn);
  }

  private void readArtists(Connection conn) throws SQLException {
    Scanner scan = new Scanner(System.in);
    String sql = ("SELECT * FROM artist");
    Statement statement = conn.createStatement();
    ResultSet rs = statement.executeQuery(sql);
    StringBuilder output = new StringBuilder();
    while (rs.next()) {
      output.append("Name: ").append(rs.getString("name")).append("\n\n");
    }
    System.out.println(output);
    statement.close();

    System.out.println("Choose a view:\nmusicians | view the list of musicians\n" +
            "bands | view the list of bands");
    String operation = scan.next();
    switch (operation) {
      case "musicians":
        this.readMusicians(conn);
        break;
      case "bands":
        this.readBands(conn);
        break;
      default:
        System.out.println("Invalid operation: " + operation + "\n");
        this.readArtists(conn);
        break;
    }

  }

  private void readRecordLabels(Connection conn) throws SQLException {
    String sql = ("SELECT * FROM recordlabel");
    Statement statement = conn.createStatement();
    ResultSet rs = statement.executeQuery(sql);
    StringBuilder output = new StringBuilder();
    while (rs.next()) {
      output.append("Name: ").append(rs.getString("name")).append(" | ")
              .append("Founded: ").append(rs.getString("yearFounded")).append(" | ")
              .append("Country of Origin: ").append(rs.getString("countryOfOrigin"))
              .append("\n\n");
    }
    System.out.println(output);
    statement.close();
    this.promptForCRUD(conn);
  }

  private void readMusicians(Connection conn) throws SQLException {
    String sql = ("CALL get_musicians()");
    CallableStatement callableStatement = conn.prepareCall(sql);
    ResultSet rs = callableStatement.executeQuery();
    StringBuilder output = new StringBuilder();
    while (rs.next()) {
      output.append("Name: ").append(rs.getString("name")).append(" | ")
              .append("Birthday: ").append(rs.getString("dateOfBirth")).append(" | ")
              .append("Died: ").append(rs.getString("dateOfDeath")).append(" | ")
              .append("Instrument: ").append(rs.getString("primaryInstrument"))
              .append("\n\n");
    }
    System.out.println(output);
    callableStatement.close();
    this.promptForCRUD(conn);
  }

  private void readBands(Connection conn) throws SQLException {
    String sql = ("CALL get_bands()");
    CallableStatement callableStatement = conn.prepareCall(sql);
    ResultSet rs = callableStatement.executeQuery();
    StringBuilder output = new StringBuilder();
    while (rs.next()) {
      output.append("Name: ").append(rs.getString("name")).append(" | ")
              .append("Formed: ").append(rs.getString("yearOfOrigin")).append(" | ")
              .append("Origin City: ").append(rs.getString("cityOfOrigin")).append(" | ")
              .append("Genre: ").append(rs.getString("primaryGenre")).append(" | ")
              .append("Active: ").append(rs.getString("isActive"))
              .append("\n\n");
    }
    System.out.println(output);
    callableStatement.close();
    this.promptForCRUD(conn);
  }

  private void modifyDatabase(Connection conn) throws SQLException {
    Scanner scan = new Scanner(System.in);
    System.out.println("Choose a table:\n" +
            "artists | modify an artist\n" +
            "record labels | modify a record label");
    String table = scan.nextLine();
    switch (table) {
      case "artists":
        this.modifyArtist(conn);
        break;
      case "record labels":
        this.modifyRecordLabel(conn);
        break;
      default:
        System.out.println("Invalid table: " + table + "\n");
        this.promptForCRUD(conn);
        break;
    }
  }

  private void modifyArtist(Connection conn) throws SQLException {
    Scanner scan = new Scanner(System.in);
    System.out.println("Choose the name of the artist to modify:\n");
    String oldName = scan.nextLine();
    System.out.println("Choose a new name:\n");
    String newName = scan.nextLine();
    String query = "UPDATE artist SET name = ? WHERE name = ?";
    PreparedStatement preparedStmt = conn.prepareStatement(query);
    preparedStmt.setString   (1, newName);
    preparedStmt.setString(2, oldName);
    preparedStmt.executeUpdate();
  }

  private void modifyRecordLabel(Connection conn) throws SQLException {
    Scanner scan = new Scanner(System.in);
    System.out.println("Choose the name of the record label to modify:\n");
    String oldName = scan.nextLine();
    System.out.println("Choose a new name:\n");
    String newName = scan.nextLine();
    String query = "UPDATE recordlabel SET name = ? WHERE name = ?";
    PreparedStatement preparedStmt = conn.prepareStatement(query);
    preparedStmt.setString   (1, newName);
    preparedStmt.setString(2, oldName);
    preparedStmt.executeUpdate();

    System.out.println("Choose a new year of founding:\n");
    String yearFounded = scan.nextLine();
    query = "UPDATE recordlabel SET yearFounded = ? WHERE name = ?";
    preparedStmt = conn.prepareStatement(query);
    preparedStmt.setString   (1, yearFounded);
    preparedStmt.setString(2, newName);
    preparedStmt.executeUpdate();

    System.out.println("Choose a new country of origin:\n");
    String countryOrigin = scan.nextLine();
    query = "UPDATE recordlabel SET countryOfOrigin = ? WHERE name = ?";
    preparedStmt = conn.prepareStatement(query);
    preparedStmt.setString   (1, countryOrigin);
    preparedStmt.setString(2, newName);
    preparedStmt.executeUpdate();
  }

  private void deleteFromDatabase(Connection conn) throws SQLException {
    Scanner scan = new Scanner(System.in);
    System.out.println("Choose a table:\nsingles | delete a single\n" +
            "artists | delete an artist\n" +
            "record labels | delete a record label");
    String table = scan.nextLine();
    switch (table) {
      case "singles":
        this.deleteSingle(conn);
        break;
      case "artists":
        this.deleteArtist(conn);
        break;
      case "record labels":
        this.deleteRecordLabel(conn);
        break;
      default:
        System.out.println("Invalid table: " + table + "\n");
        this.promptForCRUD(conn);
        break;
    }
  }

  private void deleteSingle(Connection conn) throws SQLException {
    Scanner scan = new Scanner(System.in);
    String sql = ("CALL get_singles");
    CallableStatement callableStatement = conn.prepareCall(sql);
    ResultSet rs = callableStatement.executeQuery();
    StringBuilder output = new StringBuilder();
    List<String> years = new ArrayList<>();
    while (rs.next()) {
      output.append("Year: ").append(rs.getString("year")).append(" | ")
              .append("Artist: ").append(rs.getString("artist")).append(" | ")
              .append("A-Side: ").append(rs.getString("aSide")).append(" | ")
              .append("B-Side: ").append(rs.getString("bSide")).append(" | ")
              .append("Record Label: ").append(rs.getString("recordLabel"))
              .append("\n\n");
      years.add(rs.getString("year"));
    }
    System.out.println(output);
    System.out.println("Enter the single's year to delete:");
    String userYear = scan.nextLine();
    callableStatement.close();
    if (years.contains(userYear)) {
      sql = ("DELETE FROM single WHERE year = ?");
      PreparedStatement preparedStatement = conn.prepareStatement(sql);
      preparedStatement.setString(1, userYear);
      preparedStatement.execute();
      System.out.println("Record deleted.");
      preparedStatement.close();
    } else {
      System.out.println("Invalid year.");
    }
    this.promptForCRUD(conn);
  }

  private void deleteArtist(Connection conn) throws SQLException {
    Scanner scan = new Scanner(System.in);
    String sql = ("SELECT name FROM artist");
    Statement statement = conn.createStatement();
    ResultSet rs = statement.executeQuery(sql);
    StringBuilder output = new StringBuilder();
    List<String> names = new ArrayList<>();
    while (rs.next()) {
      output.append(rs.getString("name")).append("\n\n");
      names.add(rs.getString("name"));
    }
    System.out.println(output);
    System.out.println("Enter the artist's name to delete:");
    String userName = scan.nextLine();
    statement.close();
    if (names.contains(userName)) {
      sql = ("DELETE FROM artist WHERE name = ?");
      PreparedStatement preparedStatement = conn.prepareStatement(sql);
      preparedStatement.setString(1, userName);
      preparedStatement.execute();
      System.out.println("Record deleted.");
      preparedStatement.close();
    } else {
      System.out.println("Invalid name: " + userName);
    }
    this.promptForCRUD(conn);
  }

  private void deleteRecordLabel(Connection conn) throws SQLException {
    Scanner scan = new Scanner(System.in);
    String sql = ("SELECT name FROM recordlabel");
    Statement statement = conn.createStatement();
    ResultSet rs = statement.executeQuery(sql);
    StringBuilder output = new StringBuilder();
    List<String> names = new ArrayList<>();
    while (rs.next()) {
      output.append(rs.getString("name")).append("\n\n");
      names.add(rs.getString("name"));
    }
    System.out.println(output);
    System.out.println("Enter the record label's name to delete:");
    String userName = scan.nextLine();
    statement.close();
    if (names.contains(userName)) {
      sql = ("DELETE FROM recordlabel WHERE name = ?");
      PreparedStatement preparedStatement = conn.prepareStatement(sql);
      preparedStatement.setString(1, userName);
      preparedStatement.execute();
      System.out.println("Record deleted.");
      preparedStatement.close();
    } else {
      System.out.println("Invalid name: " + userName);
    }
    this.promptForCRUD(conn);
  }

  private void createInDatabase(Connection conn) throws SQLException {
    Scanner scan = new Scanner(System.in);
    System.out.println("Choose a table:\nsingles | create a new single\n" +
            "artists | create a new artist\n" +
            "record labels | create a new record label");
    String table = scan.nextLine();
    switch (table) {
      case "singles":
        this.createSingle(conn);
        break;
      case "artists":
        this.createArtist(conn);
        break;
      case "record labels":
        this.createRecordLabel(conn);
        break;
      default:
        System.out.println("Invalid table: " + table + "\n");
        this.promptForCRUD(conn);
        break;
    }
  }

  private void createSingle(Connection conn) throws SQLException {
    Scanner scan = new Scanner(System.in);

    System.out.println("Enter the new single's year:");
    String singleYear = scan.nextLine();
    int singleYearInt = Integer.parseInt(singleYear);

    System.out.println("Enter the new single's A-Side song title:");
    String aSideTitle = scan.nextLine();
    int aSideID = 0;

    String sql = ("SELECT title FROM song");
    Statement statement = conn.createStatement();
    ResultSet rs = statement.executeQuery(sql);
    List<String> songNames = new ArrayList<>();
    while (rs.next()) {
      songNames.add(rs.getString("title"));
    }

    if (songNames.contains(aSideTitle)) {
      sql = ("SELECT get_song_id(?) AS song_id");
      PreparedStatement preparedStatement = conn.prepareStatement(sql);
      preparedStatement.setString(1, aSideTitle);
      rs = preparedStatement.executeQuery();
      while (rs.next()) {
        aSideID = rs.getInt("song_id");
      }
      preparedStatement.close();
    } else {
      this.createSong(conn);
      sql = ("SELECT get_song_id(?) AS song_id");
      PreparedStatement preparedStatement = conn.prepareStatement(sql);
      preparedStatement.setString(1, aSideTitle);
      rs = preparedStatement.executeQuery();
      while (rs.next()) {
        aSideID = rs.getInt("song_id");
      }
      preparedStatement.close();
    }

    System.out.println("Enter the new single's B-Side song title:");
    String bSideTitle = scan.nextLine();
    int bSideID = 0;

    if (songNames.contains(bSideTitle)) {
      sql = ("SELECT get_song_id(?) AS song_id");
      PreparedStatement preparedStatement = conn.prepareStatement(sql);
      preparedStatement.setString(1, bSideTitle);
      rs = preparedStatement.executeQuery();
      while (rs.next()) {
        bSideID = rs.getInt("song_id");
      }
      preparedStatement.close();
    } else {
      this.createSong(conn);
      sql = ("SELECT get_song_id(?) AS song_id");
      PreparedStatement preparedStatement = conn.prepareStatement(sql);
      preparedStatement.setString(1, bSideTitle);
      rs = preparedStatement.executeQuery();
      while (rs.next()) {
        bSideID = rs.getInt("song_id");
      }
      preparedStatement.close();
    }

    System.out.println("Enter the new single's record label name:");
    String recordLabelName = scan.nextLine();
    int recordLabelID = 0;

    sql = ("SELECT name FROM recordlabel");
    statement = conn.createStatement();
    rs = statement.executeQuery(sql);
    List<String> recordLabelNames = new ArrayList<>();
    while (rs.next()) {
      recordLabelNames.add(rs.getString("name"));
    }

    if (recordLabelNames.contains(recordLabelName)) {
      sql = ("SELECT get_record_label_id(?) AS record_label_id");
      PreparedStatement preparedStatement = conn.prepareStatement(sql);
      preparedStatement.setString(1, recordLabelName);
      rs = preparedStatement.executeQuery();
      while (rs.next()) {
        recordLabelID = rs.getInt("record_label_id");
      }
      preparedStatement.close();
    } else {
      this.createRecordLabel(conn);
      sql = ("SELECT get_record_label_id(?) AS record_label_id");
      PreparedStatement preparedStatement = conn.prepareStatement(sql);
      preparedStatement.setString(1, recordLabelName);
      rs = preparedStatement.executeQuery();
      while (rs.next()) {
        recordLabelID = rs.getInt("record_label_id");
      }
      preparedStatement.close();
    }

    String query = " insert into single (year, aSideID, bSideID, recordLabelID)"
            + " values (?, ?, ?, ?)";
    PreparedStatement preparedStmt = conn.prepareStatement(query);
    preparedStmt.setInt(1, singleYearInt);
    preparedStmt.setInt(2, aSideID);
    preparedStmt.setInt(3, bSideID);
    preparedStmt.setInt(4, recordLabelID);
    preparedStmt.execute();
    preparedStmt.close();

    System.out.println("Single successfully created.");
    this.promptForCRUD(conn);

  }

  private void createSong(Connection conn) throws SQLException {
    Scanner scan = new Scanner(System.in);

    System.out.println("Enter the new song's title:");
    String songTitle = scan.nextLine();

    System.out.println("Enter the new song's genre:");
    String songGenre = scan.nextLine();

    System.out.println("Enter the new songs's year:");
    String songYear = scan.nextLine();
    int songYearInt = 0;
    try {
      songYearInt = Integer.parseInt(songYear);
    } catch (NumberFormatException e) {
      System.out.println("Invalid year.");
      this.promptForCRUD(conn);
    }

    System.out.println("Enter the new song's length:");
    String songLength = scan.nextLine();

    System.out.println("Enter the new song's spotify link:");
    String songLink = scan.nextLine();

    System.out.println("Enter the new song's artist name:");
    String songArtistName = scan.nextLine();
    int songArtistID = 0;

    String sql = ("SELECT name FROM artist");
    Statement statement = conn.createStatement();
    ResultSet rs = statement.executeQuery(sql);
    List<String> artistNames = new ArrayList<>();
    while (rs.next()) {
      artistNames.add(rs.getString("name"));
    }

    if (artistNames.contains(songArtistName)) {
      sql = ("SELECT get_artist_id(?) AS artist_id");
      PreparedStatement preparedStatement = conn.prepareStatement(sql);
      preparedStatement.setString(1, songArtistName);
      rs = preparedStatement.executeQuery();
      while (rs.next()) {
        songArtistID = rs.getInt("artist_id");
      }
      preparedStatement.close();
    } else {
      this.createArtist(conn);
      sql = ("SELECT get_artist_id(?) AS artist_id");
      PreparedStatement preparedStatement = conn.prepareStatement(sql);
      preparedStatement.setString(1, songArtistName);
      rs = preparedStatement.executeQuery();
      while (rs.next()) {
        songArtistID = rs.getInt("artist_id");
      }
      preparedStatement.close();
    }

    String query = " insert into song (title, genre, year, length, spotifyLink, artistID)"
            + " values (?, ?, ?, ?, ?, ?)";
    PreparedStatement preparedStmt = conn.prepareStatement(query);
    preparedStmt.setString(1, songTitle);
    preparedStmt.setString(2, songGenre);
    preparedStmt.setInt(3, songYearInt);
    preparedStmt.setString(4, songLength);
    preparedStmt.setString(5, songLink);
    preparedStmt.setInt(6, songArtistID);
    preparedStmt.execute();
    preparedStmt.close();

    System.out.println("Song successfully created.");

  }

  private void createArtist(Connection conn) throws SQLException {
    Scanner scan = new Scanner(System.in);

    System.out.println("Enter the new artist's name:");
    String artistName = scan.nextLine();

    String sql = ("SELECT name FROM artist");
    Statement statement = conn.createStatement();
    ResultSet rs = statement.executeQuery(sql);
    List<String> artistNames = new ArrayList<>();
    while (rs.next()) {
      artistNames.add(rs.getString("name"));
    }
    int artistID = 0;
    if (artistNames.contains(artistName)) {
      System.out.println("Artist already exists.");
      this.promptForCRUD(conn);
    } else {

      String query = " insert into artist (name)"
              + " values (?)";
      PreparedStatement preparedStmt = conn.prepareStatement(query);
      preparedStmt.setString(1, artistName);
      preparedStmt.execute();
      preparedStmt.close();
      System.out.println("Artist successfully created.");

      sql = ("SELECT get_artist_id(?) AS artist_id");
      PreparedStatement preparedStatement = conn.prepareStatement(sql);
      preparedStatement.setString(1, artistName);
      preparedStatement.execute();
      preparedStatement.close();
      while (rs.next()) {
        artistID = rs.getInt("artist_id");
      }
    }

//    this.promptForMusicianBand(conn, artistID);

  }

//  private void promptForMusicianBand(Connection conn, int artistID) throws SQLException {
//    Scanner scan = new Scanner(System.in);
//    System.out.println("Enter a command:\nmusician | creates a new musician" +
//            "\nband | creates a new band");
//    String artistType = scan.nextLine();
//    switch (artistType) {
//      case "musician":
//        this.createMusician(conn, artistID);
//        break;
//      case "band":
//        this.createBand(conn, artistID);
//        break;
//      default:
//        System.out.println("Invalid artist type: " + artistType + "\n");
//        this.promptForMusicianBand(conn, artistID);
//        break;
//    }
//  }

//  private void createMusician(Connection conn, int artistID) throws SQLException {
//    Scanner scan = new Scanner(System.in);
//
//    System.out.println("Enter the new musician's instrument:");
//    String musicianInstrument = scan.nextLine();
//
//    String query = " insert into musician (musicianID, primaryInstrument)"
//            + " values (?, ?)";
//    PreparedStatement preparedStmt = conn.prepareStatement(query);
//    preparedStmt.setInt(1, artistID);
//    preparedStmt.setString(2, musicianInstrument);
//    preparedStmt.execute();
//    preparedStmt.close();
//
//    System.out.println("Song successfully created.");
//    this.promptForCRUD(conn);
//
//  }
//
//  private void createBand(Connection conn, int artistID) throws SQLException {
//    Scanner scan = new Scanner(System.in);
//
//    System.out.println("Enter the new band's year of origin:");
//    String bandYear = scan.nextLine();
//    int bandYearInt = Integer.parseInt(bandYear);
//
//    System.out.println("Enter the new band's city of origin:");
//    String bandCity = scan.nextLine();
//
//    System.out.println("Enter the new band's primary genre:");
//    String bandGenre = scan.nextLine();
//
//    String query = " insert into band (bandID, yearOfOrigin, cityOfOrigin, primaryGenre)"
//            + " values (?, ?, ?, ?)";
//    PreparedStatement preparedStmt = conn.prepareStatement(query);
//    preparedStmt.setInt(1, artistID);
//    preparedStmt.setInt(2, bandYearInt);
//    preparedStmt.setString(3, bandCity);
//    preparedStmt.setString(4, bandGenre);
//    preparedStmt.execute();
//    preparedStmt.close();
//
//    System.out.println("Band successfully created.");
//    this.promptForCRUD(conn);
//  }

  private void createRecordLabel(Connection conn) throws SQLException {
    Scanner scan = new Scanner(System.in);

    System.out.println("Enter the new record label's name:");
    String recordLabelName = scan.nextLine();

    System.out.println("Enter the new record label's year of origin:");
    String recordLabelYear = scan.nextLine();
    int recordLabelYearInt = Integer.parseInt(recordLabelYear);

    System.out.println("Enter the new record label's country of origin:");
    String recordLabelCountry = scan.nextLine();

    String query = " insert into recordlabel (name, yearFounded, countryOfOrigin)"
            + " values (?, ?, ?)";
    PreparedStatement preparedStmt = conn.prepareStatement(query);
    preparedStmt.setString(1, recordLabelName);
    preparedStmt.setInt(2, recordLabelYearInt);
    preparedStmt.setString(3, recordLabelCountry);
    preparedStmt.execute();
    preparedStmt.close();

    System.out.println("Record label successfully created.");
  }

  /**
   * Connect to the DB and do some stuff
   *
   * @param args
   */
  public static void main(String[] args) {
    BillboardNumberOnes app = new BillboardNumberOnes();
    app.run();
  }
}