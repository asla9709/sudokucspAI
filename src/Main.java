import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.printf("Please run this correctly thanks");
            return;
        }

        String inputFile, outputFile;
        inputFile = args[0];
        outputFile = args[1];

        Variable[][] sBoard = solveBoard(inputFile);
        if (sBoard == null) return;

        System.out.println(printBoard(sBoard));


        //read in the file
        //assign the variables

        //solve the thing

        //printout the solution

    }

    public static Variable[][] solveBoard(String inputFile) {
        var sBoard = new Variable[9][9];
        int index = 0;

        try {
            Scanner sc = new Scanner(new File(inputFile));
            while (sc.hasNextInt()) {
                sBoard[index / 9][index % 9] = new Variable(sc.nextInt());
                index++;
            }
            sc.close();
        } catch (FileNotFoundException e) {
            System.out.println("Input file not found :(");
            return null;
        }

        if (index != 9 * 9) {
            System.out.println("Input file has Incorrect Format :(");
            return null;
        }

        ArrayList<Constraint> constraints = new ArrayList<>();

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
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
                int sq = (row / 3) * 3 + col / 3;
                sq_constraints[sq].add(sBoard[row][col]);
            }
        }
        constraints.addAll(Arrays.asList(sq_constraints));

        System.out.println(printBoard(sBoard));


        //preprocessing step
        //do inferences until you can't no more
        ChangeList cl;
        do {
            cl = new ChangeList();
            for (Constraint constraint : constraints) {
                constraint.inferDomains(cl);
            }
        } while (cl.size() > 0);

        System.out.println("Board after preprocessing: ");
        System.out.println(printBoard(sBoard));

        //check if that solved the problem

        Counter.counter1 = 0;
        if (assignmentComplete(sBoard)) {
            //TODO: print board into file and exit
            System.out.println(printBoard(sBoard));
            return null;
        }

        //otherwise, continue with backtrack search
        boolean success = Backtrack(sBoard, constraints);

        if (success) {
            System.out.println("Hey I solved it\n");
            System.out.println(printBoard(sBoard));
        } else {
            System.out.println("This sudoku puzzle has no solution :(");
        }

        boolean boardFailed = isBoardFailed(constraints);

        if (boardFailed) Counter.counter2++;

        System.out.println("Count: " + Counter.counter1);
        return sBoard;
    }

    private static boolean isBoardFailed(ArrayList<Constraint> constraints) {
        boolean boardFailed = false;
        for (Constraint c : constraints) {
            if (!c.isConsistent()) {
                System.out.println("Failed Constraint " + c.name);
                System.out.println(c.inferDomains(new ChangeList()));
                boardFailed = true;
            }
        }
        return boardFailed;
    }

    static String printBoard(Variable[][] board) {
        String s = "";
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int value = board[row][col].getValue();
                s += (value != 0) ? value : "_";
                s += " ";
            }
            s += "\n";
        }
        return s;
    }

    static Variable selectUnassignedVariable(Variable[][] board) {
        //First, pick variable with minimum remaining values (smallest domain)
        int smallestDomain = 1000;
        ArrayList<Variable> selectedVariables = new ArrayList<>();
        for (var row : board) {
            for (var v : row) {
                int domain_size = v.domain.size();
                if (domain_size <= 1) continue; //variable is assigned, skip
                if (domain_size < smallestDomain) { //variable has smaller domain
                    selectedVariables.clear();
                    selectedVariables.add(v);
                }
                if (domain_size == smallestDomain) {
                    selectedVariables.add(v);
                }
            }
        }

        if (selectedVariables.size() == 1) {
            return selectedVariables.get(0);
        }

        //In case of a tie, use degree heuristic to select the best variable

        Variable finalVariable = selectedVariables.get(0);
        int largestDegree = finalVariable.getDegreeHeuristic();

        for (var v : selectedVariables) {
            if (v.getDegreeHeuristic() > largestDegree) {
                largestDegree = v.getDegreeHeuristic();
                finalVariable = v;
            }
        }

        return finalVariable;
    }

    static boolean assignmentComplete(Variable[][] board) {
        for (var row : board) {
            for (var v : row) {
                if (v.domain.size() > 1) return false;
            }
        }
        return true;
    }

    static boolean constraintsSatisfied(ArrayList<Constraint> constraints) {
        for (Constraint c : constraints) {
            if (!c.isConsistent()) return false;
        }
        return true;
    }

    static boolean Backtrack(Variable[][] variables, ArrayList<Constraint> constraints) {
        if (assignmentComplete(variables)) return constraintsSatisfied(constraints);
        Variable currentVariable = selectUnassignedVariable(variables);

        //System.out.println("Current Variable: " + currentVariable); //TEST
        //System.out.println("Board Before Inferences:"); //TEST
        //System.out.println(printBoard(variables)); //TEST

        Counter.counter1++;

        Integer[] v_domain = new Integer[currentVariable.domain.size()];
        currentVariable.domain.toArray(v_domain);
        for (Integer i : v_domain) {

            //System.out.println("Selected value: " + i); //TEST

            ChangeList cl = new ChangeList();
            currentVariable.setValue(i, cl);
            boolean isConsistent = currentVariable.doArcConsistency(cl);
            //System.out.println("Inference was a " + (isConsistent?"success":"failure")); //TEST
            //System.out.println("Board is " + (isBoardFailed(constraints)?"failed":"ok"));
            //System.out.println("Board after Inferences:"); //TEST
            //System.out.println(printBoard(variables)); //TEST

            if (isConsistent) {
                for (Constraint c : constraints) {
                    if (!c.inferDomains(cl)) isConsistent = false;
                }
            }

            if (isConsistent) {
                boolean result = Backtrack(variables, constraints);
                if (result) return true;
            }
            cl.undo();
            //System.out.println("Board after Undo:"); //TEST
            //System.out.println(printBoard(variables)); //TEST
        }
        return false;
    }

}

class Counter {
    static int counter1 = 0;
    static int counter2 = 0;
}
