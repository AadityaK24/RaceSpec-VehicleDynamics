// RaceSpec - McLaren MCL39 Reference Configuration V2.1
// Verified data is separated from RaceSpec engineering assumptions.

public class McLarenMCL39
{
    // =====================================================
    // VERIFIED MCL39 DATA
    // =====================================================

    // Overall vehicle mass including driver, excluding fuel.
    private static final double MASS = 800.0;

    // McLaren publishes 44.6% to 46.1% front axle distribution.
    // RaceSpec uses the midpoint as the static model value.
    private static final double FRONT_WEIGHT_DISTRIBUTION =
        (0.446 + 0.461) / 2.0;

    private static final String POWER_UNIT =
        "Mercedes-AMG M16 E Performance";

    private static final double ENGINE_CAPACITY = 1.6;

    private static final int CYLINDERS = 6;

    private static final double BANK_ANGLE = 90.0;

    private static final int NUMBER_OF_VALVES = 24;

    private static final double MAX_ICE_RPM = 15000.0;

    private static final double MGU_K_MAX_POWER = 120.0;

    private static final double MGU_K_MAX_SPEED = 50000.0;

    private static final double ENERGY_STORE_MINIMUM_MASS = 20.0;

    private static final double MAX_ENERGY_STORAGE = 4.0;

    private static final double MAX_MGU_K_RECOVERY = 2.0;

    private static final double MAX_MGU_K_DEPLOYMENT = 4.0;

    private static final double MAX_TURBO_SPEED = 125000.0;

    private static final double MAX_FUEL_FLOW = 100.0;

    private static final double MAX_INJECTION_PRESSURE = 500.0;

    private static final int NUMBER_OF_GEARS = 8;

    private static final double TYRE_DIAMETER = 0.720;

    private static final double FRONT_TYRE_WIDTH = 0.305;

    private static final double REAR_TYRE_WIDTH = 0.405;

    private static final String WHEEL_TYPE =
        "BBS 18-inch Magnesium";

    private static final String TYRE_TYPE =
        "Pirelli P Zero F1";

    private static final double PU_MINIMUM_MASS = 151.0;

    // =====================================================
    // DERIVED DATA
    // =====================================================

    private static final double WHEEL_RADIUS =
        TYRE_DIAMETER / 2.0;

    private static final double REAR_WEIGHT_DISTRIBUTION =
        1.0 - FRONT_WEIGHT_DISTRIBUTION;

    // =====================================================
    // RACESPEC AERODYNAMIC ASSUMPTIONS
    // =====================================================

    private static final double FRONTAL_AREA = 1.50;

    private static final double DRAG_COEFFICIENT = 1.00;

    private static final double DOWNFORCE_COEFFICIENT = 3.50;

    private static final double ROLLING_RESISTANCE = 0.012;

    private static final double WHEELBASE = 3.60;

    private static final double FRONT_TRACK = 1.60;

    private static final double REAR_TRACK = 1.60;

    private static final double CENTRE_OF_GRAVITY_HEIGHT = 0.30;

    private static final double YAW_INERTIA_FACTOR = 1.10;

    // =====================================================
    // RACESPEC POWERTRAIN ASSUMPTIONS
    // =====================================================

    // McLaren publicly describes the M16 output as being
    // somewhere north of 1000 hp, but does not publish an
    // exact maximum power curve.
    private static final double MODEL_ICE_POWER = 750.0;

    private static final double MODEL_MAX_TORQUE = 700.0;

    private static final double IDLE_RPM = 3000.0;

    private static final double PEAK_TORQUE_RPM = 8500.0;

    private static final double PEAK_POWER_RPM = 13000.0;

    private static final double TURBO_SPOOL_RPM = 5000.0;

    private static final double FULL_BOOST_RPM = 8500.0;

    private static final double LOW_RPM_TURBO_MULTIPLIER = 0.72;

    private static final double ENGINE_OUTPUT_FACTOR = 1.00;

    // MGU-K is verified at 120 kW maximum.
    private static final double ERS_START_RPM = 6000.0;

    private static final double ERS_END_RPM = 14500.0;

    // =====================================================
    // RACESPEC GEARBOX ASSUMPTIONS
    // =====================================================

    // McLaren publishes 8 forward gears but not the actual
    // MCL39 gear-ratio table.
    private static final double[] GEAR_RATIOS =
    {
        3.20,
        2.45,
        1.90,
        1.55,
        1.30,
        1.10,
        0.95,
        0.82
    };

    private static final double FINAL_DRIVE_RATIO = 3.40;

    private static final double GEARBOX_EFFICIENCY = 0.96;

    private static final double SHIFT_RPM = 13500.0;

    // =====================================================
    // RACESPEC TYRE ASSUMPTIONS
    // =====================================================

    private static final double TYRE_GRIP = 1.80;

    private static final double TYRE_MASS = 10.0;

    private static final double NOMINAL_PRESSURE = 1.40;

    private static final double OPTIMUM_TEMPERATURE = 95.0;

    private static final double LOAD_SENSITIVITY = 0.10;

