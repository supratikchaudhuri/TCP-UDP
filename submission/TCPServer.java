import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;

/**
 * Server Class for TCP communication. This class accepts 1 command line argument that tells the port on which the process is expected to run.
 * It provides services based on the client's request.
 */
class TCPServer {
  static OutputStream writer;
  static InputStream reader;
  static Properties prop;

  /**
   * Driver function for server.
   * @param args command line arguments
   * @throws Exception exception
   */
  public static void main(String[] args) throws Exception {
    try {
      // accept server PORT from command line, else throw error
      if (args.length != 1 || args[0].matches("[a-zA-Z]+") || Integer.parseInt(args[0]) > 65535) {
        throw new IllegalArgumentException("Invalid arguments. " +
                "Please provide just the PORT (0-65535) number and start again");
      }
      int PORT = Integer.parseInt(args[0]);

      ServerSocket ss = new ServerSocket(PORT);
      String start = getTimeStamp();
      System.out.println(start + " TCP Server started on port " + PORT);
      Socket clientSocket = ss.accept();
      DataInputStream din = new DataInputStream(clientSocket.getInputStream());
      DataOutputStream dout = new DataOutputStream(clientSocket.getOutputStream());

      reader = new FileInputStream("map.properties");
      prop = new Properties();
      prop.load(reader);

      writer = new FileOutputStream("map.properties", false);
      prop.store(writer, null);
      addToMap("hello", "world");
      addToMap("MS", "Computer Science");
      addToMap("CS6650", "Building Scalable Distributed System");
      addToMap("Firstname Lastname", "John Doe");
      addToMap("BTC", "Bitcoin");

      // keep communication channel open user keyboard interruption
      while (true) {
        String input = din.readUTF();
        requestLog(input, String.valueOf(clientSocket.getInetAddress()), String.valueOf(clientSocket.getPort()));

        String res = performAction(input.split(" "));
        responseLog(res);
        dout.writeUTF(res);
        dout.flush();
      }
    } catch (Exception e) {
      errorLog(e.getMessage());
    }

  }

  /**
   * helper method to print Request messages
   * @param s message string
   */
  private static void requestLog(String s, String ip, String port) {
    System.out.println(getTimeStamp() + " REQUEST from: " + ip + ":" + port  + " => "+ s);
  }

  /**
   * helper method to print Response messages
   * @param s message string
   */
  private static void responseLog(String s) { System.out.println(getTimeStamp() + " RESPONSE => " + s + "\n");}

  /**
   * helper method to print Error messages
   * @param err error message string
   */
  private static void errorLog(String err) { System.out.println(getTimeStamp() + " ERROR => " + err);}

  /**
   * helper method to return current timestamp
   */
  private static String getTimeStamp() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    return "[Time: " + sdf.format(new Date()) + "]";
  }

  /**
   * Helper method to process user request
   * @param input user request
   * @return PUT/GET/DELETE operation
   * @throws IllegalArgumentException in case of invalid input
   */
  private static String performAction(String[] input) throws IllegalArgumentException {
    try {
      String method = input[0].toUpperCase();
      String key = "";
      int j = 0;
      for(int i = 1; i < input.length; i++) {
        if(Objects.equals(input[i], "|")) {
          j = i;
          break;
        }
        else key = key + input[i] + " ";
      }
      key = key.trim();

      switch (method) {
        case "PUT": {
          String value = "";
          for(int i = j+1; i < input.length; i++) value = value + " " + input[i].trim();
          value = value.trim();
          return addToMap(key, value);
        }
        case "DELETE": {
          return deleteFromMap(key);
        }
        case "GET": {
          return getFromMap(key);
        }
        default:
          throw new IllegalArgumentException();
      }
    } catch (Exception e) {
      return "BAD REQUEST: Invalid operation, view README to check available operations.";
    }

  }

  /**
   * Fetches value associated to a key in the properties file
   * @param key who value we want to find
   * @return resposne of the operation
   * @throws IOException
   */
  private static String getFromMap(String key) throws IOException {
    String value = prop.getProperty(key);
    return value == null  || value.equals("~null~")?
            "No value found for key \"" + key + "\"" : "Key: \"" + key + "\" ,Value: \"" + value + "\"";
  }

  /**
   * Deletes a key-value pair from the properties file
   * @param key ot delete
   * @return response of the operation
   * @throws Exception
   */
  private static String deleteFromMap(String key) throws Exception {
    String res = "";
    if(prop.containsKey(key)) {
      addToMap(key, "~null~");
      prop.remove(key);
      prop.store(writer, null);
      res = "Deleted key \"" + key + "\"";
    }
    else {
      res = "Key not found.";
    }
    return res;
  }

  /**
   * Adds a key value pair to the properties file
   * @param key to insert
   * @param value to insert
   * @return Result of the operation
   * @throws Exception
   */
  static String addToMap(String key, String value) throws Exception {
    prop.setProperty(key, value);
    prop.store(writer, null);
    return "Inserted key \"" + key + "\" with value \"" + value + "\"";
  }
}


