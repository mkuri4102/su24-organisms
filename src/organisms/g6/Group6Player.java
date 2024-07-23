package organisms.g6;

import organisms.Move;
import organisms.ui.OrganismsGame;
import organisms.OrganismsPlayer;
import organisms.ui.ParseValue;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class Group6Player implements OrganismsPlayer {
    private OrganismsGame game;
    private int dna;
    private ThreadLocalRandom random;

    private int M ;
    private int v ;
    //ParseValue.parseIntegerValue(properties.getProperty("M"));
    @Override
    public void register(OrganismsGame game, int dna) throws Exception {
        this.game = game;
        this.dna = dna;
        this.random = ThreadLocalRandom.current();
        this.M = game.M();
        this.v = game.v();

    }

    @Override
    public String name() {
        return "Group 6 Player";
    }

    @Override
    public Color color() {
        return new Color(166, 124, 255, 255);
    }

    @Override
    public Move move(int foodHere, int energyLeft, boolean foodN, boolean foodE,
                     boolean foodS, boolean foodW, int neighborN, int neighborE,
                     int neighborS, int neighborW) {
        //if energy left in organism > M/2 and any adjacent cell is empty
            // {reproduce}
        if (energyLeft > M / 2 && hasEmptyAdjacentCell(neighborN, neighborE, neighborS, neighborW)) {
            return reproduce(neighborN, neighborE, neighborS, neighborW);
        }
        //else if less energy
        // if there is food on the cell stay in that cell and eating food
        // else move to place with food or if no food around then move randomly
        else{
            if(foodHere > 0){
                return Move.movement(Action.STAY_PUT);
            }
            else{
                return moveToFoodOrRandom(foodN, foodE, foodS, foodW, neighborN, neighborE, neighborS, neighborW, energyLeft);
            }

        }

       /*
       //OLD Code - Moves are totally random and may not be valid depending on whether neighboring squares are occupied!
        int actionIndex = this.random.nextInt(Action.getNumActions());
        Action actionChoice = Action.fromInt(actionIndex);

        if (actionChoice == Action.REPRODUCE) {
            // randomly pick a direction and key for the child
            int childPosIndex = this.random.nextInt(1, 5);
            Action childPosChoice = Action.fromInt(childPosIndex);
            int childKey = this.random.nextInt();
            return Move.reproduce(childPosChoice, childKey);
        } else {
            // staying put or moving in a direction
            return Move.movement(actionChoice);
        }
        */
    }

    private boolean hasEmptyAdjacentCell(int neighborN, int neighborE, int neighborS, int neighborW) {
        return neighborN == -1 || neighborE == -1 || neighborS == -1 || neighborW == -1;
    }

    private Move reproduce(int neighborN, int neighborE, int neighborS, int neighborW) {
        if (neighborN == -1) return Move.reproduce(Action.NORTH, neighborN);
        if (neighborE == -1) return Move.reproduce(Action.EAST, neighborE);
        if (neighborS == -1) return Move.reproduce(Action.SOUTH, neighborS);
        if (neighborW == -1) return Move.reproduce(Action.WEST, neighborW);
        return Move.movement(Action.STAY_PUT);
    }

    private Move moveToFoodOrRandom(boolean foodN, boolean foodE, boolean foodS, boolean foodW,
                                    int neighborN, int neighborE, int neighborS, int neighborW,
                                    int energyLeft) {
        if (foodN && neighborN == -1) return Move.movement(Action.NORTH);
        if (foodE && neighborE == -1) return Move.movement(Action.EAST);
        if (foodS && neighborS == -1) return Move.movement(Action.SOUTH);
        if (foodW && neighborW == -1) return Move.movement(Action.WEST);

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
                return Move.movement(actionChoice);
            }
        }
        return Move.movement(Action.STAY_PUT);
    }
    @Override
    public int externalState() {
        return 0;
    }
}
