package organisms.g6;

import organisms.Move;
import organisms.ui.OrganismsGame;
import organisms.OrganismsPlayer;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class Group6Player implements OrganismsPlayer {
    private OrganismsGame game;
    private int dna;
    private ThreadLocalRandom random;

    private int M;
    private int v;
    private int movesSinceFood;
    private boolean isSettler;
    private boolean[][] visited; // Keep track of visited cells for better exploring

    @Override
    public void register(OrganismsGame game, int dna) throws Exception {
        this.game = game;
        this.dna = dna;
        this.random = ThreadLocalRandom.current();
        this.M = game.M();
        this.v = game.v();
        this.movesSinceFood = 0;
        this.isSettler = false;
        this.visited = new boolean[3][3]; // Track positions around the settler
    }

    @Override
    public String name() {
        return "Group Settler/Explorer Player";
    }

    @Override
    public Color color() {
        return new Color(200, 14, 15, 200);
    }

    @Override
    public Move move(int foodHere, int energyLeft, boolean foodN, boolean foodE,
                     boolean foodS, boolean foodW, int neighborN, int neighborE,
                     int neighborS, int neighborW) {

        // Determine if this organism should become a settler
        if (!isSettler && shouldBecomeSettler(foodHere, foodN, foodE, foodS, foodW)) {
            isSettler = true;
            resetVisited(); // Reset visited cells when becoming a settler
        } else if (isSettler && (foodHere == 0 || countAdjacentFood(foodN, foodE, foodS, foodW) < 2)) {
            // Revert to explorer if the food condition is not met
            isSettler = false;
        }

        // Reproduce if possible
        if (energyLeft > M / 2 && hasEmptyAdjacentCell(neighborN, neighborE, neighborS, neighborW)) {
            return reproduce(neighborN, neighborE, neighborS, neighborW);
        }

        // Move based on current role
        if (isSettler) {
            return settlerMove(foodHere, foodN, foodE, foodS, foodW, neighborN, neighborE, neighborS, neighborW, energyLeft);
        } else {
            return explorerMove(foodHere, foodN, foodE, foodS, foodW, neighborN, neighborE, neighborS, neighborW, energyLeft);
        }
    }

    private boolean shouldBecomeSettler(int foodHere, boolean foodN, boolean foodE, boolean foodS, boolean foodW) {
        // Become a settler only if there is food here and at least two surrounding cells 
        return foodHere > 0 && countAdjacentFood(foodN, foodE, foodS, foodW) >= 2;
    }

    private int countAdjacentFood(boolean foodN, boolean foodE, boolean foodS, boolean foodW) {
        int count = 0;
        if (foodN) count++;
        if (foodE) count++;
        if (foodS) count++;
        if (foodW) count++;
        return count;
    }

    private void resetVisited() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                visited[i][j] = false;
            }
        }
    }

    private boolean hasEmptyAdjacentCell(int neighborN, int neighborE, int neighborS, int neighborW) {
        return neighborN == -1 || neighborE == -1 || neighborS == -1 || neighborW == -1;
    }

    private Move reproduce(int neighborN, int neighborE, int neighborS, int neighborW) {
        if (neighborN == -1) return Move.reproduce(Action.NORTH, random.nextInt());
        if (neighborE == -1) return Move.reproduce(Action.EAST, random.nextInt());
        if (neighborS == -1) return Move.reproduce(Action.SOUTH, random.nextInt());
        if (neighborW == -1) return Move.reproduce(Action.WEST, random.nextInt());
        return Move.movement(Action.STAY_PUT);
    }

    private Move settlerMove(int foodHere, boolean foodN, boolean foodE, boolean foodS, boolean foodW,
                             int neighborN, int neighborE, int neighborS, int neighborW, int energyLeft) {

        // Reproduce if energy  sufficient and empty adjacent cell
        if (energyLeft > M / 2 && hasEmptyAdjacentCell(neighborN, neighborE, neighborS, neighborW)) {
            return reproduce(neighborN, neighborE, neighborS, neighborW);
        }

        // Move to allow food doubling
        if (foodHere > 0) {
            // Determine next move based on visited state
            if (!visited[1][0] && neighborN == -1) {
                visited[1][0] = true;
                return Move.movement(Action.NORTH);
            }
            if (!visited[2][1] && neighborE == -1) {
                visited[2][1] = true;
                return Move.movement(Action.EAST);
            }
            if (!visited[1][2] && neighborS == -1) {
                visited[1][2] = true;
                return Move.movement(Action.SOUTH);
            }
            if (!visited[0][1] && neighborW == -1) {
                visited[0][1] = true;
                return Move.movement(Action.WEST);
            }

            // Reset visited when all directions are explored
            resetVisited();
        }

        // If no move, stay put
        return Move.movement(Action.STAY_PUT);
    }

    private Move explorerMove(int foodHere, boolean foodN, boolean foodE, boolean foodS, boolean foodW,
                              int neighborN, int neighborE, int neighborS, int neighborW, int energyLeft) {

        // Move towards food if possible
        if (foodN && neighborN == -1) {
            movesSinceFood = 0;
            return Move.movement(Action.NORTH);
        }
        if (foodE && neighborE == -1) {
            movesSinceFood = 0;
            return Move.movement(Action.EAST);
        }
        if (foodS && neighborS == -1) {
            movesSinceFood = 0;
            return Move.movement(Action.SOUTH);
        }
        if (foodW && neighborW == -1) {
            movesSinceFood = 0;
            return Move.movement(Action.WEST);
        }

        // If no food found for a while, random exploration
        if (energyLeft > v) {
            int[] possibleMoves = new int[4];
            int count = 0;
            if (neighborN == -1) possibleMoves[count++] = Action.NORTH.ordinal();
            if (neighborE == -1) possibleMoves[count++] = Action.EAST.ordinal();
            if (neighborS == -1) possibleMoves[count++] = Action.SOUTH.ordinal();
            if (neighborW == -1) possibleMoves[count++] = Action.WEST.ordinal();

            if (count > 0) {
                int actionIndex = random.nextInt(count);
                Action actionChoice = Action.fromInt(possibleMoves[actionIndex]);
                movesSinceFood++;
                return Move.movement(actionChoice);
            }
        }

        // If low energy or no valid moves, stay put
        return Move.movement(Action.STAY_PUT);
    }

    @Override
    public int externalState() {
        return 0;
    }
}
