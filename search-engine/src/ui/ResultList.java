package ui;

import index.Posting;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class ResultList extends JPanel {
    private JFrame frame;
    private JScrollPane scroll = null;
    private JList list = null;

    public ResultList(JFrame frame) {
        super();
        this.frame = frame;
    }

    public void display(JFrame frame, ArrayList<String> postings) {
        if(scroll != null) {
            remove(scroll);
        }

        list = new JList(postings.toArray());
        scroll = new JScrollPane(list);
        add(scroll);

        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    int i = list.locationToIndex(e.getPoint());
                    String path = postings.get(i);
                    ResultDialog.show(frame, path);
                }
            }
        });

        frame.invalidate();
        frame.validate();
        frame.repaint();
    }
}
