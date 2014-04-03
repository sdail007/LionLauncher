package edu.odu.lionlauncher.lionmail;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by trueLove on 4/2/14.
 */
public class JTextOutputStream extends OutputStream
{
    private final JTextArea destination;

    public JTextOutputStream (JTextArea destination)
    {
        if (destination == null)
            throw new IllegalArgumentException ("Destination is null");

        this.destination = destination;
    }

    @Override
    public void write(byte[] buffer, int offset, int length) throws IOException
    {
        final String text = new String (buffer, offset, length);
        SwingUtilities.invokeLater(new Runnable ()
        {
            @Override
            public void run()
            {
                destination.append (text);
            }
        });
    }

    @Override
    public void write(int b) throws IOException
    {
        write (new byte [] {(byte)b}, 0, 1);
    }
}