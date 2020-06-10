/***************************************************************/
/* Aakif Aslam and Manning Zhao                                */
/* CS-481, Spring 2020                                         */
/* Lab Assignment 3                                            */
/* Constraint Class: Represents a constraint of the Constraint */
/*                   Satisfaction Problem                      */
/***************************************************************/
import java.util.ArrayList;

public abstract class Constraint {
    //list of variables that are restricted by this constraint
    protected ArrayList<Variable> vars = new ArrayList<>();
    public String name; //name of the constraint

    /***************************************************************/
    /* Method: Constraint                                          */
    /* Purpose: Constructor                                        */
    /* Parameters: String name: name of the constraint             */
    /*             Variable[] vars: list of variables              */
    /* Returns: None                                               */
    /***************************************************************/
    Constraint(String name, Variable[] vars){
        this.name = name;
        for(var currentVar : vars){
            this.add(currentVar);
        }
    }

    /***************************************************************/
    /* Method: Constraint                                          */
    /* Purpose: Constructor                                        */
    /* Parameters: String name: name of the constraint             */
    /* Returns: None                                               */
    /***************************************************************/
    public Constraint(String name) {
        this.name = name;
    }

    /***************************************************************/
    /* Method: add                                                 */
    /* Purpose: Add a variable to this constraint                  */
    /* Parameters: Variable varToAdd: The variable to be added     */
    /* Returns: None                                               */
    /***************************************************************/
    void add(Variable varToAdd){
        if(!vars.contains(varToAdd)) {
            vars.add(varToAdd);
            varToAdd.constraints.add(this);
        }
    }

    /***************************************************************/
    /* Method: isConsistent                                        */
    /* Purpose: Check if a constraint is arc consistent            */
    /* Parameters: None                                            */
    /* Returns: boolean: true if this constraint is consistent     */
    /***************************************************************/
    abstract boolean isConsistent();

    /***************************************************************/
    /* Method: inferDomains                                        */
    /* Purpose: Perform inference on variables within a constraint */
    /* Parameters: ChangeList cl: the list of domain removals      */
    /* Returns: boolean: false if inference failed                 */
    /***************************************************************/
    abstract boolean inferDomains(ChangeList cl);
}
