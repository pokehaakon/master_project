package Simulation;

public interface IPop {
    public void infect(int time);
    public boolean isInfected();
    public boolean hasBeenInfected();
    public void step();
    public boolean isImmune();
    public void immunize();
    public void setUpInfect(int infectionTime);
    public boolean isVaccinated();
    public int getId();
}
