public class Gearbox
{
    private int numberOfGears;
    private double[] gearRatios;
    private double finalDriveRatio;
    private double efficiency;
    private double shiftRPM;

    public Gearbox(int gears, double[] ratios, double finalDrive,
                   double eff, double shift)
    {
        numberOfGears = gears;
        gearRatios = ratios;
        finalDriveRatio = finalDrive;
        efficiency = eff;
        shiftRPM = shift;
    }

    public int getNumberOfGears()
    {
        return numberOfGears;
    }

    public double getGearRatio(int gear)
    {
        if (gear < 1 || gear > numberOfGears)
        {
            return 0;
        }

        return gearRatios[gear - 1];
    }

    public double getFinalDriveRatio()
    {
        return finalDriveRatio;
    }

    public double getEfficiency()
    {
        return efficiency;
    }

    public double getShiftRPM()
    {
        return shiftRPM;
    }

    public double getWheelTorque(Engine engine, double rpm, int gear)
    {
        double engineTorque = engine.getTorqueAtRPM(rpm);
        double gearRatio = getGearRatio(gear);

        return engineTorque
                * gearRatio
                * finalDriveRatio
                * efficiency;
    }

    public double getDriveForce(Engine engine, double rpm,
                                int gear, double wheelRadius)
    {
        double wheelTorque = getWheelTorque(engine, rpm, gear);

        if (wheelRadius <= 0)
        {
            return 0;
        }

        return wheelTorque / wheelRadius;
    }

    public double getVehicleSpeed(double rpm, int gear,
                                  double wheelRadius)
    {
        double gearRatio = getGearRatio(gear);

        if (gearRatio <= 0 || wheelRadius <= 0)
        {
            return 0;
        }

        double wheelRPM =
                rpm / (gearRatio * finalDriveRatio);

        double wheelCircumference =
                2 * Math.PI * wheelRadius;

        return (wheelRPM * wheelCircumference * 60)
                / 1000;
    }
}