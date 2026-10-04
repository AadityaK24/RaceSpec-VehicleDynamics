// RaceSpec - Aerodynamics Model V2
// Models drag, downforce, air density, aero balance and DRS.

public class Aerodynamics
{
    private static final double SEA_LEVEL_AIR_DENSITY = 1.225;
    private static final double SEA_LEVEL_TEMPERATURE = 288.15;
    private static final double GAS_CONSTANT_AIR = 287.05;
    private static final double TEMPERATURE_LAPSE_RATE = 0.0065;

    private Vehicle vehicle;

    private double frontAeroBalance;
    private double rearAeroBalance;

    private double inducedDragFactor;
    private double groundEffectFactor;

    private double drsDragReduction;
    private double drsDownforceReduction;

    private boolean drsActive;

    public Aerodynamics(Vehicle v)
    {
        if (v == null)
        {
            throw new IllegalArgumentException(
                "Vehicle cannot be null."
            );
        }

        vehicle = v;

        frontAeroBalance = 0.45;
        rearAeroBalance = 0.55;

        inducedDragFactor = 0.025;
        groundEffectFactor = 0.15;

        drsDragReduction = 0.30;
        drsDownforceReduction = 0.12;

        drsActive = false;
    }

    // Dynamic pressure q = 1/2 * rho * v^2.
    public double getDynamicPressure(double velocity)
    {
        if (velocity < 0)
        {
            return 0;
        }

        return 0.5
            * SEA_LEVEL_AIR_DENSITY
            * velocity
            * velocity;
    }

    // Approximate ISA air density at altitude.
    public double getAirDensityAtAltitude(double altitude)
    {
        if (altitude < 0)
        {
            altitude = 0;
        }

        double temperature =
            SEA_LEVEL_TEMPERATURE
            - TEMPERATURE_LAPSE_RATE * altitude;

        if (temperature <= 0)
        {
            temperature = 1;
        }

        return SEA_LEVEL_AIR_DENSITY
            * Math.pow(
                temperature / SEA_LEVEL_TEMPERATURE,
                4.256
            );
    }

    // Dynamic pressure at a specified altitude.
    public double getDynamicPressure(
        double velocity,
        double altitude
    )
    {
        if (velocity < 0)
        {
            return 0;
        }

        double density =
            getAirDensityAtAltitude(altitude);

        return 0.5
            * density
            * velocity
            * velocity;
    }

    // Calculates baseline aerodynamic drag.
    public double getDrag(double velocity)
    {
        return getDrag(velocity, 0);
    }

    // Calculates drag including altitude and induced drag.
    public double getDrag(
        double velocity,
        double altitude
    )
    {
        if (velocity < 0)
        {
            return 0;
        }

        double density =
            getAirDensityAtAltitude(altitude);

        double dynamicPressure =
            0.5
            * density
            * velocity
            * velocity;

        double frontalArea =
            vehicle.getFrontalArea();

        double cd =
            vehicle.getDragCoefficient();

        double baseDrag =
            dynamicPressure
            * cd
            * frontalArea;

        double liftCoefficient =
            getEffectiveDownforceCoefficient(
                velocity
            );

        double inducedDrag =
            inducedDragFactor
            * liftCoefficient
            * liftCoefficient
            * dynamicPressure
            * frontalArea;

        double totalDrag =
            baseDrag + inducedDrag;

        if (drsActive)
        {
            totalDrag *=
                1.0 - drsDragReduction;
        }

        return Math.max(0, totalDrag);
    }

    // Calculates aerodynamic downforce.
    public double getDownforce(double velocity)
    {
        return getDownforce(velocity, 0);
    }

    // Calculates downforce including altitude and ground effect.
    public double getDownforce(
        double velocity,
        double altitude
    )
    {
        if (velocity < 0)
        {
            return 0;
        }

        double density =
            getAirDensityAtAltitude(altitude);

        double dynamicPressure =
            0.5
            * density
            * velocity
            * velocity;

        double cl =
            getEffectiveDownforceCoefficient(
                velocity
            );

        double downforce =
            dynamicPressure
            * cl
            * vehicle.getFrontalArea();

        if (drsActive)
        {
            downforce *=
                1.0 - drsDownforceReduction;
        }

        return Math.max(0, downforce);
    }

    // Gives a speed-dependent aero multiplier representing underbody
    // efficiency increasing with vehicle speed.
    private double getGroundEffectMultiplier(
        double velocity
    )
    {
        double normalizedSpeed =
            velocity / 100.0;

        double multiplier =
            1.0
            + groundEffectFactor
            * (
                normalizedSpeed
                / (1.0 + normalizedSpeed)
            );

        return Math.min(1.20, multiplier);
    }

    // Effective downforce coefficient after speed-dependent aero effects.
    public double getEffectiveDownforceCoefficient(
        double velocity
    )
    {
        double multiplier =
            getGroundEffectMultiplier(velocity);

        return vehicle.getDownforceCoefficient()
            * multiplier;
    }

    // Total normal tyre load = static weight + aerodynamic load.
    public double getNormalForce(double velocity)
    {
        return vehicle.getWeight()
            + getDownforce(velocity);
    }

    // Normal force at a specified altitude.
    public double getNormalForce(
        double velocity,
        double altitude
    )
    {
        return vehicle.getWeight()
            + getDownforce(velocity, altitude);
    }

    // Front aerodynamic load.
    public double getFrontDownforce(double velocity)
    {
        return getDownforce(velocity)
            * frontAeroBalance;
    }

    // Rear aerodynamic load.
    public double getRearDownforce(double velocity)
    {
        return getDownforce(velocity)
            * rearAeroBalance;
    }

    // Front total vertical tyre load.
    public double getFrontNormalForce(double velocity)
    {
        return vehicle.getStaticFrontAxleLoad()
            + getFrontDownforce(velocity);
    }

    // Rear total vertical tyre load.
    public double getRearNormalForce(double velocity)
    {
        return vehicle.getStaticRearAxleLoad()
            + getRearDownforce(velocity);
    }

    // Aero efficiency = downforce generated per unit drag.
    public double getAeroEfficiency(double velocity)
    {
        double drag = getDrag(velocity);

        if (drag <= 0)
        {
            return 0;
        }

        return getDownforce(velocity)
            / drag;
    }

    // Power required to overcome aerodynamic drag.
    public double getDragPower(double velocity)
    {
        return getDrag(velocity)
            * Math.max(velocity, 0);
    }

    // Enables or disables DRS.
    public void setDRS(boolean active)
    {
        drsActive = active;
    }

    public boolean isDRSActive()
    {
        return drsActive;
    }

    public void setFrontAeroBalance(double balance)
    {
        if (balance <= 0 || balance >= 1)
        {
            throw new IllegalArgumentException(
                "Front aero balance must be between 0 and 1."
            );
        }

        frontAeroBalance = balance;
        rearAeroBalance = 1.0 - balance;
    }

    public double getFrontAeroBalance()
    {
        return frontAeroBalance;
    }

    public double getRearAeroBalance()
    {
        return rearAeroBalance;
    }

    public double getInducedDragFactor()
    {
        return inducedDragFactor;
    }

    public double getGroundEffectFactor()
    {
        return groundEffectFactor;
    }

    public double getDRSDragReduction()
    {
        return drsDragReduction;
    }

    public double getDRSDownforceReduction()
    {
        return drsDownforceReduction;
    }

    public double getAirDensity()
    {
        return SEA_LEVEL_AIR_DENSITY;
    }

    public Vehicle getVehicle()
    {
        return vehicle;
    }
}