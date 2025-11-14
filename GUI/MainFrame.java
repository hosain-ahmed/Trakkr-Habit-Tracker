package GUI;
import Entity.HabitDataManager;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private HomePanel homePanel;
    private HabitsMainPanel habitsMainPanel;
    private SettingsPanel settingsPanel;
    private Sidebar sidebar;
    private HabitDataManager dataManager;
    private Image img = new ImageIcon(getClass().getResource("batman.png")).getImage();

    public MainFrame() {
        initializeFrame();
        createComponents();
        setupLayout();
        setVisible(true);
    }

    private void initializeFrame() {
        setTitle("Trakkr : Habit Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setIconImage(img);
    }

    private void createComponents() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        dataManager = new HabitDataManager();

        homePanel = new HomePanel(dataManager);
        habitsMainPanel = new HabitsMainPanel(homePanel,dataManager);
        settingsPanel = new SettingsPanel();
    }
    private void setupLayout() {
        mainPanel.add(homePanel, "HOME");
        mainPanel.add(habitsMainPanel, "HABITS");
        mainPanel.add(settingsPanel, "SETTINGS");

        sidebar = new Sidebar(name -> cardLayout.show(mainPanel, name));

        setLayout(new BorderLayout());
        add(sidebar, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);

        cardLayout.show(mainPanel, "HOME");

    }

}