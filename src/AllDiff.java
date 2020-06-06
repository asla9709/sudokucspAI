import java.nio.channels.AsynchronousChannelGroup;
import java.util.ArrayList;
import java.util.HashSet;

public class AllDiff extends Constraint{

    public AllDiff(Variable[] variables) {
        super(variables);
    }

    public AllDiff() {
        super();
    }

    boolean isConsistent(){
        var hs = new HashSet<Integer>();
        for(var v : vars){
            if (v.domain.size() > 1) continue; //if unset, continue
            int v_value = v.domain.iterator().next();
            if(hs.contains(v_value)){
                return false;
            }
            hs.add(v_value);
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

    boolean inferDomains(ChangeList cl){
        return inferDomains(new ArrayList<>(vars), cl);
    }

    boolean inferDomains(ArrayList<Variable> varlist, ChangeList cl){
        if(!isConsistent()) return false;
        Variable v = getVarSingletonDomain(varlist);
        if (v == null) return true;
        varlist.remove(v);

        Integer v_value = v.domain.iterator().next();
        //remove v_value from domains of all other variables
        for(var x: varlist){
            if(x == v) continue;
            x.removeFromDomain(v_value, cl);
            if(x.domain.size() == 0){
                cl.isFailure = true;
                return false;
            }
        }
        return inferDomains(varlist, cl);
    }
}
