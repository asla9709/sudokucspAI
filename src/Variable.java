import java.util.ArrayList;
import java.util.HashSet;

public class Variable {
    public int value;
    public String name;
    public HashSet<Integer> domain = new HashSet<>();
    public ArrayList<Constraint> constraints = new ArrayList<>();

    Variable(int value){
        this.value = value;
        if(value == 0){
            for (int i = 1; i <= 9; i++) {
                domain.add(i);
            }
        } else {
            this.domain.add(value);
        }
    }

    void setValue(int value, ChangeList cl){
        if(!domain.contains(value)){
            cl.isFailure = true;
            return;
        }

        var domainArr = new Integer[domain.size()];
        domain.toArray(domainArr);
        for(Integer i : domainArr){
            if(i == value) continue;
            domain.remove(i);
            removeFromDomain(i, cl);
        }
    }

    void removeFromDomain(Integer value, ChangeList cl){
        if(!domain.contains(value)) return;
        domain.remove(value);
        if (!cl.domainRemovals.containsKey(this)) {
            cl.domainRemovals.put(this, new HashSet<>());
        }
        cl.domainRemovals.get(this).add(value);
    }

    boolean doArcConsistency(ChangeList cl){
        for(Constraint constraint: constraints){
            if(!constraint.inferDomains(cl)) return false;
            assert true;
        }
        return true;
    }

    int getDegreeHeuristic(){
        //look at other variables affected by constraints
        //count number of variables that are unnasigned
        int degree = 0;
        for(Constraint constraint: constraints){
            for(Variable v: constraint.vars){
                if(v.domain.size() > 1) degree++;
            }
        }
        return degree;
    }

    @Override
    public String toString() {
        return "Variable{" +
                "name=" + name +
                "value=" + value +
                ", domain=" + domain +
                '}';
    }
}
