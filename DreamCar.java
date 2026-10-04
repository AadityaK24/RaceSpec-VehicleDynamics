// RaceSpec - Dream Car Configuration V2
// Fictional high-performance vehicle using the RaceSpec V2 physics model.

public class DreamCar
{
    // =====================================================
    // VEHICLE MODEL
    // =====================================================

    private static final double MASS = 760.0;

    private static final double WHEEL_RADIUS = 0.360;

    private static final double FRONTAL_AREA = 1.42;

    private static final double DRAG_COEFFICIENT = 0.90;

    private static final double DOWNFORCE_COEFFICIENT = 3.80;

    private static final double ROLLING_RESISTANCE = 0.010;

    private static final double WHEELBASE = 3.55;

    private static final double FRONT_TRACK = 1.62;

    private static final double REAR_TRACK = 1.60;

    private static final double CENTRE_OF_GRAVITY_HEIGHT = 0.28;

    private static final double FRONT_WEIGHT_DISTRIBUTION = 0.44;

    private static final double YAW_INERTIA_FACTOR = 1.05;

    // =====================================================
    // POWERTRAIN
    // =====================================================

    private static final String ENGINE_TYPE =
        "RaceSpec High-Performance Hybrid V10";

    private static final int CYLINDERS = 10;

    // Fictional RaceSpec concept values.
    private static final double ENGINE_POWER = 820.0;

    private static final double MAX_TORQUE = 820.0;

    private static final double MAX_RPM = 15000.0;

    private static final double IDLE_RPM = 3000.0;

    private static final double ENGINE_MASS = 145.0;

    private static final double PEAK_TORQUE_RPM = 8500.0;

    private static final double PEAK_POWER_RPM = 13500.0;

    private static final double TURBO_SPOOL_RPM = 4800.0;

    private static final double FULL_BOOST_RPM = 8200.0;

    private static final double LOW_RPM_TURBO_MULTIPLIER = 0.75;

    private static final double ENGINE_OUTPUT_FACTOR = 1.00;

    private static final double ERS_MAXIMUM_POWER = 120.0;

    private static final double ERS_START_RPM = 5500.0;

    private static final double ERS_END_RPM = 14500.0;

    // =====================================================
    // GEARBOX
    // =====================================================

    private static final int NUMBER_OF_GEARS = 8;

    // RaceSpec concept ratios.
    private static final double[] GEAR_RATIOS =
    {
        3.15,
        2.40,
        1.88,
        1.55,
        1.30,
        1.10,
        0.94,
        0.81
    };

    private static final double FINAL_DRIVE_RATIO = 3.25;

    private static final double GEARBOX_EFFICIENCY = 0.97;

    private static final double SHIFT_RPM = 14000.0;

    // =====================================================
    // TYRES
    // =====================================================

    private static final String TYRE_TYPE =
        "RaceSpec Advanced Slick";

    private static final double TYRE_GRIP = 1.90;

    private static final double TYRE_MASS = 9.5;

    private static final double TYRE_WIDTH = 0.405;

    private static final double TYRE_DIAMETER = 0.720;

    private static final double NOMINAL_PRESSURE = 1.38;

    private static final double OPTIMUM_TEMPERATURE = 100.0;

    private static final double LOAD_SENSITIVITY = 0.09;

    private static final double TEMPERATURE_SENSITIVITY = 0.012;

    private static final double PRESSURE_SENSITIVITY = 0.04;

    private static final double LONGITUDINAL_GRIP = 1.90;

    private static final double LATERAL_GRIP = 2.00;

    private static final double PEAK_SLIP_RATIO = 0.095;

    private static final double PEAK_SLIP_ANGLE = 0.095;

    private static final double TYRE_ROLLING_RESISTANCE = 0.009;

    private static final double STARTING_TEMPERATURE = 85.0;

    // =====================================================
    // BRAKES
    // =====================================================

    private static final double BRAKE_TORQUE = 13500.0;

    private static final double BRAKING_EFFICIENCY = 0.94;

    private static final double FRONT_BRAKE_BIAS = 0.58;

    // =====================================================
    // VEHICLE CREATION
    // =====================================================

    public static Vehicle createVehicle()
    {
        return new Vehicle(
            "RaceSpec Dream Car",
            MASS,
            WHEEL_RADIUS,
            FRONTAL_AREA,
            DRAG_COEFFICIENT,
            DOWNFORCE_COEFFICIENT,
            ROLLING_RESISTANCE,
            WHEELBASE,
            FRONT_TRACK,
            REAR_TRACK,
            CENTRE_OF_GRAVITY_HEIGHT,
            FRONT_WEIGHT_DISTRIBUTION,
            YAW_INERTIA_FACTOR
        );
    }

