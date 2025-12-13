package solver;

import java.util.Arrays;

public class Roux {

    int totalMoveCount=0;
    char fnt, btm, lft, rht, top, bck;

    public Roux(solver.Cube cube) {
        fnt = cube.colorMap[0];
        btm = cube.colorMap[5];
        lft = cube.colorMap[3];
        rht = cube.colorMap[1];
        top = cube.colorMap[4];
        bck = cube.colorMap[2];
    }

    public void moveExecutor(Cube cube, String moves) {
        String[] arr = moves.split(" ");
        for (String mv : arr) {
            switch (mv) {
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
    }

    public String moveOptimizer(String moves) {
        moves=moveOptimizerRec(moves);
        String[] arr=moves.split(" ");
        StringBuilder sb=new StringBuilder();
        int cnt=0;
        for(int i=0; i<arr.length; i++) {
            if(i<arr.length-1 && arr[i].equals(arr[i+1])) {
                sb.append(arr[i]+"2 ");
                i++;
            }else {
                sb.append(arr[i]+" ");
            }
            cnt++;
        }
        totalMoveCount+=cnt;
        return sb.toString()+" ("+cnt+")";
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

    // -------------------- BLOCK BUILDING STEPS --------------------//

    public boolean isEgdeAtLoadPoint(Cube cube, char col1, char col2) {
        return cube.arr[0][2][1] == col1 && cube.arr[5][0][1] == col2
                || cube.arr[0][2][1] == col2 && cube.arr[5][0][1] == col1;
    }

    public boolean isLoadPointOriented(Cube cube, char col1, char col2) {
        return cube.arr[0][2][1] == col1 && cube.arr[5][0][1] == col2;
    }

    public boolean isPairCornerExistsAt(Cube cube, char col1, char col2, char col3, String corner) {
        char[] a = new char[] { col1, col2, col3 };
        char[] b = null;
        if (corner.equals("FRD")) {
            b = new char[] { cube.arr[0][2][2], cube.arr[1][2][0], cube.arr[5][0][2] };
        } else if (corner.equals("BRD")) {
            b = new char[] { cube.arr[2][2][0], cube.arr[1][2][2], cube.arr[5][2][2] };
        } else if (corner.equals("FLT")) {
            b = new char[] { cube.arr[0][0][0], cube.arr[3][0][2], cube.arr[4][2][0] };
        } else if (corner.equals("FRT")) {
            b = new char[] { cube.arr[0][0][2], cube.arr[1][0][0], cube.arr[4][2][2] };
        } else if (corner.equals("FLD")) {
            b = new char[] { cube.arr[0][2][0], cube.arr[3][2][2], cube.arr[5][0][0] };
        } else if (corner.equals("BLD")) {
            b = new char[] { cube.arr[3][2][0], cube.arr[2][2][2], cube.arr[5][2][0] };
        }
        Arrays.sort(a);
        Arrays.sort(b);
        for (int i = 0; i < 3; i++) {
            if (a[i] != b[i])
                return false;
        }
        return true;
    }

    public boolean isCornerOrientedForPairing(Cube cube, char col1, char col2, char col3, String corner) {
        if (corner.equals("FLT")) {
            return cube.arr[0][0][0] == col1 && cube.arr[3][0][2] == col2 && cube.arr[4][2][0] == col3;
        } else if (corner.equals("FRT")) {
            return cube.arr[0][0][2] == col1 && cube.arr[1][0][0] == col2 && cube.arr[4][2][2] == col3;
        }
        return false;
    }

    public void insertLeftBottomEdge(Cube cube) {
        String moves = "";

        if (cube.arr[3][2][1] == lft && cube.arr[5][1][0] == btm) {
            return;
        }

        String mv = "";

        /*** 1. Check if this (L-B) edge piece exists between- ***/

        // (F-L)
        if (cube.arr[3][1][2] == lft && cube.arr[0][1][0] == btm
                || cube.arr[3][1][2] == btm && cube.arr[0][1][0] == lft) {
            mv = "L ";
            moves += mv;
            moveExecutor(cube, mv);
        }
        // (B-L)
        else if (cube.arr[3][1][0] == lft && cube.arr[2][1][2] == btm
                || cube.arr[3][1][0] == btm && cube.arr[2][1][2] == lft) {
            mv = "L' ";
            moves += mv;
            moveExecutor(cube, mv);
        }
        // (F-R)
        else if (cube.arr[1][1][0] == lft && cube.arr[0][1][2] == btm
                || cube.arr[1][1][0] == btm && cube.arr[0][1][2] == lft) {
            mv = "R' ";
            moves += mv;
            moveExecutor(cube, mv);
        }
        // (B-R)
        else if (cube.arr[1][1][2] == lft && cube.arr[2][1][0] == btm
                || cube.arr[1][1][2] == btm && cube.arr[2][1][0] == lft) {
            mv = "R ";
            moves += mv;
            moveExecutor(cube, mv);
        }

        /***
         * 2. Check if this piece exists on top layer. If yes bring it to the center &
         * then do M
         ***/
        boolean found = false;
        mv = "";
        for (int i = 0; i < 4; i++) {
            if (cube.arr[0][0][1] == lft && cube.arr[4][2][1] == btm
                    || cube.arr[0][0][1] == btm && cube.arr[4][2][1] == lft) {
                found = true;
                break;
            }
            mv = "U ";
            moves += mv;
            moveExecutor(cube, mv);
        }
        if (found) {
            mv = "M ";
            moves += mv;
            moveExecutor(cube, mv);
        }

        /***
         * 3. Keep performing D moves until the edge piece appears at Loading point.
         ***/
        for (int i = 0; i < 4; i++) {
            if (cube.arr[0][2][1] == lft && cube.arr[5][0][1] == btm
                    || cube.arr[0][2][1] == btm && cube.arr[5][0][1] == lft) {
                found = true;
                break;
            }
            mv = "D ";
            moves += mv;
            moveExecutor(cube, mv);
        }

        /*** 4. Now check its orientation ***/
        if (!isLoadPointOriented(cube, lft, btm)) {
            mv = "M' U U M M ";
            moves += mv;
            moveExecutor(cube, mv);
        }

        mv = "D' ";
        moves += mv;
        moveExecutor(cube, mv);
        moves = moveOptimizer(moves);
        System.out.println("L-B Edge: " + moves);

    }

    public void insertFirstPair1(Cube cube) {

        String moves = "";
        String mv = "";

        // Bringing the F-L edge piece to the Loading spot

        if (cube.arr[0][1][0] == fnt && cube.arr[3][1][2] == lft
                || cube.arr[0][1][0] == lft && cube.arr[3][1][2] == fnt) {
            mv = "F' ";
            moves += mv;
            moveExecutor(cube, mv);
        } else if (cube.arr[0][1][2] == fnt && cube.arr[1][1][0] == lft
                || cube.arr[0][1][2] == lft && cube.arr[1][1][0] == fnt) {
            mv = "F ";
            moves += mv;
            moveExecutor(cube, mv);
        } else if (cube.arr[3][1][0] == fnt && cube.arr[2][1][2] == lft
                || cube.arr[3][1][0] == lft && cube.arr[2][1][2] == fnt) {
            mv = "L U' L' M ";
            moves += mv;
            moveExecutor(cube, mv);
        } else if (cube.arr[1][1][2] == fnt && cube.arr[2][1][0] == lft
                || cube.arr[1][1][2] == lft && cube.arr[2][1][0] == fnt) {
            mv = "R' U M ";
            moves += mv;
            moveExecutor(cube, mv);
        } else if (cube.arr[1][2][1] == fnt && cube.arr[5][1][2] == lft
                || cube.arr[1][2][1] == lft && cube.arr[5][1][2] == fnt) {
            mv = "R R U M ";
            moves += mv;
            moveExecutor(cube, mv);
        } else if (cube.arr[2][2][1] == fnt && cube.arr[5][2][1] == lft
                || cube.arr[2][2][1] == lft && cube.arr[5][2][1] == fnt) {
            mv = "M' ";
            moves += mv;
            moveExecutor(cube, mv);
        }

        /***
         * 2. Check if this piece exists on top layer. If yes bring it to the center &
         * then do M
         ***/
        boolean found = false;
        mv = "";
        for (int i = 0; i < 4; i++) {
            if (cube.arr[0][0][1] == lft && cube.arr[4][2][1] == fnt
                    || cube.arr[0][0][1] == fnt && cube.arr[4][2][1] == lft) {
                found = true;
                break;
            }
            mv = "U ";
            moves += mv;
            moveExecutor(cube, mv);
        }
        if (found) {
            mv = "M ";
            moves += mv;
            moveExecutor(cube, mv);
        }

        /*** 4. Now check its orientation ***/
        if (!isLoadPointOriented(cube, lft, fnt)) {
            mv = "M' U U M M ";
            moves += mv;
            moveExecutor(cube, mv);
        }

        // Bringing the FLD corner piece to the top layer for paring.
        if (isPairCornerExistsAt(cube, fnt, lft, btm, "BLD")) {
            mv = "L U' U' L' ";
            moves += mv;
            moveExecutor(cube, mv);
        } else if (isPairCornerExistsAt(cube, fnt, lft, btm, "BRD")) {
            mv = "R' ";
            moves += mv;
            moveExecutor(cube, mv);
        } else if (isPairCornerExistsAt(cube, fnt, lft, btm, "FLD")) {
            mv = "L' U L ";
            moves += mv;
            moveExecutor(cube, mv);
        } else if (isPairCornerExistsAt(cube, fnt, lft, btm, "FRD")) {
            mv = "R ";
            moves += mv;
            moveExecutor(cube, mv);
        }

        for (int i = 0; i < 3; i++) {
            if (isPairCornerExistsAt(cube, fnt, lft, btm, "FLT"))
                break;
            mv = "U ";
            moves += mv;
            moveExecutor(cube, mv);
        }

        while (!isCornerOrientedForPairing(cube, fnt, btm, lft, "FLT")) {
            mv = "R U' U' R' U ";
            moves += mv;
            moveExecutor(cube, mv);
        }

        mv = "M' F' ";
        moves += mv;
        moveExecutor(cube, mv);
        moves = moveOptimizer(moves);
        System.out.println("1st pair: " + moves);
    }

    public void insertSecondPair1(Cube cube) {
        String moves = "";
        String mv = "";
        // back-right
        if (cube.arr[2][1][0] == bck && cube.arr[1][1][2] == lft
                || cube.arr[2][1][0] == lft && cube.arr[1][1][2] == bck) {
            mv = "R' U M R ";
        }
        // back-left
        else if (cube.arr[2][1][2] == bck && cube.arr[3][1][0] == lft
                || cube.arr[2][1][2] == lft && cube.arr[3][1][0] == bck) {
            mv = "L U' L' M ";
        }
        // back-bottom
        else if (cube.arr[2][2][1] == bck && cube.arr[5][2][1] == lft
                || cube.arr[2][2][1] == lft && cube.arr[5][2][1] == bck) {
            mv = "M' ";
        }
        // front-right
        else if (cube.arr[0][1][2] == bck && cube.arr[1][1][0] == lft
                || cube.arr[0][1][2] == lft && cube.arr[1][1][0] == bck) {
            mv = "R ";
        }
        // right-bottom
        else if (cube.arr[1][2][1] == bck && cube.arr[5][1][2] == lft
                || cube.arr[1][2][1] == lft && cube.arr[5][1][2] == bck) {
            mv = "R R ";
        }
        moves += mv;
        moveExecutor(cube, mv);
        for (int i = 0; i < 3; i++) {
            if (isEgdeAtLoadPoint(cube, lft, bck))
                break;
            mv = "M ";
            moves += mv;
            moveExecutor(cube, mv);
        }
        if (!isEgdeAtLoadPoint(cube, lft, bck)) {
            mv = "U ";
            moves += mv;
            moveExecutor(cube, mv);
        }
        for (int i = 0; i < 3; i++) {
            if (isEgdeAtLoadPoint(cube, lft, bck))
                break;
            mv = "M ";
            moves += mv;
            moveExecutor(cube, mv);
        }
        if (!isLoadPointOriented(cube, bck, lft)) {
            mv = "M' U U M M ";
            moves += mv;
            moveExecutor(cube, mv);
        }
        if (isPairCornerExistsAt(cube, bck, lft, btm, "BLD")) {
            mv = "L U' L' ";
            moves += mv;
            moveExecutor(cube, mv);
        }
        for (int i = 0; i < 3; i++) {
            if (isPairCornerExistsAt(cube, bck, lft, btm, "FLT"))
                break;
            mv = "U ";
            moves += mv;
            moveExecutor(cube, mv);
        }
        while (!isCornerOrientedForPairing(cube, lft, btm, bck, "FLT")) {
            mv = "U L U' L' U ";
            moves += mv;
            moveExecutor(cube, mv);
        }
        mv = "M' U U L U' L' ";
        moves += mv;
        moveExecutor(cube, mv);
        moves = moveOptimizer(moves);
        System.out.println("2nd pair: " + moves);
    }

    public void insertRightBottomEdge(Cube cube) {
        String moves = "";
        char rht = cube.colorMap[1];
        char btm = cube.colorMap[5];
        // Only 18 cases are there
        // Already in the correct place and correct orientation
        if (cube.arr[1][2][1] == rht && cube.arr[5][1][2] == btm) {

        }
        // Correct place but wrong orientation.
        else if (cube.arr[1][2][1] == btm && cube.arr[5][1][2] == rht) {
            moves = "R R U Rw U R' R'";
        }
        // The edge is between front & right. Orientation is correct.
        else if (cube.arr[0][1][2] == btm && cube.arr[1][1][0] == rht) {
            moves = "R'";
        }
        // The edge is between front & right. Orientation is not correct.
        else if (cube.arr[0][1][2] == rht && cube.arr[1][1][0] == btm) {
            moves = "R U Rw U R' R'";
        }
        // The edge is between back & right. Orientation is correct.
        else if (cube.arr[2][1][0] == btm && cube.arr[1][1][2] == rht) {
            moves = "R";
        }
        // The edge is between back & right. Orientation is not correct.
        else if (cube.arr[2][1][0] == rht && cube.arr[1][1][2] == btm) {
            moves = "R' U Rw U R' R'";
        }
        // The edge is between top & right. Orientation is correct.
        else if (cube.arr[4][1][2] == btm && cube.arr[1][0][1] == rht) {
            moves = "R R";
        }
        // The edge is between top & right. Orientation is not correct.
        else if (cube.arr[4][1][2] == rht && cube.arr[1][0][1] == btm) {
            moves = "U Rw U R' R'";
        }
        // The edge is between top & front. Orientation is correct.
        else if (cube.arr[4][2][1] == btm && cube.arr[0][0][1] == rht) {
            moves = "U' R R";
        }
        // The edge is between top & front. Orientation is not correct.
        else if (cube.arr[4][2][1] == rht && cube.arr[0][0][1] == btm) {
            moves = "M' U R R";
        }
        // The edge is between top & left. Orientation is correct.
        else if (cube.arr[4][1][0] == btm && cube.arr[3][0][1] == rht) {
            moves = "U' U' R R";
        }
        // The edge is between top & left. Orientation is not correct.
        else if (cube.arr[4][2][1] == rht && cube.arr[0][0][1] == btm) {
            moves = "U M' U' R R";
        }
        // The edge is between top & back. Orientation is correct.
        else if (cube.arr[4][0][1] == btm && cube.arr[2][0][1] == rht) {
            moves = "U R R";
        }
        // The edge is between top & back. Orientation is not correct.
        else if (cube.arr[4][0][1] == rht && cube.arr[2][0][1] == btm) {
            moves = "M' U' R R";
        }
        // The edge is between bottom & back. Orientation is correct.
        else if (cube.arr[5][2][1] == btm && cube.arr[2][2][1] == rht) {
            moves = "M' M' U' R R";
        }
        // The edge is between bottom & back. Orientation is not correct.
        else if (cube.arr[5][2][1] == rht && cube.arr[2][2][1] == btm) {
            moves = "M U R R";
        }
        // The edge is between bottom & front. Orientation is correct.
        else if (cube.arr[5][0][1] == btm && cube.arr[0][2][1] == rht) {
            moves = "M' M' U R R";
        }
        // The edge is between bottom & front. Orientation is not correct.
        else if (cube.arr[5][0][1] == rht && cube.arr[0][2][1] == btm) {
            moves = "M' U' R R";
        }

        moveExecutor(cube, moves);
        moves = moveOptimizer(moves);
        System.out.println("R-B Edge: " + moves);
    }

    public void insertFirstPair2(Cube cube) {

        String moves = "";
        String mv = "";
        // F-R
        if (cube.arr[0][1][2] == fnt && cube.arr[1][1][0] == rht
                || cube.arr[0][1][2] == rht && cube.arr[1][1][0] == fnt) {
            mv = "R U R' M ";
        }
        // R-G
        else if (cube.arr[2][1][0] == fnt && cube.arr[1][1][2] == rht
                || cube.arr[2][1][0] == rht && cube.arr[1][1][2] == fnt) {
            mv = "R' U R M ";
        }
        // Bottom-Back
        else if (cube.arr[2][2][1] == fnt && cube.arr[5][2][1] == rht
                || cube.arr[2][2][1] == rht && cube.arr[5][2][1] == fnt) {
            mv = "M' ";
        }

        moves += mv;
        moveExecutor(cube, mv);
        for (int i = 0; i < 3; i++) {
            if (isEgdeAtLoadPoint(cube, rht, fnt))
                break;
            mv = "M ";
            moves += mv;
            moveExecutor(cube, mv);
        }
        if (!isEgdeAtLoadPoint(cube, rht, fnt)) {
            mv = "U ";
            moves += mv;
            moveExecutor(cube, mv);
        }
        for (int i = 0; i < 3; i++) {
            if (isEgdeAtLoadPoint(cube, rht, fnt))
                break;
            mv = "M ";
            moves += mv;
            moveExecutor(cube, mv);
        }
        if (!isLoadPointOriented(cube, fnt, rht)) {
            mv = "M' U U M M ";
            moves += mv;
            moveExecutor(cube, mv);
        }
        if (isPairCornerExistsAt(cube, fnt, rht, btm, "FRD")) {
            mv = "R U R' ";
            moves += mv;
            moveExecutor(cube, mv);
        }
        if (isPairCornerExistsAt(cube, fnt, rht, btm, "BRD")) {
            mv = "R' U U R ";
            moves += mv;
            moveExecutor(cube, mv);
        }
        for (int i = 0; i < 3; i++) {
            if (isPairCornerExistsAt(cube, fnt, rht, btm, "FLT"))
                break;
            mv = "U ";
            moves += mv;
            moveExecutor(cube, mv);
        }
        while (!isCornerOrientedForPairing(cube, rht, btm, fnt, "FLT")) {
            mv = "R U' U' R' U ";
            moves += mv;
            moveExecutor(cube, mv);
        }
        mv = "M' R U' R' ";
        moves += mv;
        moveExecutor(cube, mv);
        moves = moveOptimizer(moves);
        System.out.println("1st pair: " + moves);
    }

    public void insertSecondPair2(Cube cube) {
        String moves = "";
        String mv = "";
        if (cube.arr[2][1][0] == bck && cube.arr[1][1][2] == rht
                || cube.arr[2][1][0] == rht && cube.arr[1][1][2] == bck) {
            mv = "R' U M R ";
        } else if (cube.arr[2][2][1] == bck && cube.arr[5][2][1] == rht
                || cube.arr[2][2][1] == rht && cube.arr[5][2][1] == bck) {
            mv = "M' ";
        }

        moves += mv;
        moveExecutor(cube, mv);

        for (int i = 0; i < 3; i++) {
            if (isEgdeAtLoadPoint(cube, rht, bck))
                break;
            mv = "M ";
            moves += mv;
            moveExecutor(cube, mv);
        }
        if (!isEgdeAtLoadPoint(cube, rht, bck)) {
            mv = "U ";
            moves += mv;
            moveExecutor(cube, mv);
        }
        for (int i = 0; i < 3; i++) {
            if (isEgdeAtLoadPoint(cube, rht, bck))
                break;
            mv = "M ";
            moves += mv;
            moveExecutor(cube, mv);
        }

        if (!isLoadPointOriented(cube, bck, rht)) {
            mv = "M' U U M M ";
            moves += mv;
            moveExecutor(cube, mv);
        }

        if (isPairCornerExistsAt(cube, bck, rht, btm, "BRD")) {
            mv = "R' U U R U' ";
            moves += mv;
            moveExecutor(cube, mv);
        }
        for (int i = 0; i < 3; i++) {
            if (isPairCornerExistsAt(cube, bck, rht, btm, "FRT"))
                break;
            mv = "U ";
            moves += mv;
            moveExecutor(cube, mv);
        }
        while (!isCornerOrientedForPairing(cube, rht, btm, bck, "FRT")) {
            mv = "U' R' U R U' ";
            moves += mv;
            moveExecutor(cube, mv);
        }
        mv = "M' U U R' U R ";
        moves += mv;
        moveExecutor(cube, mv);
        moves = moveOptimizer(moves);
        System.out.println("2nd pair: " + moves);
    }

    // -------------------- CMLL STEPS --------------------//
    public int countGoodCorners(Cube cube) {
        int cnt = 0;
        for (int i = 0; i < 4; i++) {
            if (cube.arr[4][2][0] == top)
                cnt++;
            cube.moveU();
        }
        return cnt;
    }

    public String sune(Cube cube) {
        String moves = "";
        String mv = "";

        int goodCornerCnt = countGoodCorners(cube);
        if (goodCornerCnt == 4) {
            return "";
        }
        // Ensure that there will be exactly 1 top color after a sune.
        if (goodCornerCnt != 1) {
            // BLT_B, BRT_R, FRT_F, FLT_T
            char[] upcoming = new char[] { cube.arr[2][0][2], cube.arr[1][0][2], cube.arr[0][0][2], cube.arr[0][2][0] };
            int cnt2 = 0;
            for (char ch : upcoming)
                if (ch == top)
                    cnt2++;
            while (cnt2 != 1) {
                mv = "U ";
                moves += mv;
                moveExecutor(cube, mv);
                cnt2 = 0;
                upcoming = new char[] { cube.arr[2][0][2], cube.arr[1][0][2], cube.arr[0][0][2], cube.arr[0][2][0] };
                for (char ch : upcoming)
                    if (ch == top)
                        cnt2++;
            }
            mv = "R U R' U R U U R' ";
            moves += mv;
            moveExecutor(cube, mv);
        }
        // Bring the top piece to the Front-Top-Left corner with some U moves.
        while (cube.arr[4][2][0] != top) {
            mv = "U ";
            moves += mv;
            moveExecutor(cube, mv);
        }
        // apply sune again.
        mv = "R U R' U R U U R' ";
        moves += mv;
        moveExecutor(cube, mv);
        return moves + sune(cube);
    }

    public String JPerm(Cube cube) {
        // base case
        boolean ok = true;
        for (int i = 0; i < 4; i++) {
            if (cube.arr[i][0][0] != cube.arr[i][0][2])
                ok = false;
        }
        if (ok)
            return "";

        String moves = "";
        String mv = "";
        for (int i = 0; i < 4; i++) {
            if (cube.arr[3][0][0] == cube.arr[3][0][2])
                break;
            mv = "U ";
            moves += mv;
            moveExecutor(cube, mv);
        }
        mv = "R U R' F' R U R' U' R' F R R U' R' ";
        moves += mv;
        moveExecutor(cube, mv);
        return moves + JPerm(cube);
    }

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
        String moves = "";
        String mv = "";
        // No. of badEdges can be either one of these [0, 2, 4, 6]
        if (badEdges == 0) {
            return "";
        } else if (badEdges == 2) {
            if (topBadEdges == 1 && btmBadEdges == 1) {
                if (isBadEdge(cube, 5, 0, 1)) {
                    while (!isBadEdge(cube, 4, 0, 1)) {
                        mv = "U ";
                        moves += mv;
                        moveExecutor(cube, mv);
                    }
                    mv = "M' U' M ";
                    moves += mv;
                    moveExecutor(cube, mv);
                } else {
                    while (!isBadEdge(cube, 4, 2, 1)) {
                        mv = "U ";
                        moves += mv;
                        moveExecutor(cube, mv);
                    }
                    mv = "M U' M' ";
                    moves += mv;
                    moveExecutor(cube, mv);
                }
            } else if (btmBadEdges == 2) {
                mv = "M' U U M ";
                moves += mv;
                moveExecutor(cube, mv);
            } else if (topBadEdges == 2) {
                while (!isBadEdge(cube, 4, 2, 1)) {
                    mv = "U ";
                    moves += mv;
                    moveExecutor(cube, mv);
                }
                mv = "M' U U M ";
                moves += mv;
                moveExecutor(cube, mv);
            }
        } else if (badEdges == 4) {
            if (topBadEdges == 4) {
                mv = "M' U U M ";
                moves += mv;
                moveExecutor(cube, mv);
            }

            if (isBadEdge(cube, 5, 0, 1)) {
                while (!(isBadEdge(cube, 4, 1, 0) && isBadEdge(cube, 4, 2, 1) && isBadEdge(cube, 4, 1, 2))) {
                    mv = "U ";
                    moves += mv;
                    moveExecutor(cube, mv);
                }
                mv = "M' U M' ";
                moves += mv;
                moveExecutor(cube, mv);
            } else {
                while (!(isBadEdge(cube, 4, 1, 0) && isBadEdge(cube, 4, 0, 1) && isBadEdge(cube, 4, 1, 2))) {
                    mv = "U ";
                    moves += mv;
                    moveExecutor(cube, mv);
                }
                mv = "M U M' ";
                moves += mv;
                moveExecutor(cube, mv);
            }

        } else if (badEdges == 6) {
            mv = "M' U M' ";
            moves += mv;
            moveExecutor(cube, mv);
        }

        return moves + convertToGoodEdges(cube);
    }

    public String solveLeftRightFaces(Cube cube) {
        String moves = "";
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
            moves += mv;
            moveExecutor(cube, mv);
        } else {
            for (int i = 0; i < 4; i++) {
                if (cube.arr[0][0][1] == lft || cube.arr[0][0][1] == rht) {
                    mv = "M' M' ";
                    moves += mv;
                    moveExecutor(cube, mv);
                    break;
                }
                mv = "U ";
                moves += mv;
                moveExecutor(cube, mv);
            }
            if (cube.arr[0][2][1] == lft) {
                // We have to bring the top-right edge to the opposite loading spot.
                if (cube.arr[2][2][1] != rht) {
                    for (int i = 0; i < 4; i++) {
                        if (cube.arr[2][0][1] == rht)
                            break;
                        mv = "U ";
                        moves += mv;
                        moveExecutor(cube, mv);
                    }
                    mv = "M U U M' ";
                    moves += mv;
                    moveExecutor(cube, mv);
                }
                for (int i = 0; i < 4; i++) {
                    if (cube.arr[0][0][0] == rht)
                        break;
                    mv = "U ";
                    moves += mv;
                    moveExecutor(cube, mv);
                }
                mv = "M' M' U' ";
                moves += mv;
                moveExecutor(cube, mv);
            } else if (cube.arr[0][2][1] == rht) {
                // We have to bring the top-right edge to the opposite loading spot.
                if (cube.arr[2][2][1] != lft) {
                    for (int i = 0; i < 4; i++) {
                        if (cube.arr[2][0][1] == lft)
                            break;
                        mv = "U ";
                        moves += mv;
                        moveExecutor(cube, mv);
                    }
                    mv = "M U U M' ";
                    moves += mv;
                    moveExecutor(cube, mv);
                }
                for (int i = 0; i < 4; i++) {
                    if (cube.arr[0][0][0] == lft)
                        break;
                    mv = "U ";
                    moves += mv;
                    moveExecutor(cube, mv);
                }
                mv = "M' M' U ";
                moves += mv;
                moveExecutor(cube, mv);
            } else if (cube.arr[2][2][1] == lft) {
                // We have to bring the top-right edge to the opposite loading spot.
                if (cube.arr[0][2][1] != rht) {
                    for (int i = 0; i < 4; i++) {
                        if (cube.arr[0][0][1] == rht)
                            break;
                        mv = "U ";
                        moves += mv;
                        moveExecutor(cube, mv);
                    }
                    mv = "M' U U M ";
                    moves += mv;
                    moveExecutor(cube, mv);
                }
                for (int i = 0; i < 4; i++) {
                    if (cube.arr[2][0][0] == rht)
                        break;
                    mv = "U ";
                    moves += mv;
                    moveExecutor(cube, mv);
                }
                mv = "M' M' U ";
                moves += mv;
                moveExecutor(cube, mv);
            } else if (cube.arr[2][2][1] == rht) {
                // We have to bring the top-right edge to the opposite loading spot.
                if (cube.arr[0][2][1] != lft) {
                    for (int i = 0; i < 4; i++) {
                        if (cube.arr[0][0][1] == lft)
                            break;
                        mv = "U ";
                        moves += mv;
                        moveExecutor(cube, mv);
                    }
                    mv = "M' U U M ";
                    moves += mv;
                    moveExecutor(cube, mv);
                }
                for (int i = 0; i < 4; i++) {
                    if (cube.arr[2][0][0] == lft)
                        break;
                    mv = "U ";
                    moves += mv;
                    moveExecutor(cube, mv);
                }
                mv = "M' M' U' ";
                moves += mv;
                moveExecutor(cube, mv);
            }
        }
        moves += adjustMiddleSliceCentres(cube);
        return moves;
    }

    public boolean last4EdgesDFS(Cube cube, String prevMove, String path, int depth, int MAX_DEPTH, String[] moves) {
        if (cube.isSolved()) {
            moves[0] = path;
            return true;
        }
        if (depth > MAX_DEPTH)
            return false;
        if (!prevMove.equals("U U ") && prevMove.length() > 0) {
            cube.moveU();
            cube.moveU();
            if (last4EdgesDFS(cube, "U U ", path + "U U ", depth + 1, MAX_DEPTH, moves)) {
                return true;
            }
            cube.moveUp();
            cube.moveUp();
        }
        if (!prevMove.equals("M ")) {
            cube.moveMp();
            if (last4EdgesDFS(cube, "M' ", path + "M' ", depth + 1, MAX_DEPTH, moves)) {
                return true;
            }
            cube.moveM();
        }
        if (!prevMove.equals("M' ")) {
            cube.moveM();
            if (last4EdgesDFS(cube, "M ", path + "M ", depth + 1, MAX_DEPTH, moves)) {
                return true;
            }
            cube.moveMp();
        }
        return false;
    }

    public String lastFourMiddleEdges(Cube cube) {
        String[] moves = new String[1];
        for (int MAX_DEPTH = 0; MAX_DEPTH <= 10; MAX_DEPTH++) {
            if (last4EdgesDFS(cube, "", "", 0, MAX_DEPTH, moves))
                break;
        }
        return moveOptimizer(moves[0]);
    }

    public void buildFirstBlock(Cube cube) {
        System.out.println("BUILD THE FIRST BLOCK");
        System.out.println("----------------------------------------");
        insertLeftBottomEdge(cube);
        insertFirstPair1(cube);
        insertSecondPair1(cube);
        System.out.println();
    }

    public void buildSecondBlock(Cube cube) {
        System.out.println("BUILD THE SECOND BLOCK");
        System.out.println("----------------------------------------");
        insertRightBottomEdge(cube);
        insertFirstPair2(cube);
        insertSecondPair2(cube);
        System.out.println();
    }

    public void CMLL(Cube cube) {
        System.out.println("CMLL");
        System.out.println("----------------------------------------");
        String moves = sune(cube);
        moves = moveOptimizer(moves);
        System.out.println("SUNE: " + moves);
        System.out.println();
    }

    public void PLL(Cube cube) {
        System.out.println("PLL");
        System.out.println("----------------------------------------");
        String moves = JPerm(cube);
        moves = moveOptimizer(moves);
        System.out.println("JPERM: " + moves);
        System.out.println();
    }

    public void LSE(Cube cube) {
        System.out.println("LSE");
        System.out.println("----------------------------------------");
        String moves = adjustMiddleSliceCentres(cube);
        moves += convertToGoodEdges(cube);
        moves = moveOptimizer(moves);
        System.out.println("GOOD EDGE: " + moves);
        moves = solveLeftRightFaces(cube);
        moves = moveOptimizer(moves);
        System.out.println("L-R FACE: " + moves);
        moves = lastFourMiddleEdges(cube);
        System.out.println("LAST 4 MIDDLE EDGE: " + moves);
        System.out.println();
    }

    public void solve(Cube cube) {
        buildFirstBlock(cube);
        buildSecondBlock(cube);
        CMLL(cube);
        PLL(cube);
        LSE(cube);
        if (cube.isSolved()) {
            System.out.println("CONGRATULATIONS!! CUBE IS SOLVED IN "+totalMoveCount+" MOVES.");
        }
    }

}
