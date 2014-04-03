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

    File watchFile;
    File backFile;

    EvaluationStrategy evaluationStrategy;

    Boolean sendEmail;
    private long lastModified;

    private Boolean paused = false;

    public HighScoreEvaluator(File oldFile, File newFile, Boolean sendMail)
    {
        evaluationStrategy = new EvaluationStrategy(oldFile, newFile, new File(System.getProperty("user.dir") + "/emailBody.txt"));

        sendEmail = sendMail;

        watchFile = newFile;
        backFile = oldFile;

        System.out.println("LionMail v2\nWritten By Stephen Dailey\n");
        System.out.println(watchFile.getAbsolutePath());
        lastModified = System.currentTimeMillis();
    }

    public void loadStrategy(EvaluationStrategy e)
    {
        paused = true;

        evaluationStrategy = e;

        paused = false;
    }

    @Override
    public void run()
    {
        while(!paused)
        {
            if (hasUpdated())
            {
                System.out.println("Modified: " + new Date(lastModified).toString());
                Evaluate();
            }
            try{
                Thread.sleep (1000L);
            }
            catch(InterruptedException e){}
        }
    }

//    public void start()
//    {
//        System.out.println("Starting: " + new Date(System.currentTimeMillis()).toString());
//        run();
//    }

    private boolean hasUpdated()
    {
        return (watchFile.lastModified() > lastModified);
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

        newFile = readHighScoreFile(watchFile);

        //
        //  OPEN AND READ OLD FILE
        //

       oldFile = readHighScoreFile(backFile);


        //Tranfer contents of new file into old file.  Make a "new" new file.
        try{
            FileOutputStream writer = new FileOutputStream(backFile);
            writer.write((new String()).getBytes());
            for(String s: newFile)
            {
            writer.write(s.getBytes());
            writer.write(System.getProperty("line.separator").getBytes());
            }
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
            }
            else if(oldFile.get(i).substring(0,4).equals("name"))
            {
                try{
                    int index = Integer.parseInt(oldFile.get(i).substring(4, oldFile.get(i).lastIndexOf('=')));

                    oldNames[index-1] = oldFile.get(i).substring(oldFile.get(i).lastIndexOf('=') + 1, oldFile.get(i).length());
                }
                catch(NumberFormatException e)
                {System.out.println(e);}
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
            }
            else if(newFile.get(i).substring(0,4).equals("name"))
            {
                try{
                    int index = Integer.parseInt(newFile.get(i).substring(4, newFile.get(i).lastIndexOf('=')));

                    newNames[index-1] = newFile.get(i).substring(newFile.get(i).lastIndexOf('=') + 1, newFile.get(i).length());
                }
                catch(NumberFormatException e)
                {System.out.println(e);}
            }
        }

        //Print differences
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

        //Get Candidate for bumping
        String bumpedOff;
        try{
            bumpedOff = oldNames[oldNames.length-1];
            System.out.println("Checking of bumped: " + bumpedOff);
        }
        catch(Exception e){System.out.println(e + "\nError: Continuing without email notification\n"); return;}

        //Determine if they are completely off the leaderboards (don't have a score higher up)
        if(newFile.size() > 0 && !Arrays.asList(newNames).contains(bumpedOff))
        {
            System.out.println(bumpedOff + " has been bumped off the leaderboard!\n");
            String sendAddress = bumpedOff + "@ODU.EDU";
            if(sendEmail)
            {
                System.out.println("Sending Message to: " + sendAddress);
                try{
                    //Send message
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

        //update for next time
        lastModified = watchFile.lastModified();
    }

    private String bumpedOffMessage(String bumped)
    {
        String message = null;
        try {
            message = readFile(evaluationStrategy.getEmailBodyFile().getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private ArrayList<String> readHighScoreFile(File highscoreFile)
    {
        BufferedReader fileReader = null;
        ArrayList<String> fileContentsByLine = new ArrayList<String>();
        try
        {
            fileReader = new BufferedReader(new FileReader(highscoreFile));
            System.out.println("Opened " + highscoreFile.getName() + " successfully");
        }
        catch(java.io.FileNotFoundException FNF){System.out.println("Error opening " + highscoreFile);}

        try {
            String lineOld = fileReader.readLine();

            while (lineOld != null)
            {
                fileContentsByLine.add(lineOld);
                lineOld = fileReader.readLine();
            }
        }
        catch(Exception IOE)
        {
            System.out.println("Something happened");
        }
        System.out.println(fileContentsByLine.size() + " lines read");
        try{
            fileReader.close();
            System.out.println("Closed " + highscoreFile.getName() + " successfully\n");
        }
        catch(java.io.IOException IOE){System.out.println("Error in closing " + highscoreFile.getName());}

        return fileContentsByLine;
    }


    private void PrintArray(String[] array)
    {
        for(int i = 0; i<array.length;i++)
        {
            System.out.println(array[i]);
        }
    }
}