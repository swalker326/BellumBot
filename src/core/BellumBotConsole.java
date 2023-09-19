package core;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;

public class BellumBotConsole extends JFrame {

    private JTextArea outputArea;
    private JScrollPane scrollPane;
    private boolean autoScroll = true;
    private JTextField inputField;
    private JCheckBox alwaysOnTopCheckBox;
    private Boolean isVisible = false;

    public BellumBotConsole(BellumBot bot) {
        setTitle("BellumBotConsole");

        // Layout and components
        setLayout(new BorderLayout());

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setBackground(Color.BLACK);
        outputArea.setForeground(Color.WHITE);
        scrollPane = new JScrollPane(outputArea);
        scrollPane.getVerticalScrollBar().addAdjustmentListener(this::handleScroll);

        inputField = new JTextField();
        inputField.setBackground(Color.BLACK);
        inputField.setForeground(Color.WHITE);
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String command = inputField.getText();
                processCommand(command);
                inputField.setText("");  // clear input field
            }
        });

        // Checkbox to control "always on top" behavior
        alwaysOnTopCheckBox = new JCheckBox("Always on Top");
        alwaysOnTopCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAlwaysOnTop(alwaysOnTopCheckBox.isSelected());
            }
        });

        // Panel to hold the input field and the checkbox
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(inputField, BorderLayout.CENTER);
        southPanel.add(alwaysOnTopCheckBox, BorderLayout.EAST);

        add(scrollPane, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public void capture(String message) {
        SwingUtilities.invokeLater(() -> {
            outputArea.append(message + "\n");
            if (autoScroll) {
                outputArea.setCaretPosition(outputArea.getDocument().getLength());
            }
        });
    }

    private void handleScroll(AdjustmentEvent e) {
        JScrollBar scrollBar = (JScrollBar) e.getAdjustable();
        int extent = scrollBar.getModel().getExtent();
        int maximum = scrollBar.getModel().getMaximum();
        if (e.getValue() + extent == maximum) { // Scroll is at the bottom
            autoScroll = true;
            scrollPane.setBorder(null);
        } else {
            autoScroll = false;
            scrollPane.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        }
    }

    private void processCommand(String command) {
        if (command.equals("exit")) {
            hideConsole();
        } else if (command.equals("clear")) {
            outputArea.setText("");
        } else if (command.equals("help")) {
            outputArea.append("Commands:\n");
            outputArea.append("  exit - Hide the console\n");
            outputArea.append("  clear - Clear the console\n");
            outputArea.append("  help - Show this help message\n");
        } else {
            // Here, process the command as you see fit and output results to the console.
            // For demonstration, just echo the command.
            outputArea.append("Command received: " + command + "\n");
        }
    }

    public void hideConsole() {
        SwingUtilities.invokeLater(() -> {
            this.isVisible = false;
            setVisible(false);
        });
    }

    public void showConsole() {
        SwingUtilities.invokeLater(() -> {
            this.isVisible = true;
            setVisible(true);
        });
    }
}
