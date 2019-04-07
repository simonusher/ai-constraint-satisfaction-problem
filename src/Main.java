public class Main {
    public static void main(String[] args) {
        Problem problem = new FutoshikiProblem();
        problem.load("test_futo_9_1.txt");
        if(problem.isLoaded()){
            ForwardCheckingSolver solver = new ForwardCheckingSolver(problem);
//            BacktrackingSolver solver = new BacktrackingSolver(problem);
            long startTime = System.nanoTime();
            solver.solve();
            long endTime = System.nanoTime();
            long elapsedTime = endTime - startTime;
            System.out.println("Execution time in seconds: " + elapsedTime / 1000000000f);
            problem.saveSolutionsToFile();
            System.out.println(solver.getNumberOfCalls());
        }
    }
}
