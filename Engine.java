public class Engine
{
    private String type;
    private int cylinders;

    private double maximumPower;
    private double maximumTorque;
    private double maximumRPM;
    private double idleRPM;
    private double engineMass;

    public Engine(String t, int c, double power, double torque,
                  double maxRPM, double idle, double mass)
    {
        type = t;
        cylinders = c;
        maximumPower = power;
        maximumTorque = torque;
        maximumRPM = maxRPM;
        idleRPM = idle;
        engineMass = mass;
    }

    public String getType()
    {
        return type;
    }

    public int getCylinders()
    {
        return cylinders;
    }

    public double getMaximumPower()
    {
        return maximumPower;
    }

    public double getMaximumTorque()
    {
        return maximumTorque;
    }

    public double getMaximumRPM()
    {
        return maximumRPM;
    }

    public double getIdleRPM()
    {
        return idleRPM;
    }

    public double getEngineMass()
    {
        return engineMass;
    }

    public double getPowerAtRPM(double rpm)
    {
        if (rpm <= 0)
        {
            return 0;
        }

        if (rpm >= maximumRPM)
        {
            return maximumPower;
        }

        double powerFactor = rpm / maximumRPM;

        return maximumPower * powerFactor;
    }

    public double getTorqueAtRPM(double rpm)
    {
        if (rpm <= 0)
        {
            return 0;
        }

        double power = getPowerAtRPM(rpm);

        return (power * 9550) / rpm;
    }
}