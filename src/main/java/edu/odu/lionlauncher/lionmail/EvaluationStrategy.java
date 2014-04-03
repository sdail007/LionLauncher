package edu.odu.lionlauncher.lionmail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by trueLove on 4/2/14.
 */
public class EvaluationStrategy {


    private File highscoresFile;
    private File highscoresBackfile;
    private File emailBodyFile;

    private ArrayList<EvaluationRule> evaluationRules;

    public EvaluationStrategy(File hsfile, File backfile, File messageBody)
    {
        highscoresFile = hsfile;
        highscoresBackfile = backfile;
        emailBodyFile = messageBody;
    }

    public File getHighscoresFile() {
        return highscoresFile;
    }

    public void setHighscoresFile(File highscoresFile) {
        this.highscoresFile = highscoresFile;
    }

    public File getHighscoresBackfile() {
        return highscoresBackfile;
    }

    public void setHighscoresBackfile(File highscoresBackfile) {
        this.highscoresBackfile = highscoresBackfile;
    }

    public File getEmailBodyFile() {
        return emailBodyFile;
    }

    public void setEmailBodyFile(File emailBodyFile) {
        this.emailBodyFile = emailBodyFile;
    }

    public ArrayList<EvaluationRule> getEvaluationRules() {
        return evaluationRules;
    }

    public void setEvaluationRules(ArrayList<EvaluationRule> evaluationRules) {
        this.evaluationRules = evaluationRules;
    }
}
