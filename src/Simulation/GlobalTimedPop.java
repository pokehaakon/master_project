package Simulation;

public class GlobalTimedPop implements IPop {

    private int id;
    private int[] globalTimer;
    private boolean vaccinated = false;
    private int endOfInfection = 1000;

    public GlobalTimedPop(int n, int[] globalTimer) {
        id = n;
        this.globalTimer = globalTimer;
    }
    @Override
    public void infect(int time) {
        endOfInfection = globalTimer[0] + time;
    }

    @Override
    public boolean isInfected() {
        return endOfInfection < globalTimer[0];
    }

    @Override
    public boolean hasBeenInfected() {
        return endOfInfection < 1000;
    }

    @Override
    public void step() {}

    @Override
    public boolean isImmune() {
        return vaccinated || endOfInfection < globalTimer[0];
    }

    @Override
    public void immunize() {
        vaccinated = true;
    }

    @Override
    public void setUpInfect(int infectionTime) {
        endOfInfection = infectionTime + globalTimer[0];
    }

    @Override
    public boolean isVaccinated() {
        return vaccinated;
    }

    @Override
    public int getId() {
        return id;
    }
}
