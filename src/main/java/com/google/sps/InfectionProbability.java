package com.google.sps;

public class InfectionProbability {

    public double   NO_PROTECTION,
                    WITH_MASK,
                    WITH_VACCINE,
                    WITH_MASK_AND_VACCINE;

    public InfectionProbability(double noProtection, double withMask, double withVaccine) {
        this.NO_PROTECTION = noProtection;
        this.WITH_MASK = withMask;
        this.WITH_VACCINE = withVaccine;
        this.WITH_MASK_AND_VACCINE = withMask + withVaccine - withMask * withVaccine;
    }

    public String toString() {
        return "INFECTION PROBABILITIES\nNo protection: " + NO_PROTECTION + "\nWith Mask: " +  
                WITH_MASK + "\nWith Vaccine: " + WITH_VACCINE + "\nWith Mask and Vaccine: " + WITH_MASK_AND_VACCINE;
    }
}