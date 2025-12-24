package solver;

public class Cube {
    /*
     * W(4)
     * G(0) R(1) B(2) O(3)
     * Y(5)
     */
    int N = 3;
    public char[] colorMap = new char[] { 'G', 'R', 'B', 'O', 'W', 'Y' };
    char[] dirMap = new char[] { 'F', 'R', 'B', 'L', 'U', 'D' };
    public char[][][] arr = new char[6][N][N];

    public void resetCube() {
        for (int color = 0; color < 6; color++) {
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    arr[color][i][j] = colorMap[color];
                }
            }
        }
    }

    public boolean isSolved() {
        for (int color = 0; color < 6; color++) {
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if(arr[color][i][j] != colorMap[color])
                        return false;
                }
            }
        }
        return true;
    }

    public void printSide(char[][] side) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.print(side[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void printCurrState() {
        for (int color = 0; color < 6; color++) {
            System.out.println("Side: " + dirMap[color]);
            printSide(arr[color]);
        }
        System.out.println("------------------------");
    }

    // CUBE MOVES
    public void clockwiseTurn(char[][] side) {
        // Transpose
        for (int i = 0; i < N; i++) {
            for (int j = 0; j <= i; j++) {
                char temp = side[i][j];
                side[i][j] = side[j][i];
                side[j][i] = temp;
            }
        }
        // Horizontal Flip
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N / 2; j++) {
                char temp = side[i][j];
                side[i][j] = side[i][N - 1 - j];
                side[i][N - 1 - j] = temp;
            }
        }
    }

    public void antiClockwiseTurn(char[][] side) {
        // Transpose
        for (int i = 0; i < N; i++) {
            for (int j = 0; j <= i; j++) {
                char temp = side[i][j];
                side[i][j] = side[j][i];
                side[j][i] = temp;
            }
        }
        // Vertical Flip
        for (int i = 0; i < N / 2; i++) {
            for (int j = 0; j < N; j++) {
                char temp = side[i][j];
                side[i][j] = side[N - 1 - i][j];
                side[N - 1 - i][j] = temp;
            }
        }
    }

    /* U-move */
    public void moveU() {
        // Top will rotate by 90 degrees
        clockwiseTurn(arr[4]);
        // Adjust (Front-Left-Back-Right)
        char[] temp = arr[0][0];
        arr[0][0] = arr[1][0];
        arr[1][0] = arr[2][0];
        arr[2][0] = arr[3][0];
        arr[3][0] = temp;
    }

    /* U'-move */
    public void moveUp() {
        // Top will rotate by 90 degrees
        antiClockwiseTurn(arr[4]);
        // Adjust (Front-Left-Back-Right) | (0 - 1 - 2 - 3)
        char[] temp = arr[3][0];
        arr[3][0] = arr[2][0];
        arr[2][0] = arr[1][0];
        arr[1][0] = arr[0][0];
        arr[0][0] = temp;
    }

    /* R-move */
    public void moveR() {
        clockwiseTurn(arr[1]);
        // Adjust (Front-Down-Back-Top) | (0 - 5 - 2 - 4)
        char[] up = new char[] { arr[4][0][2], arr[4][1][2], arr[4][2][2] };
        char[] back = new char[] { arr[2][0][0], arr[2][1][0], arr[2][2][0] };
        char[] down = new char[] { arr[5][0][2], arr[5][1][2], arr[5][2][2] };
        char[] front = new char[] { arr[0][0][2], arr[0][1][2], arr[0][2][2] };

        // D → F
        for (int i = 0; i < 3; i++)
            arr[0][i][2] = down[i];
        // F → U
        for (int i = 0; i < 3; i++)
            arr[4][i][2] = front[i];
        // U → B (reversed)
        for (int i = 0; i < 3; i++)
            arr[2][i][0] = up[2 - i];
        // B → D (reversed)
        for (int i = 0; i < 3; i++)
            arr[5][i][2] = back[2 - i];

    }

    /* R'-move */
    public void moveRp() {
        antiClockwiseTurn(arr[1]);

        char[] up = new char[] { arr[4][0][2], arr[4][1][2], arr[4][2][2] };
        char[] back = new char[] { arr[2][0][0], arr[2][1][0], arr[2][2][0] };
        char[] down = new char[] { arr[5][0][2], arr[5][1][2], arr[5][2][2] };
        char[] front = new char[] { arr[0][0][2], arr[0][1][2], arr[0][2][2] };
        // U->F
        for (int i = 0; i < 3; i++)
            arr[0][i][2] = up[i];
        // F->D
        for (int i = 0; i < 3; i++)
            arr[5][i][2] = front[i];
        // D->B(reversed)
        for (int i = 0; i < 3; i++)
            arr[2][i][0] = down[2 - i];
        // B->U (reversed)
        for (int i = 0; i < 3; i++)
            arr[4][i][2] = back[2 - i];
    }

    /* L-move */
    public void moveL() {
        clockwiseTurn(arr[3]);

        char[] up = new char[] { arr[4][0][0], arr[4][1][0], arr[4][2][0] };
        char[] front = new char[] { arr[0][0][0], arr[0][1][0], arr[0][2][0] };
        char[] down = new char[] { arr[5][0][0], arr[5][1][0], arr[5][2][0] };
        char[] back = new char[] { arr[2][0][2], arr[2][1][2], arr[2][2][2] }; // RIGHT column of B

        // U → F
        for (int i = 0; i < 3; i++)
            arr[0][i][0] = up[i];
        // F → D
        for (int i = 0; i < 3; i++)
            arr[5][i][0] = front[i];
        // D → B (reversed)
        for (int i = 0; i < 3; i++)
            arr[2][i][2] = down[2 - i];
        // B → U (reversed)
        for (int i = 0; i < 3; i++)
            arr[4][i][0] = back[2 - i];
    }

    /* L'-move */
    public void moveLp() {
        antiClockwiseTurn(arr[3]);

        char[] up = new char[] { arr[4][0][0], arr[4][1][0], arr[4][2][0] };
        char[] front = new char[] { arr[0][0][0], arr[0][1][0], arr[0][2][0] };
        char[] down = new char[] { arr[5][0][0], arr[5][1][0], arr[5][2][0] };
        char[] back = new char[] { arr[2][0][2], arr[2][1][2], arr[2][2][2] };

        // Reverse direction of cycle
        for (int i = 0; i < 3; i++)
            arr[4][i][0] = front[i];
        for (int i = 0; i < 3; i++)
            arr[0][i][0] = down[i];
        for (int i = 0; i < 3; i++)
            arr[5][i][0] = back[2 - i];
        for (int i = 0; i < 3; i++)
            arr[2][i][2] = up[2 - i];
    }

    public void moveF() {
        // Rotate front face
        clockwiseTurn(arr[0]);

        // Capture affected stickers
        char[] up = new char[] { arr[4][2][0], arr[4][2][1], arr[4][2][2] };
        char[] right = new char[] { arr[1][0][0], arr[1][1][0], arr[1][2][0] };
        char[] down = new char[] { arr[5][0][0], arr[5][0][1], arr[5][0][2] };
        char[] left = new char[] { arr[3][0][2], arr[3][1][2], arr[3][2][2] };

        // U bottom row → R left column
        for (int i = 0; i < 3; i++)
            arr[1][i][0] = up[i];

        // R left column → D top row (reversed)
        for (int i = 0; i < 3; i++)
            arr[5][0][i] = right[2 - i];

        // D top row → L right column
        for (int i = 0; i < 3; i++)
            arr[3][i][2] = down[i];

        // L right column → U bottom row (reversed)
        for (int i = 0; i < 3; i++)
            arr[4][2][i] = left[2 - i];
    }

    public void moveFp() {
        // Rotate front anti-clockwise
        antiClockwiseTurn(arr[0]);

        char[] up = new char[] { arr[4][2][0], arr[4][2][1], arr[4][2][2] };
        char[] right = new char[] { arr[1][0][0], arr[1][1][0], arr[1][2][0] };
        char[] down = new char[] { arr[5][0][0], arr[5][0][1], arr[5][0][2] };
        char[] left = new char[] { arr[3][0][2], arr[3][1][2], arr[3][2][2] };

        // U ← R
        for (int i = 0; i < 3; i++)
            arr[4][2][i] = right[i];
        // L ← U (reversed)
        for (int i = 0; i < 3; i++)
            arr[3][i][2] = up[2 - i];
        // D ← L (reversed)
        for (int i = 0; i < 3; i++)
            arr[5][0][i] = left[i];
        // R ← D
        for (int i = 0; i < 3; i++)
            arr[1][i][0] = down[2 - i];
    }

    /* M-move */
    public void moveM() {
        char[] up = new char[] { arr[4][0][1], arr[4][1][1], arr[4][2][1] };
        char[] front = new char[] { arr[0][0][1], arr[0][1][1], arr[0][2][1] };
        char[] down = new char[] { arr[5][0][1], arr[5][1][1], arr[5][2][1] };
        char[] back = new char[] { arr[2][0][1], arr[2][1][1], arr[2][2][1] };

        // U → F
        for (int i = 0; i < 3; i++)
            arr[0][i][1] = up[i];
        // F → D
        for (int i = 0; i < 3; i++)
            arr[5][i][1] = front[i];
        // D → B reversed
        for (int i = 0; i < 3; i++)
            arr[2][i][1] = down[2 - i];
        // B → U reversed
        for (int i = 0; i < 3; i++)
            arr[4][i][1] = back[2 - i];
    }

    /* M'-move */
    public void moveMp() {
        char[] up = new char[] { arr[4][0][1], arr[4][1][1], arr[4][2][1] };
        char[] front = new char[] { arr[0][0][1], arr[0][1][1], arr[0][2][1] };
        char[] down = new char[] { arr[5][0][1], arr[5][1][1], arr[5][2][1] };
        char[] back = new char[] { arr[2][0][1], arr[2][1][1], arr[2][2][1] };

        for (int i = 0; i < 3; i++)
            arr[4][i][1] = front[i];
        for (int i = 0; i < 3; i++)
            arr[0][i][1] = down[i];
        for (int i = 0; i < 3; i++)
            arr[5][i][1] = back[2 - i];
        for (int i = 0; i < 3; i++)
            arr[2][i][1] = up[2 - i];
    }

    /* D-move */
    public void moveD() {
        // Bottom will rotate by 90 degrees
        clockwiseTurn(arr[5]);
        // Adjust (Front-Left-Back-Right)
        char[] temp = arr[0][2];
        arr[0][2] = arr[3][2];
        arr[3][2] = arr[2][2];
        arr[2][2] = arr[1][2];
        arr[1][2] = temp;
    }

    /* D'-move */
    public void moveDp() {
        // Bottom will rotate by 90 degrees
        antiClockwiseTurn(arr[5]);
        // Adjust (Front-Left-Back-Right) | (0 - 1 - 2 - 3)
        char[] temp = arr[0][2];
        arr[0][2] = arr[1][2];
        arr[1][2] = arr[2][2];
        arr[2][2] = arr[3][2];
        arr[3][2] = temp;
    }

    public void moveE() {

        char[] front = new char[] { arr[0][1][0], arr[0][1][1], arr[0][1][2] };
        char[] right = new char[] { arr[1][1][0], arr[1][1][1], arr[1][1][2] };
        char[] back = new char[] { arr[2][1][0], arr[2][1][1], arr[2][1][2] };
        char[] left = new char[] { arr[3][1][0], arr[3][1][1], arr[3][1][2] };

        // front → right
        for (int i = 0; i < 3; i++)
            arr[1][1][i] = front[i];
        // right → back
        for (int i = 0; i < 3; i++)
            arr[2][1][i] = right[i];
        // back → left
        for (int i = 0; i < 3; i++)
            arr[3][1][i] = back[i];
        // left → front
        for (int i = 0; i < 3; i++)
            arr[0][1][i] = left[i];
    }

    public void moveEp() {

        char[] front = new char[] { arr[0][1][0], arr[0][1][1], arr[0][1][2] };
        char[] right = new char[] { arr[1][1][0], arr[1][1][1], arr[1][1][2] };
        char[] back = new char[] { arr[2][1][0], arr[2][1][1], arr[2][1][2] };
        char[] left = new char[] { arr[3][1][0], arr[3][1][1], arr[3][1][2] };

        // front ← left
        for (int i = 0; i < 3; i++)
            arr[0][1][i] = left[i];

        // left ← back
        for (int i = 0; i < 3; i++)
            arr[3][1][i] = back[i];

        // back ← right
        for (int i = 0; i < 3; i++)
            arr[2][1][i] = right[i];

        // right ← front
        for (int i = 0; i < 3; i++)
            arr[1][1][i] = front[i];
    }

    public void moveWideR() {
        moveR();
        moveMp();
    }

    public void moveWideRp() {
        moveRp();
        moveM();
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        for(int side=0; side<6; side++) {
            for(int i=0; i<3; i++) {
                for(int j=0; j<3; j++) {
                    sb.append(arr[side][i][j]);
                }
            }
        }
        return sb.toString();
    }
    public void scramble() {

        arr = new char[][][] {
                {
                        { 'R', 'O', 'O' },
                        { 'W', 'G', 'Y' },
                        { 'R', 'O', 'G' }
                },
                {
                        { 'W', 'B', 'O' },
                        { 'R', 'R', 'R' },
                        { 'Y', 'R', 'R' }
                },
                {
                        { 'G', 'R', 'W' },
                        { 'G', 'B', 'W' },
                        { 'B', 'B', 'Y' }
                },
                {
                        { 'R', 'G', 'G' },
                        { 'G', 'O', 'O' },
                        { 'B', 'O', 'G' }
                },


                {
                        { 'B', 'W', 'W' },
                        { 'Y', 'W', 'Y' },
                        { 'W', 'G', 'B' }
                },
                {
                        { 'Y', 'B', 'O' },
                        { 'Y', 'Y', 'B' },
                        { 'O', 'W', 'Y' }
                }
        };
    }
}
