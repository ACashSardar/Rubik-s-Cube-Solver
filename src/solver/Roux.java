package solver;


import java.util.*;


public class Roux {

    /**
     * 1. 1st Block -- IDDFS
     * 2. 2nd Block -- IDDFS
     * 3. CMLL -- Programmatic cube inspection
     * 4. LSE -- IDDFS
     **/

    int totalMoveCount = 0;
    char fnt, btm, lft, rht, top, bck;
    static Map<String, String> algoMap=new HashMap<>();

    public Roux(solver.Cube cube) {
        fnt = cube.colorMap[0];
        rht = cube.colorMap[1];
        bck = cube.colorMap[2];
        lft = cube.colorMap[3];
        top = cube.colorMap[4];
        btm = cube.colorMap[5];
        algoMap.put("SUNE", "R U R' U R U U R'");
        algoMap.put("ANTISUNE", "R U U R' U' R U' R'");
        algoMap.put("SUNE_REV", "R U' U' R' U' R U' R'");
        algoMap.put("ANTISUNE_REV", "R U R' U R U' U' R'");
        algoMap.put("JPERM", "R U R' F' R U R' U' R' F R R U' R'");
    }


    public void atomicOperation(String mv, Cube cube) {
        switch (mv) {
            case "":
                break;
            case "U":
                cube.moveU();
                break;
            case "U'":
                cube.moveUp();
                break;
            case "F":
                cube.moveF();
                break;
            case "F'":
                cube.moveFp();
                break;
            case "R":
                cube.moveR();
                break;
            case "R'":
                cube.moveRp();
                break;
            case "L":
                cube.moveL();
                break;
            case "L'":
                cube.moveLp();
                break;
            case "M":
                cube.moveM();
                break;
            case "M'":
                cube.moveMp();
                break;
            case "D":
                cube.moveD();
                break;
            case "D'":
                cube.moveDp();
                break;
            case "Rw":
                cube.moveWideR();
                break;
            case "Rw'":
                cube.moveWideRp();
                break;
            default:
                break;
        }
    }


    public void moveExecutor(Cube cube, String moves) {
        String[] arr = moves.split(" ");
        for (String mv : arr) {
            atomicOperation(mv, cube);
        }
    }


