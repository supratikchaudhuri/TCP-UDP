import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;

/**
 * Server Class for UDP communication
 */
public class ServerUDP {
  static OutputStream writer;
  static InputStream reader;
  static Properties prop;

  /**
   * Drive function for the server
   * @param args command line arguments
   */
  public static void main(String[] args) throws IOException {
    try {
      // accept server PORT from command line, else throw error
      if(args.length != 1 || args[0].matches("[a-zA-Z]+") || Integer.parseInt(args[0]) > 65535) {
        throw new IllegalArgumentException("Invalid arguments. " +
                "Please provide just the PORT (0-65535) number and start again");
      }
      int PORT = Integer.parseInt(args[0]);

      DatagramSocket ds = new DatagramSocket(PORT);
      String start = getTimeStamp();
      System.out.println(start + " UDP Server started on port " + PORT);
      byte[] requestBuffer = new byte[512];
      byte[] responseBuffer;

      reader = new FileInputStream("map.properties");
      prop = new Properties();
      prop.load(reader);
      writer = new FileOutputStream("map.properties", false);
      addToMap("hello", "world");
      addToMap("MS", "Computer Science");
      addToMap("CS6650", "Building Scalable Distributed System");
      addToMap("Firstname Lastname", "John Doe");
      addToMap("BTC", "Bitcoin");

      // keep communication channel open until user keyboard interruption
      while (true) {
        DatagramPacket receivePacket = new DatagramPacket(requestBuffer, requestBuffer.length);
        ds.receive(receivePacket);
        InetAddress client = receivePacket.getAddress();
        int clientPort = receivePacket.getPort();
        String req = new String(receivePacket.getData());
        requestLog(req, client.toString(), String.valueOf(clientPort));

        try {
          String[] input = req.split(" ");
          String res = performAction(input);
          responseLog(res);
          responseBuffer = res.getBytes();

        } catch (IllegalArgumentException e) {
          String errorMsg = e.getMessage();
          errorLog(errorMsg);
          responseBuffer = errorMsg.getBytes();
        }

        DatagramPacket resPacket = new DatagramPacket(responseBuffer, responseBuffer.length, client, clientPort);
        ds.send(resPacket);
        requestBuffer = new byte[512];

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
   * @return result of PUT/GET/DELETE operation
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

  private static String getFromMap(String key) throws IOException {
    String value = prop.getProperty(key);
    return value == null || value.equals("~null~") ?
            "No value found for key \"" + key + "\"" : "Key: \"" + key + "\" ,Value: \"" + value + "\"";
  }

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

  static String addToMap(String key, String value) throws Exception {
    prop.setProperty(key, value);
    prop.store(writer, null);
    return "Inserted key \"" + key + "\" with value \"" + value + "\"";
  }
}
