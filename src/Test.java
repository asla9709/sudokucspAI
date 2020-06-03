public class Test {
    public static void main(String[] args) {
        Variable X1 = new Variable(1);
        Variable X2 = new Variable(0);
        Variable X3 = new Variable(0);
        Variable X4 = new Variable(0);

        AllDiff C1 = new AllDiff(new Variable[]{X1, X2, X3, X4});

        C1.reduceDomains();
    }
}
