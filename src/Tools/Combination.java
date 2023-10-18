package Tools;

public record Combination(
        int graphIndex,
        int subSimNum,
        int numberAtInitialization,
        String planName,
        int totalSimTime,
        float numberToVaccinateEachStep,
        int timeBetweenEachCycle,
        int infectionTime,
        float infectionChance,
        int totalSimNumber) {
}
