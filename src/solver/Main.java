package solver;

import solver.Cube;

public class Main {
    public static void main(String[] args) {

        Cube cube = new Cube();
        cube.resetCube();

        cube.scramble();

        Roux roux = new Roux(cube);
        roux.solve(cube);
    }
}