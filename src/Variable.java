/***************************************************************/
/* Aakif Aslam and Manning Zhao                                */
/* CS-481, Spring 2020                                         */
/* Lab Assignment 3                                            */
/* Variable Class: Represents a variable of the Constraint     */
/*                 Satisfaction Problem                        */
/***************************************************************/
import java.util.ArrayList;
import java.util.HashSet;

public class Variable {
    public String name;     //name of the variable
    public HashSet<Integer> domain = new HashSet<>();   //list of domains for a variable
    public ArrayList<Constraint> constraints = new ArrayList<>();   //list of constraints this variable is involved in

    /***************************************************************/
    /* Method: Variable                                            */
    /* Purpose: Constructor                                        */
    /* Parameters: int value: number value of the current variable */
    /* Returns: None                                               */
    /***************************************************************/
    Variable(int value){
        //if value is 0, this variable is unassigned, domain is 1-9
        if(value == 0){
            for (int index = 1; index <= 9; index++) {
                domain.add(index);
            }
        } else {
            //a valid value is assigned, variable's domain is set to the assigned value
            this.domain.add(value);
        }
    }

    /***************************************************************/
    /* Method: setValue                                            */
    /* Purpose: Set the value of a variable                        */
    /* Parameters: int value: Value assigned to a variable         */
    /*             ChangeList cl: holds the values that were       */
    /*             removed from the domain of a variable           */
    /* Returns: None                                               */
    /***************************************************************/
    void setValue(int value, ChangeList cl){
        //assign failure if the assigned value is not a valid value of the variable's domain
        if(!domain.contains(value)){
            cl.isFailure = true;
            return;
        }

        //get the domain array
        var domainArr = new Integer[domain.size()];
        domain.toArray(domainArr);
        //remove all domain values except for the value that is assigned to the variable
        for(Integer domainValue : domainArr){
            if(domainValue == value) continue;
            removeFromDomain(domainValue, cl);
        }
    }

    /***************************************************************/
    /* Method: getValue                                            */
    /* Purpose: Returns the value of a variable                    */
    /* Parameters: None                                            */
    /* Returns: int: the value of a variable                       */
    /***************************************************************/
    int getValue(){
        //returns 0 if unassigned
        if(domain.size() != 1) return 0;
        //else return the value
        return domain.iterator().next();
    }

    /***************************************************************/
    /* Method: removeFromDomain                                    */
    /* Purpose: Remove a value from the domain of a variable       */
    /* Parameters: Integer value: name of the constraint           */
    /*             ChangeList cl: list of variables                */
    /* Returns: None                                               */
    /***************************************************************/
    void removeFromDomain(Integer value, ChangeList cl){
        if(!domain.contains(value)) return;
        domain.remove(value);
        if (!cl.domainRemovals.containsKey(this)) {
            cl.domainRemovals.put(this, new HashSet<>());
        }
        cl.domainRemovals.get(this).add(value);
    }

    /***************************************************************/
    /* Method: doArcConsistency                                    */
    /* Purpose: Check arc consistency of a variable                */
    /* Parameters: ChangeList cl: list of domain removals          */
    /* Returns: boolean: true if the variable is arc-consistent    */
    /***************************************************************/
    boolean doArcConsistency(ChangeList cl){
        for(Constraint constraint: constraints){
            if(!constraint.inferDomains(cl)) return false;
        }
        return true;
    }

    /***************************************************************/
    /* Method: getDegreeHeuristic                                  */
    /* Purpose: Returns the number of unassigned variables         */
    /*          affected by the constraints of a variable. This is */
    /*          the degree heuristic score of a variable.          */
    /* Parameters: None                                            */
    /* Returns: int: the degree heuristic score of a variable      */
    /***************************************************************/
    int getDegreeHeuristic(){
        //look at other variables affected by constraints
        //count number of variables that are unnasigned
        int degree = 0;
        for(Constraint constraint: constraints){
            for(Variable constrVariable: constraint.vars){
                if(constrVariable.domain.size() > 1) degree++;
            }
        }
        return degree;
    }

    /***************************************************************/
    /* Method: toString                                            */
    /* Purpose: Returns the name and domain of a variable in       */
    /*          string format                                      */
    /* Parameters: None                                            */
    /* Returns: String:                                            */
    /***************************************************************/
    @Override
    public String toString() {
        return "Variable{" +
                "name=" + name +
                ", domain=" + domain +
                '}';
    }
}
