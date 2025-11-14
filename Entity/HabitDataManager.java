package Entity;

import FileIO.*;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HabitDataManager {
    private static final String FILE_NAME = "habits.txt";
    private List<Habit> habits;
    private int nextId;

    public HabitDataManager() {
        habits = new ArrayList<>();
        nextId = 1;
        try {
            loadHabits();
        } catch (FileOperationException e) {
            System.out.println("Starting with empty habit list: " + e.getMessage());
        }
    }

    public void addHabit(Habit habit) throws HabitException {
        if (habit.getName() == null || habit.getName().trim().isEmpty()) {
            throw new HabitException("Habit name cannot be empty");
        }

        for (Habit existingHabit : habits) {
            if (existingHabit.getName().equalsIgnoreCase(habit.getName())) {
                throw new HabitException("Habit with this name already exists");
            }
        }

        habit.setId(nextId);
        nextId++;
        habits.add(habit);
        saveHabits();
    }

    public List<Habit> getAllHabits() {
        return new ArrayList<>(habits);
    }

    public Habit getHabitById(int id) throws HabitException {
        for (Habit habit : habits) {
            if (habit.getId() == id) {
                return habit;
            }
        }
        throw new HabitException("Habit not found with ID: " + id);
    }

    public void updateHabit(Habit updatedHabit) throws HabitException {
        for (int i = 0; i < habits.size(); i++) {
            if (habits.get(i).getId() == updatedHabit.getId()) {
                habits.set(i, updatedHabit);
                saveHabits();
                return;
            }
        }
        throw new HabitException("Habit not found for update");
    }

    public void markHabitComplete(int id) throws HabitException {
        Habit habit = getHabitById(id);
        habit.setCompletedOnDate(LocalDate.now(), true);
        updateHabit(habit);
    }

    public boolean isHabitCompletedOnDate(int id, LocalDate date) throws HabitException {
        Habit habit = getHabitById(id);
        return habit.isCompletedOnDate(date);
    }

    public List<Habit> searchHabits(String searchTerm) {
        List<Habit> results = new ArrayList<>();
        String term = searchTerm.toLowerCase();

        for (Habit habit : habits) {
            boolean nameMatches = habit.getName().toLowerCase().contains(term);

            boolean descMatches = false;
            if (habit.getDescription() != null) {
                descMatches = habit.getDescription().toLowerCase().contains(term);
            }

            if (nameMatches || descMatches) {
                results.add(habit);
            }
        }
        return results;
    }

    public void deleteHabit(int id) throws HabitException {
        for (int i = 0; i < habits.size(); i++) {
            if (habits.get(i).getId() == id) {
                habits.remove(i);
                saveHabits();
                return;
            }
        }
        throw new HabitException("Habit not found for deletion");
    }


    private void saveHabits() {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(FILE_NAME));

            writer.write("NEXT_ID:" + nextId);
            writer.newLine();

            for (Habit habit : habits) {
                String line = "";
                line = line + habit.getId() + "/";
                line = line + habit.getName() + "/";

                if (habit.getDescription() != null) {
                    line = line + habit.getDescription();
                }
                line = line + "/";
                line = line + habit.getStreak() + "/";

                Map<LocalDate, Boolean> history = habit.getCompletionHistory();
                String completionDates = "";
                for (LocalDate date : history.keySet()) {
                    if (history.get(date) == true) {
                        if (!completionDates.isEmpty()) {
                            completionDates = completionDates + ",";
                        }
                        completionDates = completionDates + date.toString();
                    }
                }
                line = line + completionDates;

                writer.write(line);
                writer.newLine();
            }

        } catch (IOException e) {
            System.out.println("Error saving habits: " + e.getMessage());
        } finally {

            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    System.out.println("Error closing file: " + e.getMessage());
                }
            }
        }
    }

    private void loadHabits() throws FileOperationException {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            throw new FileOperationException("Habits file does not exist");
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                if (isFirstLine && line.startsWith("NEXT_ID:")) {
                    String idPart = line.substring(8);
                    nextId = Integer.parseInt(idPart);
                    isFirstLine = false;
                    continue;
                }

                String[] parts = line.split("/", -1);
                if (parts.length >= 5) {
                    Habit habit = new Habit();

                    habit.setId(Integer.parseInt(parts[0]));
                    habit.setName(parts[1]);

                    if (!parts[2].isEmpty()) {
                        habit.setDescription(parts[2]);
                    }

                    habit.setStreak(Integer.parseInt(parts[3]));

                    Map<LocalDate, Boolean> completionHistory = new HashMap<>();
                    if (parts.length > 4 && !parts[4].isEmpty()) {
                        String[] dates = parts[4].split(",");
                        for (String dateString : dates) {
                            if (!dateString.trim().isEmpty()) {
                                try {
                                    LocalDate date = LocalDate.parse(dateString.trim());
                                    completionHistory.put(date, true);
                                } catch (Exception e) {
                                    System.out.println("Skipping invalid date: " + dateString);
                                }
                            }
                        }
                    }

                    habit.setCompletionHistory(completionHistory);
                    habits.add(habit);
                }
            }

            reader.close();

        } catch (IOException e) {
            throw new FileOperationException("Error loading habits from file", e);
        } catch (NumberFormatException e) {
            throw new FileOperationException("Error parsing habit data", e);
        }
    }
}