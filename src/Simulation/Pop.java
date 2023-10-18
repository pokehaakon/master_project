package Simulation;

import java.util.BitSet;

public class Pop implements IPop {
    private int infectedCounter;
    private boolean prevStep;
    private boolean nextStep;
    private boolean isImmune;
    public final int id;
    private boolean vaccinated;
    private boolean hasBeenInfected;

    public Pop(int n) {
        id = n;
        prevStep = false;
        nextStep = false;
        isImmune = false;
        hasBeenInfected = false;
        vaccinated = false;
    }

    public void infect(int time) {
        infectedCounter = time;
        nextStep = true;
        hasBeenInfected = true;
    }

    public boolean isInfected() {
        return prevStep;
    }

    public boolean hasBeenInfected() {return this.hasBeenInfected;}

    public void step() {

        infectedCounter--;
        if (infectedCounter <= 0) {
            if (prevStep) {
                isImmune = true;
            }
            prevStep = nextStep;
            nextStep = false;
        } else {
            prevStep = nextStep;
            nextStep = true;
        }

/*
        isImmune = prevStep && infectedCounter <= 0;
        prevStep = nextStep;
        nextStep = !(infectedCounter <= 0);
*/

    }

    public boolean isImmune() {return this.isImmune;}
    public void immunize() {this.isImmune = true; vaccinated = true;}

    public void setUpInfect(int infectionTime) {
        prevStep = true;
        nextStep = true;
        this.infect(infectionTime);
    }

    public boolean isVaccinated() {
        return vaccinated;
    }

    public int getId() {
        return id;
    }

}
