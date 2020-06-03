import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class AllDiff {
    public ArrayList<Variable> vars = new ArrayList<>();

    AllDiff(Variable[] vars){
        this.vars = new ArrayList<Variable>(Arrays.asList(vars));
    }

    public AllDiff() {
        this.vars = new ArrayList<>();
    }

    boolean isSatisfied(){
        var hs = new HashSet<Integer>();
        for(var v : vars){
            if (v.value == 0) continue;
            if(hs.contains(v.value)){
                return false;
            }
            hs.add(v.value);
        }
        return true;
    }

    Variable getVarSingletonDomain(ArrayList<Variable> varlist){
        for(var v: varlist){
            if (v.domain.size() == 1)
                return v;
        }
        return null;
    }

    boolean reduceDomains(){
        return reduceDomains(new ArrayList<>(vars));
    }

    boolean reduceDomains(ArrayList<Variable> varlist){
        Variable v = getVarSingletonDomain(varlist);
        if (v == null) return true;
        varlist.remove(v);

        Integer v_value = v.domain.get(0);
        //remove v_value from domains of all other variables
        for(var x: vars){
            if(x == v) continue;
            x.domain.remove(v_value);
            if(x.domain.size() == 0){
                return false;
            }
        }
        return reduceDomains(varlist);
    }
}
