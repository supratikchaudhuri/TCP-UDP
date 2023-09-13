# Project 1

### UDP Client-Serve Communication

To set up a connectionless client server communication, please follow the steps in order: 

**Step 1**: Start the UDP server with the following code in the terminal, with **PORT** as a command line argument:
>`java -jar TCPServer.jar <PORT>`

**Step 2**: Start the UDP client with the following code in the terminal and pass **IP** and **PORT** as a command line argument
>`java -jar TCPClient.jar <IP> <PORT>`

You can also pass *localhost* as the **IP** parameter to start communication with the server in the same machine.

**Step 3:** Pass request from the client by following the instructions listed in the terminal.

**Note** Please make sure you have the `map.properties` file in the same folder as the executable jar files. 

---

### TCP Client-Serve Communication

To set up a connection-oriented (TCP) client server communication, please follow the steps in order:

**Step 1**: Start the TCP server with the following code in the terminal, with **PORT** as a command line argument:
>`java -jar ServerUDP.jar <PORT>`

**Step 2**: Start the UDP client with the following code in the terminal and pass **IP** and **PORT** as a command line arguments:
>`java -jar ClientUDP.jar <IP> <PORT>`

You can also pass *localhost* as the **IP** parameter to start communication with the server in the same machine.

**Step 3:** Pass request from the client by following the instructions listed in the terminal.

**Note** Please make sure you have the `map.properties` file in the same folder as the executable jar files.


---

### Interaction Rules with the server

1. The **GET** operation accepts a `string` as an input and returns the value associated with it in the map. If no value is associated, it returns `NULL`.
2. The **PUT** operation accepts 2 arguments, name a key and a value as `string`, and stores them in the map. In case the key already exits, it rewrites the value with the latest passed argument.
3. The **DELETE** operation deletes a key-value pair from the map. If the key does not exist,  the map remains unchanged. 

---

### Map characteristics: 

1. key-value pairs are case sensitive. 
2. key-value pairs are stored as`string`
3. keys and values are trimmed of trailing spaces before storing in the map. 
4. At the start of the program, following 5 key-value pairs are initially added:
 
| Key               | Value                                |   
------------------------|--------------------------------------|
   | MS                 | Computer Scince                      |
   | Firstname Lastname | John Doe                             |
   | hello              | world                                |
   | CS6650             | Building Scalable Distributed System |
   | BTC                | Bitcoin                              |