import java.util.ArrayList;
import java.util.Arrays;

public class Variable {
    public int value;
    public ArrayList<Integer> domain = new ArrayList<>();

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

    @Override
    public String toString() {
        return "Variable{" +
                "value=" + value +
                ", domain=" + domain +
                '}';
    }
}
