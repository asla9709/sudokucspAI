/***************************************************************/
/* Aakif Aslam and Manning Zhao                                */
/* CS-481, Spring 2020                                         */
/* Lab Assignment 3                                            */
/* AllDiff Class: A type of constraint. Variables in a AllDiff */
/*                constraint must all have different assigned  */
/*                values.                                      */
/***************************************************************/
import java.nio.channels.AsynchronousChannelGroup;
import java.util.ArrayList;
import java.util.HashSet;

public class AllDiff extends Constraint{

    /***************************************************************/
    /* Method: AllDiff                                             */
    /* Purpose: Constructor                                        */
    /* Parameters: String name: name of the AllDiff constraint     */
    /*             Variable[] variables: list of variables that    */
    /*             are restricted by the AllDiff constraint.       */
    /* Returns: None                                               */
    /***************************************************************/
    public AllDiff(String name, Variable[] variables) {
        super(name, variables);
    }

    /***************************************************************/
    /* Method: AllDiff                                             */
    /* Purpose: Constructor                                        */
    /* Parameters: String name: name of the AllDiff constraint     */
    /* Returns: None                                               */
    /***************************************************************/
    public AllDiff(String name) {
        super(name);
    }

    /***************************************************************/
    /* Method: isConsistent                                        */
    /* Purpose: Check if the values assigned to the constraint's   */
    /*          variables are consistent with the AllDiff          */
    /*          constraint. That is all values are different.      */
    /* Parameters: None                                            */
    /* Returns: boolean: true if all the values are different      */
    /***************************************************************/
    boolean isConsistent(){
        //list of variable values of the current constraint
        var hs = new HashSet<Integer>();
        for(var currentVar : vars){
            if (currentVar.domain.size() > 1) continue; //if unset, continue
            int v_value = currentVar.domain.iterator().next();
            //add assigned variable value to the hash set
            if(hs.contains(v_value)){
                return false;
            }
            hs.add(v_value);
        }
        return true;
    }

    /***************************************************************/
    /* Method: getVarSingletonDomain                               */
    /* Purpose: Returns an assigned variable. An assigned variable */
    /*          has a domain of size one.                          */
    /* Parameters: ArrayList<> varList: list of variables          */
    /* Returns: Variable: A variable with a domain of size one     */
    /***************************************************************/
    Variable getVarSingletonDomain(ArrayList<Variable> varList){
        for(var currentVar: varList){
            if (currentVar.domain.size() == 1)
                return currentVar;
        }
        return null;
    }

    /***************************************************************/
    /* Method: inferDomains                                        */
    /* Purpose: Perform inference on the variables in a AllDiff    */
    /*          constraint. Using constraints to limit legal       */
    /*          values for the variables                           */
    /* Parameters: ChangeList cl: list of domain removals          */
    /* Returns: boolean: true if inference is successful           */
    /***************************************************************/
    boolean inferDomains(ChangeList cl){
        return inferDomains(new ArrayList<>(vars), cl);
    }

    /***************************************************************/
    /* Method: inferDomains                                        */
    /* Purpose: Perform inference on the variables in a AllDiff    */
    /*          constraint. Using constraints to limit legal       */
    /*          values for the variables                           */
    /* Parameters: ArrayList<> varList: list of variables          */
    /*             ChangeList cl: list of domain removals          */
    /* Returns: boolean: true if inference is successful           */
    /***************************************************************/
    boolean inferDomains(ArrayList<Variable> varList, ChangeList cl){

        //returns false if there are more than one variable with the same value assigned
        if(!isConsistent()) return false;

        //get an assigned variable from the constraint's list of variables
        Variable v = getVarSingletonDomain(varList);
        if (v == null) return isConsistent();
        //remove assigned variables from the current constraint's list of variables
        varList.remove(v);

        Integer v_value = v.domain.iterator().next();
        //remove v_value from domains of all other variables
        for(var x: varList){
            if(x == v) continue;
            x.removeFromDomain(v_value, cl);
            if(x.domain.size() == 0){
                cl.isFailure = true;
                return false;
            }
        }
        return inferDomains(varList, cl);
    }
}
