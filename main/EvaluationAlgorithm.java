package main;

import java.util.*;

import classes.Customer;
import classes.Solution;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;

public class EvaluationAlgorithm {
    private static int popSize = 100;			// Population size
    private static int numOffsprings = 30;		// Number of offsprings
    private static boolean survival = true;	// true=Elitism and false=Generational. I elitism så overlever foreldrene (the fittest) til neste generasjon
    private static double mutationRate = 0.01;		// Mutation rate
    private static double recombProbability = 0.7; // For hver forelder som blir valgt, er det 70% sjanse for at det blir gjort en crossover, og 30% at det blir en kopi av forelder
    private static int maxRuns = 100;				// Maximum number of runs before termination
    private static int tournamentSize = 5;		// Number of individuals to choose from population at random
    // Eventuelt legge til "No improvement in the last 25 generations"
    
    private static ArrayList<Solution> population;
    private static Solution bestSolution;
    private ArrayList<Solution> ob;
    Random r = new Random();
    
    /*
     * Algorithm
     */
    public EvaluationAlgorithm() {

    }

    public void run(){
        // If elitism is turned off
        if(!survival ){
            numOffsprings = popSize;
        }

        // Initialize population
        population = new ArrayList<>();

        while(population.size() < popSize) {
            Collections.shuffle(Run.customers);
            Solution temp = new Solution(true);
            if(temp.valid)
                population.add(temp);
        }
        System.out.println("Initialize population done. " + popSize + " random solutions found");

        // Calculate fitness score
        for(Solution s : population){
            s.calculateTotalCost();
        }

        int generation = 0;
        while(generation < maxRuns) {
            ArrayList<Solution> offsprings = new ArrayList<>();

            while(offsprings.size() < numOffsprings) {
                // Selection
                Solution[] selected = tournamentSelection();

                // Crossover
                Solution[] offspringsTemp = crossover(selected[0], selected[1]);

                // Mutation
                for(Solution child : offspringsTemp){
                    mutate(child);
                    if(child.valid){
                        offsprings.add(child);
                    }
                }
            }
            while (offsprings.size() > numOffsprings){
                offsprings.remove(offsprings.size()-1);
            }

            if(!survival) {
                population.clear();
            }

            for(Solution solution : offsprings) {
                population.add(solution);
            }

            Collections.sort(population);

            while(population.size() > popSize) {
                population.remove(population.size() - 1);
            }

            bestSolution = population.get(0);

            System.out.println("Result: " + bestSolution.getTotalCost());
            ob.add(bestSolution);

            generation++;
        }
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

            offspring.validate();

            // MakeValid
            offspring.repair();
        }

    	return offsprings;
    }
    
    public void mutate(Solution solution) {
        solution.mutate(mutationRate);
        solution.validate();
        solution.repair();
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
    public static double getMutationRate() {
        return mutationRate;
    }
    public void setMutationRate(double mutationRate) {
        this.mutationRate = mutationRate;
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