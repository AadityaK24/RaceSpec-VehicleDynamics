// RaceSpec - Engine Dynamics Model V2.1
// Models ICE output, turbo response, ERS deployment and RPM behaviour.

public class Engine
{
    private static final double POWER_CONVERSION = 9550.0;

    private String type;
    private int cylinders;

    private double maximumPower;
    private double maximumTorque;

    private double maximumRPM;
    private double idleRPM;
    private double engineMass;

    private double peakTorqueRPM;
    private double peakPowerRPM;

    private double turboSpoolRPM;
    private double fullBoostRPM;
    private double turboLowRPMMultiplier;

    private double outputMultiplier;

    private double ersMaximumPower;
    private double ersDeploymentStartRPM;
    private double ersDeploymentEndRPM;

    private double throttle;

    public Engine(
        String t,
        int c,
        double power,
        double torque,
        double maxRPM,
        double idle,
        double mass,
        double torqueRPM,
        double powerRPM
    )
    {
        this(
            t,
            c,
            power,
            torque,
            maxRPM,
            idle,
            mass,
            torqueRPM,
            powerRPM,
            torqueRPM * 0.65,
            torqueRPM * 0.85,
            0.72,
            1.00,
            0.0,
            0.0,
            maxRPM
        );
    }

    public Engine(
        String t,
        int c,
        double power,
        double torque,
        double maxRPM,
        double idle,
        double mass,
        double torqueRPM,
        double powerRPM,
        double spoolRPM,
        double boostRPM,
        double turboMultiplier,
        double outputFactor,
        double ersPower,
        double ersStartRPM,
        double ersEndRPM
    )
    {
        if (c <= 0)
        {
            throw new IllegalArgumentException(
                "Cylinder count must be greater than zero."
            );
        }

        if (power <= 0 || torque <= 0)
        {
            throw new IllegalArgumentException(
                "Engine power and torque must be greater than zero."
            );
        }

        if (maxRPM <= 0 || idle <= 0)
        {
            throw new IllegalArgumentException(
                "Engine RPM values must be greater than zero."
            );
        }

        if (idle >= maxRPM)
        {
            throw new IllegalArgumentException(
                "Idle RPM must be below maximum RPM."
            );
        }

        if (torqueRPM <= idle || torqueRPM > maxRPM)
        {
            throw new IllegalArgumentException(
                "Peak torque RPM is outside the operating range."
            );
        }

        if (powerRPM <= torqueRPM || powerRPM > maxRPM)
        {
            throw new IllegalArgumentException(
                "Peak power RPM is outside the operating range."
            );
        }

        if (spoolRPM < idle || spoolRPM > maxRPM)
        {
            throw new IllegalArgumentException(
                "Turbo spool RPM is outside the operating range."
            );
        }

        if (boostRPM < spoolRPM || boostRPM > maxRPM)
        {
            throw new IllegalArgumentException(
                "Full boost RPM is outside the operating range."
            );
        }

        if (
            turboMultiplier <= 0
            || turboMultiplier > 1.0
        )
        {
            throw new IllegalArgumentException(
                "Turbo multiplier must be between 0 and 1."
            );
        }

        if (
            outputFactor <= 0
            || outputFactor > 1.0
        )
        {
            throw new IllegalArgumentException(
                "Output factor must be between 0 and 1."
            );
        }

        if (ersPower < 0)
        {
            throw new IllegalArgumentException(
                "ERS power cannot be negative."
            );
        }

        type = t;
        cylinders = c;

        maximumPower = power;
        maximumTorque = torque;

        maximumRPM = maxRPM;
        idleRPM = idle;
        engineMass = mass;

        peakTorqueRPM = torqueRPM;
        peakPowerRPM = powerRPM;

        turboSpoolRPM = spoolRPM;
        fullBoostRPM = boostRPM;
        turboLowRPMMultiplier = turboMultiplier;

        outputMultiplier = outputFactor;

        ersMaximumPower = ersPower;
        ersDeploymentStartRPM = ersStartRPM;
        ersDeploymentEndRPM = ersEndRPM;

        throttle = 1.0;
    }

    public String getType()
    {
        return type;
    }

    public int getCylinders()
    {
        return cylinders;
    }

    public double getMaximumPower()
    {
        return maximumPower;
    }

    public double getMaximumTorque()
    {
        return maximumTorque;
    }

    public double getMaximumRPM()
    {
        return maximumRPM;
    }

    public double getIdleRPM()
    {
        return idleRPM;
    }

    public double getEngineMass()
    {
        return engineMass;
    }

    public double getPeakTorqueRPM()
    {
        return peakTorqueRPM;
    }

    public double getPeakPowerRPM()
    {
        return peakPowerRPM;
    }

    public double getTurboSpoolRPM()
    {
        return turboSpoolRPM;
    }

    public double getFullBoostRPM()
    {
        return fullBoostRPM;
    }

    public double getMechanicalEfficiency()
    {
        return outputMultiplier;
    }

    public double getERSMaximumPower()
    {
        return ersMaximumPower;
    }

    public double getERSDeploymentStartRPM()
    {
        return ersDeploymentStartRPM;
    }

    public double getERSDeploymentEndRPM()
    {
        return ersDeploymentEndRPM;
    }

    public double getThrottle()
    {
        return throttle;
    }

    public void setThrottle(double value)
    {
        throttle =
            Math.max(
                0.0,
                Math.min(1.0, value)
            );
    }

