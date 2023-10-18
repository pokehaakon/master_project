package Plans;

import Simulation.BitSetSimulation;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

public abstract class PlanGeneratorBit {
    public static  Plan generate(String planName, BitSetSimulation sim, Map<String, List<List<Integer>>> centralities, Map<String, List<Double>> simpleCentralities, Lock centralityLock) {
        return switch (planName) {
            case "1-Ranked-Neighbourhood-neg-and-ign-vac" -> new RankedKNeighbourhoodIgnoreVaccinatedBitSetUsingBitSim(sim, 1, centralityLock);
            case "2-Ranked-Neighbourhood-neg-and-ign-vac" -> new RankedKNeighbourhoodIgnoreVaccinatedBitSetUsingBitSim(sim, 2, centralityLock);
            case "3-Ranked-Neighbourhood-neg-and-ign-vac" -> new RankedKNeighbourhoodIgnoreVaccinatedBitSetUsingBitSim(sim, 3, centralityLock);
            case "4-Ranked-Neighbourhood-neg-and-ign-vac" -> new RankedKNeighbourhoodIgnoreVaccinatedBitSetUsingBitSim(sim, 4, centralityLock);

            case "1-Ranked-Neighbourhood-neg-and-ign-imm" -> new RankedKNeighbourhoodIgnoreImmuneBitSetUsingBitSim(sim, 1, centralityLock);
            case "2-Ranked-Neighbourhood-neg-and-ign-imm" -> new RankedKNeighbourhoodIgnoreImmuneBitSetUsingBitSim(sim, 2, centralityLock);
            case "3-Ranked-Neighbourhood-neg-and-ign-imm" -> new RankedKNeighbourhoodIgnoreImmuneBitSetUsingBitSim(sim, 3, centralityLock);
            case "4-Ranked-Neighbourhood-neg-and-ign-imm" -> new RankedKNeighbourhoodIgnoreImmuneBitSetUsingBitSim(sim, 4, centralityLock);


            default -> throw new RuntimeException("Cannot construct plan " + planName);
        };
    }
}
