package edu.odu.lionlauncher.lionmail;

import googlemail.GoogleMail;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by trueLove on 4/2/14.
 */
public class HighScoreEvaluator implements Runnable{
    BufferedReader readNew = null;
    BufferedReader readOld = null;

    String oldFileContents = null;

    String oldFileName;
    String newFileName;

    File watchFile;

    Boolean sendEmail;
    private long lastModified;

    private Boolean paused = false;

    public HighScoreEvaluator(String oldf, String newf, Boolean send)
    {
        oldFileName = oldf;
        newFileName = newf;
        sendEmail = send;

        watchFile = new File(System.getProperty("user.dir") + "/" + newFileName);
        System.out.println(watchFile.getAbsolutePath());
        lastModified = System.currentTimeMillis();

        System.out.println(bumpedOffMessage("SDAIL007"));
    }

    @Override
    public void run()
    {
        System.out.println("Running");
        while(!paused)
        {
            System.out.println("test");
            if (hasUpdated())
            {
                System.out.println("Modified: " + new Date(getLastModified()).toString());
                Evaluate();
            }
            try{
                Thread.sleep (1000L);
            }
            catch(InterruptedException e){}
        }
    }

    public void start()
    {
        System.out.println("Starting: " + new Date(System.currentTimeMillis()).toString());
        run();
    }

    public boolean hasUpdated()
    {
        return (watchFile.lastModified() > lastModified);
    }

    public long getLastModified()
    {
        return lastModified;
    }

