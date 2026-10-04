// RaceSpec - Track-Aware Lap Simulation V2
// Simulates acceleration, braking and cornering around the circuit.

public class LapSimulation
{
    private PerformanceCalculator calculator;
    private Circuit circuit;

    private double simulationTime;
    private double distanceTravelled;
    private double velocity;

    private double maximumVelocity;
    private double acceleration;

    private int currentGear;
    private int currentCorner;

    private boolean completed;

    private static final double TIME_STEP = 0.02;
    private static final double MAX_SIMULATION_TIME = 180.0;

    public LapSimulation(
        PerformanceCalculator pc,
        Circuit c
    )
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

    // Runs the complete lap.
    public LapResult simulate()
    {
        reset();

        double targetDistance =
            circuit.getTrackLength() * 1000.0;

        int corners =
            circuit.getNumberOfCorners();

        double straightTotal =
            circuit.getTotalStraightLength() * 1000.0;

        double cornerTotal =
            targetDistance - straightTotal;

        if (cornerTotal < 0)
        {
            cornerTotal = targetDistance * 0.30;
        }

        double straightSection =
            straightTotal / corners;

        double cornerSection =
            cornerTotal / corners;

        for (
            int corner = 1;
            corner <= corners;
            corner++
        )
        {
            currentCorner = corner;

            if (
                distanceTravelled >= targetDistance
                || simulationTime >= MAX_SIMULATION_TIME
            )
            {
                break;
            }

            // Accelerate along the straight.
            simulateStraight(
                straightSection,
                targetDistance
            );

            if (
                distanceTravelled >= targetDistance
                || simulationTime >= MAX_SIMULATION_TIME
            )
            {
                break;
            }

            // Calculate the speed required for the upcoming corner.
            double cornerTargetSpeed =
                getCornerTargetSpeed(
                    corner
                );

            // Brake only until the car reaches the required corner speed.
            simulateBrakingToSpeed(
                cornerTargetSpeed,
                targetDistance
            );

            if (
                distanceTravelled >= targetDistance
                || simulationTime >= MAX_SIMULATION_TIME
            )
            {
                break;
            }

            // Travel through the corner.
            simulateCorner(
                corner,
                cornerSection,
                cornerTargetSpeed,
                targetDistance
            );
        }

        if (
            distanceTravelled >= targetDistance
        )
        {
            distanceTravelled =
                targetDistance;

            completed = true;
        }

        calculator.setDRS(false);

        return createResult();
    }

    // Calculates the usable speed for a corner.
    private double getCornerTargetSpeed(
        int corner
    )
    {
        double radius =
            circuit.getCornerRadius(
                corner
            );

        if (radius <= 0)
        {
            radius =
                circuit.getAverageCornerRadius();
        }

        double publishedModelSpeed =
            circuit.getCornerApexSpeed(
                corner
            ) / 3.6;

        double physicsSpeed =
            calculator.getMaximumCorneringSpeed(
                radius
            );

        double targetSpeed =
            Math.min(
                publishedModelSpeed,
                physicsSpeed
            );

        double gripFactor;

        if (targetSpeed < 25.0)
        {
            gripFactor =
                circuit.getLowSpeedFactor();
        }
        else if (targetSpeed < 45.0)
        {
            gripFactor =
                circuit.getMediumSpeedFactor();
        }
        else
        {
            gripFactor =
                circuit.getHighSpeedFactor();
        }

        targetSpeed *=
            Math.sqrt(
                Math.max(
                    0.50,
                    gripFactor
                )
            );

        return Math.max(
            12.0,
            targetSpeed
        );
    }

    // Simulates acceleration along a straight.
    private void simulateStraight(
        double sectionDistance,
        double targetDistance
    )
    {
        double startDistance =
            distanceTravelled;

        while (
            distanceTravelled - startDistance
                < sectionDistance
            && distanceTravelled < targetDistance
            && simulationTime < MAX_SIMULATION_TIME
        )
        {
            double remaining =
                sectionDistance
                - (
                    distanceTravelled
                    - startDistance
                );

            boolean useDRS =
                remaining > 300.0
                && velocity > 45.0;

            calculator.setDRS(
                useDRS
            );

            currentGear =
                getBestGearSafely();

            double rpm =
                getOperatingRPM(
                    velocity,
                    currentGear
                );

            acceleration =
                calculator.getAcceleration(
                    rpm,
                    currentGear,
                    velocity
                );

            if (acceleration < 0)
            {
                acceleration = 0;
            }

            updateState(
                acceleration,
                false
            );
        }

        calculator.setDRS(false);
    }

