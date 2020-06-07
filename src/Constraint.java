import java.util.ArrayList;

public abstract class Constraint {
    protected ArrayList<Variable> vars = new ArrayList<>();
    public String name;

    Constraint(String name, Variable[] vars){
        this.name = name;
        for(var v : vars){
            this.add(v);
        }
    }

    public Constraint(String name) {
        this.name = name;
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
