import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 *
 */
public class UDPServer {
  private final String fileName = "contents.txt";
  private Map<String, String> map;
  private boolean reqStatus;

  private void populateMap() {
    map = new HashMap<>();
    File file = new File(fileName);
    try {
      Scanner fileReader = new Scanner(file);
      while (fileReader.hasNextLine()) {
        String line = fileReader.nextLine();
        String[] req = line.split("\\s+");
        map.put(req[0], req[1]);
      }
      fileReader.close();
    } catch (FileNotFoundException ignored) {
    }

  }

  private ValidationCode isValidRequest(String[] req) {
    req[0] = req[0].toUpperCase();
    for (int i = 0; i < req.length; i++) {
      req[i] = req[i].trim();
    }

    switch (req[0]) {
      case "GET":
      case "DELETE":
        return req.length == 2 ? ValidationCode.VALID_REQUEST_TYPE : ValidationCode.INCORRECT_PARAMETER_COUNT;

      case "PUT":
        return req.length == 3 ? ValidationCode.VALID_REQUEST_TYPE : ValidationCode.INCORRECT_PARAMETER_COUNT;

      default:
        return ValidationCode.INVALID_REQUEST_TYPE;
    }
  }

  private String handleRequest(String[] req) {
    reqStatus = false;

    switch (req[0]) {
      case "GET":
        if (map.containsKey(req[1])) {
          this.reqStatus = true;
          return map.get(req[1]);
        }
        return "Invalid request. Can't get key that doesn't exist.";

      case "PUT":
        map.put(req[1], req[2]);
        reqStatus = true;
        return "put successful";

      case "DELETE":
        if (map.containsKey(req[1])) {
          map.remove(req[1]);
          reqStatus = true;
          return "delete successful";
        }
        return "Invalid request. Can't delete key that doesnt exist.";

      default:
        return "never gonna happen";
    }
  }

  private void writeToFile() {
    try {
      File file = new File(fileName);
      boolean x = file.createNewFile();
      FileWriter fileWriter = new FileWriter(fileName);
      for (String key : map.keySet()) {
        fileWriter.write(key + " " + map.get(key) + "\n");
      }
      fileWriter.close();
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }

  private void start(int port) {
    populateMap();

    try {
      DatagramSocket serverSocket = new DatagramSocket(port);

      while (true) {
        System.out.println();
        try {
          byte[] receiveData = new byte[1024];
          byte[] sendData;

          DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
          serverSocket.receive(receivePacket);

          String request = new String(receivePacket.getData());
          System.out.println("req : " + request);

          if (request.trim().equalsIgnoreCase("stop")) {
            break;
          }
          String[] req = request.split("\\s+");

          InetAddress ip = receivePacket.getAddress();
          int clientPort = receivePacket.getPort();

          String res;
          ValidationCode validationCode = isValidRequest(req);
          if (validationCode == ValidationCode.VALID_REQUEST_TYPE) {
            res = handleRequest(req);
            if (!reqStatus) {
              long timestamp = System.currentTimeMillis();
              String msg = "timestamp: " + timestamp + ". " + res;
              System.out.println(msg);
            }
            sendData = res.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, clientPort);
            serverSocket.send(sendPacket);
          } else {
            long timestamp = System.currentTimeMillis();
            res = "timestamp: " + timestamp + ". Malformed request, ";
            if (validationCode == ValidationCode.INCORRECT_PARAMETER_COUNT) {
              res += "incorrect parameter count";
            } else {
              res += "invalid request type. Must be GET, PUT or DELETE only.";
            }
            System.out.println(res);
          }
        } catch (IOException e) {
          System.out.println(e.getMessage());
        }
      }
      writeToFile();
      serverSocket.close();
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   *
   * @param args
   */
  public static void main(String[] args) {
    try {
      UDPServer ob = new UDPServer();
      if (args.length != 1) {
        throw new IllegalArgumentException("Invalid number of arguments. Should be exactly 1.");
      }

      int port = Integer.parseInt(args[0]);
      if (port < 0 || port > 65535) {
        throw new IllegalArgumentException("Invalid port number. Must be in range 0-65535.");
      }

      ob.start(port);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   *
   */
  public enum ValidationCode {
    INCORRECT_PARAMETER_COUNT, INVALID_REQUEST_TYPE, VALID_REQUEST_TYPE
  }
}
