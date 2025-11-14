package GUI;

import Entity.*;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HomePanel extends JPanel {
    private HabitDataManager dataManager;
    private JLabel greetingLabel;
    private JLabel timeLabel;
    private JPanel overviewPanel;
    private JPanel statsPanel;


    public HomePanel(HabitDataManager dataManager) {
        this.dataManager = dataManager;
        initializeComponents();
        setupLayout();
        refreshData();
    }

    private void initializeComponents() {
        setBackground(Color.WHITE);

        greetingLabel = new JLabel();
        greetingLabel.setFont(new Font("Arial", Font.BOLD, 20));

        timeLabel = new JLabel();
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        updateGreeting();

        overviewPanel = createOverviewPanel();
        statsPanel = createStatsPanel();
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel headerPanel = createHeaderPanel();

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(overviewPanel, BorderLayout.NORTH);
        contentPanel.add(statsPanel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));

        JPanel textPanel = new JPanel();
        textPanel.setBackground(Color.WHITE);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.add(greetingLabel);
        textPanel.add(timeLabel);

        panel.add(textPanel, BorderLayout.WEST);
        return panel;
    }

    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Today's Habits"));

        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Statistics"));

        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

    private void updateGreeting() {
        LocalDateTime now = LocalDateTime.now();
        String time = now.format(DateTimeFormatter.ofPattern("h:mm a"));
        String date = now.format(DateTimeFormatter.ofPattern("EEEE, MMMM d"));
        int hour = now.getHour();

        String greeting = "Good Morning";
        if (hour >= 12 && hour < 17) {
            greeting = "Good Afternoon";
        } else if (hour >= 17) {
            greeting = "Good Evening";
        }

        greetingLabel.setText(greeting);
        timeLabel.setText(date + " - " + time);
    }

    public void refreshData() {
        updateGreeting();
        updateOverviewPanel();
        updateStatsPanel();
    }

    public void updateOverviewPanel() {
        JPanel panel = overviewPanel;
        JPanel contentPanel = (JPanel) panel.getComponent(0);
        contentPanel.removeAll();

        List<Habit> habits = dataManager.getAllHabits();

        if (habits.isEmpty()) {
            JLabel emptyLabel = new JLabel("No habits added yet!");
            contentPanel.add(emptyLabel);
        } else {
            for (int i = 0; i < Math.min(habits.size(), 5); i++) {
                Habit habit = habits.get(i);
                JPanel habitRow = createHabitRow(habit);
                contentPanel.add(habitRow);
            }
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createHabitRow(Habit habit) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.setBackground(Color.WHITE);

        JLabel statusLabel = new JLabel();
        if (habit.isCompletedToday()) {
            statusLabel.setText("✓");
            statusLabel.setForeground(Color.GREEN);
        } else {
            statusLabel.setText("○");
            statusLabel.setForeground(Color.GRAY);
        }

        JLabel nameLabel = new JLabel(habit.getName());
        if (habit.isCompletedToday()) {
            nameLabel.setForeground(Color.GREEN);
        }

        JLabel streakLabel = new JLabel(" (Streak: " + habit.getStreak() + ")");
        streakLabel.setForeground(Color.GRAY);

        row.add(statusLabel);
        row.add(nameLabel);
        row.add(streakLabel);

        return row;
    }

    private void updateStatsPanel() {
        JPanel panel = statsPanel;
        JPanel contentPanel = (JPanel) panel.getComponent(0);
        contentPanel.removeAll();

        List<Habit> habits = dataManager.getAllHabits();

        int totalHabits = habits.size();
        int completedToday = 0;
        int totalStreak = 0;

        for (Habit habit : habits) {
            if (habit.isCompletedToday()) {
                completedToday++;
            }
            totalStreak += habit.getStreak();
        }

        contentPanel.add(new JLabel("Total Habits: " + totalHabits));
        contentPanel.add(new JLabel("Completed Today: " + completedToday + "/" + totalHabits));
        contentPanel.add(new JLabel("Total Streak Days: " + totalStreak));

        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
