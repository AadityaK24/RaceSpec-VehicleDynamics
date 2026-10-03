// RaceSpec - Basic Lap Simulation
// Estimates lap performance using the current vehicle dynamics model.

public class LapSimulation
{
    private PerformanceCalculator calculator;
    private Circuit circuit;

    private double simulationTime;
    private double distanceTravelled;
    private double velocity;
    private double acceleration;

    private static final double TIME_STEP = 0.05;
    private static final double START_RPM = 7000.0;
    private static final int START_GEAR = 1;
    private static final double MAX_SIMULATION_TIME = 300.0;

    public LapSimulation(PerformanceCalculator pc, Circuit c)
    {
        if (pc == null || c == null)
        {
            throw new IllegalArgumentException(
                "Performance calculator and circuit cannot be null."
            );
        }

        calculator = pc;
        circuit = c;

        reset();
    }

    // Runs the basic forward lap simulation.
    public void simulate()
    {
        reset();

        double targetDistance =
            circuit.getTrackLength() * 1000.0;

        int gear = START_GEAR;
        double rpm = START_RPM;

        while (distanceTravelled < targetDistance
               && simulationTime < MAX_SIMULATION_TIME)
        {
            acceleration =
                calculator.getAcceleration(
                    rpm,
                    gear,
                    velocity
                );

            if (acceleration < 0)
            {
                acceleration = 0;
            }

            velocity =
                velocity + acceleration * TIME_STEP;

            distanceTravelled =
                distanceTravelled + velocity * TIME_STEP;

            simulationTime =
                simulationTime + TIME_STEP;

            // Basic gear progression for the initial simulation model.
            if (velocity > 35 && gear < 2)
            {
                gear = 2;
            }
            else if (velocity > 55 && gear < 3)
            {
                gear = 3;
            }
            else if (velocity > 75 && gear < 4)
            {
                gear = 4;
            }
            else if (velocity > 95 && gear < 5)
            {
                gear = 5;
            }
            else if (velocity > 115 && gear < 6)
            {
                gear = 6;
            }
            else if (velocity > 135 && gear < 7)
            {
                gear = 7;
            }
            else if (velocity > 155 && gear < 8)
            {
                gear = 8;
            }

            rpm = calculateRPM(velocity, gear);
        }
    }

    // Estimates engine RPM from vehicle speed and selected gear.
    private double calculateRPM(double speed, int gear)
    {
        double wheelRadius =
            calculator.getVehicle().getWheelRadius();

        double wheelRPM =
            (speed / (2.0 * Math.PI * wheelRadius))
            * 60.0;

        double gearRatio =
            calculator.getGearbox().getGearRatio(gear);

        double finalDrive =
            calculator.getGearbox().getFinalDriveRatio();

        double rpm =
            wheelRPM * gearRatio * finalDrive;

        if (rpm < 7000.0)
        {
            rpm = 7000.0;
        }

        return rpm;
    }

    // Resets the simulation to its starting state.
    public void reset()
    {
        simulationTime = 0;
        distanceTravelled = 0;
        velocity = 0;
        acceleration = 0;
    }

    public double getLapTime()
    {
        return simulationTime;
    }

    public double getDistanceTravelled()
    {
        return distanceTravelled;
    }

    public double getVelocity()
    {
        return velocity;
    }

    public double getAcceleration()
    {
        return acceleration;
    }

    public Circuit getCircuit()
    {
        return circuit;
    }

    public boolean hasCompletedLap()
    {
        double targetDistance =
            circuit.getTrackLength() * 1000.0;

        return distanceTravelled >= targetDistance;
    }

    public void printResults()
    {
        System.out.println("=== RaceSpec Lap Simulation ===");
        System.out.println();
        System.out.println("CIRCUIT");
        System.out.println("Name: " + circuit.getName());
        System.out.println(
            "Length: " + circuit.getTrackLength() + " km"
        );
        System.out.println();

        System.out.println("SIMULATION");
        System.out.println(
            "Lap Time: " + simulationTime + " s"
        );
        System.out.println(
            "Distance: " + distanceTravelled + " m"
        );
        System.out.println(
            "Final Velocity: " + velocity + " m/s"
        );
        System.out.println(
            "Final Velocity: " + (velocity * 3.6) + " km/h"
        );
        System.out.println(
            "Lap Completed: " + hasCompletedLap()
        );
    }
}