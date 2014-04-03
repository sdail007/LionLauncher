package edu.odu.lionlauncher.lionmail;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.io.File;

/**
 * Created by trueLove on 4/2/14.
 */
public class LionMailWindow extends JFrame{

    private JPanel backupPanel;

    private HighScoreEvaluator hse;

    public LionMailWindow()
    {
        setTitle("LionMail");
        JTextArea textArea = new JTextArea (25, 80);
        textArea.setEditable (false);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new FlowLayout());
        contentPane.add(
                new JScrollPane(
                        textArea,
                        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED),
                BorderLayout.CENTER);
        JTextOutputStream out = new JTextOutputStream (textArea);
        System.setOut (new PrintStream(out));

        backupPanel = new JPanel();
        backupPanel.setLayout(new BoxLayout(backupPanel, BoxLayout.PAGE_AXIS));


        JButton loadBackupButton = new JButton("Load Backup");
        loadBackupButton.setVisible(true);
        loadBackupButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser loader = new JFileChooser(new java.io.File(System.getProperty("user.dir")));
                int actionTaken = loader.showOpenDialog(LionMailWindow.this);
                if(actionTaken == JFileChooser.APPROVE_OPTION)
                {
                    System.out.println(loader.getSelectedFile());
                }
            }
        });
        backupPanel.add(loadBackupButton);

        JButton newBackupButton = new JButton("New Backup");
        newBackupButton.setVisible(true);
        newBackupButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser saver = new JFileChooser(new java.io.File(System.getProperty("user.dir")));
                int actionTaken = saver.showSaveDialog(null);
                if (actionTaken == JFileChooser.APPROVE_OPTION) {
                    try {
                        //saver.getSelectedFile()+".ini");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        backupPanel.add(newBackupButton);

        JButton resetButton = new JButton("Reset Highscores");
        resetButton.setVisible(true);
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {}
         });
        backupPanel.add(resetButton);

        backupPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        contentPane.add(backupPanel);

        pack();
        setVisible (true);

        hse = new HighScoreEvaluator(new File("/Users/trueLove/Documents/Video Game Development/Ride the Lion/LionLauncher/rideold.ini") , new File("/Users/trueLove/Documents/Video Game Development/Ride the Lion/LionLauncher/ride.ini"), false);
        new Thread(hse).start();            //This enables the program to continue execution
    }
}
