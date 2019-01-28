package main;

import java.util.ArrayList;

import classes.Solution;

public class EvaluationAlgorithm {
    private static int popSize = 100;			// Population size
    private static int numOffsprings = 0;		// Number of offspring (antall barn generert)
    private static boolean survival = false;	// true=Elitism og false=Generational. I elitism så overlever foreldrene (the fittest) til neste generasjon
    private static double mp = 0.0;				// Muation probability pm (1/n) - (Mutation rate)
    // Må håndteres annerledes i Elitism
    private static double recombProbability = 0.7; // For hver forelder som blir valgt, er det 70% sjanse for at det blir gjort en crossover, og 30% at det blir en kopi av forelder
    private static int maxRuns = 0;				// Maximum number of runs before termination
    private static int tournamentSize = 5;		// How many random picks from population
    // Eventuelt legge til "No improvement in the last 25 generations"

    /*
     * Algorithm
     */
    public EvaluationAlgorithm() {
        // Initialize population
        ArrayList<Solution> population = new ArrayList<>();

        for(int i = 0; i < popSize; i++) {
            population.add(new Solution());
        }
        System.out.println("Initialize population done. " + popSize + " random solutions found");
        // Calculate fitness score

        for(Solution s : population){
            s.calculateTotalCost();
        }

        // While
    }














    /*
     * Getters and Setters
     */
    public static int getPopSize() {
        return popSize;
    }
    public void setPopSize(int popSize) {
        this.popSize = popSize;
    }
    public static int getNumOffsprings() {
        return numOffsprings;
    }
    public void setNumOffsprings(int numOffsprings) {
        this.numOffsprings = numOffsprings;
    }
    public static boolean isSurvival() {
        return survival;
    }
    public void setSurvival(boolean survival) {
        this.survival = survival;
    }
    public static double getMp() {
        return mp;
    }
    public void setMp(double mp) {
        this.mp = mp;
    }
    public static double getRecombProbability() {
        return recombProbability;
    }
    public void setRecombProbability(double recombProbability) {
        this.recombProbability = recombProbability;
    }
    public static int getMaxRuns() {
        return maxRuns;
    }
    public void setMaxRuns(int maxRuns) {
        this.maxRuns = maxRuns;
    }
    public static int getTournamentSize() {
        return tournamentSize;
    }
    public void setTournamentSize(int tournamentSize) {
        this.tournamentSize = tournamentSize;
    }
}