package edu.odu.lionlauncher.lionmail;

import javax.swing.*;
import java.awt.*;
import java.io.PrintStream;

/**
 * Created by trueLove on 4/2/14.
 */
public class LionMailWindow {

    private HighScoreEvaluator hse;

    public LionMailWindow()
    {
        JTextArea textArea = new JTextArea (25, 80);

        textArea.setEditable (false);

        JFrame frame = new JFrame ("stdout");
        frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        Container contentPane = frame.getContentPane ();
        contentPane.setLayout (new BorderLayout ());
        contentPane.add (
                new JScrollPane (
                        textArea,
                        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED),
                BorderLayout.CENTER);
        frame.pack ();
        frame.setVisible (true);

        JTextOutputStream out = new JTextOutputStream (textArea);
        System.setOut (new PrintStream(out));

        hse = new HighScoreEvaluator("rideold.ini" , "ride.ini", false);
        hse.start();

        System.out.println("started hse");
    }
}
