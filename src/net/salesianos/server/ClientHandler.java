package net.salesianos.server;


import java.io.*;
import java.net.*;
import java.util.*;

class ClientHandler implements Runnable {
  private final Socket socket;
  private final Map<String, String[]> wordCategories;
  private String category;
  private String word;
  private char[] guessedWord;
  private int attemptsLeft = 6;
  private final Set<Character> guessedLetters = new HashSet<>();

  public ClientHandler(Socket socket, Map<String, String[]> wordCategories) {
    this.socket = socket;
    this.wordCategories = wordCategories;
    initializeGame();
  }

  private void initializeGame() {
    Random rand = new Random();
    List<String> categories = new ArrayList<>(wordCategories.keySet());
    category = categories.get(rand.nextInt(categories.size()));
    String[] words = wordCategories.get(category);
    word = words[rand.nextInt(words.length)];
    guessedWord = new char[word.length()];
    Arrays.fill(guessedWord, '_');
  }

  @Override
  public void run() {
    try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
    ) {
      out.println("Categoría: " + category);
      sendGameState(out);

      while (attemptsLeft > 0 && new String(guessedWord).contains("_")) {
        String guess = in.readLine();
        if (guess == null) {
          break; // Client disconnected
        }

        guess = guess.toUpperCase();
        if (!isValidGuess(guess)) {
          out.println("Entrada inválida o repetida.");
          continue;
        }

        processGuess(guess);
        sendGameState(out);
      }

      sendGameResult(out);

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        socket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private boolean isValidGuess(String guess) {
    return guess.length() == 1 && !guessedLetters.contains(guess.charAt(0));
  }

  private void processGuess(String guess) {
    guessedLetters.add(guess.charAt(0));
    if (word.contains(guess)) {
      for (int i = 0; i < word.length(); i++) {
        if (word.charAt(i) == guess.charAt(0)) {
          guessedWord[i] = guess.charAt(0);
        }
      }
    } else {
      attemptsLeft--;
    }
  }

  private void sendGameState(PrintWriter out) {
    out.println("Estado: " + new String(guessedWord) + " | Intentos restantes: " + attemptsLeft);
  }

  private void sendGameResult(PrintWriter out) {
    if (new String(guessedWord).equals(word)) {
      out.println("¡Ganaste! La palabra era: " + word);
    } else {
      out.println("Perdiste. La palabra era: " + word);
    }
  }
}