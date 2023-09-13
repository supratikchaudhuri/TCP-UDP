import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Scanner;

/**
 *
 */
public class UDPClient {
  private void start(String name, int port) {
    Scanner sc = new Scanner(System.in);
    try {
      DatagramSocket clientSocket = new DatagramSocket();
      clientSocket.setSoTimeout(1000);
      InetAddress ip = InetAddress.getByName(name);

      System.out.println("All valid request formats:\n\n1. GET x\n2. PUT x y\n3. DELETE x\nType stop to exit");

      while (true) {
        System.out.println();
        try {
          byte[] sendData;
          byte[] receiveData = new byte[1024];

          String sentence = sc.nextLine();
          sendData = sentence.getBytes();

          DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, port);
          clientSocket.send(sendPacket);

          if (sentence.trim().equalsIgnoreCase("stop")) {
            break;
          }

          DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
          clientSocket.receive(receivePacket);
          System.out.println("res : " + new String(receivePacket.getData()));
        } catch (SocketTimeoutException e) {
          System.out.println(e.getMessage() + ". Check server log for possible explanation.");
        } catch (IOException e) {
          System.out.println(e.getMessage());
        }
      }
      clientSocket.close();
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    try {
      UDPClient ob = new UDPClient();
      if (args.length != 2) {
        throw new IllegalArgumentException("Invalid number of arguments. Should be exactly 2.");
      }

      int port = Integer.parseInt(args[1]);
      if (port < 0 || port > 65535) {
        throw new IllegalArgumentException("Invalid port number. Must be in range 0-65535.");
      }

      ob.start(args[0], port);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }
}
