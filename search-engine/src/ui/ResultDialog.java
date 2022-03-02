package ui;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public final class ResultDialog {
    private ResultDialog() { }

    public static void show(JFrame frame, String path) {
        JDialog dialog = new JDialog(frame, path);
        dialog.setSize(640, 480);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLocationRelativeTo(frame);

        JTextArea textArea = new JTextArea();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line = "";

            while((line = reader.readLine()) != null) {
                textArea.append(line + "\n");
            }
        } catch(IOException ex) {
            System.err.println("Failed to read file!");
            ex.printStackTrace();
        }

        textArea.setLineWrap(true);
        textArea.setEditable(false);

        dialog.add(textArea);

        dialog.setVisible(true);
    }
}