    // Models turbo spool-up with a smooth transition.
    public double getTurboMultiplier(double rpm)
    {
        if (rpm <= turboSpoolRPM)
        {
            return turboLowRPMMultiplier;
        }

        if (rpm >= fullBoostRPM)
        {
            return 1.0;
        }

        double progress =
            (
                rpm - turboSpoolRPM
            )
            /
            (
                fullBoostRPM - turboSpoolRPM
            );

        double smoothProgress =
            progress
            * progress
            * (3.0 - 2.0 * progress);

        return
            turboLowRPMMultiplier
            + smoothProgress
            * (
                1.0
                - turboLowRPMMultiplier
            );
    }

    // Generates the base ICE torque curve.
    public double getBaseTorqueAtRPM(double rpm)
    {
        if (
            rpm < idleRPM
            || rpm > maximumRPM
        )
        {
            return 0;
        }

        if (rpm <= peakTorqueRPM)
        {
            double progress =
                (
                    rpm - idleRPM
                )
                /
                (
                    peakTorqueRPM - idleRPM
                );

            progress =
                Math.max(
                    0.0,
                    Math.min(1.0, progress)
                );

            double smoothProgress =
                progress
                * progress
                * (3.0 - 2.0 * progress);

            double startingTorque =
                maximumTorque * 0.55;

            return
                startingTorque
                + smoothProgress
                * (
                    maximumTorque
                    - startingTorque
                );
        }

        if (rpm <= peakPowerRPM)
        {
            double targetTorque =
                (
                    maximumPower
                    * POWER_CONVERSION
                )
                /
                peakPowerRPM;

            double progress =
                (
                    rpm - peakTorqueRPM
                )
                /
                (
                    peakPowerRPM - peakTorqueRPM
                );

            progress =
                Math.max(
                    0.0,
                    Math.min(1.0, progress)
                );

            double smoothProgress =
                progress
                * progress
                * (3.0 - 2.0 * progress);

            return
                maximumTorque
                + smoothProgress
                * (
                    targetTorque
                    - maximumTorque
                );
        }

        double peakPowerTorque =
            (
                maximumPower
                * POWER_CONVERSION
            )
            /
            peakPowerRPM;

        double progress =
            (
                rpm - peakPowerRPM
            )
            /
            (
                maximumRPM - peakPowerRPM
            );

        progress =
            Math.max(
                0.0,
                Math.min(1.0, progress)
            );

        double smoothProgress =
            progress
            * progress
            * (3.0 - 2.0 * progress);

        double endTorque =
            peakPowerTorque * 0.50;

        return
            peakPowerTorque
            + smoothProgress
            * (
                endTorque
                - peakPowerTorque
            );
    }

    // Returns ICE torque after turbo behaviour and throttle.
    public double getTorqueAtRPM(double rpm)
    {
        if (
            rpm < idleRPM
            || rpm > maximumRPM
        )
        {
            return 0;
        }

        double torque =
            getBaseTorqueAtRPM(rpm);

        torque *=
            getTurboMultiplier(rpm);

        torque *=
            throttle;

        torque *=
            outputMultiplier;

        return Math.max(
            0,
            Math.min(
                torque,
                maximumTorque
            )
        );
    }

    // Calculates ICE power from torque and RPM.
    public double getPowerAtRPM(double rpm)
    {
        double torque =
            getTorqueAtRPM(rpm);

        return
            (
                torque * rpm
            )
            /
            POWER_CONVERSION;
    }

    // Simplified ERS deployment window.
    public double getERSPowerAtRPM(double rpm)
    {
        if (ersMaximumPower <= 0)
        {
            return 0;
        }

        if (
            rpm < ersDeploymentStartRPM
            || rpm > ersDeploymentEndRPM
        )
        {
            return 0;
        }

        double range =
            ersDeploymentEndRPM
            - ersDeploymentStartRPM;

        double centre =
            (
                ersDeploymentStartRPM
                + ersDeploymentEndRPM
            )
            / 2.0;

        double distance =
            Math.abs(
                rpm - centre
            );

        double factor =
            1.0
            - distance
            / (range / 2.0);

        factor =
            Math.max(
                0,
                Math.min(1.0, factor)
            );

        return
            ersMaximumPower
            * factor
            * throttle;
    }

    // Total available power before gearbox losses.
    public double getTotalPowerAtRPM(double rpm)
    {
        return
            getPowerAtRPM(rpm)
            + getERSPowerAtRPM(rpm);
    }

    // Kept for compatibility with PerformanceCalculator.
    // Engine output is no longer reduced by an artificial drivetrain loss.
    public double getUsablePowerAtRPM(double rpm)
    {
        return getTotalPowerAtRPM(rpm);
    }

    // Converts total available power into equivalent crank torque.
    public double getTotalTorqueAtRPM(double rpm)
    {
        if (rpm <= 0)
        {
            return 0;
        }

        double power =
            getTotalPowerAtRPM(rpm);

        return
            (
                power * POWER_CONVERSION
            )
            /
            rpm;
    }

    public double findRPMForPower(
        double targetPower
    )
    {
        if (targetPower <= 0)
        {
            return idleRPM;
        }

        double bestRPM =
            idleRPM;

        double smallestDifference =
            Double.MAX_VALUE;

        for (
            double rpm = idleRPM;
            rpm <= maximumRPM;
            rpm += 25.0
        )
        {
            double power =
                getTotalPowerAtRPM(rpm);

            double difference =
                Math.abs(
                    power - targetPower
                );

            if (
                difference
                < smallestDifference
            )
            {
                smallestDifference =
                    difference;

                bestRPM =
                    rpm;
            }
        }

        return bestRPM;
    }

    public boolean isAtRevLimiter(
        double rpm
    )
    {
        return rpm >= maximumRPM;
    }

    public double applyRevLimiter(
        double rpm
    )
    {
        if (rpm > maximumRPM)
        {
            return maximumRPM;
        }

        if (rpm < idleRPM)
        {
            return idleRPM;
        }

        return rpm;
    }
}