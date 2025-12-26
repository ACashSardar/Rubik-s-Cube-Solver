<img width="200" height="200" alt="image" src="https://github.com/user-attachments/assets/21391889-50de-4cf6-8af6-93f089596c35" />

<h1>🧩 Rubik’s Cube Solver – ROUX Method</h1>

A step-by-step, beginner-friendly Rubik’s Cube solver implemented using the ROUX method.
This project focuses on creating an intuitive, human-like solving approach rather than finding the shortest possible move sequence.

<h3>📌 Project Goal</h3>

Most algorithmic solvers aim for optimal solutions using advanced heuristics or large lookup tables.
In contrast, this project aims to:

  i) Mimic how a human solves the cube
  
  ii) Break the problem into clear, logical steps
  
  iii) Avoid massive search spaces

iv) Be easy to understand and implement

<h3>🧠 Why Not Blind Search?</h3>

Applying blind search algorithms like DFS, BFS, or Dijkstra’s algorithm directly from a scrambled state to a solved state leads to an explosive search space, even with pruning.

Branching factor ≈ 12

Depth ≈ 15+ moves

12^15 states → Not feasible

Alternative Approaches

Heuristic search (A*) – powerful but requires pattern database

Human methods (CFOP / ROUX) – structured and efficient

👉 This project follows a structured ROUX-based approach, implemented programmatically.

<h3>🔷 ROUX Method Overview </h3>

The solver follows the classic 4-step ROUX workflow:

  i) First Block

  ii) Second Block
  
  iii) CMLL (Corners of the Last Layer)

  iv) LSE (Last Six Edges)

Each step is solved independently using small, manageable searches.

<h4>🧱 Step 1: First Block</h4>
Allowed Moves [U, U', R, R', L, L', M, M', D, D', F, F']

Problem:

Worst case ≈ 15 moves

Branching factor = 12

Blind search is infeasible

Solution: Break It Down

The First Block is split into three smaller substeps:

  i) Insert the bottom-right edge
  
  ii) Insert the first pair
  
  iii) Insert the second pair

Each substep:

Solvable in ~7–8 moves

Uses Iterative Deepening DFS (IDDFS)

Finds short solutions without high memory usage

<h4>🧱 Step 2: Second Block</h4>

The Second Block is similar to the First Block, but with restricted moves to avoid breaking the solved block.

Allowed Moves [U, U', R, R', M, M', Rw, Rw']

Reduced branching factor: 8

Each substep ≈ 7–8 moves

Solved using IDDFS

<h4>🔄 Step 3: CMLL (Corners of the Last Layer)</h4>

CMLL orients and permutes the last-layer corners.

Allowed Moves [U, U', Sune, Antisune]

  Sune → clockwise corner twist
  
  Antisune → counter-clockwise corner twist
  
  Corner twist notation:
  
  Sune:      +1 +1 +1 -3
  Antisune:  -1 -1 -1 +3


✅ It can be proven that at most two Sune / Antisune applications are sufficient.

<h4>🔚 Step 4: LSE (Last Six Edges)</h4>

LSE is divided into three substeps:

1️⃣ Edge Orientation (EO)

  Goal: Bring all good edges to the top and bottom.
  
  After CMLL, the number of bad edges can only be: [0, 2, 4, 6]
  
  All cases are transformed into the arrow case (3 bad edges on top, 1 on bottom), then solved using:
  
  M' U M   or   M U M'
  
  This step is implemented using case inspection + recursion, since the number of possibilities is limited.

2️⃣ UL–UR Edges

  This step completely solves the left and right edges of the cube.

3️⃣ Last Four Edges

  The final four middle-slice edges are solved using combinations of:

  M, M', U2

Very small branching factor

Solved using IDDFS

<h4>✅ Final Result</h4>

After completing all four ROUX steps, the cube is fully solved 🎉
