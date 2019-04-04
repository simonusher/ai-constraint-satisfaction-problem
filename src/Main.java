public class Main {
    public static void main(String[] args) {
        Problem problem = new FutoshikiProblem();
        problem.load("test_futo_4_0.txt");
        if(problem.isLoaded()){
            ForwardCheckingSolver solver = new ForwardCheckingSolver(problem);
//            BacktrackingSolver solver = new BacktrackingSolver(problem);
            solver.solve();
            problem.saveSolutionsToFile();
            System.out.println(solver.getNumberOfReturns());
        }
    }
}
