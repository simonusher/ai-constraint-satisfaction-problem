public class Main {
    public static void main(String[] args) {
//        Problem problem = new FutoshikiProblem();
//        problem.load("test_futo_8_1.txt");
        Problem problem = new SkyscrapperProblem();
        problem.load("test_sky_5_0.txt");
        if(problem.isLoaded()){
//            ForwardCheckingSolver solver = new ForwardCheckingSolver(problem);
            BacktrackingSolver solver = new BacktrackingSolver(problem);
            long startTime = System.nanoTime();
            solver.solve();
            long endTime = System.nanoTime();
            long elapsedTime = endTime - startTime;
            System.out.println("Execution time in seconds: " + elapsedTime / 1000000000f);
            System.out.println(solver.getNumberOfCalls());
        }
    }
}
