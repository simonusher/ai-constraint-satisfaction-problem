public class Main {
    public static void main(String[] args) {
        Problem problem = new FutoshikiProblem();
        problem.load("test_futo_8_1.txt");
        if(problem.isLoaded()){
            ForwardCheckingSolver solver = new ForwardCheckingSolver(problem);
//            BacktrackingSolver solver = new BacktrackingSolver(problem);
            solver.solve();
            problem.saveSolutionsToFile();
            System.out.println(solver.getNumberOfCalls());
        }
    }
}
