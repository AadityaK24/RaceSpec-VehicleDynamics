// RaceSpec - Vehicle Dynamics & Performance Analysis
// Combines the individual vehicle models into one performance calculator.

public class PerformanceCalculator
{
    private Vehicle vehicle;
    private Engine engine;
    private Gearbox gearbox;
    private Tyre tyre;
    private Aerodynamics aerodynamics;
    private Brakes brakes;

    // Constructor
    public PerformanceCalculator(Vehicle v,
                                 Engine e,
                                 Gearbox g,
                                 Tyre t,
                                 Aerodynamics a,
                                 Brakes b)
    {
        if (v == null || e == null || g == null
                || t == null || a == null || b == null)
        {
            throw new IllegalArgumentException(
                "PerformanceCalculator components cannot be null."
            );
        }

        vehicle = v;
        engine = e;
        gearbox = g;
        tyre = t;
        aerodynamics = a;
        brakes = b;
    }

    // Drive force at the wheels
    // F = T_wheel / r
    // Result: N
    public double getDriveForce(double rpm, int gear)
    {
        return gearbox.getDriveForce(
            engine,
            rpm,
            gear,
            vehicle.getWheelRadius()
        );
    }

    // Aerodynamic drag
    // Fd = 1/2 * rho * Cd * A * v^2
    public double getAerodynamicDrag(double velocity)
    {
        return aerodynamics.getDrag(velocity);
    }

    // Aerodynamic downforce
    // Fdown = 1/2 * rho * Cl * A * v^2
    public double getDownforce(double velocity)
    {
        return aerodynamics.getDownforce(velocity);
    }

    // Total normal force on the tyres
    // N = mg + Fdown
    public double getNormalForce(double velocity)
    {
        return aerodynamics.getNormalForce(velocity);
    }

    // Rolling resistance
    // Frr = Crr * N
    public double getRollingResistance(double velocity)
    {
        return vehicle.getRollingResistanceCoefficient()
                * getNormalForce(velocity);
    }

    // Maximum tyre grip force
    // Fgrip = mu * N
    public double getMaximumGripForce(double velocity)
    {
        return tyre.getMaximumGripForce(
            getNormalForce(velocity)
        );
    }

    // Drive force limited by available tyre grip
    public double getTractionLimitedDriveForce(double rpm,
                                               int gear,
                                               double velocity)
    {
        double driveForce = getDriveForce(rpm, gear);
        double gripForce = getMaximumGripForce(velocity);

        return Math.min(driveForce, gripForce);
    }

    // Net longitudinal force
    // Fnet = Fdrive - Fdrag - Frolling
    public double getNetForce(double rpm,
                              int gear,
                              double velocity)
    {
        double driveForce =
                getTractionLimitedDriveForce(rpm, gear, velocity);

        double drag =
                getAerodynamicDrag(velocity);

        double rollingResistance =
                getRollingResistance(velocity);

        return driveForce
                - drag
                - rollingResistance;
    }

    // Vehicle acceleration
    // a = Fnet / m
    // Result: m/s^2
    public double getAcceleration(double rpm,
                                  int gear,
                                  double velocity)
    {
        if (vehicle.getMass() <= 0)
        {
            return 0;
        }

        double netForce =
                getNetForce(rpm, gear, velocity);

        return netForce / vehicle.getMass();
    }

    // Maximum braking force limited by tyre grip
    public double getMaximumBrakingForce(double velocity)
    {
        double brakeForce =
                brakes.getMaximumBrakingForce(
                    vehicle.getWheelRadius()
                );

        double tyreGrip =
                getMaximumGripForce(velocity);

        return Math.min(brakeForce, tyreGrip);
    }

    // Maximum braking deceleration
    // a = Fbrake / m
    public double getMaximumBrakingAcceleration(double velocity)
    {
        if (vehicle.getMass() <= 0)
        {
            return 0;
        }

        return getMaximumBrakingForce(velocity)
                / vehicle.getMass();
    }

    // Stopping distance
    // d = v^2 / (2a)
    // Velocity must be in m/s
    public double getStoppingDistance(double velocity)
    {
        double brakingAcceleration =
                getMaximumBrakingAcceleration(velocity);

        if (brakingAcceleration <= 0)
        {
            return 0;
        }

        return (velocity * velocity)
                / (2 * brakingAcceleration);
    }

    // Maximum lateral acceleration
    // alat = mu * N / m
    public double getMaximumLateralAcceleration(double velocity)
    {
        if (vehicle.getMass() <= 0)
        {
            return 0;
        }

        return getMaximumGripForce(velocity)
                / vehicle.getMass();
    }

    // Maximum cornering speed
    // v = sqrt(alat * r)
    // Radius in metres
    // Result: m/s
    public double getMaximumCorneringSpeed(double radius,
                                           double velocity)
    {
        if (radius <= 0)
        {
            return 0;
        }

        double lateralAcceleration =
                getMaximumLateralAcceleration(velocity);

        return Math.sqrt(
            lateralAcceleration * radius
        );
    }

    // Power-to-weight ratio
    // Result: kW per tonne
    public double getPowerToWeight()
    {
        if (vehicle.getMass() <= 0)
        {
            return 0;
        }

        return engine.getMaximumPower()
                / (vehicle.getMass() / 1000.0);
    }

    // Estimate top speed by checking all gears
    // and a range of engine RPM values.
    public double getEstimatedTopSpeed()
    {
        double highestSpeed = 0;
        double rpmStep = 100;

        for (int gear = 1;
             gear <= gearbox.getNumberOfGears();
             gear++)
        {
            for (double rpm = engine.getIdleRPM();
                 rpm <= engine.getMaximumRPM();
                 rpm += rpmStep)
            {
                double speedKmh =
                        gearbox.getVehicleSpeed(
                            rpm,
                            gear,
                            vehicle.getWheelRadius()
                        );

                double speedMs =
                        speedKmh / 3.6;

                double netForce =
                        getNetForce(
                            rpm,
                            gear,
                            speedMs
                        );

                if (netForce >= 0)
                {
                    if (speedKmh > highestSpeed)
                    {
                        highestSpeed = speedKmh;
                    }
                }
            }
        }

        return highestSpeed;
    }

    // Getters
    public Vehicle getVehicle()
    {
        return vehicle;
    }

    public Engine getEngine()
    {
        return engine;
    }

    public Gearbox getGearbox()
    {
        return gearbox;
    }

    public Tyre getTyre()
    {
        return tyre;
    }

    public Aerodynamics getAerodynamics()
    {
        return aerodynamics;
    }

    public Brakes getBrakes()
    {
        return brakes;
    }
}