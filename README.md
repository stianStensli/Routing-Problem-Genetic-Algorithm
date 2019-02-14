## MDVRP solved by Genetic Algorithm

### MDVRP
The Multiple Depots Vehicle Routing Problem (MDVRP) describes the vehicle scheduling challenge for a transportation company. The transportation company has
multiple depots from which their vehicles depart and arrive, and has multiple customers being served from the different
depots. The challenge is to make a schedule for each vehicle individually so that the vehicles drive in the most efficient
way optimizing one or several objectives.

The MDVRP is NP-hard, which means that an efficient algorithm for solving the problem to optimality is unavailable.

### Genetic Algorithm
A Genetic Algorithm (GA) is a metaheuristic inspired by natural selection.
Genetic algorithms are commonly used to generate high-quality solutions to optimization and search problems by relying on bio-inspired operators such as selection, crossover and mutation.

##### Pseudocode
```
Generate a initial population
Calculate fitness value of individuals in population
REPEAT
    Selection: find the best individuals
    Crossover: create offsprings based on these individuals
    Mutation: possibility of editing the genes of the offsprings
UNTIL population has converged
```