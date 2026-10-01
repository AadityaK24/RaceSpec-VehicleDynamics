public class Engine
{
    private String type;
    private int cylinders;

    private double maximumPower;
    private double maximumTorque;
    private double maximumRPM;
    private double idleRPM;
    private double engineMass;

    private double peakTorqueRPM;
    private double peakPowerRPM;

    public Engine(String t, int c, double power, double torque,
                  double maxRPM, double idle, double mass,
                  double torqueRPM, double powerRPM)
    {
        type = t;
        cylinders = c;

        maximumPower = power;
        maximumTorque = torque;
        maximumRPM = maxRPM;
        idleRPM = idle;
        engineMass = mass;

        peakTorqueRPM = torqueRPM;
        peakPowerRPM = powerRPM;
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

    public double getPeakTorqueRPM()
    {
        return peakTorqueRPM;
    }

    public double getPeakPowerRPM()
    {
        return peakPowerRPM;
    }

    public double getTorqueAtRPM(double rpm)
    {
        if (rpm < idleRPM || rpm > maximumRPM)
        {
            return 0;
        }

        double torqueAtPeakPower =
                (maximumPower * 9550) / peakPowerRPM;

        if (rpm <= peakTorqueRPM)
        {
            double progress =
                    (rpm - idleRPM) /
                    (peakTorqueRPM - idleRPM);

            double startingTorque =
                    maximumTorque * 0.65;

            return startingTorque +
                    progress *
                    (maximumTorque - startingTorque);
        }

        if (rpm <= peakPowerRPM)
        {
            double progress =
                    (rpm - peakTorqueRPM) /
                    (peakPowerRPM - peakTorqueRPM);

            return maximumTorque +
                    progress *
                    (torqueAtPeakPower - maximumTorque);
        }

        double progress =
                (rpm - peakPowerRPM) /
                (maximumRPM - peakPowerRPM);

        return torqueAtPeakPower * (1 - progress);
    }

    public double getPowerAtRPM(double rpm)
    {
        double torque = getTorqueAtRPM(rpm);

        return (torque * rpm) / 9550;
    }
}