    // Brakes until the vehicle reaches the target corner speed.
    private void simulateBrakingToSpeed(
        double targetSpeed,
        double targetDistance
    )
    {
        while (
            velocity > targetSpeed + 1.0
            && velocity > 0.5
            && distanceTravelled < targetDistance
            && simulationTime < MAX_SIMULATION_TIME
        )
        {
            double brakingAcceleration =
                calculator.getMaximumBrakingAcceleration(
                    velocity
                );

            if (brakingAcceleration <= 0)
            {
                break;
            }

            double requiredDeceleration =
                (
                    velocity
                    - targetSpeed
                )
                / TIME_STEP;

            double appliedDeceleration =
                Math.min(
                    brakingAcceleration,
                    requiredDeceleration
                );

            acceleration =
                -appliedDeceleration;

            double brakingForce =
                calculator.getMaximumBrakingForce(
                    velocity
                );

            calculator.updateBrakeTemperature(
                brakingForce,
                velocity,
                TIME_STEP
            );

            currentGear =
                Math.max(
                    1,
                    getBestGearSafely()
                );

            updateState(
                acceleration,
                true
            );
        }

        // Never carry braking into the corner unnecessarily.
        if (
            velocity < targetSpeed
        )
        {
            acceleration = 0;
        }
    }

    // Simulates movement through a corner.
    private void simulateCorner(
        int corner,
        double sectionDistance,
        double targetSpeed,
        double targetDistance
    )
    {
        if (sectionDistance <= 0)
        {
            return;
        }

        double radius =
            circuit.getCornerRadius(
                corner
            );

        if (radius <= 0)
        {
            radius =
                circuit.getAverageCornerRadius();
        }

        double startDistance =
            distanceTravelled;

        while (
            distanceTravelled - startDistance
                < sectionDistance
            && distanceTravelled < targetDistance
            && simulationTime < MAX_SIMULATION_TIME
        )
        {
            double speedDifference =
                targetSpeed - velocity;

            currentGear =
                getBestGearSafely();

            if (speedDifference > 2.0)
            {
                double rpm =
                    getOperatingRPM(
                        velocity,
                        currentGear
                    );

                acceleration =
                    Math.max(
                        0,
                        calculator.getAcceleration(
                            rpm,
                            currentGear,
                            velocity
                        )
                    );
            }
            else if (speedDifference < -2.0)
            {
                double brakingAcceleration =
                    calculator.getMaximumBrakingAcceleration(
                        velocity
                    );

                acceleration =
                    -Math.min(
                        brakingAcceleration,
                        Math.abs(
                            speedDifference
                        ) / TIME_STEP
                    );
            }
            else
            {
                acceleration = 0;
            }

            // Account for the tyre force already being used laterally.
            double lateralForce =
                calculator.getRequiredCorneringForce(
                    radius,
                    Math.max(
                        velocity,
                        0
                    )
                );

            double maximumGrip =
                calculator.getMaximumGripForce(
                    Math.max(
                        velocity,
                        0
                    )
                );

            if (
                maximumGrip > 0
                && acceleration > 0
            )
            {
                double lateralUsage =
                    lateralForce
                    / maximumGrip;

                lateralUsage =
                    Math.max(
                        0,
                        Math.min(
                            1.0,
                            lateralUsage
                        )
                    );

                double longitudinalFactor =
                    Math.sqrt(
                        Math.max(
                            0,
                            1.0
                            - lateralUsage
                            * lateralUsage
                        )
                    );

                acceleration *=
                    longitudinalFactor;
            }

            updateState(
                acceleration,
                acceleration < 0
            );
        }
    }

    // Returns a valid gear for the current speed.
    private int getBestGearSafely()
    {
        int gear =
            calculator.getBestGear(
                velocity
            );

        if (gear < 1)
        {
            gear = 1;
        }

        int maximumGear =
            calculator.getGearbox().getNumberOfGears();

        if (gear > maximumGear)
        {
            gear = maximumGear;
        }

        return gear;
    }

