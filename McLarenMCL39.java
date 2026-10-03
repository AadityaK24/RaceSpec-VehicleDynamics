// RaceSpec - McLaren MCL39 Reference Configuration
// Stores the fixed McLaren reference configuration used for comparison.

public class McLarenMCL39
{
    // -------------------------------------------------
    // MCL39 VEHICLE DATA
    // -------------------------------------------------

    // Published McLaren vehicle weight including driver, excluding fuel
    private static final double MASS = 800.0; // kg

    // Simplified RaceSpec modelling assumptions
    private static final double WHEEL_RADIUS = 0.36; // m
    private static final double FRONTAL_AREA = 1.50; // m^2
    private static final double DRAG_COEFFICIENT = 1.00;
    private static final double DOWNFORCE_COEFFICIENT = 3.50;
    private static final double ROLLING_RESISTANCE = 0.012;

    // -------------------------------------------------
    // MCL39 ENGINE DATA
    // -------------------------------------------------

    // Published McLaren power unit information
    private static final String ENGINE_TYPE = "Mercedes-AMG M16 E Performance";
    private static final int CYLINDERS = 6;
    private static final double MAX_RPM = 15000.0;
    private static final double ENGINE_MASS = 151.0; // minimum PU regulation weight

    // Simplified RaceSpec modelling assumptions
    private static final double MAXIMUM_POWER = 1000.0; // kW
    private static final double MAXIMUM_TORQUE = 700.0; // Nm
    private static final double IDLE_RPM = 3000.0;
    private static final double PEAK_TORQUE_RPM = 8500.0;
    private static final double PEAK_POWER_RPM = 13000.0;

    // -------------------------------------------------
    // MCL39 GEARBOX DATA
    // -------------------------------------------------

    // McLaren specifies eight forward gears
    private static final int NUMBER_OF_GEARS = 8;

    // RaceSpec modelling assumptions for simplified gearing
    private static final double[] GEAR_RATIOS =
    {
        3.20, 2.40, 1.80, 1.45,
        1.20, 1.00, 0.82, 0.68
    };

    private static final double FINAL_DRIVE_RATIO = 3.50;
    private static final double GEARBOX_EFFICIENCY = 0.95;
    private static final double SHIFT_RPM = 12000.0;

    // -------------------------------------------------
    // TYRE DATA
    // -------------------------------------------------

    // Simplified RaceSpec reference tyre model
    private static final String TYRE_COMPOUND = "F1 Slick";
    private static final double GRIP_COEFFICIENT = 1.80;
    private static final double TYRE_MASS = 10.0; // kg per tyre

    // -------------------------------------------------
    // BRAKE DATA
    // -------------------------------------------------

    // Simplified RaceSpec brake model
    private static final double BRAKE_TORQUE = 12000.0; // Nm
    private static final double BRAKING_EFFICIENCY = 0.90;
    private static final double FRONT_BRAKE_BIAS = 0.60;

    // -------------------------------------------------
    // OBJECT CREATION
    // -------------------------------------------------

    public static Vehicle createVehicle()
    {
        return new Vehicle(
            "McLaren MCL39",
            MASS,
            WHEEL_RADIUS,
            FRONTAL_AREA,
            DRAG_COEFFICIENT,
            DOWNFORCE_COEFFICIENT,
            ROLLING_RESISTANCE
        );
    }

    public static Engine createEngine()
    {
        return new Engine(
            ENGINE_TYPE,
            CYLINDERS,
            MAXIMUM_POWER,
            MAXIMUM_TORQUE,
            MAX_RPM,
            IDLE_RPM,
            ENGINE_MASS,
            PEAK_TORQUE_RPM,
            PEAK_POWER_RPM
        );
    }

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

    public static Tyre createTyre()
    {
        return new Tyre(
            TYRE_COMPOUND,
            GRIP_COEFFICIENT,
            TYRE_MASS
        );
    }

    public static Aerodynamics createAerodynamics(Vehicle vehicle)
    {
        return new Aerodynamics(vehicle);
    }

    public static Brakes createBrakes()
    {
        return new Brakes(
            BRAKE_TORQUE,
            BRAKING_EFFICIENCY,
            FRONT_BRAKE_BIAS
        );
    }

    // Creates the complete MCL39 performance calculator
    public static PerformanceCalculator createPerformanceCalculator()
    {
        Vehicle vehicle = createVehicle();
        Engine engine = createEngine();
        Gearbox gearbox = createGearbox();
        Tyre tyre = createTyre();
        Aerodynamics aerodynamics =
                createAerodynamics(vehicle);
        Brakes brakes = createBrakes();

        return new PerformanceCalculator(
            vehicle,
            engine,
            gearbox,
            tyre,
            aerodynamics,
            brakes
        );
    }
}