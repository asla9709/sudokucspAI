import java.util.ArrayList;

public abstract class Constraint {
    protected ArrayList<Variable> vars = new ArrayList<>();

    Constraint(Variable[] vars){
        for(var v : vars){
            this.add(v);
        }
    }

    public Constraint() {
    }

    void add(Variable v){
        if(!vars.contains(v)) {
            vars.add(v);
            v.constraints.add(this);
        }
    }

    abstract boolean isConsistent();

    abstract boolean inferDomains(ChangeList cl);
}