    // Returns a usable engine RPM.
    private double getOperatingRPM(
        double speed,
        int gear
    )
    {
        if (speed < 1.0)
        {
            return calculator.getEngine().getIdleRPM();
        }

        double rpm =
            calculator.getRPMForSpeed(
                speed,
                gear
            );

        double idleRPM =
            calculator.getEngine().getIdleRPM();

        double maximumRPM =
            calculator.getEngine().getMaximumRPM();

        if (rpm < idleRPM)
        {
            rpm = idleRPM;
        }

        if (rpm > maximumRPM)
        {
            rpm = maximumRPM;
        }

        return rpm;
    }

    // Updates vehicle position, speed and thermal state.
    private void updateState(
        double currentAcceleration,
        boolean braking
    )
    {
        double oldVelocity =
            velocity;

        velocity +=
            currentAcceleration
            * TIME_STEP;

        if (velocity < 0)
        {
            velocity = 0;
        }

        double averageVelocity =
            (
                oldVelocity
                + velocity
            ) / 2.0;

        distanceTravelled +=
            averageVelocity
            * TIME_STEP;

        simulationTime +=
            TIME_STEP;

        if (
            velocity > maximumVelocity
        )
        {
            maximumVelocity =
                velocity;
        }

        double slipRatio = 0.0;

        if (braking)
        {
            slipRatio = 0.05;
        }
        else if (currentAcceleration > 2.0)
        {
            slipRatio = 0.02;
        }

        calculator.updateTyreState(
            velocity,
            slipRatio,
            0.0,
            TIME_STEP
        );
    }

    private LapResult createResult()
    {
        double averageVelocity = 0;

        if (simulationTime > 0)
        {
            averageVelocity =
                distanceTravelled
                / simulationTime;
        }

        return new LapResult(
            calculator.getVehicle().getName(),
            circuit.getName(),
            simulationTime,
            distanceTravelled,
            velocity,
            maximumVelocity,
            averageVelocity,
            completed
        );
    }

    public void reset()
    {
        simulationTime = 0;
        distanceTravelled = 0;
        velocity = 0;

        maximumVelocity = 0;
        acceleration = 0;

        currentGear = 1;
        currentCorner = 0;

        completed = false;

        calculator.setDRS(false);
        calculator.getBrakes().resetTemperature();
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

    public double getMaximumVelocity()
    {
        return maximumVelocity;
    }

    public double getAverageVelocity()
    {
        if (simulationTime <= 0)
        {
            return 0;
        }

        return distanceTravelled
            / simulationTime;
    }

    public double getAcceleration()
    {
        return acceleration;
    }

    public int getCurrentGear()
    {
        return currentGear;
    }

    public int getCurrentCorner()
    {
        return currentCorner;
    }

    public boolean hasCompletedLap()
    {
        return completed;
    }

    public Circuit getCircuit()
    {
        return circuit;
    }

    public PerformanceCalculator getCalculator()
    {
        return calculator;
    }

    public LapResult getResult()
    {
        return createResult();
    }

    public void printResults()
    {
        System.out.println(
            "=== RaceSpec Lap Simulation ==="
        );

        System.out.println();
        System.out.println("CIRCUIT");

        System.out.println(
            "Name: "
            + circuit.getName()
        );

        System.out.println(
            "Length: "
            + circuit.getTrackLength()
            + " km"
        );

        System.out.println();
        System.out.println("SIMULATION");

        System.out.println(
            "Lap Time: "
            + simulationTime
            + " s"
        );

        System.out.println(
            "Distance: "
            + distanceTravelled
            + " m"
        );

        System.out.println(
            "Final Velocity: "
            + velocity * 3.6
            + " km/h"
        );

        System.out.println(
            "Maximum Velocity: "
            + maximumVelocity * 3.6
            + " km/h"
        );

        System.out.println(
            "Average Velocity: "
            + getAverageVelocity() * 3.6
            + " km/h"
        );

        System.out.println(
            "Final Gear: "
            + currentGear
        );

        System.out.println(
            "Last Corner: "
            + currentCorner
        );

        System.out.println(
            "Lap Completed: "
            + completed
        );
    }
}