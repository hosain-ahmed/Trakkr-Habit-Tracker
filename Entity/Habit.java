package Entity;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Habit implements IHabit {
    private int id;
    private String name;
    private String desc;
    private int streak;
    private Map<LocalDate, Boolean> completionHistory;

    public Habit() {
        this.completionHistory = new HashMap<>();
    }

    public Habit(int id, String name, String desc) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.streak = 0;
        this.completionHistory = new HashMap<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return desc;
    }

    public void setDescription(String desc) {
        this.desc = desc;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public boolean isCompletedOnDate(LocalDate date) {
        if (completionHistory.containsKey(date)) {
            return completionHistory.get(date);
        }
        return false;
    }

    public void setCompletedOnDate(LocalDate date, boolean completed) {
        if (completed) {
            completionHistory.put(date, true);
        } else {
            completionHistory.remove(date);
        }
        calculateStreak();
    }

    public Map<LocalDate, Boolean> getCompletionHistory() {
        return new HashMap<>(completionHistory);
    }

    public void setCompletionHistory(Map<LocalDate, Boolean> completionHistory) {
        this.completionHistory = new HashMap<>(completionHistory);
        calculateStreak();
    }

    private void calculateStreak() {
        LocalDate today = LocalDate.now();
        int currentStreak = 0;

        LocalDate checkDate = today;
        while (isCompletedOnDate(checkDate)) {
            currentStreak++;
            checkDate = checkDate.minusDays(1);
        }

        this.streak = currentStreak;
    }

    public boolean isCompletedToday() {
        return isCompletedOnDate(LocalDate.now());
    }

    public String toString() {
        return name + " (Streak: " + streak + ")";
    }
}