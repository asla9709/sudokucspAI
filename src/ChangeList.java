/***************************************************************/
/* Aakif Aslam and Manning Zhao                                */
/* CS-481, Spring 2020                                         */
/* Lab Assignment 3                                            */
/* ChangeList Class: Records domain changes for each variable  */
/*                   on the Sudoku board in a HashMap and      */
/*                   allow these changes to be undone          */
/***************************************************************/
import java.util.HashMap;
import java.util.HashSet;

public class ChangeList {
    public boolean isFailure = false;   //True if the current change leads to a faliure
    public HashMap<Variable, HashSet<Integer>> domainRemovals = new HashMap<>();

    /***************************************************************/
    /* Method: ChangeList                                          */
    /* Purpose: Constructor                                        */
    /* Parameters: None                                            */
    /* Returns: None                                               */
    /***************************************************************/
    ChangeList() {
    }

    /***************************************************************/
    /* Method: size                                                */
    /* Purpose: Returns the size of domainRemovals                 */
    /* Parameters: None                                            */
    /* Returns: int: the size of the domainRemovals HashMap        */
    /***************************************************************/
    int size(){
        return domainRemovals.size();
    }

    /***************************************************************/
    /* Method: combine                                             */
    /* Purpose: Combine two change list together into one list     */
    /* Parameters: ChangeList cl: A list of domain removals        */
    /* Returns: None                                               */
    /***************************************************************/
    void combine(ChangeList cl){
        isFailure = this.isFailure || cl.isFailure;
        cl.domainRemovals.forEach(
                (key, value) -> domainRemovals.merge(
                        key,
                        value,
                        (hs1, hs2) -> {
                            hs1.addAll(hs2);
                            return hs1;
                        }
                )
        );
    }

//    void apply() {
//        for (Variable v : domainRemovals.keySet()) {
//            for (Integer i : domainRemovals.get(v)) {
//                v.domain.remove(i);
//            }
//        }
////        for (Variable v : setValue.keySet()) {
////            v.value = setValue.get(v);
////        }
//    }

    /***************************************************************/
    /* Method: undo                                                */
    /* Purpose: Undo the changes stored in the domainRemovals      */
    /* Parameters: None                                            */
    /* Returns: None                                               */
    /***************************************************************/
    void undo() {
        for (Variable currentVar : domainRemovals.keySet()) {
            currentVar.domain.addAll(domainRemovals.get(currentVar));
        }
//        for (Variable v : setValue.keySet()) {
//            v.value = 0;
//        }
    }
}
