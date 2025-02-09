
package net.salesianos.server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HangmanServer {
  private static final int PORT = 5000;
  private final Map<String, String[]> wordCategories;
  private final ExecutorService executorService;
  private static final Logger logger = Logger.getLogger(HangmanServer.class.getName());

  public HangmanServer() {
    wordCategories = createWordCategories();
    executorService = Executors.newFixedThreadPool(10);
  }

  private Map<String, String[]> createWordCategories() {
    Map<String, String[]> categories = new HashMap<>();
    categories.put("Tecnología", new String[]{"PC", "PROGRAMACION", "SOCKET", "JAVA", "PYTHON", "SQL", "HTML"});
    categories.put("Animales", new String[]{"PERRO", "ELEFANTE", "TIGRE", "CANGURO", "GATO", "LEOPARDO", "COCODRILO"});
    categories.put("Países", new String[]{"ARGENTINA", "ESPAÑA", "ALEMANIA", "BRASIL", "CANADA", "CHINA", "RUSIA"});
    return categories;
  }

  public void startServer() {
    System.out.println("Servidor de Ahorcado iniciado...");
    try (ServerSocket serverSocket = new ServerSocket(PORT)) {
      while (true) {
        Socket clientSocket = serverSocket.accept();
        logger.log(Level.INFO, "Cliente conectado: {0}", clientSocket.getInetAddress().getHostAddress());
        ClientHandler clientHandler = new ClientHandler(clientSocket, wordCategories);
        executorService.submit(clientHandler);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      executorService.shutdown();
    }
  }

  public static void main(String[] args) {
    HangmanServer server = new HangmanServer();
    server.startServer();
  }
}