    public void Evaluate()
    {
        List<String> oldFile = new ArrayList<String>();
        List<String> newFile = new ArrayList<String>();

        String[] oldScores = new String[10];
        String[] newScores = new String[10];
        String[] oldNames = new String[10];
        String[] newNames = new String[10];

        //
        //  OPEN AND READ NEW FILE
        //
        try
        {
            readNew = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/" + newFileName));
        }
        catch(java.io.FileNotFoundException FNF){System.out.println("Error opening " + System.getProperty("user.dir") + "/" + newFileName);}
        System.out.println("Opened " + newFileName + " successfully");


        try {
            String lineNew = readNew.readLine();

            oldFileContents = "";

            while (lineNew != null)
            {
                newFile.add(lineNew);
                oldFileContents += (lineNew + "\n");
                lineNew = readNew.readLine();
            }
        }
        catch(Exception IOE)
        {
            System.out.println("Something happened");
        }
        System.out.println(newFile.size() + " lines read");
        try{
            readNew.close();
        }
        catch(java.io.IOException IOE){System.out.println("Error in closing " + newFileName);}

        System.out.println("Closed " + newFileName + " successfully\n");

        //
        //  OPEN AND READ OLD FILE
        //

        try
        {
            readOld = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/" + oldFileName));
        }
        catch(java.io.FileNotFoundException FNF){System.out.println("Error opening " + System.getProperty("user.dir") + "/" + oldFileName);}
        System.out.println("Opened " + oldFileName + " successfully");

        try {
            String lineOld = readOld.readLine();

            while (lineOld != null)
            {
                oldFile.add(lineOld);
                lineOld = readOld.readLine();
            }
        }
        catch(Exception IOE)
        {
            System.out.println("Something happened");
        }
        System.out.println(oldFile.size() + " lines read");
        try{
            readOld.close();
        }
        catch(java.io.IOException IOE){System.out.println("Error in closing " + oldFileName);}

        System.out.println("Closed " + oldFileName + " successfully\n");


        //Tranfer contents of new file into old file.  Make a "new" new file.
        try{
            FileOutputStream writer = new FileOutputStream(System.getProperty("user.dir") + "/" + oldFileName);
            writer.write((new String()).getBytes());
            writer.write(oldFileContents.getBytes());
            writer.close();
        }
        catch(Exception e){System.out.println("Failed to write");}


        //End reading of files
        //Begin parsing files

        for(int i = 0; i < oldFile.size(); i++)
        {
            if(oldFile.get(i).equals(null)){/*Ignore line*/}
            else if(oldFile.get(i).length() < 2){/*Ignore line*/}
            else if(oldFile.get(i).substring(0,5).equals("score"))
            {
                try{
                    int index = Integer.parseInt(oldFile.get(i).substring(5, oldFile.get(i).lastIndexOf('=')));

                    oldScores[index-1] = oldFile.get(i).substring(oldFile.get(i).lastIndexOf('=') + 1, oldFile.get(i).length());
                }
                catch(NumberFormatException e)
                {System.out.println(e);}
                //oldScores.add(oldFile.get(i).substring(7,oldFile.get(i).length()));
            }
            else if(oldFile.get(i).substring(0,4).equals("name"))
            {
                try{
                    int index = Integer.parseInt(oldFile.get(i).substring(4, oldFile.get(i).lastIndexOf('=')));

                    oldNames[index-1] = oldFile.get(i).substring(oldFile.get(i).lastIndexOf('=') + 1, oldFile.get(i).length());
                }
                catch(NumberFormatException e)
                {System.out.println(e);}
                //oldNames.add(oldFile.get(i).substring(6,oldFile.get(i).length()));
            }
        }

        for(int i = 0; i < newFile.size(); i++)
        {
            if(newFile.get(i).equals(null)){/*Ignore line*/}
            else if(newFile.get(i).length() < 2){/*Ignore line*/}
            else if(newFile.get(i).substring(0,5).equals("score"))
            {
                try{
                    int index = Integer.parseInt(newFile.get(i).substring(5, newFile.get(i).lastIndexOf('=')));

                    newScores[index-1] = newFile.get(i).substring(newFile.get(i).lastIndexOf('=') + 1, newFile.get(i).length());
                }
                catch(NumberFormatException e)
                {System.out.println(e);}
                //oldScores.add(oldFile.get(i).substring(7,oldFile.get(i).length()));
            }
            else if(newFile.get(i).substring(0,4).equals("name"))
            {
                try{
                    int index = Integer.parseInt(newFile.get(i).substring(4, newFile.get(i).lastIndexOf('=')));

                    newNames[index-1] = newFile.get(i).substring(newFile.get(i).lastIndexOf('=') + 1, newFile.get(i).length());
                }
                catch(NumberFormatException e)
                {System.out.println(e);}
                //oldScores.add(oldFile.get(i).substring(7,oldFile.get(i).length()));
            }
        }

        for(int i = 0; i < oldNames.length; i++)
        {
            try{
                if(!oldNames[i].equals(newNames[i]) || !oldScores[i].equals(newScores[i]))
                    System.out.println((i+1) + ") " + oldNames[i] + "\t" + oldScores[i] + " replaced with\t"
                            + (i+1) + ") " + newNames[i] + "\t" + newScores[i]);
            }
            catch(Exception e){System.out.println(e);}
        }
        System.out.println();

        String bumpedOff;
        try{
            bumpedOff = oldNames[oldNames.length-1];
            System.out.println("Checking of bumped: " + bumpedOff);
        }
        catch(Exception e){System.out.println(e + "\nError: Continuing without email notification\n"); return;}

        if(newFile.size() > 0 && !Arrays.asList(newNames).contains(bumpedOff))
        {
            System.out.println(bumpedOff + " has been bumped off the leaderboard!\n");
            String sendAddress = bumpedOff + "@ODU.EDU";
            if(sendEmail)
            {
                System.out.println("Sending Message to : " + sendAddress);
                try{

                    GoogleMail.Send("ODUVideoGameDesignClub", "VGDCADMIN001", sendAddress,
                            "SDAILEY@CS.ODU.EDU", "Your score was beaten!", bumpedOffMessage(bumpedOff));
                    System.out.println("Sending Successful\n");
                }
                catch(Exception e)
                {
                    System.out.println("Failed to send\n" + e + "\n");
                }
            }
            else
            {
                System.out.println("Debug mode on - email not sent");
            }
        }
        else System.out.println("Continuing without email notification");

        lastModified = watchFile.lastModified();
    }

    private String bumpedOffMessage(String bumped)
    {
        String message = null;
        try {
            message = readFile(System.getProperty("user.dir") + "/emailBody.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //"We have some bad news, " + bumped + "!\n\nYou have been bumped off the leaderboards of Ride the Lion!" +
        //			" Come back soon to take back your place!\n\nSincerely,\nThe Video Game Design and Development Club\nOld Dominion University";

        message = message.replace("<player>",bumped);
        return message;
    }

    private String readFile( String file ) throws IOException {
        BufferedReader reader = new BufferedReader( new FileReader (file));
        String         line = null;
        StringBuilder  stringBuilder = new StringBuilder();
        String         ls = System.getProperty("line.separator");

        while( ( line = reader.readLine() ) != null ) {
            stringBuilder.append( line );
            stringBuilder.append( ls );
        }

        return stringBuilder.toString();
    }


    private void PrintArray(String[] array)
    {
        for(int i = 0; i<array.length;i++)
        {
            System.out.println(array[i]);
        }
    }
}