    // =====================================================
    // ENGINE CREATION
    // =====================================================

    public static Engine createEngine()
    {
        return new Engine(
            ENGINE_TYPE,
            CYLINDERS,
            ENGINE_POWER,
            MAX_TORQUE,
            MAX_RPM,
            IDLE_RPM,
            ENGINE_MASS,
            PEAK_TORQUE_RPM,
            PEAK_POWER_RPM,
            TURBO_SPOOL_RPM,
            FULL_BOOST_RPM,
            LOW_RPM_TURBO_MULTIPLIER,
            ENGINE_OUTPUT_FACTOR,
            ERS_MAXIMUM_POWER,
            ERS_START_RPM,
            ERS_END_RPM
        );
    }

    // =====================================================
    // GEARBOX CREATION
    // =====================================================

    public static Gearbox createGearbox()
    {
        return new Gearbox(
            NUMBER_OF_GEARS,
            GEAR_RATIOS,
            FINAL_DRIVE_RATIO,
            GEARBOX_EFFICIENCY,
            SHIFT_RPM
        );
    }

    // =====================================================
    // TYRE CREATION
    // =====================================================

    public static Tyre createTyre()
    {
        return new Tyre(
            TYRE_TYPE,
            TYRE_GRIP,
            TYRE_MASS,
            TYRE_WIDTH,
            TYRE_DIAMETER,
            NOMINAL_PRESSURE,
            OPTIMUM_TEMPERATURE,
            LOAD_SENSITIVITY,
            TEMPERATURE_SENSITIVITY,
            PRESSURE_SENSITIVITY,
            LONGITUDINAL_GRIP,
            LATERAL_GRIP,
            PEAK_SLIP_RATIO,
            PEAK_SLIP_ANGLE,
            TYRE_ROLLING_RESISTANCE,
            STARTING_TEMPERATURE
        );
    }

    // =====================================================
    // AERODYNAMICS
    // =====================================================

    public static Aerodynamics createAerodynamics(
        Vehicle vehicle
    )
    {
        Aerodynamics aero =
            new Aerodynamics(vehicle);

        aero.setFrontAeroBalance(0.44);

        return aero;
    }

    // =====================================================
    // BRAKES
    // =====================================================

    public static Brakes createBrakes()
    {
        return new Brakes(
            BRAKE_TORQUE,
            BRAKING_EFFICIENCY,
            FRONT_BRAKE_BIAS
        );
    }

    // =====================================================
    // COMPLETE PERFORMANCE MODEL
    // =====================================================

    public static PerformanceCalculator
        getPerformanceCalculator()
    {
        Vehicle vehicle =
            createVehicle();

        Engine engine =
            createEngine();

        Gearbox gearbox =
            createGearbox();

        Tyre tyre =
            createTyre();

        Aerodynamics aerodynamics =
            createAerodynamics(
                vehicle
            );

        Brakes brakes =
            createBrakes();

        return new PerformanceCalculator(
            vehicle,
            engine,
            gearbox,
            tyre,
            aerodynamics,
            brakes
        );
    }

    // Kept for compatibility with older RaceSpec code.
    public static PerformanceCalculator
        createPerformanceCalculator()
    {
        return getPerformanceCalculator();
    }

    // =====================================================
    // GETTERS
    // =====================================================

    public static double getMass()
    {
        return MASS;
    }

    public static double getEnginePower()
    {
        return ENGINE_POWER;
    }

    public static double getMaximumTorque()
    {
        return MAX_TORQUE;
    }

    public static double getMaximumRPM()
    {
        return MAX_RPM;
    }

    public static double getERSMaximumPower()
    {
        return ERS_MAXIMUM_POWER;
    }

    public static double getWheelbase()
    {
        return WHEELBASE;
    }

    public static double getFrontWeightDistribution()
    {
        return FRONT_WEIGHT_DISTRIBUTION;
    }

    public static double getRearWeightDistribution()
    {
        return 1.0
            - FRONT_WEIGHT_DISTRIBUTION;
    }

    public static double getFrontalArea()
    {
        return FRONTAL_AREA;
    }

    public static double getDragCoefficient()
    {
        return DRAG_COEFFICIENT;
    }

    public static double getDownforceCoefficient()
    {
        return DOWNFORCE_COEFFICIENT;
    }

    public static double getTyreGrip()
    {
        return TYRE_GRIP;
    }

    public static double getBrakeTorque()
    {
        return BRAKE_TORQUE;
    }
}
