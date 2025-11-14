package GUI;

import Entity.*;
import FileIO.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.List;

public class HabitsMainPanel extends JPanel {
    private HabitDataManager dataManager;
    private DefaultListModel<HabitCheckboxItem> listModel;
    private JList<HabitCheckboxItem> habitList;
    private JTextField nameField;
    private JTextField descField;
    private JTextField searchField;
    private HomePanel homePanel;

    private class HabitCheckboxItem {
        private Habit habit;
        private JCheckBox checkBox;

        public HabitCheckboxItem(Habit habit) {
            this.habit = habit;
            this.checkBox = new JCheckBox();
            try {
                this.checkBox.setSelected(dataManager.isHabitCompletedOnDate(habit.getId(), LocalDate.now()));
            } catch (HabitException e) {
                this.checkBox.setSelected(false);
            }
        }

        public Habit getHabit() {
            return habit;
        }

        public JCheckBox getCheckBox() {
            return checkBox;
        }

        @Override
        public String toString() {
            String status = "";
            try {
                if (dataManager.isHabitCompletedOnDate(habit.getId(), LocalDate.now())) {
                    status = "✓ ";
                }
            } catch (HabitException e) {
            }
            return status + habit.getName();
        }
    }

    public HabitsMainPanel(HomePanel homePanel, HabitDataManager dataManager) {
        this.dataManager = dataManager;
        this.homePanel = homePanel;
        initializeComponents();
        setupLayout();
        loadHabits();
    }

    private void initializeComponents() {
        setBackground(Color.WHITE);
        listModel = new DefaultListModel<>();
        habitList = new JList<>(listModel);
        habitList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        habitList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = habitList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        HabitCheckboxItem item = listModel.getElementAt(index);
                        try {
                            boolean isCompletedToday = dataManager.isHabitCompletedOnDate(item.getHabit().getId(), LocalDate.now());
                            if (!isCompletedToday) {
                                dataManager.markHabitComplete(item.getHabit().getId());
                                loadHabits();
                                homePanel.refreshData();
                                showSuccessMessage("Habit completed!");
                            }
                        } catch (HabitException ex) {
                            showErrorMessage("Error: " + ex.getMessage());
                        }
                    }
                }
            }
        });
        nameField = new JTextField(15);
        descField = new JTextField(15);
        searchField = new JTextField(10);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel addPanel = createAddPanel();
        JPanel listPanel = createListPanel();
        JPanel actionPanel = createActionPanel();
        add(addPanel, BorderLayout.NORTH);
        add(listPanel, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);
    }

    private JPanel createAddPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Add New Habit"));
        panel.setLayout(new GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Description:"));
        panel.add(descField);
        panel.add(new JLabel(""));
        JButton addBtn = new JButton("Add Habit");
        addBtn.setForeground(Color.WHITE);
        addBtn.setBackground(new Color(34, 79, 164));
        addBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addHabit();
            }
        });
        panel.add(addBtn);
        return panel;
    }

    private JPanel createListPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Your Habits"));
        JLabel instructionLabel = new JLabel("Double-click on a habit to mark it as complete");
        instructionLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        instructionLabel.setForeground(Color.GRAY);
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.setBackground(Color.WHITE);
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        JButton searchBtn = new JButton("Search");
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setBackground(new Color(34, 79, 164));
        searchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchHabits();
            }
        });
        searchPanel.add(searchBtn);
        JButton showAllBtn = new JButton("Show All");
        showAllBtn.setForeground(Color.WHITE);
        showAllBtn.setBackground(new Color(34, 79, 164));
        showAllBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadHabits();
            }
        });
        searchPanel.add(showAllBtn);
        JScrollPane scrollPane = new JScrollPane(habitList);
        JPanel listContainer = new JPanel(new BorderLayout());
        listContainer.setBackground(Color.WHITE);
        listContainer.add(scrollPane, BorderLayout.CENTER);
        listContainer.add(instructionLabel, BorderLayout.SOUTH);
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(listContainer, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(Color.WHITE);
        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setBackground(new Color(34, 79, 164));
        deleteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteHabit();
            }
        });
        panel.add(deleteBtn);
        return panel;
    }

    private void addHabit() {
        try {
            String name = nameField.getText().trim();
            String desc = descField.getText().trim();
            if (name.isEmpty()) {
                showErrorMessage("Please enter a habit name");
                return;
            }
            Habit habit = new Habit(0, name, desc);
            dataManager.addHabit(habit);
            nameField.setText("");
            descField.setText("");
            loadHabits();
            homePanel.refreshData();
            showSuccessMessage("Habit added successfully!");
        } catch (HabitException ex) {
            showErrorMessage("Error: " + ex.getMessage());
        }
    }

    private void deleteHabit() {
        HabitCheckboxItem selected = habitList.getSelectedValue();
        if (selected == null) {
            showErrorMessage("Please select a habit");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this habit?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dataManager.deleteHabit(selected.getHabit().getId());
                loadHabits();
                homePanel.refreshData();
                showSuccessMessage("Habit deleted successfully!");
            } catch (HabitException ex) {
                showErrorMessage("Error: " + ex.getMessage());
            }
        }
    }

    private void searchHabits() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadHabits();
            return;
        }
        List<Habit> results = dataManager.searchHabits(searchTerm);
        listModel.clear();
        for (Habit habit : results) {
            listModel.addElement(new HabitCheckboxItem(habit));
        }
    }

    private void loadHabits() {
        listModel.clear();
        List<Habit> habits = dataManager.getAllHabits();
        for (Habit habit : habits) {
            listModel.addElement(new HabitCheckboxItem(habit));
        }
    }

    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
