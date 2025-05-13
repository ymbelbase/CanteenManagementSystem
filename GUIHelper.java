import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;

public class GUIHelper {
    public static JButton createButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.addActionListener(listener);
        return button;
    }

    public static JPanel createLabeledPanel(String label, JComponent component) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel(label));
        panel.add(component);
        return panel;
    }

    public static void showMessage(String title, String message, int messageType) {
        JOptionPane.showMessageDialog(null, message, title, messageType);
    }

    public static void showMessage(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static JTextField createTextField(int columns) {
        return new JTextField(columns);
    }

    public static JComboBox<String> createComboBox(String[] options) {
        return new JComboBox<>(options);
    }
}