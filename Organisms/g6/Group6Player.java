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

    @Override
    public void register(OrganismsGame game, int dna) throws Exception {
        this.game = game;
        this.dna = dna;
        this.random = ThreadLocalRandom.current();

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
            // else move to place with food or move randomly
       else{
           if(foodHere > 0){
               return Move.movement(Action.STAY_PUT);
           }
           else{
               return moveToFoodOrRandom(foodN, foodE, foodS, foodW, energyLeft);
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
        if (neighborN == -1) return Move.reproduce(Action.NORTH, random.nextInt());
        if (neighborE == -1) return Move.reproduce(Action.EAST, random.nextInt());
        if (neighborS == -1) return Move.reproduce(Action.SOUTH, random.nextInt());
        if (neighborW == -1) return Move.reproduce(Action.WEST, random.nextInt());
        return Move.movement(Action.STAY_PUT);
    }

    private Move moveToFoodOrRandom(boolean foodN, boolean foodE, boolean foodS, boolean foodW, int energyLeft) {
        if (foodN) return Move.movement(Action.NORTH);
        if (foodE) return Move.movement(Action.EAST);
        if (foodS) return Move.movement(Action.SOUTH);
        if (foodW) return Move.movement(Action.WEST);
        if (energyLeft > v) {
            // Move randomly if no food is nearby
            int actionIndex = random.nextInt(Action.getNumActions());
            Action actionChoice = Action.fromInt(actionIndex);
            return Move.movement(actionChoice);
        } else {
            // Stay put if energy is low
            return Move.movement(Action.STAY_PUT);
        }
    }
    @Override
    public int externalState() {
        return 0;
    }
}
