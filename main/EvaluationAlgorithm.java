package main;

import java.util.*;

import classes.Customer;
import classes.Solution;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;

public class EvaluationAlgorithm {
    private static int popSize = 20;			// Population size
    private static int numOffsprings = 0;		// Number of offsprings
    private static boolean survival = false;	// true=Elitism and false=Generational. I elitism så overlever foreldrene (the fittest) til neste generasjon
    private static double mp = 0.01;				// Mutation probability pm (1/n) - (Mutation rate)
    private static double recombProbability = 0.7; // For hver forelder som blir valgt, er det 70% sjanse for at det blir gjort en crossover, og 30% at det blir en kopi av forelder
    private static int maxRuns = 0;				// Maximum number of runs before termination
    private static int tournamentSize = 5;		// Number of individuals to choose from population at random
    // Eventuelt legge til "No improvement in the last 25 generations"
    
    private static ArrayList<Solution> population;
    private static Solution bestSolution;

    private ArrayList<Solution> ob;
    
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
            population.add(new Solution(true));
        }
        System.out.println("Initialize population done. " + popSize + " random solutions found");

        // Calculate fitness score
        for(Solution s : population){
            s.calculateTotalCost();
        }

        // Sort population on (1)fitness score, (2)if it's valid and (3)number of customers in the route
        Collections.sort(population);

        bestSolution = population.get(0);
        System.out.println("Result: " + bestSolution.getTotalCost());

        // Selection
        Solution[] selection = tournamentSelection();
        Solution selectionBestSolution = selection[0];
        Solution selectionSecondBestSolution = selection[1];

        // Crossover
        Solution offsprings[] = crossover(selectionBestSolution, selectionSecondBestSolution);
        bestSolution = offsprings[0];

        // Mutation
        mutate(bestSolution);

        while(!bestSolution.valid){
            bestSolution.makeValid();
            //Platform.runLater(new Runnable() {
              //  @Override
                //public void run() {
                   // ob.add(bestSolution);
                //}
            //});

        }
        ob.add(bestSolution);
        

    }

    /*
     * Methods
     */
    public Solution[] tournamentSelection() {
    	List<Solution> tournament = new ArrayList<>();

    	for(int i = 0; i < tournamentSize; i++) {
    	    while(true) {
                int randomIndex = r.nextInt(population.size());
                Solution tempSolution = population.get(randomIndex);

    	        if(!tournament.contains(tempSolution)) {
    	            tournament.add(tempSolution);
    	            break;
                }
            }
        }

    	Collections.sort(tournament);

        return new Solution[]{tournament.get(0), tournament.get(1)};
    }
    
    public Solution[] crossover(Solution father, Solution mother) {
    	int crossoverPoint = (int) (Math.random()*(father.getVehicles().size() - 2));
        Solution[] offsprings = new Solution[]{new Solution(), new Solution()};

        for(int i = 0; i < father.getVehicles().size(); i++) {
            if(i <= crossoverPoint) {
                offsprings[0].addVehicle(father.getVehicles().get(i));
                offsprings[1].addVehicle(mother.getVehicles().get(i));
            }
            else {
                offsprings[0].addVehicle(mother.getVehicles().get(i));
                offsprings[1].addVehicle(father.getVehicles().get(i));
            }
        }

        for(Solution offspring : offsprings) {
            // Remove duplicates
            offspring.removeDuplicateCustomers();

            // Check if all customers are in solution
            offspring.updateMissingCustomers();

            // MakeValid
        }

    	return offsprings;
    }
    
    public void mutate(Solution solution) {

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
	public void loadObservableList(ArrayList<Solution> ob){
        this.ob = ob;
    }
}