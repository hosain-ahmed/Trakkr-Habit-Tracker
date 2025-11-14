package GUI;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class Sidebar extends JPanel {
    private JButton homeButton, habitButton,settingsButton;

    public Sidebar(Consumer<String> onNavigation) {
        ImageIcon[] icons = new ImageIcon[3];

        icons[0] = new ImageIcon(getClass().getResource("/Images/homes.png"));
        icons[1] = new ImageIcon(getClass().getResource("/Images/puzzle.png"));
        icons[2] = new ImageIcon(getClass().getResource("/Images/setting.png"));

        homeButton = new JButton(icons[0]);
        habitButton = new JButton(icons[1]);
        settingsButton = new JButton(icons[2]);

        setLayout(null);
        setPreferredSize(new Dimension(40, 600));
        setBackground(new Color(28, 28, 30));

        int btnSize = 20;
        homeButton.setBounds(10, 20, btnSize, btnSize);
        habitButton.setBounds(10, 80, btnSize, btnSize);
        settingsButton.setBounds(10, 750 - btnSize - 5, btnSize, btnSize);

        JButton[] buttons = {homeButton, habitButton, settingsButton};
        for (JButton button : buttons) {
            button.setBackground(Color.GRAY);
            button.setFocusPainted(false);
            button.setFocusable(false);
            add(button);
        }

        homeButton.addActionListener(e -> onNavigation.accept("HOME"));
        habitButton.addActionListener(e -> onNavigation.accept("HABITS"));
        settingsButton.addActionListener(e -> onNavigation.accept("SETTINGS"));
    }
}
