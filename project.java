package project4;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class TypingGame extends JFrame {

    private JTextField textField;
    private JButton startButton;
    private JButton clearButton;
    private JLabel challengeLabel;
    private JLabel resultLabel;
    private JLabel highestSpeedLabel;
    private JLabel accuracyLabel;
    private JLabel statusLabel;
    private JComboBox<String> difficultyComboBox;
    private JProgressBar progressBar;
    private Timer timer;

    private long startTime;
    private boolean typingStarted;
    private double highestCPM;
    private double highestWPM;
    private int totalTypedCharacters;
    private int totalCorrectCharacters;
    private int challengeTimeLimit; // in seconds

    private String[] easyChallenges = {
            "The quick brown fox jumps over the lazy dog.",
            "Programming is fun and challenging.",
            "Practice makes perfect.",
            "Java Swing provides a rich set of GUI components."
    };

    private String[] mediumChallenges = {
            "Effort only fully releases its reward after a person refuses to quit.",
            "Success is not final, failure is not fatal: It is the courage to continue that counts.",
            "Your time is limited, don't waste it living someone else's life.",
            "Life is what happens when you're busy making other plans."
    };

    private String[] hardChallenges = {
            "The only way to do great work is to love what you do.",
            "Life is really simple, but we insist on making it complicated.",
            "Don't watch the clock; do what it does. Keep going.",
            "Believe you can and you're halfway there."
    };

    private static final double LOW_ACCURACY_THRESHOLD = 90.0;
    private static final double SLOW_SPEED_THRESHOLD = 50.0;
    private static final double IMPRESSIVE_SPEED_THRESHOLD = 100.0;

    public TypingGame() {
        setTitle("Typing Game");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        textField = new JTextField(25);
        startButton = new JButton("Start Typing");
        clearButton = new JButton("Clear");
        challengeLabel = new JLabel();
        resultLabel = new JLabel("Type the challenge below and press Enter:");
        highestSpeedLabel = new JLabel("Highest typing speed: 0 CPM / 0 WPM");
        accuracyLabel = new JLabel("Accuracy: 100%");
        statusLabel = new JLabel();
        difficultyComboBox = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        progressBar = new JProgressBar();
        timer = new Timer(1000, e -> updateTimer());

        resultLabel.setFont(new Font("Arial", Font.BOLD, 16));
        highestSpeedLabel.setFont(new Font("Arial", Font.BOLD, 14));
        accuracyLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        resultLabel.setForeground(new Color(41, 128, 185)); // Dark Blue
        highestSpeedLabel.setForeground(new Color(231, 76, 60)); // Red
        accuracyLabel.setForeground(new Color(39, 174, 96)); // Green

        startButton.setBackground(new Color(52, 152, 219)); // Blue
        startButton.setForeground(Color.WHITE);
        clearButton.setBackground(new Color(192, 57, 43)); // Red
        clearButton.setForeground(Color.WHITE);

        startButton.addActionListener(e -> startTyping());
        clearButton.addActionListener(e -> clearSession());

        textField.addActionListener(e -> calculateTypingSpeed());

        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                handleTextChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                handleTextChange();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                handleTextChange();
            }

            private void handleTextChange() {
                String typedText = textField.getText();
                String targetText = challengeLabel.getText();

                int correctCharacters = 0;
                int length = Math.min(typedText.length(), targetText.length());

                for (int i = 0; i < length; i++) {
                    if (typedText.charAt(i) == targetText.charAt(i)) {
                        correctCharacters++;
                    }
                }

                totalTypedCharacters += typedText.length();
                totalCorrectCharacters += correctCharacters;

                if (correctCharacters == typedText.length()) {
                    textField.setForeground(new Color(39, 174, 96)); // Green for correct characters
                } else {
                    textField.setForeground(new Color(231, 76, 60)); // Red for incorrect characters
                }
            }
        });

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        panel.setBackground(new Color(236, 240, 241)); // Silver Sand
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 0, 20, 0);
        panel.add(resultLabel, gbc);

        gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add(challengeLabel, gbc);

        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 0, 10, 0);
        panel.add(textField, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 5, 0, 5);
        panel.add(startButton, gbc);

        gbc.gridx++;
        panel.add(clearButton, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Difficulty:"), gbc);

        gbc.gridx++;
        panel.add(difficultyComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 0, 0);
        panel.add(highestSpeedLabel, gbc);

        gbc.gridy++;
        panel.add(accuracyLabel, gbc);

        gbc.gridy++;
        panel.add(statusLabel, gbc);

        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JLabel("Time Remaining:"), gbc);

        gbc.gridy++;
        panel.add(progressBar, gbc);

        add(panel);
    }

    private void startTyping() {
        // Set timer based on difficulty
        switch ((String) difficultyComboBox.getSelectedItem()) {
            case "Easy":
                challengeTimeLimit = 30;
                break;
            case "Medium":
                challengeTimeLimit = 45;
                break;
            case "Hard":
                challengeTimeLimit = 60;
                break;
            default:
                challengeTimeLimit = 30; // Default to easy challenge time limit
        }

        startTime = System.currentTimeMillis();
        typingStarted = true;
        resultLabel.setText("Type the challenge below and press Enter:");
        accuracyLabel.setText("Accuracy: 100%");
        statusLabel.setText("");
        textField.setText("");
        setRandomChallenge();
        textField.requestFocus();

        // Initialize and start the progress bar and timer
        progressBar.setMaximum(challengeTimeLimit);
        progressBar.setValue(0);
        progressBar.setString("Time Remaining: " + challengeTimeLimit + "s");
        timer.start();
    }

    private void calculateTypingSpeed() {
        if (!typingStarted) {
            return;
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        String typedText = textField.getText();
        int totalCharacters = typedText.length();
        int correctCharacters = calculateCorrectCharacters(typedText);

        double cpm = (totalCharacters / (double) totalTime) * 60000; // Characters Per Minute
        double wpm = (totalCharacters / 5.0) / (totalTime / 60000.0); // Words Per Minute
        double accuracy = (correctCharacters / (double) totalCharacters) * 100; // Accuracy

        DecimalFormat df = new DecimalFormat("#.##");

        resultLabel.setText(String.format("Your typing speed: %s CPM / %s WPM", df.format(cpm), df.format(wpm)));
        accuracyLabel.setText(String.format("Accuracy: %s%%", df.format(accuracy)));
        typingStarted = false;

        updateHighestSpeed(cpm, wpm);
        provideFeedback(cpm, accuracy);
        setRandomChallenge(); // Set a new challenge after completion

        // Stop the progress bar and timer
        progressBar.setValue(challengeTimeLimit);
        progressBar.setString("Time Remaining: 0s");
        timer.stop();
    }

    private void updateTimer() {
        int elapsedTime = (int) ((System.currentTimeMillis() - startTime) / 1000);
        progressBar.setValue(elapsedTime);

        // Updated: Added a heading to the timer bar
        progressBar.setString("Time Remaining: " + (challengeTimeLimit - elapsedTime) + "s");

        if (elapsedTime >= challengeTimeLimit) {
            calculateTypingSpeed(); // Calculate typing speed when time is up
        }
    }

    private void clearSession() {
        startTyping(); // Reset the session
        highestCPM = 0;
        highestWPM = 0;
        highestSpeedLabel.setText("Highest typing speed: 0 CPM / 0 WPM");
        statusLabel.setText("");
    }

    private void setRandomChallenge() {
        String challenge = getRandomChallenge();
        challengeLabel.setText(challenge);
    }

    private String getRandomChallenge() {
        String[] challenges;

        switch ((String) difficultyComboBox.getSelectedItem()) {
            case "Easy":
                challenges = easyChallenges;
                break;
            case "Medium":
                challenges = mediumChallenges;
                break;
            case "Hard":
                challenges = hardChallenges;
                break;
            default:
                challenges = easyChallenges; // Default to easy challenges
        }

        Random random = new Random();
        int index = random.nextInt(challenges.length);

        return challenges[index];
    }

    private int calculateCorrectCharacters(String typedText) {
        String targetText = challengeLabel.getText();

        int correctCharacters = 0;
        int length = Math.min(typedText.length(), targetText.length());

        for (int i = 0; i < length; i++) {
            if (typedText.charAt(i) == targetText.charAt(i)) {
                correctCharacters++;
            }
        }

        return correctCharacters;
    }

    private void updateHighestSpeed(double cpm, double wpm) {
        if (cpm > highestCPM) {
            highestCPM = cpm;
            highestSpeedLabel.setText(String.format("Highest typing speed: %s CPM / %s WPM", formatSpeed(highestCPM), formatSpeed(highestWPM)));
            statusLabel.setText("New record!");
        } else {
            statusLabel.setText("");
        }

        if (wpm > highestWPM) {
            highestWPM = wpm;
            highestSpeedLabel.setText(String.format("Highest typing speed: %s CPM / %s WPM", formatSpeed(highestCPM), formatSpeed(highestWPM)));
            statusLabel.setText("New record!");
        } else {
            statusLabel.setText("");
        }
    }

    private String formatSpeed(double speed) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(speed);
    }

    private void provideFeedback(double cpm, double accuracy) {
        if (accuracy < LOW_ACCURACY_THRESHOLD) {
            statusLabel.setText("Accuracy is below the recommended level. Focus on accuracy for better results.");
        } else if (cpm < SLOW_SPEED_THRESHOLD) {
            statusLabel.setText("Your typing speed is a bit slow. Keep practicing to improve.");
        } else if (cpm > IMPRESSIVE_SPEED_THRESHOLD) {
            statusLabel.setText("Impressive typing speed! Keep up the good work!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TypingGame().setVisible(true));
    }
}
