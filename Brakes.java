public class Brakes
{
    private double maximumBrakeTorque;
    private double brakingEfficiency;
    private double frontBrakeBias;

    public Brakes(double torque, double efficiency, double bias)
    {
        maximumBrakeTorque = torque;
        brakingEfficiency = efficiency;
        frontBrakeBias = bias;
    }

    public double getMaximumBrakeTorque()
    {
        return maximumBrakeTorque;
    }

    public double getBrakingEfficiency()
    {
        return brakingEfficiency;
    }

    public double getFrontBrakeBias()
    {
        return frontBrakeBias;
    }

    public double getMaximumBrakingForce(double wheelRadius)
    {
        if (wheelRadius <= 0)
        {
            return 0;
        }

        return (maximumBrakeTorque * brakingEfficiency)
                / wheelRadius;
    }

    public double getFrontBrakingForce(double wheelRadius)
    {
        return getMaximumBrakingForce(wheelRadius)
                * frontBrakeBias;
    }

    public double getRearBrakingForce(double wheelRadius)
    {
        return getMaximumBrakingForce(wheelRadius)
                * (1 - frontBrakeBias);
    }

    public double getBrakingAcceleration(double wheelRadius,
                                         double vehicleMass)
    {
        if (wheelRadius <= 0 || vehicleMass <= 0)
        {
            return 0;
        }

        return getMaximumBrakingForce(wheelRadius)
                / vehicleMass;
    }

    public double getStoppingDistance(double wheelRadius,
                                      double vehicleMass,
                                      double velocity)
    {
        if (wheelRadius <= 0 || vehicleMass <= 0 || velocity <= 0)
        {
            return 0;
        }

        double acceleration =
                getBrakingAcceleration(wheelRadius, vehicleMass);

        if (acceleration <= 0)
        {
            return 0;
        }

        return (velocity * velocity)
                / (2 * acceleration);
    }
}