// RaceSpec - Vehicle Dynamics & Performance Analysis V2
// Couples engine, gearbox, tyres, aerodynamics and brakes.

public class PerformanceCalculator
{
    private Vehicle vehicle;
    private Engine engine;
    private Gearbox gearbox;
    private Tyre tyre;
    private Aerodynamics aerodynamics;
    private Brakes brakes;

    private static final double AIR_DENSITY = 1.225;

    public PerformanceCalculator(
        Vehicle v,
        Engine e,
        Gearbox g,
        Tyre t,
        Aerodynamics a,
        Brakes b
    )
    {
        if (
            v == null
            || e == null
            || g == null
            || t == null
            || a == null
            || b == null
        )
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

        brakes.setDynamics(
            vehicle,
            tyre,
            aerodynamics
        );
    }

    // Calculates wheel torque including the engine's current total output.
    public double getWheelTorque(
        double rpm,
        int gear
    )
    {
        double torque =
            engine.getTotalTorqueAtRPM(rpm);

        double gearRatio =
            gearbox.getGearRatio(gear);

        if (gearRatio <= 0)
        {
            return 0;
        }

        return torque
            * gearRatio
            * gearbox.getFinalDriveRatio()
            * gearbox.getEfficiency();
    }

    // Converts wheel torque into drive force.
    public double getDriveForce(
        double rpm,
        int gear
    )
    {
        double wheelRadius =
            vehicle.getWheelRadius();

        if (wheelRadius <= 0)
        {
            return 0;
        }

        return getWheelTorque(rpm, gear)
            / wheelRadius;
    }

    // Calculates engine RPM corresponding to vehicle speed and gear.
    public double getRPMForSpeed(
        double velocity,
        int gear
    )
    {
        if (velocity < 0)
        {
            velocity = 0;
        }

        double gearRatio =
            gearbox.getGearRatio(gear);

        if (gearRatio <= 0)
        {
            return 0;
        }

        double wheelCircumference =
            2.0
            * Math.PI
            * vehicle.getWheelRadius();

        double wheelRPM =
            velocity
            / wheelCircumference
            * 60.0;

        return wheelRPM
            * gearRatio
            * gearbox.getFinalDriveRatio();
    }

    // Recalculates drive force using speed instead of supplied RPM.
    public double getDriveForceAtSpeed(
        double velocity,
        int gear
    )
    {
        double rpm =
            getRPMForSpeed(
                velocity,
                gear
            );

        rpm =
            engine.applyRevLimiter(rpm);

        return getDriveForce(
            rpm,
            gear
        );
    }

    // Aerodynamic drag.
    public double getAerodynamicDrag(
        double velocity
    )
    {
        return aerodynamics.getDrag(velocity);
    }

    // Aerodynamic downforce.
    public double getDownforce(
        double velocity
    )
    {
        return aerodynamics.getDownforce(velocity);
    }

    // Total aerodynamic and gravitational vertical load.
    public double getNormalForce(
        double velocity
    )
    {
        return aerodynamics.getNormalForce(velocity);
    }

    // Front vertical load.
    public double getFrontNormalForce(
        double velocity,
        double acceleration
    )
    {
        double mechanicalLoad =
            vehicle.getFrontAxleLoad(
                acceleration
            );

        double aeroLoad =
            aerodynamics.getFrontDownforce(
                velocity
            );

        return Math.max(
            0,
            mechanicalLoad + aeroLoad
        );
    }

    // Rear vertical load.
    public double getRearNormalForce(
        double velocity,
        double acceleration
    )
    {
        double mechanicalLoad =
            vehicle.getRearAxleLoad(
                acceleration
            );

        double aeroLoad =
            aerodynamics.getRearDownforce(
                velocity
            );

        return Math.max(
            0,
            mechanicalLoad + aeroLoad
        );
    }

    // Rolling resistance using the total instantaneous vertical load.
    public double getRollingResistance(
        double velocity
    )
    {
        return tyre.getRollingResistance(
            getNormalForce(velocity)
        );
    }

    // Maximum total lateral grip.
    public double getMaximumGripForce(
        double velocity
    )
    {
        return tyre.getMaximumGripForce(
            getNormalForce(velocity)
        );
    }

    // Rear-axle traction limit for this rear-wheel-drive model.
    public double getRearTractionLimit(
        double velocity,
        double acceleration
    )
    {
        double rearLoad =
            getRearNormalForce(
                velocity,
                acceleration
            );

        return tyre.getMaximumLongitudinalForce(
            rearLoad
        );
    }

