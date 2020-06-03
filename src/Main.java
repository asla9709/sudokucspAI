import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.management.BufferPoolMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if(args.length < 2){
            System.out.printf("Please run this correctly thanks");
            return;
        }

        String inputFile, outputFile;
        inputFile = args[0];
        outputFile = args[1];

        var sBoard = new Variable[9][9];
        int index = 0;

        try {
            Scanner sc = new Scanner(new File(inputFile));
            while (sc.hasNextInt()){
               sBoard[index/9][index%9] = new Variable(sc.nextInt());
               index++;
            }
            sc.close();
        } catch (FileNotFoundException e) {
            System.out.println("Input file not found :(");
            return;
        }

        if (index != 9*9) {
            System.out.println("Input file has Incorrect Format :(");
            return;
        }

        var constraints = new ArrayList<AllDiff>();

        //generate constraints
        //rows
        for (int row = 0; row < 9; row++) {
            var c = new AllDiff(sBoard[row]);
            constraints.add(c);
        }
        //columns
        for (int col = 0; col < 9; col++) {
            var c = new AllDiff();
            for (int row = 0; row < 9; row++) {
                c.vars.add(sBoard[row][col]);
            }
            constraints.add(c);
        }
        //squares
        var sq_constraints = new AllDiff[9];
        for (int i = 0; i < 9; i++) {
           sq_constraints[i] = new AllDiff();
        }
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int sq = (row / 3) * 3 + col/3;
                sq_constraints[sq].vars.add(sBoard[row][col]);
            }
        }
        constraints.addAll(Arrays.asList(sq_constraints));

        System.out.println(printBoard(sBoard));


        while(true) {
            for (var c : constraints) {
                c.reduceDomains();
            }

            System.out.println(printBoard(sBoard));
        }





        //read in the file
        //assign the variables

        //solve the thing

        //printout the solution

    }

    static String printBoard(Variable[][] board){
        String s = "";
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if(board[row][col].domain.size() == 1){
                    s += board[row][col].domain.get(0);
                } else {
                    s += "_";
                }
                s += " ";
            }
            s += "\n";
        }
        return s;
    }

}
