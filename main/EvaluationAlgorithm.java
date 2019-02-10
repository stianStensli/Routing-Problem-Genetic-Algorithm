package main;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import classes.Solution;
import classes.Vehicle;

public class EvaluationAlgorithm {
    private static int popSize = 100; // Population size
    private static int numOffsprings = 10; // Number of offsprings
    private static boolean survival = true; // true=Elitism and false=Generational
    private static double mutationRate = 0.08; // Mutation rate
    private static double recombProbability = 0.7; // Used only for Generational. recombProbability of doing crossover, and 1-recombProbability of copying a parent
    private static int maxRuns = 100; // Maximum number of runs before termination
    private static int tournamentSize = 20; // Number of individuals to choose from population at random
    // Eventuelt legge til "No improvement in the last 25 generations"
    
    private static ArrayList<Solution> population;
    private static Solution bestSolution;
    private ArrayList<Solution> ob;
    private AtomicInteger generation = new AtomicInteger(0);
    
    /*
     * Algorithm
     */
    public EvaluationAlgorithm() {

    }

    public void run() {
        // If elitism is turned off
        if(!survival ) {
            numOffsprings = popSize;
        }

        // Initialize population
        population = new ArrayList<>();

        while(population.size() < popSize) {
            Collections.shuffle(Run.customers);
            Solution temp = new Solution(true);
            if(temp.isValid())
                population.add(temp);
        }
        System.out.println("Initialize population done. " + popSize + " random solutions found");

        // Calculate fitness score
        for(Solution s : population) {
            s.calculateTotalCost();
        }

        generation.set(0);
        while(generation.incrementAndGet() < maxRuns) {
            ArrayList<Solution> offsprings = new ArrayList<>();

            while(offsprings.size() < numOffsprings) {
                // Selection
                Solution[] selected = rouletteWheelSelection();

                // Crossover
                Solution[] offspringsTemp = crossover(selected[0], selected[1]);

                // Mutation
                for(Solution child : offspringsTemp) {
                    mutate(child);
                    if(child.isValid()) {
                        offsprings.add(child);
                    }
                }
            }
            while (offsprings.size() > numOffsprings) {
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
            ob.add(bestSolution);
        }
    }

    /*
     * Methods
     */
    public Solution[] customRankSelectionDiverse() {
        Collections.sort(population);

        LinkedList<Solution> topN = new LinkedList<>();

        while(topN.size() < tournamentSize) {
            topN.add(population.get(topN.size()));
        }

        //Adding a random Node for diversity
        int rnd = (int)(Math.random()*(popSize-tournamentSize))+tournamentSize;
        topN.add(population.get(rnd));
        Collections.shuffle(topN);

        return new Solution[]{topN.pop(), topN.pop()};
    }

    public Solution[] crossover(Solution father, Solution mother) {
        Solution[] offsprings;

        if(Math.random() < recombProbability) {
            offsprings = singlePointCrossover(father, mother);
        }else{
            offsprings = new Solution[]{new Solution(father), new Solution(mother)};
        }

        for(Solution offspring : offsprings) {
            // Remove duplicates
            offspring.removeDuplicateCustomers();

            // Find potential customers that has no vehicle assigned to it
            offspring.updateMissingCustomers();

            // Make the solution valid, meaning all customers are used and no constraints are broken
            offspring.validate();
            offspring.repair();
        }

        return offsprings;
    }
    public Solution[] singlePointCrossover(Solution father, Solution mother) {
    	int crossoverPoint = (int) (Math.random()*(father.getVehicles().size() - 2));
        Solution[] offsprings = new Solution[]{new Solution(), new Solution()};

        for(int i = 0; i < father.getVehicles().size(); i++) {
            if(i <= crossoverPoint) {
                offsprings[0].addVehicle(new Vehicle(father.getVehicles().get(i)));
                offsprings[1].addVehicle(new Vehicle(mother.getVehicles().get(i)));
            }
            else {
                offsprings[0].addVehicle(new Vehicle(mother.getVehicles().get(i)));
                offsprings[1].addVehicle(new Vehicle(father.getVehicles().get(i)));
            }
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
    public static int getPopSize() { return popSize; }
    public void setPopSize(int popSize) { this.popSize = popSize; }
    public static int getNumOffsprings() { return numOffsprings; }
    public void setNumOffsprings(int numOffsprings) { this.numOffsprings = numOffsprings; }
    public static boolean isSurvival() { return survival; }
    public void setSurvival(boolean survival) { this.survival = survival; }
    public static double getMutationRate() { return mutationRate; }
    public void setMutationRate(double mutationRate) { this.mutationRate = mutationRate; }
    public static double getRecombProbability() { return recombProbability; }
    public void setRecombProbability(double recombProbability) { this.recombProbability = recombProbability; }
    public static int getMaxRuns() { return maxRuns; }
    public void setMaxRuns(int maxRuns) { this.maxRuns = maxRuns; }
    public static int getTournamentSize() { return tournamentSize; }
    public void setTournamentSize(int tournamentSize) { this.tournamentSize = tournamentSize; }

	public static List<Solution> getPopulation() { return population; }
	public static Solution getBestSolution() { return bestSolution; }
	public void loadObservableList(ArrayList<Solution> ob){ this.ob = ob; }
    public AtomicInteger getGeneration() { return generation; }

    /*
     *
     * These methods were implemented and tested, but did not make the cut
     *
     */
    public Solution[] tournamentSelection() {
        List<Solution> tournament = new ArrayList<>();

        for(int i = 0; i < tournamentSize; i++) {
            while(true) {
                int randomIndex = (int) (Math.random()*population.size());
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
    public Solution[] rouletteWheelSelection() {
        Collections.sort(population);
        double sumFitness = 0.0;

        for(Solution s : population) {
            sumFitness += 1 / s.getTotalCost();
        }

        double valueFather = Math.random() * sumFitness;
        double valueMother = Math.random() * sumFitness;

        Solution father = null;
        Solution mother = null;

        for(Solution s : population) {
            valueFather -= 1 / s.getTotalCost();
            valueMother -= 1 / s.getTotalCost();

            if(valueFather < 0 && father == null) {
                father = s;
            }
            if(valueMother < 0 && mother == null && !s.equals(father)) {
                mother = s;
            }
            if(father != null && mother != null) {
                break;
            }
        }

        return new Solution[]{father, mother};
    }
    public Solution[] rankSelectionActual() {
        Collections.sort(population);
        int shares = (popSize+1)*popSize/2;
        int index = (int)(Math.random()*shares) + 1;

        Solution father = null;
        int rmShares = 0;
        int lastIndex = 0;
        for(int i = 0; i < popSize; i++){
            rmShares += popSize-i;
            if(shares - rmShares < index){
                father = population.get(i);
                lastIndex = i;
                break;
            }
        }

        Solution mother = null;
        rmShares = 0;
        for(int i = 0; i < popSize; i++){
            rmShares += popSize-i;
            if(shares - rmShares < index){
                mother = population.get(i);
                break;
            }
        }

        return new Solution[]{father, mother};
    }

    public Solution[] uniformCrossover(Solution father, Solution mother) {
        Solution[] offsprings = new Solution[]{new Solution(), new Solution()};

        for(int i = 0; i < father.getVehicles().size(); i++) {
            if(Math.random() < 0.5) {
                offsprings[0].addVehicle(new Vehicle(father.getVehicles().get(i)));
                offsprings[1].addVehicle(new Vehicle(mother.getVehicles().get(i)));
            }
            else {
                offsprings[0].addVehicle(new Vehicle(mother.getVehicles().get(i)));
                offsprings[1].addVehicle(new Vehicle(father.getVehicles().get(i)));
            }
        }

        return offsprings;
    }
}