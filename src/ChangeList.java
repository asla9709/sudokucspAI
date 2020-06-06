import java.util.HashMap;
import java.util.HashSet;

public class ChangeList {
    public boolean isFailure = false;
    public HashMap<Variable, HashSet<Integer>> domainRemovals = new HashMap<>();

    ChangeList() {
    }

    int size(){
        return domainRemovals.size();
    }

    void combine(ChangeList b){
        isFailure = this.isFailure || b.isFailure;
        b.domainRemovals.forEach(
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

    void undo() {
        for (Variable v : domainRemovals.keySet()) {
            v.domain.addAll(domainRemovals.get(v));
        }
//        for (Variable v : setValue.keySet()) {
//            v.value = 0;
//        }
    }
}
