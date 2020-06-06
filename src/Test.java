import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Test {
    public static void main(String[] args) {
//        Variable X1 = new Variable(1);
//        Variable X2 = new Variable(0);
//        Variable X3 = new Variable(0);
//        Variable X4 = new Variable(0);
//
//        var vs = new Variable[]{X1, X2, X3, X4};
//
//        for(var v: vs) {
//            System.out.println(v);
//        }
//
//        AllDiff C1 = new AllDiff(new Variable[]{X1, X2, X3, X4});
//
//        ChangeList c = C1.inferDomains();
//
//        for(var v: vs) {
//            System.out.println(v);
//        }
//
//        c.undo();
//
//        for(var v: vs) {
//            System.out.println(v);
//        }
//        //SHould be back to the start
//        return;

        var H1 =  new HashMap<Integer, HashSet<Integer>>();
        var H2 =  new HashMap<Integer, HashSet<Integer>>();

        H1.put(1, new HashSet<>());
        H1.get(1).add(1);
        H1.get(1).add(2);
        H1.put(41, new HashSet<>());

        H2.put(1, new HashSet<>());
        H2.get(1).add(3);
        H2.get(1).add(4);
        H2.put(2, new HashSet<>());


        H2.forEach(
                (key, value) -> H1.merge(
                        key,
                        value,
                        (hs1, hs2) -> {
                            hs1.addAll(hs2);
                            return hs1;
                        }
                )
        );

        return;

    }
}
