package nist;

import java.io.*;
import java.util.*;

public class Learner implements Runnable {
    Scanner in; // for reading networks
    PrintStream out; // for printing out solutions
    String line;
    int[] input;
    int iCount = 0;
    int[] iExamplesD = new int[10];
    int[][] iExamplesDI = new int[10][65];
    
    
    public Learner(Scanner in, PrintStream out) {
        this.in = in;
        this.out = out;
    }

    public void run() {
        input = new int[65];
        line = in.nextLine();       
        while (! line.equals("quit")) {
            input[0] = 1; // for bias weight
            for (int i = 0; i < 64; i++) {
                char c = line.charAt(i);
                input[i + 1] = (c == '1') ? 1 : 0;
            }
            
            int prediction = 0;
            double iYX = 1.0;
            double iHighest=Math.log(0.0);
			/*******************************************************   
			 This assignment was to create a Naive Bayes prediction. 
			 
			********************************************************/
            for (int d = 0; d <= 9; d++) {
                for (int i = 1; i <= 64; i++) {
                	if(input[i]==1)
                		iYX *= (double)(iExamplesDI[d][i]+1)/(iExamplesD[d]+2);
                	else
                		iYX *= (double)(iExamplesD[d]-iExamplesDI[d][i]+1)/(iExamplesD[d]+2);
                }
                iYX *=(double)(iExamplesD[d]+1)/(iCount+10); 
                iYX =(double) Math.log(iYX);
                if( iYX > iHighest){
                	iHighest =(double) iYX;
                	prediction = d;
                }
                iYX = 1.0;
            }

            out.print(prediction);
            out.print('\n');

            line = in.nextLine();

            int label = 0;
            if (line.startsWith("incorrect")) {
                label = line.charAt(20) - 48;
            } else {
                label = line.charAt(18) - 48;
            }
	    
	    // use label to update naive Bayes counts
            iCount++;
            iExamplesD[label] += 1;
            for (int i = 1; i <= 64; i++) {
            	if(input[i] == 1)
            		iExamplesDI[label][i] += 1;
            } 
            
            line = in.nextLine();
        }
        in.close();
        out.close();
    }

    public static final String[] cleanImages = {
            "0000000000111100010000100100001001000010010000100100001000111100",
            "0000000000001000000010000000100000001000000010000000100000001000",
            "0000000000111100010000100000010000001000000100000010000001111110",
            "0000000000111100010000100000001000111100000000100100001000111100",
            "0000000001000100010001000100010001111110000001000000010000000100",
            "0000000001111110010000000100000001111100000000100000001001111100",
            "0000000000000000000000000000000000000000000000000000000000000000",
            "0000000000111100010000100100000001111100010000100100001000111100",
            "0000000001111110000000100000010000001000000100000010000001000000",
            "0000000000111100010000100100001000111100010000100100001000111100",
            "0000000000111100010000100100001000111110000000100100001000111100",
            "0011110001000010010000100100001001000010010000100011110000000000",
            "0001000000010000000100000001000000010000000100000001000000000000",
            "0011110001000010000001000000100000010000001000000111111000000000",
            "0011110001000010000000100011110000000010010000100011110000000000",
            "0100010001000100010001000111111000000100000001000000010000000000",
            "0111111001000000010000000111110000000010000000100111110000000000",
            "0011110001000010010000000111110001000010010000100011110000000000",
            "0111111000000010000001000000100000010000001000000100000000000000",
            "0011110001000010010000100011110001000010010000100011110000000000",
            "0011110001000010010000100011111000000010010000100011110000000000" };

    public static void main(String[] args) throws Exception {
        PipedOutputStream pipeout = new PipedOutputStream();
        PipedInputStream pipein;
        try {
            pipein = new PipedInputStream(pipeout);
        } catch (Exception e) {
            throw new RuntimeException("pipe failed " + e);
        }
        Scanner agentIn = new Scanner(pipein);
        PrintStream printToAgent = new PrintStream(pipeout, true);
        pipeout = new PipedOutputStream();
        try {
            pipein = new PipedInputStream(pipeout);
        } catch (Exception e) {
            throw new RuntimeException("pipe failed " + e);
        }
        Scanner readFromAgent = new Scanner(new InputStreamReader(pipein));
        PrintStream agentOut = new PrintStream(pipeout, true);

        Runnable agent = new Learner(agentIn, agentOut);
        Thread athread = new Thread(agent);
        athread.start();

        int correct = 0, incorrect = 0;
        for (int i = 0; i < 1000; i++) {
            String image = cleanImages[i % 20];
            int label = i % 10;
            printToAgent.print(image);
            printToAgent.print('\n');
            // System.out.println(image);
            while (!readFromAgent.hasNext()) {
                Thread.sleep(0);
            }
            int prediction = readFromAgent.nextInt();
            String line = "";
            if (prediction == label) {
                line += "correct ";
                correct++;
            } else {
                line += "incorrect ";
                incorrect++;
            }
            line += String.format("(label is %d, error rate = %d/%d = %.2f)",
                    label, incorrect, correct + incorrect, 100
                            * (0.0 + incorrect) / (0.0 + correct + incorrect));
            printToAgent.print(line);
            printToAgent.print('\n');
            int linetest = 10;
            while (linetest < i + 1)
                linetest *= 10;
            linetest /= 10;
            if ((i + 1) % linetest == 0) {
                System.out.println(image);
                System.out.println(prediction);
                System.out.println(line);
            }
        }
        printToAgent.print("quit");
        printToAgent.print('\n');
        athread.join();
        agentIn.close();
        agentOut.close();
        readFromAgent.close();
        printToAgent.close();

    }
}