    public String moveOptimizer(String moves) {
        moves = moveOptimizerRec(moves);
        String[] arr = moves.split(" ");
        StringBuilder sb = new StringBuilder();
        int cnt = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].length() == 0)
                continue;
            if (i < arr.length - 1 && arr[i].equals(arr[i + 1])) {
                sb.append(arr[i] + "2 ");
                i++;
            } else {
                sb.append(arr[i] + " ");
            }
            cnt++;
        }
        totalMoveCount += cnt;
        return sb.toString() + " (" + cnt + ")";
    }


    public String moveOptimizerRec(String moves) {
        // 1. If 4 consecutive elements are same, discard the entire sequence of 4 moves
        // 2. If 3 consecutive elements are same, replace them with opposite moves.
        // 3. If 2 consecutive moves are exactly opposite, skip them.
        // 4. Perform the steps recursively as long as it is possible.
        String[] arr = moves.trim().split(" ");
        int n = arr.length;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            if (i + 3 < n && arr[i].equals(arr[i + 1]) && arr[i].equals(arr[i + 2]) && arr[i].equals(arr[i + 3])) {
                i += 3;
            } else if (i + 2 < n && arr[i].equals(arr[i + 1]) && arr[i].equals(arr[i + 2])) {
                if (arr[i].contains("'")) {
                    sb.append(arr[i].substring(0, arr[i].length() - 1) + " ");
                } else {
                    sb.append(arr[i] + "' ");
                }
                i += 2;
            } else if (i + 1 < n && (arr[i].equals(arr[i + 1] + "'") || arr[i + 1].equals(arr[i] + "'"))) {
                i += 1;
            } else {
                sb.append(arr[i] + " ");
            }
        }
        if (sb.toString().trim().length() == moves.trim().length()) {
            return sb.toString().trim();
        }
        return moveOptimizerRec(sb.toString());
    }


    public boolean IDDFS(Cube cube, String prev, String path, int depth, int MAX_DEPTH, List<String> allowedMoves,
                         String step, String[] moves, int RouxStep) {


        // base case
        if ((RouxStep == 1 && (step.equals("Step:LBE") && checkLeftBottomEdge(cube) ||
                step.equals("Step:FBFP") && checkFirstBlockFirstPair(cube) ||
                step.equals("Step:FBSP") && checkFirstBlockSecondPair(cube))) ||
                (RouxStep == 2 && (step.equals("Step:RBE") && checkRightBottomEdge(cube) ||
                        step.equals("Step:SBFP") && checkSecondBlockFirstPair(cube) ||
                        step.equals("Step:SBSP") && checkSecondBlockSecondPair(cube)))
                ||
                (RouxStep == 4 && cube.isSolved())) {
            moves[0] = path;
            return true;
        }


        if (depth > MAX_DEPTH)
            return false;
        for (String mv : allowedMoves) {
            String mv_inv = mv + "'";
            if (mv.contains("'")) {
                mv_inv = mv.substring(0, mv.length() - 1);
            }
            if (!prev.equals(mv_inv)) {
                atomicOperation(mv, cube);
                if (IDDFS(cube, mv, path + mv + " ", depth + 1, MAX_DEPTH, allowedMoves, step, moves, RouxStep))
                    return true;
                atomicOperation(mv_inv, cube);
            }
        }
        return false;
    }


    // -------------------- BLOCK BUILDING STEPS --------------------//

    public boolean checkLeftBottomEdge(Cube cube) {
        return cube.arr[3][2][1] == lft && cube.arr[5][1][0] == btm;
    }


    public boolean checkFirstBlockFirstPair(Cube cube) {
        return (cube.arr[3][1][2] == lft && cube.arr[0][1][0] == fnt) &&
                (cube.arr[3][2][2] == lft && cube.arr[0][2][0] == fnt && cube.arr[5][0][0] == btm) &&
                checkLeftBottomEdge(cube);
    }


    public boolean checkFirstBlockSecondPair(Cube cube) {
        return (cube.arr[3][1][0] == lft && cube.arr[2][1][2] == bck) &&
                (cube.arr[3][2][0] == lft && cube.arr[2][2][2] == bck && cube.arr[5][2][0] == btm) &&
                checkFirstBlockFirstPair(cube) &&
                checkLeftBottomEdge(cube);
    }


    public boolean checkRightBottomEdge(Cube cube) {
        return cube.arr[1][2][1] == rht && cube.arr[5][1][2] == btm;
    }


    public boolean checkSecondBlockFirstPair(Cube cube) {
        return (cube.arr[1][1][0] == rht && cube.arr[0][1][2] == fnt) &&
                (cube.arr[1][2][0] == rht && cube.arr[0][2][2] == fnt && cube.arr[5][0][2] == btm) &&
                checkRightBottomEdge(cube);
    }


    public boolean checkSecondBlockSecondPair(Cube cube) {
        return (cube.arr[1][1][2] == rht && cube.arr[2][1][0] == bck) &&
                (cube.arr[1][2][2] == rht && cube.arr[2][2][0] == bck && cube.arr[5][2][2] == btm) &&
                checkSecondBlockFirstPair(cube) &&
                checkRightBottomEdge(cube);
    }


    public void FirstBlock(Cube cube) {
        System.out.println("FIRST BLOCK\n----------------------------------------");
        String ans = "";
        String[] moves = { "$" };
        List<String> listOfSteps = Arrays.asList("Step:LBE", "Step:FBFP", "Step:FBSP");
        List<String> allowedMoves = Arrays.asList("U", "U'", "F", "F'", "L", "L'", "R", "R'", "M", "M'", "D", "D'");
        int buildCount = 0;
        for (String step : listOfSteps) {
            for (int MAX_DEPTH = 0; MAX_DEPTH <= 10; MAX_DEPTH++) {
                if (IDDFS(cube, "", "", 0, MAX_DEPTH, allowedMoves, step, moves, 1)) {
                    ans += moves[0];
                    buildCount++;
                    break;
                }
            }
        }
        if (buildCount == 3) {
            ans = moveOptimizer(ans);
            System.out.println("Steps: "+ans);
        } else {
            System.out.println("FIRST BLOCK FAILED!!");
        }
        System.out.println();
    }

    public void SecondBlock(Cube cube) {
        System.out.println("SECOND BLOCK\n----------------------------------------");
        String ans = "";
        String[] moves = { "$" };
        List<String> listOfSteps = Arrays.asList("Step:RBE", "Step:SBFP", "Step:SBSP");
        List<String> allowedMoves = Arrays.asList("U", "U'", "R", "R'", "M", "M'", "Rw", "Rw'");
        int buildCount = 0;
        for (String step : listOfSteps) {
            for (int MAX_DEPTH = 0; MAX_DEPTH <= 10; MAX_DEPTH++) {
                if (IDDFS(cube, "", "", 0, MAX_DEPTH, allowedMoves, step, moves, 2)) {
                    ans += moves[0];
                    buildCount++;
                    break;
                }
            }
        }
        if (buildCount == 3) {
            ans = moveOptimizer(ans);
            System.out.println("Steps: "+ans);
        } else {
            System.out.println("SECOND BLOCK FAILED!!");
        }
        System.out.println();
    }

    // -------------------- CMLL STEPS --------------------//
    public boolean areCornersOriented(Cube cube){
        return cube.arr[4][0][0]==top && cube.arr[4][0][2]==top && cube.arr[4][2][0]==top && cube.arr[4][2][2]==top;
    }

    public boolean orientCorners(Cube cube, String path, int depth, int MAX_DEPTH, List<String> allowedMoves, String[] moves){
        if(areCornersOriented(cube)){
            moves[0]=path;
            return true;
        }
        if(depth>MAX_DEPTH){
            return false;
        }
        for(String mv: allowedMoves){
            String actualMove="";
            String actualMove_inv="";
            switch(mv){
                case "SUNE":
                    actualMove=algoMap.get("SUNE");
                    actualMove_inv=algoMap.get("SUNE_REV");
                    break;
                case "ANTISUNE":
                    actualMove=algoMap.get("ANTISUNE");
                    actualMove_inv=algoMap.get("ANTISUNE_REV");
                    break;
                case "U":
                    actualMove="U";
                    actualMove_inv="U'";
                    break;
                case "U'":
                    actualMove="U'";
                    actualMove_inv="U";
                    break;
                case "U U":
                    actualMove="U U";
                    actualMove_inv="U' U'";
                    break;
                default:
                    break;
            }
            moveExecutor(cube, actualMove);
            if(orientCorners(cube, path+actualMove+" ", depth+1, MAX_DEPTH, allowedMoves, moves)){
                return true;
            }
            moveExecutor(cube, actualMove_inv);
        }
        return false;
    }

    public String JPerm(Cube cube) {
        // base case - 4 matching headlights found
        boolean allHeadlightsFound = true;
        for (int i = 0; i < 4; i++) {
            if (cube.arr[i][0][0] != cube.arr[i][0][2])
                allHeadlightsFound = false;
        }
        if (allHeadlightsFound)
            return "";
        StringBuilder moves = new StringBuilder();
        String mv = "";
        for (int i = 0; i < 4; i++) {
            if (cube.arr[3][0][0] == cube.arr[3][0][2])
                break;
            mv = "U ";
            moves.append(mv);
            moveExecutor(cube, mv);
        }
        mv = "R U R' F' R U R' U' R' F R R U' R' ";
        moves.append(mv);
        moveExecutor(cube, mv);
        return moves.toString() + JPerm(cube);
    }

    public void CMLL(Cube cube) {
        System.out.println("CMLL");
        System.out.println("----------------------------------------");
        String[] moves={"$"};
        List<String> allowedMoves=Arrays.asList("U", "U'", "SUNE", "ANTISUNE");
        for(int MAX_DEPTH=0; MAX_DEPTH<=100; MAX_DEPTH++) {
            if(orientCorners(cube, "", 0, MAX_DEPTH, allowedMoves, moves))
                break;
        }
        moves[0] = moveOptimizer(moves[0]);
        System.out.println("Corners: " + moves[0]);
        moves[0] = JPerm(cube);
        moves[0] = moveOptimizer(moves[0]);
        System.out.println("JPerm: " + moves[0]);
        System.out.println();
    }


    // -------------------- LSE STEPS --------------------//
    public String adjustMiddleSliceCentres(Cube cube) {
        String moves = "";
        String mv = "";
        while (cube.arr[0][1][1] != fnt) {
            mv = "M' ";
            moves += mv;
            moveExecutor(cube, mv);
        }
        return moves;
    }


    public boolean isBadEdge(Cube cube, int face, int i, int j) {
        return cube.arr[face][i][j] != top && cube.arr[face][i][j] != btm;
    }


    public int countBadEdgeTop(Cube cube) {
        int cnt = 0;
        for (int i = 0; i < 4; i++) {
            if (isBadEdge(cube, 4, 2, 1))
                cnt++;
            cube.moveU();
        }
        return cnt;
    }


    public int countBadEdgeBottom(Cube cube) {
        int cnt = 0;
        if (isBadEdge(cube, 5, 0, 1))
            cnt++;
        if (isBadEdge(cube, 5, 2, 1))
            cnt++;
        return cnt;
    }


    public String convertToGoodEdges(Cube cube) {
        int topBadEdges = countBadEdgeTop(cube);
        int btmBadEdges = countBadEdgeBottom(cube);
        int badEdges = topBadEdges + btmBadEdges;
        StringBuilder moves = new StringBuilder();
        String mv = "";
        // No. of badEdges can be either one of these [0, 2, 4, 6]
        if (badEdges == 0) {
            return "";
        } else if (badEdges == 2) {
            if (topBadEdges == 1 && btmBadEdges == 1) {
                if (isBadEdge(cube, 5, 0, 1)) {
                    while (!isBadEdge(cube, 4, 0, 1)) {
                        mv = "U ";
                        moves.append(mv);
                        moveExecutor(cube, mv);
                    }
                    mv = "M' U' M ";
                    moves.append(mv);
                    moveExecutor(cube, mv);
                } else {
                    while (!isBadEdge(cube, 4, 2, 1)) {
                        mv = "U ";
                        moves.append(mv);
                        moveExecutor(cube, mv);
                    }
                    mv = "M U' M' ";
                    moves.append(mv);
                    moveExecutor(cube, mv);
                }
            } else if (btmBadEdges == 2) {
                mv = "M' U U M ";
                moves.append(mv);
                moveExecutor(cube, mv);
            } else if (topBadEdges == 2) {
                while (!isBadEdge(cube, 4, 2, 1)) {
                    mv = "U ";
                    moves.append(mv);
                    moveExecutor(cube, mv);
                }
                mv = "M' U U M ";
                moves.append(mv);
                moveExecutor(cube, mv);
            }
        } else if (badEdges == 4) {

            // 1. All the bad edges are on the top
            if (topBadEdges == 4) {
                mv = "M' U U M ";
                moves.append(mv);
                moveExecutor(cube, mv);
            }


            // 2. All the bad edges are NOT on the top (3+1 or 2+2)
            if (isBadEdge(cube, 5, 0, 1) && !isBadEdge(cube, 5, 2, 1)) {
                while (!(isBadEdge(cube, 4, 1, 0) && isBadEdge(cube, 4, 2, 1) && isBadEdge(cube, 4, 1, 2))) {
                    mv = "U ";
                    moves.append(mv);
                    moveExecutor(cube, mv);
                }
                mv = "M' U M' ";
                moves.append(mv);
                moveExecutor(cube, mv);
            } else if (!isBadEdge(cube, 5, 0, 1) && isBadEdge(cube, 5, 2, 1)) {
                while (!(isBadEdge(cube, 4, 1, 0) && isBadEdge(cube, 4, 0, 1) && isBadEdge(cube, 4, 1, 2))) {
                    mv = "U ";
                    moves.append(mv);
                    moveExecutor(cube, mv);
                }
                mv = "M U M' ";
                moves.append(mv);
                moveExecutor(cube, mv);
            } else { // 2 on top & 2 at bottom
                while(isBadEdge(cube, 4, 2, 1)){
                    mv = "U ";
                    moves.append(mv);
                    moveExecutor(cube, mv);
                }
                mv = "M' U U M ";
                moves.append(mv);
                moveExecutor(cube, mv);
            }
        } else if (badEdges == 6) {
            mv = "M' U M' ";
            moves.append(mv);
            moveExecutor(cube, mv);
        }


        return moves.toString() + convertToGoodEdges(cube);
    }


    public String solveLeftRightFaces(Cube cube) {
        StringBuilder moves = new StringBuilder();
        String mv = "";
        boolean leftOk = false, rightOk = false;
        // search for top-left edge
        for (int i = 0; i < 4; i++) {
            if (cube.arr[0][0][1] == lft && cube.arr[4][2][1] == top
                    || cube.arr[0][0][1] == top && cube.arr[4][2][1] == lft) {
                if (cube.arr[0][0][1] == cube.arr[0][0][0] && cube.arr[0][0][1] == cube.arr[0][0][2]) {
                    leftOk = true;
                }
            }
            if (cube.arr[0][0][1] == rht && cube.arr[4][2][1] == top
                    || cube.arr[0][0][1] == top && cube.arr[4][2][1] == rht) {
                if (cube.arr[0][0][1] == cube.arr[0][0][0] && cube.arr[0][0][1] == cube.arr[0][0][2]) {
                    rightOk = true;
                }
            }
            cube.moveU();
        }
        if (leftOk && rightOk) {
            if (cube.arr[0][0][1] == lft) {
                mv = "U ";
            } else if (cube.arr[0][0][1] == rht) {
                mv = "U' ";
            } else if (cube.arr[0][0][1] == bck) {
                mv = "U U ";
            } else {
                mv = "";
            }
            moves.append(mv);
            moveExecutor(cube, mv);
        } else {
            for (int i = 0; i < 4; i++) {
                if (cube.arr[0][0][1] == lft || cube.arr[0][0][1] == rht) {
                    mv = "M' M' ";
                    moves.append(mv);
                    moveExecutor(cube, mv);
                    break;
                }
                mv = "U ";
                moves.append(mv);
                moveExecutor(cube, mv);
            }
            if (cube.arr[0][2][1] == lft) {
                // We have to bring the top-right edge to the opposite loading spot.
                if (cube.arr[2][2][1] != rht) {
                    for (int i = 0; i < 4; i++) {
                        if (cube.arr[2][0][1] == rht)
                            break;
                        mv = "U ";
                        moves.append(mv);
                        moveExecutor(cube, mv);
                    }
                    mv = "M U U M' ";
                    moves.append(mv);
                    moveExecutor(cube, mv);
                }
                for (int i = 0; i < 4; i++) {
                    if (cube.arr[0][0][0] == rht)
                        break;
                    mv = "U ";
                    moves.append(mv);
                    moveExecutor(cube, mv);
                }
                mv = "M' M' U' ";
                moves.append(mv);
                moveExecutor(cube, mv);
            } else if (cube.arr[0][2][1] == rht) {
                // We have to bring the top-right edge to the opposite loading spot.
                if (cube.arr[2][2][1] != lft) {
                    for (int i = 0; i < 4; i++) {
                        if (cube.arr[2][0][1] == lft)
                            break;
                        mv = "U ";
                        moves.append(mv);
                        moveExecutor(cube, mv);
                    }
                    mv = "M U U M' ";
                    moves.append(mv);
                    moveExecutor(cube, mv);
                }
                for (int i = 0; i < 4; i++) {
                    if (cube.arr[0][0][0] == lft)
                        break;
                    mv = "U ";
                    moves.append(mv);
                    moveExecutor(cube, mv);
                }
                mv = "M' M' U ";
                moves.append(mv);
                moveExecutor(cube, mv);
            } else if (cube.arr[2][2][1] == lft) {
                // We have to bring the top-right edge to the opposite loading spot.
                if (cube.arr[0][2][1] != rht) {
                    for (int i = 0; i < 4; i++) {
                        if (cube.arr[0][0][1] == rht)
                            break;
                        mv = "U ";
                        moves.append(mv);
                        moveExecutor(cube, mv);
                    }
                    mv = "M' U U M ";
                    moves.append(mv);
                    moveExecutor(cube, mv);
                }
                for (int i = 0; i < 4; i++) {
                    if (cube.arr[2][0][0] == rht)
                        break;
                    mv = "U ";
                    moves.append(mv);
                    moveExecutor(cube, mv);
                }
                mv = "M' M' U ";
                moves.append(mv);
                moveExecutor(cube, mv);
            } else if (cube.arr[2][2][1] == rht) {
                // We have to bring the top-right edge to the opposite loading spot.
                if (cube.arr[0][2][1] != lft) {
                    for (int i = 0; i < 4; i++) {
                        if (cube.arr[0][0][1] == lft)
                            break;
                        mv = "U ";
                        moves.append(mv);
                        moveExecutor(cube, mv);
                    }
                    mv = "M' U U M ";
                    moves.append(mv);
                    moveExecutor(cube, mv);
                }
                for (int i = 0; i < 4; i++) {
                    if (cube.arr[2][0][0] == lft)
                        break;
                    mv = "U ";
                    moves.append(mv);
                    moveExecutor(cube, mv);
                }
                mv = "M' M' U' ";
                moves.append(mv);
                moveExecutor(cube, mv);
            }
        }
        moves.append(adjustMiddleSliceCentres(cube));
        return moves.toString();
    }


    public String lastFourMiddleEdges(Cube cube) {
        String[] moves = { "$" };
        List<String> allowedSteps = Arrays.asList("M", "M'", "U");
        for (int MAX_DEPTH = 0; MAX_DEPTH <= 100; MAX_DEPTH++) {
            if (IDDFS(cube, "", "", 0, MAX_DEPTH, allowedSteps, null, moves, 4)) {
                break;
            }
        }
        return moveOptimizer(moves[0]);
    }

    public void LSE(Cube cube) {
        System.out.println("LSE");
        System.out.println("----------------------------------------");
        String moves = adjustMiddleSliceCentres(cube);
        moves += convertToGoodEdges(cube);
        moves = moveOptimizer(moves);
        System.out.println("EO (Edge Orientation): " + moves);
        moves = solveLeftRightFaces(cube);
        moves = moveOptimizer(moves);
        System.out.println("UL UR Edges: " + moves);
        moves = lastFourMiddleEdges(cube);
        System.out.println("Last 4 Edges Permutations: " + moves);
        System.out.println();
    }


    public void solve(Cube cube) {
        FirstBlock(cube);
        SecondBlock(cube);
        CMLL(cube);
        LSE(cube);
        if (cube.isSolved()) {
            System.out.println("CONGRATULATIONS!! CUBE IS SOLVED IN " + totalMoveCount +
                    " MOVES.");
        }
    }
}

