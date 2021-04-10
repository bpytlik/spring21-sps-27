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
}