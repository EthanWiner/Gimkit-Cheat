package com.GimkitCheat.main;

import com.GimkitCheat.main.Main;
import com.GimkitCheat.selenium.XPathOf;
import com.GimkitCheat.selenium.Driver;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Answer {
    public static HashMap correctly(HashMap<String, List> map, String key) {
        boolean alreadyAnswered = false;
        for(Object s : map.get(key)) {
            if(alreadyAnswered) {
                break;
            }
            for(String o : XPathOf.AnswerButtons()) {
                String answer = "";
                while(answer.equals("") && Main.repeat) {
                    answer = Driver.findText(o);
                }
//                System.out.println("Button: " + answer);
                if(answer.equals(s)) {
                    Driver.click(o);
                    boolean x = false;
                    while(!x && Main.repeat) {
                        try {
                            Driver.click(XPathOf.continueAfterCorrect);
                            x = true;
                        } catch(NoSuchElementException e){
                            ConvenientMethods.sleep(100);
                        }
                    }
                    ConvenientMethods.sleep(800);
                    return map;
                }
            }
        }
        if(!alreadyAnswered) {
            map = guessAnswer(map, key);
        }
        return map;
    }
    public static HashMap guessAnswer(HashMap<String, List> map, String question) {
        if(question.equals("")) {
//            System.out.println("fuck, there was a blank question");
            return new HashMap();
        }

        String topLeftButton = XPathOf.AnswerButtons().get(0);
        String clickedAnswer = Driver.findText(topLeftButton);
//        System.out.println("we made it to guessAnswer");
//        System.out.println("Question: " + question);
//        System.out.println("Guessed Answer: " + clickedAnswer);
        try{
            Driver.click(topLeftButton);
        } catch(ElementNotInteractableException e){
            ConvenientMethods.sleep(200);
//            System.out.println("Couldn't hit button");
            return new HashMap();
        }
        ConvenientMethods.sleep(400);
        char result = 'a';
        while(!(result == '+') && !(result == '-') && Main.repeat) {
            try {
                result = Driver.findText(XPathOf.moneyResult).charAt(0);
            } catch (IndexOutOfBoundsException e) {
                ConvenientMethods.sleep(100);
            }
        }
        //Process for when question is guessed correctly

        if(result == '+') {
//            System.out.println("guessed correctly");
//            System.out.println("Answer: " + clickedAnswer);

            try {
                List newLst = new ArrayList();
                newLst.addAll(map.get(question));
                newLst.add(clickedAnswer);
                map.put(question, newLst);
            } catch(NullPointerException e) {
                List newLst = new ArrayList();
                newLst.add(clickedAnswer);
                map.put(question, newLst);
            }
            Driver.click(XPathOf.continueAfterCorrect);
            ConvenientMethods.sleep(500);
        } else if (result == ('-')) {
//            System.out.println("Guessed incorrectly");
            Driver.click(XPathOf.showCorrectAnswer);
            ConvenientMethods.sleep(100);
            String correctAnswer = "";
            do {
                ConvenientMethods.sleep(100);
                correctAnswer = Driver.findText(XPathOf.correctAnswerLocation);
            } while(correctAnswer.equals("") && Main.repeat);
//            System.out.println("Correct Answer: " + correctAnswer);
            try {
                List newLst = new ArrayList();
                newLst.addAll(map.get(question));
                newLst.add(correctAnswer);
                map.put(question, newLst);
            } catch (NullPointerException e) {
                List newLst = Arrays.asList(correctAnswer);
                map.put(question, newLst);
            }
            Driver.click(XPathOf.continueAfterWrongAnswer);
            ConvenientMethods.sleep(300);
            }
        FileStuff.save(map);
        return map;
    }


    public static void AnswerLoop() {
        String question;
        HashMap<String, List> answers = ConvenientMethods.StartProcess();
        boolean alreadyAnswered = false;
        while(Main.repeat) {
            try {
                while (Main.repeat) {
                    //Finding the question
                    question = Driver.findText(XPathOf.question);
//                    System.out.println("Question: " + question);
                    //Answering if question is recognized
                    for (String key : answers.keySet()) {
                        if (key.equals(question)) {
//                            System.out.println("answer is known\n");
                            boolean found = false;
                            while (!found && Main.repeat)
                                try {
                                    answers = Answer.correctly(answers, question);
                                    found = true;
                                    alreadyAnswered = true;
                                } catch (StaleElementReferenceException e) {
                                    ConvenientMethods.sleep(100);
                                }
                            break;
                        }
                    }
                    if (!alreadyAnswered) {
                        HashMap newMap = Answer.guessAnswer(answers, question);
                        if(newMap.equals(new HashMap())) {
//                            System.out.println("but we made it here and continued");
                            alreadyAnswered = false;
                            continue;
                        } else {
                            answers = newMap;
                        }
                    }
                    alreadyAnswered = false;
//                    System.out.println("bottom of loop\n");
                }
            } catch(Exception e) {
                ConvenientMethods.sleep(100);
                //System.out.println("there was an error");
                alreadyAnswered = false;
            }
        }
        Driver.driver.quit();
        Main.finished = true;
    }

}



