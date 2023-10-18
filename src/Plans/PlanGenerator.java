package Plans;

import Simulation.Simulation;
import Tools.KClosenessConstructor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

public abstract class PlanGenerator {
    public static  Plan generate(String planName, Simulation sim, Map<String, List<List<Integer>>> centralities, Map<String, List<Double>> simpleCentralities, Lock centralityLock) {
        return switch (planName) {
            case "NoPlan" -> new NoPlan(sim, centralityLock);

            case "RemoveEdgesPlan" -> new RemoveEdgesPlan(sim, centralityLock);

            case "RandomPops" -> new RandomPops(sim, centralityLock);

            case "1-Neighbourhood" -> new KNeighbourhood(sim, 1, centralities, centralityLock);
            case "2-Neighbourhood" -> new KNeighbourhood(sim, 2, centralities, centralityLock);
            case "3-Neighbourhood" -> new KNeighbourhood(sim, 3, centralities, centralityLock);
            case "4-Neighbourhood" -> new KNeighbourhood(sim, 4, centralities, centralityLock);

            case "1-Ranked-Neighbourhood" -> new RankedKNeigbourhood(sim, 1, centralities, centralityLock);
            case "2-Ranked-Neighbourhood" -> new RankedKNeigbourhood(sim, 2, centralities, centralityLock);
            case "3-Ranked-Neighbourhood" -> new RankedKNeigbourhood(sim, 3, centralities, centralityLock);
            case "4-Ranked-Neighbourhood" -> new RankedKNeigbourhood(sim, 4, centralities, centralityLock);

            case "1-Ranked-Neighbourhood-neg-vac" -> new RankedKNeigbourhoodNegativeVaccinated(sim, 1, centralities, centralityLock);
            case "2-Ranked-Neighbourhood-neg-vac" -> new RankedKNeigbourhoodNegativeVaccinated(sim, 2, centralities, centralityLock);
            case "3-Ranked-Neighbourhood-neg-vac" -> new RankedKNeigbourhoodNegativeVaccinated(sim, 3, centralities, centralityLock);
            case "4-Ranked-Neighbourhood-neg-vac" -> new RankedKNeigbourhoodNegativeVaccinated(sim, 4, centralities, centralityLock);

            case "1-Ranked-Neighbourhood-neg-imm" -> new RankedKNeigbourhoodNegativeImmune(sim, 1, centralities, centralityLock);
            case "2-Ranked-Neighbourhood-neg-imm" -> new RankedKNeigbourhoodNegativeImmune(sim, 2, centralities, centralityLock);
            case "3-Ranked-Neighbourhood-neg-imm" -> new RankedKNeigbourhoodNegativeImmune(sim, 3, centralities, centralityLock);
            case "4-Ranked-Neighbourhood-neg-imm" -> new RankedKNeigbourhoodNegativeImmune(sim, 4, centralities, centralityLock);

            case "1-Ranked-Neighbourhood-neg-and-ign-vac" -> new RankedKNeighbourhoodIgnoreSomethingBitSet(sim, 1, centralityLock, (x) -> sim.population[x].isVaccinated());
            case "2-Ranked-Neighbourhood-neg-and-ign-vac" -> new RankedKNeighbourhoodIgnoreSomethingBitSet(sim, 2, centralityLock, (x) -> sim.population[x].isVaccinated());
            case "3-Ranked-Neighbourhood-neg-and-ign-vac" -> new RankedKNeighbourhoodIgnoreSomethingBitSet(sim, 3, centralityLock, (x) -> sim.population[x].isVaccinated());
            case "4-Ranked-Neighbourhood-neg-and-ign-vac" -> new RankedKNeighbourhoodIgnoreSomethingBitSet(sim, 4, centralityLock, (x) -> sim.population[x].isVaccinated());

            case "1-Ranked-Neighbourhood-neg-and-ign-imm" -> new RankedKNeighbourhoodIgnoreSomethingBitSet(sim, 1, centralityLock, (x) -> sim.population[x].isImmune());
            case "2-Ranked-Neighbourhood-neg-and-ign-imm" -> new RankedKNeighbourhoodIgnoreSomethingBitSet(sim, 2, centralityLock, (x) -> sim.population[x].isImmune());
            case "3-Ranked-Neighbourhood-neg-and-ign-imm" -> new RankedKNeighbourhoodIgnoreSomethingBitSet(sim, 3, centralityLock, (x) -> sim.population[x].isImmune());
            case "4-Ranked-Neighbourhood-neg-and-ign-imm" -> new RankedKNeighbourhoodIgnoreSomethingBitSet(sim, 4, centralityLock, (x) -> sim.population[x].isImmune());

            case "closeness" -> new Closeness(sim,  simpleCentralities, centralityLock);
            case "1-closeness" -> new Closeness(sim, 1,  simpleCentralities, centralityLock);
            case "2-closeness" -> new Closeness(sim, 2, simpleCentralities, centralityLock);
            case "3-closeness" -> new Closeness(sim, 3, simpleCentralities, centralityLock);
            case "4-closeness" -> new Closeness(sim, 4, simpleCentralities, centralityLock);

            case "betweenness" -> new Betweenness(sim, simpleCentralities, centralityLock);
            case "1-betweenness" -> new Betweenness(sim, 1, simpleCentralities, centralityLock);
            case "2-betweenness" -> new Betweenness(sim, 2, simpleCentralities, centralityLock);
            case "3-betweenness" -> new Betweenness(sim, 3, simpleCentralities, centralityLock);
            case "4-betweenness" -> new Betweenness(sim, 4, simpleCentralities, centralityLock);


            default -> throw new RuntimeException("Cannot construct plan " + planName);
        };
    }
}