    private static final double TEMPERATURE_SENSITIVITY = 0.015;

    private static final double PRESSURE_SENSITIVITY = 0.05;

    private static final double LONGITUDINAL_GRIP = 1.80;

    private static final double LATERAL_GRIP = 1.90;

    private static final double PEAK_SLIP_RATIO = 0.10;

    private static final double PEAK_SLIP_ANGLE = 0.10;

    private static final double TYRE_ROLLING_RESISTANCE = 0.010;

    private static final double STARTING_TEMPERATURE = 80.0;

    // =====================================================
    // RACESPEC BRAKE ASSUMPTIONS
    // =====================================================

    private static final double BRAKE_TORQUE = 12000.0;

    private static final double BRAKING_EFFICIENCY = 0.90;

    private static final double FRONT_BRAKE_BIAS = 0.60;

    // =====================================================
    // VEHICLE
    // =====================================================

    public static Vehicle createVehicle()
    {
        return new Vehicle(
            "McLaren MCL39",
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
    // ENGINE
    // =====================================================

    public static Engine createEngine()
    {
        return new Engine(
            POWER_UNIT,
            CYLINDERS,
            MODEL_ICE_POWER,
            MODEL_MAX_TORQUE,
            MAX_ICE_RPM,
            IDLE_RPM,
            PU_MINIMUM_MASS,
            PEAK_TORQUE_RPM,
            PEAK_POWER_RPM,
            TURBO_SPOOL_RPM,
            FULL_BOOST_RPM,
            LOW_RPM_TURBO_MULTIPLIER,
            ENGINE_OUTPUT_FACTOR,
            MGU_K_MAX_POWER,
            ERS_START_RPM,
            ERS_END_RPM
        );
    }

    // =====================================================
    // GEARBOX
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
    // TYRES
    // =====================================================

    public static Tyre createTyre()
    {
        return new Tyre(
            TYRE_TYPE,
            TYRE_GRIP,
            TYRE_MASS,
            REAR_TYRE_WIDTH,
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

        aero.setFrontAeroBalance(0.45);

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
        createPerformanceCalculator()
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

    // =====================================================
    // VERIFIED DATA GETTERS
    // =====================================================

    public static double getMass()
    {
        return MASS;
    }

    public static double getFrontWeightDistribution()
    {
        return FRONT_WEIGHT_DISTRIBUTION;
    }

    public static double getRearWeightDistribution()
    {
        return REAR_WEIGHT_DISTRIBUTION;
    }

    public static String getPowerUnit()
    {
        return POWER_UNIT;
    }

    public static double getEngineCapacity()
    {
        return ENGINE_CAPACITY;
    }

    public static int getCylinders()
    {
        return CYLINDERS;
    }

    public static double getBankAngle()
    {
        return BANK_ANGLE;
    }

    public static int getNumberOfValves()
    {
        return NUMBER_OF_VALVES;
    }

    public static double getMaximumIceRPM()
    {
        return MAX_ICE_RPM;
    }

    public static double getMGUKMaximumPower()
    {
        return MGU_K_MAX_POWER;
    }

    public static double getMGUKMaximumSpeed()
    {
        return MGU_K_MAX_SPEED;
    }

    public static double getEnergyStoreMinimumMass()
    {
        return ENERGY_STORE_MINIMUM_MASS;
    }

    public static double getMaximumEnergyStorage()
    {
        return MAX_ENERGY_STORAGE;
    }

    public static double getMaximumMGUKRecovery()
    {
        return MAX_MGU_K_RECOVERY;
    }

    public static double getMaximumMGUKDeployment()
    {
        return MAX_MGU_K_DEPLOYMENT;
    }

    public static double getMaximumTurboSpeed()
    {
        return MAX_TURBO_SPEED;
    }

    public static double getMaximumFuelFlow()
    {
        return MAX_FUEL_FLOW;
    }

    public static double getMaximumInjectionPressure()
    {
        return MAX_INJECTION_PRESSURE;
    }

    public static int getNumberOfGears()
    {
        return NUMBER_OF_GEARS;
    }

    public static double getTyreDiameter()
    {
        return TYRE_DIAMETER;
    }

    public static double getFrontTyreWidth()
    {
        return FRONT_TYRE_WIDTH;
    }

    public static double getRearTyreWidth()
    {
        return REAR_TYRE_WIDTH;
    }

    public static String getWheelType()
    {
        return WHEEL_TYPE;
    }

    public static String getTyreType()
    {
        return TYRE_TYPE;
    }

    public static double getPUMinimumMass()
    {
        return PU_MINIMUM_MASS;
    }

    // =====================================================
    // RACESPEC ASSUMPTION GETTERS
    // =====================================================

    public static double getModelIcePower()
    {
        return MODEL_ICE_POWER;
    }

    public static double getModelMaximumTorque()
    {
        return MODEL_MAX_TORQUE;
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

    public static double getWheelbase()
    {
        return WHEELBASE;
    }

    public static double getCentreOfGravityHeight()
    {
        return CENTRE_OF_GRAVITY_HEIGHT;
    }
}