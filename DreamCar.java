// RaceSpec - Dream Car Configuration
// Stores the baseline Dream Car used for vehicle comparisons.

public class DreamCar
{
    private Vehicle vehicle;
    private Engine engine;
    private Gearbox gearbox;
    private Tyre tyre;
    private Aerodynamics aerodynamics;
    private Brakes brakes;
    private PerformanceCalculator performanceCalculator;

    public DreamCar()
    {
        // Vehicle configuration
        vehicle = new Vehicle(
            "RaceSpec Dream Car",
            780.0,
            0.34,
            1.45,
            0.85,
            3.80,
            0.011
        );

        // Engine configuration
        engine = new Engine(
            "RaceSpec V10",
            10,
            900.0,
            780.0,
            15000.0,
            5000.0,
            155.0,
            9500.0,
            11500.0
        );

        // Eight-speed sequential gearbox
        double[] gearRatios =
        {
            3.20,
            2.55,
            2.10,
            1.75,
            1.48,
            1.28,
            1.12,
            0.98
        };

        gearbox = new Gearbox(
            8,
            gearRatios,
            3.20,
            0.97,
            12500.0
        );

        // Racing slick tyre configuration
        tyre = new Tyre(
            "RaceSpec Slick",
            2.10,
            9.0
        );

        // Aerodynamic model
        aerodynamics = new Aerodynamics(vehicle);

        // High-performance braking system
        brakes = new Brakes(
            7600.0,
            0.97,
            0.56
        );

        // Combined performance model
        performanceCalculator = new PerformanceCalculator(
            vehicle,
            engine,
            gearbox,
            tyre,
            aerodynamics,
            brakes
        );
    }

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

    public PerformanceCalculator getPerformanceCalculator()
    {
        return performanceCalculator;
    }
}