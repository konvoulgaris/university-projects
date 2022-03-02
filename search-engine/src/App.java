package gr.unipi.ds;

import index.InvertedIndex;
import index.Posting;
import query.BooleQueryParser;
import ui.ResultList;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class App {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Search App");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new FlowLayout(FlowLayout.CENTER));

        InvertedIndex index = new InvertedIndex();
        ResultList resultList = new ResultList(frame);

        // Menu
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem createItem = new JMenuItem("Create Index...");
        JMenuItem loadItem = new JMenuItem("Load Index...");
        JMenu aboutMenu = new JMenu("About");
        fileMenu.add(createItem);
        fileMenu.add(loadItem);
        menuBar.add(fileMenu);
        menuBar.add(aboutMenu);

        createItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Choose directory to index");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);
                chooser.setCurrentDirectory(new File(System.getProperty("user.home")));

                int option = chooser.showOpenDialog(null);

                if(option != JFileChooser.APPROVE_OPTION) {
                    return;
                }

                String directory = null;

                try {
                     directory = chooser.getSelectedFile().getCanonicalPath();
                } catch (IOException ex) {
                    System.err.println("Failed to get directory to index");
                    ex.printStackTrace();
                }

                chooser.setDialogTitle("Choose index save path");
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.setAcceptAllFileFilterUsed(true);
                chooser.setCurrentDirectory(new File(System.getProperty("user.home")));

                option = chooser.showSaveDialog(null);

                if(option != JFileChooser.APPROVE_OPTION) {
                    return;
                }

                String savePath = null;

                try {
                    savePath = chooser.getSelectedFile().getCanonicalPath();
                } catch (IOException ex) {
                    System.err.println("Failed to get save path for index");
                    ex.printStackTrace();
                }

                index.build(directory);
                index.save(index, savePath);

                JOptionPane.showMessageDialog(null, "Index created and ready to use!");
                frame.setTitle(savePath);
            }
        });

        loadItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Choose index to load");
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.setAcceptAllFileFilterUsed(true);
                chooser.setCurrentDirectory(new File(System.getProperty("user.home")));

                int option = chooser.showOpenDialog(null);

                if(option != JFileChooser.APPROVE_OPTION) {
                    return;
                }

                String loadPath = null;

                try {
                    loadPath = chooser.getSelectedFile().getCanonicalPath();
                } catch (IOException ex) {
                    System.err.println("Failed to get load path for index");
                    ex.printStackTrace();
                }

                index.load(loadPath);

                JOptionPane.showMessageDialog(null, "Index loaded and ready to use!");
                frame.setTitle(loadPath);
            }
        });

        aboutMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                JOptionPane.showMessageDialog(null, "Created by Konstantinos Voulgaris (E17027)");
            }

            @Override
            public void menuDeselected(MenuEvent e) {
                return;
            }

            @Override
            public void menuCanceled(MenuEvent e) {
                return;
            }
        });

        frame.setJMenuBar(menuBar);

        // Main screen
        // Search
        JPanel searchPanel = new JPanel();
        JLabel searchLabel = new JLabel("Search");
        JTextField searchField = new JTextField();
        searchField.setColumns(50);
        JButton searchButton = new JButton("Execute");
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query = searchField.getText();
                ArrayList<Posting> result = BooleQueryParser.parse(index, query);
                ArrayList<String> resultFiles = new ArrayList<>();

                result.forEach(posting -> resultFiles.add(index.getDocumentMapper().get(posting.getDocumentID())));

                resultList.display(frame, resultFiles);
            }
        });

        frame.add(searchPanel);
        frame.add(resultList);
        frame.setVisible(true);
    }
}
