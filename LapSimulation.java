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
    private double totalSpeedDistance;

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

    public LapResult simulate()
    {
        reset();

        double targetDistance =
            circuit.getTrackLength() * 1000.0;

        int corners =
            circuit.getNumberOfCorners();

        double straightDistance =
            (
                circuit.getTotalStraightLength()
                * 1000.0
            ) / corners;

        double cornerDistance =
            (
                targetDistance
                - circuit.getTotalStraightLength() * 1000.0
            ) / corners;

        cornerDistance =
            Math.max(
                40.0,
                cornerDistance
            );

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

            simulateStraight(
                straightDistance,
                targetDistance
            );

            if (
                distanceTravelled >= targetDistance
                || simulationTime >= MAX_SIMULATION_TIME
            )
            {
                break;
            }

            simulateBraking(
                getBrakingDistance(corner)
            );

            simulateCorner(
                corner,
                cornerDistance,
                targetDistance
            );
        }

        if (distanceTravelled >= targetDistance)
        {
            distanceTravelled =
                targetDistance;

            completed = true;
        }

        calculator.setDRS(false);

        return createResult();
    }

    private double getBrakingDistance(int corner)
    {
        int zone =
            Math.min(
                corner,
                circuit.getNumberOfBrakingZones()
            );

        double brakingDistance =
            circuit.getBrakingDistance(zone);

        if (brakingDistance <= 0)
        {
            return 50.0;
        }

        return brakingDistance;
    }

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

            boolean drs =
                remaining > 300.0
                && velocity > 45.0;

            calculator.setDRS(drs);

            if (velocity < 1.0)
            {
                currentGear = 1;

                double rpm =
                    calculator.getEngine().getIdleRPM();

                acceleration =
                    calculator.getAcceleration(
                        rpm,
                        currentGear,
                        velocity
                    );
            }
            else
            {
                currentGear =
                    calculator.getBestGear(
                        velocity
                    );

                if (currentGear < 1)
                {
                    currentGear = 1;
                }

                double rpm =
                    calculator.getRPMForSpeed(
                        velocity,
                        currentGear
                    );

                if (
                    rpm < calculator.getEngine().getIdleRPM()
                )
                {
                    rpm =
                        calculator.getEngine().getIdleRPM();
                }

                if (
                    rpm > calculator.getEngine().getMaximumRPM()
                )
                {
                    currentGear =
                        Math.min(
                            calculator.getGearbox().getNumberOfGears(),
                            currentGear + 1
                        );

                    rpm =
                        calculator.getRPMForSpeed(
                            velocity,
                            currentGear
                        );
                }

                acceleration =
                    calculator.getAcceleration(
                        rpm,
                        currentGear,
                        velocity
                    );
            }

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

    private void simulateBraking(
        double brakingDistance
    )
    {
        if (brakingDistance <= 0)
        {
            return;
        }

        double startDistance =
            distanceTravelled;

        while (
            distanceTravelled - startDistance
                < brakingDistance
            && velocity > 0.5
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

            acceleration =
                -brakingAcceleration;

            currentGear =
                calculator.getBestGear(
                    velocity
                );

            if (currentGear < 1)
            {
                currentGear = 1;
            }

            double brakingForce =
                calculator.getMaximumBrakingForce(
                    velocity
                );

            calculator.updateBrakeTemperature(
                brakingForce,
                velocity,
                TIME_STEP
            );

            updateState(
                acceleration,
                true
            );
        }
    }

    private void simulateCorner(
        int corner,
        double sectionDistance,
        double targetDistance
    )
    {
        if (sectionDistance <= 0)
        {
            return;
        }

        double radius =
            circuit.getCornerRadius(corner);

        if (radius <= 0)
        {
            radius =
                circuit.getAverageCornerRadius();
        }

        double circuitApexSpeed =
            circuit.getCornerApexSpeed(corner)
            / 3.6;

        double physicsLimit =
            calculator.getMaximumCorneringSpeed(
                radius
            );

        double targetSpeed =
            Math.min(
                circuitApexSpeed,
                physicsLimit
            );

        targetSpeed =
            Math.max(
                8.0,
                targetSpeed
            );

        double startDistance =
            distanceTravelled;

        while (
            distanceTravelled - startDistance
                < sectionDistance
            && distanceTravelled < targetDistance
            && simulationTime < MAX_SIMULATION_TIME
        )
        {
            double speedError =
                targetSpeed - velocity;

            if (speedError > 1.0)
            {
                currentGear =
                    calculator.getBestGear(
                        velocity
                    );

                if (currentGear < 1)
                {
                    currentGear = 1;
                }

                double rpm;

                if (velocity < 1.0)
                {
                    rpm =
                        calculator.getEngine().getIdleRPM();
                }
                else
                {
                    rpm =
                        calculator.getRPMForSpeed(
                            velocity,
                            currentGear
                        );
                }

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
            else if (speedError < -1.0)
            {
                acceleration =
                    -calculator.getMaximumBrakingAcceleration(
                        velocity
                    );
            }
            else
            {
                acceleration = 0;
            }

            double lateralForce =
                calculator.getRequiredCorneringForce(
                    radius,
                    velocity
                );

            double availableGrip =
                calculator.getMaximumGripForce(
                    velocity
                );

            if (availableGrip > 0)
            {
                double lateralUtilisation =
                    lateralForce
                    / availableGrip;

                lateralUtilisation =
                    Math.max(
                        0,
                        Math.min(
                            1.0,
                            lateralUtilisation
                        )
                    );

                double longitudinalFactor =
                    Math.sqrt(
                        Math.max(
                            0,
                            1.0
                            - lateralUtilisation
                            * lateralUtilisation
                        )
                    );

                if (acceleration > 0)
                {
                    acceleration *=
                        longitudinalFactor;
                }
            }

            updateState(
                acceleration,
                acceleration < 0
            );
        }
    }

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

        totalSpeedDistance +=
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
            slipRatio = 0.08;
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
        totalSpeedDistance = 0;

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