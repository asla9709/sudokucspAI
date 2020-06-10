/***************************************************************/
/* Aakif Aslam and Manning Zhao                                */
/* CS-481, Spring 2020                                         */
/* Lab Assignment 3                                            */
/* Purpose: Create a sudoku solver using CSP techniques        */
/* main Class: Get sudoku puzzle from user's input file and    */
/*             then generate a solution in a output file       */
/***************************************************************/
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    /***************************************************************/
    /* Method: main                                                */
    /* Purpose: Solve the puzzle in the input file and then print  */
    /*          out the solution in the output file                */
    /* Parameters: String[]args: names of the input file and       */
    /*             output file                                     */
    /* Returns: None                                               */
    /***************************************************************/
    public static void main(String[] args) {
        //check if the command line input is valid
        if (args.length < 2) {
            System.out.printf("Please run this correctly thanks");
            return;
        }

        //read in the file names
        String inputFile, outputFile;
        inputFile = args[0];
        outputFile = args[1];

        //assign the variables and solve the puzzle
        Variable[][] sBoard = solveBoard(inputFile);
        if (sBoard == null) return;

        //printout the solution
        System.out.println("Solution: ");
        System.out.println(printBoard(sBoard));
    }

    /***************************************************************/
    /* Method: solveBoard                                          */
    /* Purpose: Use CSP techniques to solve a given Sudoku puzzle  */
    /* Parameters: String inputFile: A given Sudoku puzzle         */
    /* Returns: Variable[][]: The solution board, a 2d array of    */
    /*          variables                                          */
    /***************************************************************/
    public static Variable[][] solveBoard(String inputFile) {
        var sBoard = new Variable[9][9];    //board to load in the puzzle
        int index = 0;  //a counter to count the number of variables in the input file

        //read the input file to load the given Sudoku puzzle in sBoard
        try {
            //scan the input file
            Scanner sc = new Scanner(new File(inputFile));
            //read every integer
            while (sc.hasNextInt()) {
                sBoard[index / 9][index % 9] = new Variable(sc.nextInt());
                index++;
            }
            //close the file
            sc.close();
        } catch (FileNotFoundException e) {
            System.out.println("Input file not found :(");
            return null;
        }

        //if the number of integers(Variables) is not 81, it is an invalid input file
        if (index != 9 * 9) {
            System.out.println("Input file has Incorrect Format :(");
            return null;
        }

        ArrayList<Constraint> constraints = new ArrayList<>();  //holds a list of constraints

        //assign variable name to each Variable on the 2d array board
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                char a = 'A';
                sBoard[row][col].name = row + "," + col;
            }
        }

        //generate constraints
        //row constraints
        for (int row = 0; row < 9; row++) {
            var rowConstriant = new AllDiff("Row " + row, sBoard[row]);
            constraints.add(rowConstriant);
        }
        //column constraints
        for (int col = 0; col < 9; col++) {
            var columnConstraint = new AllDiff("Col " + col);
            for (int row = 0; row < 9; row++) {
                columnConstraint.add(sBoard[row][col]);
            }
            constraints.add(columnConstraint);
        }
        //square constraints
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

        //print out the puzzle board
        System.out.println(printBoard(sBoard));

        //preprocessing step
        //do inferences until you can't no more
        ChangeList cl;  //A list to hold each variable's domain changes
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

        //check if the solution board failed to satisfy all the constraints
        boolean boardFailed = isBoardFailed(constraints);

        //if the solution failed, try again
        if (boardFailed) Counter.counter2++;

        System.out.println("Count: " + Counter.counter1);

        //print out the solution board
        return sBoard;
    }

    /***************************************************************/
    /* Method: isBoardFailed                                       */
    /* Purpose: Check if the solution board failed to satisfy      */
    /*          all constraints                                    */
    /* Parameters: ArrayList<> constraints: list of constraints    */
    /* Returns: boolean: returns true if board failed              */
    /***************************************************************/
    private static boolean isBoardFailed(ArrayList<Constraint> constraints) {
        boolean boardFailed = false;
        for (Constraint constr : constraints) {
            if (!constr.isConsistent()) {
                System.out.println("Failed Constraint " + constr.name);
                System.out.println(constr.inferDomains(new ChangeList()));
                boardFailed = true;
            }
        }
        return boardFailed;
    }

    /***************************************************************/
    /* Method: printBoard                                          */
    /* Purpose: Generate a String representation for a given       */
    /*          2d array Sudoku board                              */
    /* Parameters: Variable[][] board: A Sudoku board              */
    /* Returns: String: The Sudoku board in a String format        */
    /***************************************************************/
    static String printBoard(Variable[][] board) {
        String str = "";
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int value = board[row][col].getValue();
                str += (value != 0) ? value : "_";
                str += " ";
            }
            str += "\n";
        }
        return str;
    }

    /***************************************************************/
    /* Method: selectUnassignedVariable                            */
    /* Purpose: Select the next Variable to assign using the       */
    /*          Minimum Value Heuristic and Degree Heuristic       */
    /* Parameters: variable[][] board: The current Sudoku board    */
    /* Returns: Variable: The next Variable to be assigned         */
    /***************************************************************/
    static Variable selectUnassignedVariable(Variable[][] board) {
        //First, pick variable with minimum remaining values (smallest domain)
        int smallestDomain = 1000;  //minimum number of domains
        ArrayList<Variable> selectedVariables = new ArrayList<>();  // list of variables with the minimum domain
        for (var row : board) {
            for (var currentVar : row) {
                int domain_size = currentVar.domain.size();
                //variable is assigned, skip
                if (domain_size <= 1) continue;
                //variable has smaller domain
                if (domain_size < smallestDomain) {
                    selectedVariables.clear();
                    selectedVariables.add(currentVar);
                }
                //add variable to the list of variables with minimum domain
                if (domain_size == smallestDomain) {
                    selectedVariables.add(currentVar);
                }
            }
        }

        //return the variable with the minimum number of domain values
        if (selectedVariables.size() == 1) {
            return selectedVariables.get(0);
        }

        //multiple variable with the smallest number of domain
        //In case of a tie, use Degree Heuristic to select the best variable
        Variable finalVariable = selectedVariables.get(0);
        int largestDegree = finalVariable.getDegreeHeuristic();
        for (var currentVar : selectedVariables) {
            if (currentVar.getDegreeHeuristic() > largestDegree) {
                largestDegree = currentVar.getDegreeHeuristic();
                finalVariable = currentVar;
            }
        }

        return finalVariable;
    }

    /***************************************************************/
    /* Method: assignmentComplete                                  */
    /* Purpose: Check if every variable on the board is assigned   */
    /* Parameters: Variable[][] board: The current Sudoku board    */
    /* Returns: boolean: true if all variables are assigned        */
    /***************************************************************/
    static boolean assignmentComplete(Variable[][] board) {
        for (var row : board) {
            for (var currentVar : row) {
                if (currentVar.domain.size() > 1) return false;
            }
        }
        return true;
    }

    /***************************************************************/
    /* Method: constraintsSatisfied                                */
    /* Purpose: Check if all the constraints are satisfied         */
    /* Parameters: ArrayList<> constraints: list of constraints    */
    /* Returns: boolean: true if all constraints are satisfied     */
    /***************************************************************/
    static boolean constraintsSatisfied(ArrayList<Constraint> constraints) {
        for (Constraint constr : constraints) {
            if (!constr.isConsistent()) return false;
        }
        return true;
    }

    /***************************************************************/
    /* Method: Backtrack                                           */
    /* Purpose: Use backtrack to find a Sudoku solution            */
    /* Parameters: Variable[][] variables: The current variables   */
    /*             on the Sudoku board                             */
    /*             ArrayList<> constraints: list of constraints    */
    /* Returns: boolean: True if a Sudoku solution is found        */
    /***************************************************************/
    static boolean Backtrack(Variable[][] variables, ArrayList<Constraint> constraints) {
        //if all variables are assigned, check if that solution satisfy all the constraints
        if (assignmentComplete(variables)) return constraintsSatisfied(constraints);
        //find the next variable to be assigned
        Variable currentVariable = selectUnassignedVariable(variables);

        //System.out.println("Current Variable: " + currentVariable); //TEST
        //System.out.println("Board Before Inferences:"); //TEST
        //System.out.println(printBoard(variables)); //TEST

        //increase the counter every time the program backtracks
        Counter.counter1++;

        //look through the domains of the current variable
        Integer[] v_domain = new Integer[currentVariable.domain.size()];
        currentVariable.domain.toArray(v_domain);
        for (Integer i : v_domain) {
            //System.out.println("Selected value: " + i); //TEST

            ChangeList cl = new ChangeList();   //list to hold all variable's domain changes

            //Use the first domain value to set the value for the current variable
            currentVariable.setValue(i, cl);
            //check arc consistency after assigning a value
            boolean isConsistent = currentVariable.doArcConsistency(cl);
            //System.out.println("Inference was a " + (isConsistent?"success":"failure")); //TEST
            //System.out.println("Board is " + (isBoardFailed(constraints)?"failed":"ok"));
            //System.out.println("Board after Inferences:"); //TEST
            //System.out.println(printBoard(variables)); //TEST

            //perform inferences
            if (isConsistent) {
                for (Constraint constr : constraints) {
                    if (!constr.inferDomains(cl)) isConsistent = false;
                }
            }

            //if it is consistent, assign the next variable
            if (isConsistent) {
                boolean result = Backtrack(variables, constraints);
                if (result) return true;
            }

            //if it is not consistent, backtracks, undo the current variable assignment
            cl.undo();
            //System.out.println("Board after Undo:"); //TEST
            //System.out.println(printBoard(variables)); //TEST
        }
        return false;
    }

}

/***************************************************************/
/* Counter Class: Counters for...                              */
/***************************************************************/
class Counter {
    static int counter1 = 0;
    static int counter2 = 0;
}
