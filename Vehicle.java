// RaceSpec - Vehicle Dynamics Model
// Stores the physical properties and mass characteristics of a vehicle.

public class Vehicle
{
    private static final double GRAVITY = 9.80665;

    private String name;
    private double mass;

    private double wheelRadius;
    private double frontalArea;

    private double dragCoefficient;
    private double downforceCoefficient;
    private double rollingResistanceCoefficient;

    private double wheelbase;
    private double frontTrack;
    private double rearTrack;

    private double centreOfGravityHeight;
    private double frontWeightDistribution;

    private double yawInertiaFactor;

    public Vehicle(
        String n,
        double m,
        double wr,
        double fa,
        double cd,
        double cl,
        double crr
    )
    {
        this(
            n,
            m,
            wr,
            fa,
            cd,
            cl,
            crr,
            3.60,
            1.60,
            1.60,
            0.30,
            0.45,
            1.10
        );
    }

    public Vehicle(
        String n,
        double m,
        double wr,
        double fa,
        double cd,
        double cl,
        double crr,
        double wb,
        double ft,
        double rt,
        double cgHeight,
        double frontDistribution,
        double inertiaFactor
    )
    {
        if (m <= 0)
        {
            throw new IllegalArgumentException(
                "Vehicle mass must be greater than zero."
            );
        }

        if (wr <= 0)
        {
            throw new IllegalArgumentException(
                "Wheel radius must be greater than zero."
            );
        }

        if (fa <= 0)
        {
            throw new IllegalArgumentException(
                "Frontal area must be greater than zero."
            );
        }

        if (cd < 0 || cl < 0 || crr < 0)
        {
            throw new IllegalArgumentException(
                "Vehicle coefficients cannot be negative."
            );
        }

        if (wb <= 0 || ft <= 0 || rt <= 0)
        {
            throw new IllegalArgumentException(
                "Vehicle geometry must be greater than zero."
            );
        }

        if (cgHeight <= 0)
        {
            throw new IllegalArgumentException(
                "Centre of gravity height must be greater than zero."
            );
        }

        if (frontDistribution <= 0 || frontDistribution >= 1)
        {
            throw new IllegalArgumentException(
                "Front weight distribution must be between 0 and 1."
            );
        }

        if (inertiaFactor <= 0)
        {
            throw new IllegalArgumentException(
                "Yaw inertia factor must be greater than zero."
            );
        }

        name = n;
        mass = m;

        wheelRadius = wr;
        frontalArea = fa;

        dragCoefficient = cd;
        downforceCoefficient = cl;
        rollingResistanceCoefficient = crr;

        wheelbase = wb;
        frontTrack = ft;
        rearTrack = rt;

        centreOfGravityHeight = cgHeight;
        frontWeightDistribution = frontDistribution;

        yawInertiaFactor = inertiaFactor;
    }

    public String getName()
    {
        return name;
    }

    public double getMass()
    {
        return mass;
    }

    public double getWheelRadius()
    {
        return wheelRadius;
    }

    public double getFrontalArea()
    {
        return frontalArea;
    }

    public double getDragCoefficient()
    {
        return dragCoefficient;
    }

    public double getDownforceCoefficient()
    {
        return downforceCoefficient;
    }

    public double getRollingResistanceCoefficient()
    {
        return rollingResistanceCoefficient;
    }

    public double getWheelbase()
    {
        return wheelbase;
    }

    public double getFrontTrack()
    {
        return frontTrack;
    }

    public double getRearTrack()
    {
        return rearTrack;
    }

    public double getCentreOfGravityHeight()
    {
        return centreOfGravityHeight;
    }

    public double getFrontWeightDistribution()
    {
        return frontWeightDistribution;
    }

    public double getRearWeightDistribution()
    {
        return 1.0 - frontWeightDistribution;
    }

    public double getWeight()
    {
        return mass * GRAVITY;
    }

    public double getStaticFrontAxleLoad()
    {
        return getWeight() * frontWeightDistribution;
    }

    public double getStaticRearAxleLoad()
    {
        return getWeight() * getRearWeightDistribution();
    }

    public double getStaticFrontWheelLoad()
    {
        return getStaticFrontAxleLoad() / 2.0;
    }

    public double getStaticRearWheelLoad()
    {
        return getStaticRearAxleLoad() / 2.0;
    }

    // Longitudinal load transfer = mass * acceleration * CG height / wheelbase.
    public double getLongitudinalLoadTransfer(double acceleration)
    {
        return (
            mass
            * acceleration
            * centreOfGravityHeight
        ) / wheelbase;
    }

    // Positive acceleration shifts load toward the rear axle.
    public double getFrontAxleLoad(double acceleration)
    {
        double loadTransfer =
            getLongitudinalLoadTransfer(acceleration);

        return getStaticFrontAxleLoad() - loadTransfer;
    }

    // Positive acceleration shifts load toward the rear axle.
    public double getRearAxleLoad(double acceleration)
    {
        double loadTransfer =
            getLongitudinalLoadTransfer(acceleration);

        return getStaticRearAxleLoad() + loadTransfer;
    }

    public double getFrontWheelLoad(double acceleration)
    {
        return getFrontAxleLoad(acceleration) / 2.0;
    }

    public double getRearWheelLoad(double acceleration)
    {
        return getRearAxleLoad(acceleration) / 2.0;
    }

    // CG position measured rearward from the front axle.
    public double getCentreOfGravityPositionFromFrontAxle()
    {
        return wheelbase * getRearWeightDistribution();
    }

    // CG position measured forward from the rear axle.
    public double getCentreOfGravityPositionFromRearAxle()
    {
        return wheelbase * frontWeightDistribution;
    }

    // Approximate yaw inertia using the vehicle geometry.
    // The factor accounts for mass concentration not captured by a rectangle.
    public double getYawMomentOfInertia()
    {
        double averageTrack =
            (frontTrack + rearTrack) / 2.0;

        double geometricInertia =
            mass
            * (
                (wheelbase * wheelbase)
                + (averageTrack * averageTrack)
            )
            / 12.0;

        return geometricInertia * yawInertiaFactor;
    }

    public double getAverageTrack()
    {
        return (frontTrack + rearTrack) / 2.0;
    }

    public double getSpecificWeight()
    {
        return getWeight() / mass;
    }

    public static double getGravity()
    {
        return GRAVITY;
    }
}