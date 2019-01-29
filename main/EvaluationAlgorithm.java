package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

import classes.Solution;
import javafx.collections.ObservableList;

public class EvaluationAlgorithm {
    private static int popSize = 100;			// Population size
    private static int numOffsprings = 0;		// Number of offsprings
    private static boolean survival = false;	// true=Elitism and false=Generational. I elitism så overlever foreldrene (the fittest) til neste generasjon
    private static double mp = 0.0;				// Mutation probability pm (1/n) - (Mutation rate)
    private static double recombProbability = 0.7; // For hver forelder som blir valgt, er det 70% sjanse for at det blir gjort en crossover, og 30% at det blir en kopi av forelder
    private static int maxRuns = 0;				// Maximum number of runs before termination
    private static int tournamentSize = 5;		// Number of individuals to choose from population at random
    // Eventuelt legge til "No improvement in the last 25 generations"
    
    private static List<Solution> population;
    private static Solution bestSolution;

    private ObservableList<Solution> ob;
    
    /*
     * Algorithm
     */
    public EvaluationAlgorithm() {

    }

    public void run(){
        // Initialize population
        population = new ArrayList<>();

        for(int i = 0; i < popSize; i++) {
            Collections.shuffle(Run.customers);
            population.add(new Solution());
        }
        System.out.println("Initialize population done. " + popSize + " random solutions found");

        // Calculate fitness score
        for(Solution s : population){
            s.calculateTotalCost();
        }

        // Sort population based on fitness score
        population.sort((s1, s2) -> Double.compare(s1.getTotalCost(), s2.getTotalCost()));

        bestSolution = population.get(0);
        System.out.println("Result: "+bestSolution.getTotalCost());

        tournamentSelection(population);
        
        ob.clear();
        ob.add(bestSolution);

    }
    /*
     * Methods
     */
    public void tournamentSelection(List<Solution> population) {
    	List<Solution> tournament = new ArrayList<>();
    	
    }
    
    public void crossover() {
    	
    }
    
    public void mutate() {
    	
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

	public static List<Solution> getPopulation() {
		return population;
	}
	public static Solution getBestSolution() {
		return bestSolution;
	}

	public void loadObservableList(ObservableList<Solution> ob){
        this.ob = ob;
    }

}