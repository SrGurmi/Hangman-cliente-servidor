package net.salesianos.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class HangmanClient {
  private JFrame frame;
  private JLabel categoryLabel, wordLabel, attemptsLabel, messageLabel, scoreLabel;
  private JTextField inputField;
  private JButton sendButton, restartButton;
  private HangmanPanel hangmanPanel;
  private Socket socket;
  private BufferedReader in;
  private PrintWriter out;
  private int score = 0;
  private int failedAttempts = 0;

  public HangmanClient() {
    initializeUI();
    connectToServer();
  }

  private void initializeUI() {
    frame = new JFrame("Ahorcado en Red");
    frame.setLayout(new BorderLayout());
    frame.setSize(500, 400);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JPanel topPanel = new JPanel(new GridLayout(5, 1));
    categoryLabel = new JLabel("Conectando...", SwingConstants.CENTER);
    wordLabel = new JLabel("Esperando palabra...", SwingConstants.CENTER);
    attemptsLabel = new JLabel("Intentos restantes: ", SwingConstants.CENTER);
    scoreLabel = new JLabel("Puntuación: " + score, SwingConstants.CENTER);
    messageLabel = new JLabel("", SwingConstants.CENTER);

    topPanel.add(categoryLabel);
    topPanel.add(wordLabel);
    topPanel.add(attemptsLabel);
    topPanel.add(scoreLabel);
    topPanel.add(messageLabel);

    JPanel inputPanel = new JPanel();
    inputField = new JTextField(5);
    sendButton = new JButton("Enviar");
    restartButton = new JButton("Reiniciar");
    inputPanel.add(inputField);
    inputPanel.add(sendButton);
    inputPanel.add(restartButton);

    hangmanPanel = new HangmanPanel();

    frame.add(topPanel, BorderLayout.NORTH);
    frame.add(hangmanPanel, BorderLayout.CENTER);
    frame.add(inputPanel, BorderLayout.SOUTH);

    sendButton.addActionListener(e -> sendGuess());
    inputField.addActionListener(e -> sendGuess());
    restartButton.addActionListener(e -> restartGame());

    frame.setVisible(true);
  }

  private void connectToServer() {
    try {
      socket = new Socket("localhost", 5000);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      out = new PrintWriter(socket.getOutputStream(), true);

      startResponseListener();
    } catch (IOException e) {
      messageLabel.setText("No se pudo conectar al servidor.");
    }
  }

  private void startResponseListener() {
    new Thread(() -> {
      try {
        String response;
        while ((response = in.readLine()) != null) {
          updateUI(response);
        }
      } catch (IOException e) {
        messageLabel.setText("Conexión perdida.");
      }
    }).start();
  }

  private void sendGuess() {
    String guess = inputField.getText().toUpperCase();
    if (guess.length() != 1 || !Character.isLetter(guess.charAt(0))) {
      messageLabel.setText("Introduce una sola letra.");
      return;
    }

    out.println(guess);
    inputField.setText("");
  }

  private void restartGame() {
    frame.dispose();
    new HangmanClient();
  }

  private void updateUI(String response) {
    if (response.startsWith("Categoría: ")) {
      categoryLabel.setText(response);
    } else if (response.startsWith("Estado: ")) {
      updateGameState(response);
    } else if (response.startsWith("¡Ganaste!")) {
      handleWin(response);
    } else if (response.startsWith("Perdiste")) {
      handleLoss(response);
    } else {
      messageLabel.setText(response);
    }
  }

  private void updateGameState(String response) {
    String[] parts = response.split(" \\| ");
    wordLabel.setText(parts[0].replace("Estado: ", ""));
    attemptsLabel.setText(parts[1]);
    failedAttempts = 6 - Integer.parseInt(parts[1].replace("Intentos restantes: ", ""));
    hangmanPanel.repaint();
  }

  private void handleWin(String response) {
    score += 10;
    scoreLabel.setText("Puntuación: " + score);
    JOptionPane.showMessageDialog(frame, response);
  }

  private void handleLoss(String response) {
    score = Math.max(0, score - 5);
    scoreLabel.setText("Puntuación: " + score);
    JOptionPane.showMessageDialog(frame, response);
  }

  class HangmanPanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      drawGallows(g);
      drawHangman(g);
    }

    private void drawGallows(Graphics g) {
      g.drawLine(50, 200, 150, 200); // Base
      g.drawLine(100, 50, 100, 200); // Poste
      g.drawLine(100, 50, 150, 50);  // Viga
      g.drawLine(150, 50, 150, 70);  // Cuerda
    }

    private void drawHangman(Graphics g) {
      if (failedAttempts > 0) g.drawOval(130, 70, 40, 40); // Cabeza
      if (failedAttempts > 1) g.drawLine(150, 110, 150, 150); // Cuerpo
      if (failedAttempts > 2) g.drawLine(150, 120, 130, 140); // Brazo izquierdo
      if (failedAttempts > 3) g.drawLine(150, 120, 170, 140); // Brazo derecho
      if (failedAttempts > 4) g.drawLine(150, 150, 130, 180); // Pierna izquierda
      if (failedAttempts > 5) g.drawLine(150, 150, 170, 180); // Pierna derecha
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new HangmanClient());
  }
}