package GUI;
import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {
    public SettingsPanel() {
        initializeComponents();
        setupLayout();
    }

    private void initializeComponents() {
        setBackground(Color.WHITE);
    }
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel settingsPanel = createSettingsPanel();
        add(settingsPanel, BorderLayout.CENTER);
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Settings"));
        panel.setLayout(new BorderLayout());

        JTextArea aboutText = new JTextArea();
        aboutText.setText("Habit Tracker v1.0\n\n" +
                "A modern habit tracking application built with Java Swing.\n" +
                "Designed with a focus on simplicity and effectiveness.\n\n" +
                "© 2025 Practice Project");
        aboutText.setEditable(false);
        aboutText.setBackground(Color.WHITE);
        aboutText.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        aboutText.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(aboutText, BorderLayout.CENTER);

        return panel;
    }
}