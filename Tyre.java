public class Tyre
{
    private String compound;
    private double gripCoefficient;
    private double tyreMass;

    public Tyre(String c, double grip, double mass)
    {
        compound = c;
        gripCoefficient = grip;
        tyreMass = mass;
    }

    public String getCompound()
    {
        return compound;
    }

    public double getGripCoefficient()
    {
        return gripCoefficient;
    }

    public double getTyreMass()
    {
        return tyreMass;
    }

    public double getMaximumGripForce(double normalForce)
    {
        if (normalForce <= 0)
        {
            return 0;
        }

        return gripCoefficient * normalForce;
    }

    public double getMaximumAcceleration(double normalForce,
                                         double vehicleMass)
    {
        if (vehicleMass <= 0)
        {
            return 0;
        }

        double gripForce = getMaximumGripForce(normalForce);

        return gripForce / vehicleMass;
    }
}