    // Solves traction limit iteratively because acceleration changes rear load.
    public double getTractionLimitedDriveForce(
        double rpm,
        int gear,
        double velocity
    )
    {
        double driveForce =
            getDriveForce(
                rpm,
                gear
            );

        double acceleration =
            driveForce
            / vehicle.getMass();

        for (int i = 0; i < 8; i++)
        {
            double rearGrip =
                getRearTractionLimit(
                    velocity,
                    acceleration
                );

            double actualDriveForce =
                Math.min(
                    driveForce,
                    rearGrip
                );

            double newAcceleration =
                (
                    actualDriveForce
                    - getAerodynamicDrag(velocity)
                    - getRollingResistance(velocity)
                )
                / vehicle.getMass();

            if (
                Math.abs(
                    newAcceleration
                    - acceleration
                ) < 0.001
            )
            {
                acceleration =
                    newAcceleration;

                break;
            }

            acceleration =
                newAcceleration;
        }

        return Math.max(
            0,
            Math.min(
                driveForce,
                getRearTractionLimit(
                    velocity,
                    acceleration
                )
            )
        );
    }

    // Net longitudinal force.
    public double getNetForce(
        double rpm,
        int gear,
        double velocity
    )
    {
        double driveForce =
            getTractionLimitedDriveForce(
                rpm,
                gear,
                velocity
            );

        double drag =
            getAerodynamicDrag(
                velocity
            );

        double rollingResistance =
            getRollingResistance(
                velocity
            );

        return driveForce
            - drag
            - rollingResistance;
    }

    // Vehicle longitudinal acceleration.
    public double getAcceleration(
        double rpm,
        int gear,
        double velocity
    )
    {
        if (vehicle.getMass() <= 0)
        {
            return 0;
        }

        return getNetForce(
            rpm,
            gear,
            velocity
        )
        / vehicle.getMass();
    }

    // Calculates acceleration directly from vehicle speed.
    public double getAccelerationAtSpeed(
        double velocity,
        int gear
    )
    {
        double rpm =
            getRPMForSpeed(
                velocity,
                gear
            );

        return getAcceleration(
            rpm,
            gear,
            velocity
        );
    }

    // Dynamic maximum braking force.
    public double getMaximumBrakingForce(
        double velocity
    )
    {
        return brakes.getBrakingAcceleration(
                vehicle.getWheelRadius(),
                vehicle.getMass(),
                velocity
            )
            * vehicle.getMass();
    }

    // Maximum braking deceleration.
    public double getMaximumBrakingAcceleration(
        double velocity
    )
    {
        return brakes.getBrakingAcceleration(
            vehicle.getWheelRadius(),
            vehicle.getMass(),
            velocity
        );
    }

    // Stopping distance.
    public double getStoppingDistance(
        double velocity
    )
    {
        double acceleration =
            getMaximumBrakingAcceleration(
                velocity
            );

        if (acceleration <= 0)
        {
            return 0;
        }

        return (
            velocity * velocity
        )
        / (
            2.0 * acceleration
        );
    }

    // Maximum lateral acceleration from the current vertical load.
    public double getMaximumLateralAcceleration(
        double velocity
    )
    {
        if (vehicle.getMass() <= 0)
        {
            return 0;
        }

        return getMaximumGripForce(
            velocity
        )
        / vehicle.getMass();
    }

    // Maximum cornering speed using iterative aero/grip coupling.
    public double getMaximumCorneringSpeed(
        double radius,
        double velocity
    )
    {
        if (radius <= 0)
        {
            return 0;
        }

        double estimate =
            Math.sqrt(
                getMaximumLateralAcceleration(
                    velocity
                )
                * radius
            );

        for (int i = 0; i < 12; i++)
        {
            double lateralAcceleration =
                getMaximumLateralAcceleration(
                    estimate
                );

            double newEstimate =
                Math.sqrt(
                    lateralAcceleration
                    * radius
                );

            if (
                Math.abs(
                    newEstimate
                    - estimate
                ) < 0.01
            )
            {
                estimate =
                    newEstimate;

                break;
            }

            estimate =
                newEstimate;
        }

        return estimate;
    }

    // Cornering speed without requiring an initial velocity.
    public double getMaximumCorneringSpeed(
        double radius
    )
    {
        return getMaximumCorneringSpeed(
            radius,
            60.0
        );
    }

    // Lateral force required at a given speed and corner radius.
    public double getRequiredCorneringForce(
        double radius,
        double velocity
    )
    {
        if (radius <= 0)
        {
            return 0;
        }

        return vehicle.getMass()
            * velocity
            * velocity
            / radius;
    }

