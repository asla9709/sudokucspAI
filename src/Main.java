import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.management.BufferPoolMXBean;
import java.nio.channels.AsynchronousChannelGroup;
import java.sql.SQLOutput;
import java.time.temporal.ValueRange;
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

        for(int row = 0; row < 9; row++){
            for(int col = 0; col < 9; col++){
                char a = 'A';
                sBoard[row][col].name = row + "," + col;
            }
        }

        //generate constraints
        //rows
        for (int row = 0; row < 9; row++) {
            var c = new AllDiff("Row " + row, sBoard[row]);
            constraints.add(c);
        }
        //columns
        for (int col = 0; col < 9; col++) {
            var c = new AllDiff("Col " + col);
            for (int row = 0; row < 9; row++) {
                c.add(sBoard[row][col]);
            }
            constraints.add(c);
        }
        //squares
        var sq_constraints = new AllDiff[9];
        for (int i = 0; i < 9; i++) {
           sq_constraints[i] = new AllDiff("Square " + i);
        }
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int sq = (row / 3) * 3 + col/3;
                sq_constraints[sq].add(sBoard[row][col]);
            }
        }
        constraints.addAll(Arrays.asList(sq_constraints));

        System.out.println(printBoard(sBoard));


        //preprocessing step
        //do inferences until you can't no more
        ChangeList cl;
        do{
            cl = new ChangeList();
            for(Constraint constraint : constraints){
                constraint.inferDomains(cl);
            }
        }while(cl.size() > 0);

        //check if that solved the problem

        if(assignmentComplete(sBoard)){
            //TODO: print board into file and exit
            System.out.println(printBoard(sBoard));
            return;
        }

        //otherwise, continue with backtrack search
        boolean success = Backtrack(sBoard);

        if(success){
            System.out.println("Hey I solved it\n");
            System.out.println(printBoard(sBoard));
        } else {
            System.out.println("This sudoku puzzle has no solution :(");
        }

        for(Constraint c : constraints){
            if(!c.isConsistent()){
                System.out.println("Failed Constraint " + c.name);
                System.out.println(c.inferDomains(new ChangeList()));
            }
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
                    s += board[row][col].domain.iterator().next();
                } else {
                    s += "_";
                }
                s += " ";
            }
            s += "\n";
        }
        return s;
    }

    static Variable selectUnassignedVariable(Variable[][] board){
        //First, pick variable with minimum remaining values (smallest domain)
       int smallestDomain = 1000;
       ArrayList<Variable> selectedVariables = new ArrayList<>();
        for (var row : board) {
           for(var v: row){
               if(v.domain.size() <= 1) continue; //variable is assigned, skip
               if(v.domain.size() < smallestDomain){ //variable has smaller domain
                   selectedVariables.clear();
                   selectedVariables.add(v);
               }
               if(v.domain.size() == smallestDomain){
                   selectedVariables.add(v);
               }
           }
        }

        if(selectedVariables.size() == 1){
            return selectedVariables.get(0);
        }

        //In case of a tie, use degree heuristic to select the best variable

        Variable finalVariable = selectedVariables.get(0);
        int largestDegree = finalVariable.getDegreeHeuristic();

        for(var v: selectedVariables){
            if(v.getDegreeHeuristic() > largestDegree){
                largestDegree = v.getDegreeHeuristic();
                finalVariable = v;
            }
        }

        return finalVariable;
    }

    static boolean assignmentComplete(Variable[][] board){
        for(var row : board){
            for(var v: row){
                if(v.domain.size() > 1) return false;
            }
        }
        return true;
    }

    static boolean Backtrack(Variable[][] board){
        if(assignmentComplete(board)) return true;
        Variable v = selectUnassignedVariable(board);
        Integer[] v_domain = new Integer[v.domain.size()];
        v.domain.toArray(v_domain);
        for(Integer i : v_domain){
            ChangeList cl = new ChangeList();
            v.setValue(i, cl);
            boolean failure = v.doArcConsistency(cl);
            if(!failure){
                boolean result = Backtrack(board);
                if(result){
                    return result;
                }
            }
            cl.undo();
        }
        return false;
    };

}
