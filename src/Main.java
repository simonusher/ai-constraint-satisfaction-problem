import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
//        test();
        Problem sudokuProblem = new SudokuProblem("results/sudoku/");
        sudokuProblem.load("test_sudoku_0_0.txt");
        ForwardCheckingSolver solver = new ForwardCheckingSolver(sudokuProblem, true, false);
        solver.solve();
    }

    public static void test() {
        List<String> filenames = new ArrayList<>(List.of(
                "test_futo_4_0.txt",
                "test_futo_4_1.txt",
                "test_futo_4_2.txt",
                "test_futo_5_0.txt",
                "test_futo_5_1.txt",
                "test_futo_5_2.txt",
                "test_futo_6_0.txt",
                "test_futo_6_1.txt",
                "test_futo_6_2.txt",
                "test_futo_7_0.txt",
                "test_futo_7_1.txt",
                "test_futo_7_2.txt",
                "test_sky_4_0.txt",
                "test_sky_4_1.txt",
                "test_sky_4_2.txt",
                "test_sky_4_3.txt",
                "test_sky_4_4.txt",
                "test_sky_5_0.txt",
                "test_sky_5_1.txt",
                "test_sky_5_2.txt",
                "test_sky_5_3.txt",
                "test_sky_5_4.txt"
        ));

        Tester tester = new Tester(filenames, true, true);
        tester.runTests();

        filenames = new ArrayList<>(List.of(
//                "test_futo_8_0.txt",
//                "test_futo_8_1.txt",
//                "test_futo_8_2.txt",
//                "test_futo_9_0.txt",
//                "test_futo_9_1.txt",
//                "test_futo_9_2.txt",
                "test_sky_6_0.txt",
                "test_sky_6_1.txt",
                "test_sky_6_2.txt",
                "test_sky_6_4.txt",
                "test_sky_6_3.txt"
        ));
        tester = new Tester(filenames, true, true);
        tester.runTests();
    }
}