    // Checks whether the requested corner is within tyre grip.
    public boolean canCorner(
        double radius,
        double velocity
    )
    {
        return getRequiredCorneringForce(
                radius,
                velocity
            )
            <= getMaximumGripForce(
                velocity
            );
    }

    // Engine power at the selected RPM.
    public double getEnginePower(
        double rpm
    )
    {
        return engine.getUsablePowerAtRPM(
            rpm
        );
    }

    // Engine torque at the selected RPM.
    public double getEngineTorque(
        double rpm
    )
    {
        return engine.getTotalTorqueAtRPM(
            rpm
        );
    }

    // ERS power at the selected RPM.
    public double getERSPower(
        double rpm
    )
    {
        return engine.getERSPowerAtRPM(
            rpm
        );
    }

    // Wheel power after drivetrain losses.
    public double getWheelPower(
        double rpm
    )
    {
        return engine.getUsablePowerAtRPM(rpm)
            * gearbox.getEfficiency();
    }

    // Power-to-weight ratio using usable peak power.
    public double getPowerToWeight()
    {
        if (vehicle.getMass() <= 0)
        {
            return 0;
        }

        double peakRPM =
            engine.getPeakPowerRPM();

        double usablePower =
            engine.getUsablePowerAtRPM(
                peakRPM
            );

        return usablePower
            / (vehicle.getMass() / 1000.0);
    }

    // Estimates top speed using a force-balance search in every gear.
    public double getEstimatedTopSpeed()
    {
        double highestSpeed = 0;

        for (
            int gear = 1;
            gear <= gearbox.getNumberOfGears();
            gear++
        )
        {
            double previousSpeed =
                0;

            double previousForce =
                getNetForce(
                    engine.getIdleRPM(),
                    gear,
                    0
                );

            for (
                double speed = 1.0;
                speed <= 120.0;
                speed += 1.0
            )
            {
                double rpm =
                    getRPMForSpeed(
                        speed,
                        gear
                    );

                if (
                    rpm > engine.getMaximumRPM()
                )
                {
                    break;
                }

                double force =
                    getNetForce(
                        rpm,
                        gear,
                        speed
                    );

                if (
                    previousForce >= 0
                    && force < 0
                )
                {
                    double low =
                        previousSpeed;

                    double high =
                        speed;

                    for (int i = 0; i < 20; i++)
                    {
                        double mid =
                            (low + high)
                            / 2.0;

                        double midRPM =
                            getRPMForSpeed(
                                mid,
                                gear
                            );

                        double midForce =
                            getNetForce(
                                midRPM,
                                gear,
                                mid
                            );

                        if (midForce >= 0)
                        {
                            low = mid;
                        }
                        else
                        {
                            high = mid;
                        }
                    }

                    highestSpeed =
                        Math.max(
                            highestSpeed,
                            low * 3.6
                        );
                }

                previousSpeed =
                    speed;

                previousForce =
                    force;
            }
        }

        return highestSpeed;
    }

    // Finds the gear that gives the greatest wheel force at a speed.
    public int getBestGear(
        double velocity
    )
    {
        int bestGear = 1;
        double bestForce = 0;

        for (
            int gear = 1;
            gear <= gearbox.getNumberOfGears();
            gear++
        )
        {
            double rpm =
                getRPMForSpeed(
                    velocity,
                    gear
                );

            if (
                rpm < engine.getIdleRPM()
                || rpm > engine.getMaximumRPM()
            )
            {
                continue;
            }

            double force =
                getDriveForce(
                    rpm,
                    gear
                );

            if (force > bestForce)
            {
                bestForce = force;
                bestGear = gear;
            }
        }

        return bestGear;
    }

    // Allows the aero system to activate or deactivate DRS.
    public void setDRS(boolean active)
    {
        aerodynamics.setDRS(active);
    }

    public boolean isDRSActive()
    {
        return aerodynamics.isDRSActive();
    }

    // Updates tyre state during a simulation step.
    public void updateTyreState(
        double speed,
        double slipRatio,
        double slipAngle,
        double timeStep
    )
    {
        tyre.updateTemperature(
            speed,
            slipRatio,
            slipAngle,
            timeStep
        );

        tyre.updateWear(
            slipRatio,
            slipAngle,
            timeStep
        );
    }

    // Updates brake temperature during a simulation step.
    public void updateBrakeTemperature(
        double brakingForce,
        double velocity,
        double timeStep
    )
    {
        brakes.updateTemperature(
            brakingForce,
            velocity,
            timeStep
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
}