// RaceSpec - Engine Dynamics Model V2
// Models engine torque, power, RPM behaviour, turbo response and ERS deployment.

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

    private double minimumOperatingRPM;

    private double turboSpoolRPM;
    private double fullBoostRPM;
    private double turboLowRPMMultiplier;

    private double mechanicalEfficiency;

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
            torqueRPM * 0.75,
            1.0,
            0.90,
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
        double turboLowMultiplier,
        double efficiency,
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
                "Peak torque RPM is outside the valid operating range."
            );
        }

        if (powerRPM <= torqueRPM || powerRPM > maxRPM)
        {
            throw new IllegalArgumentException(
                "Peak power RPM is outside the valid operating range."
            );
        }

        if (spoolRPM < idle || spoolRPM > maxRPM)
        {
            throw new IllegalArgumentException(
                "Turbo spool RPM is outside the valid operating range."
            );
        }

        if (boostRPM < spoolRPM || boostRPM > maxRPM)
        {
            throw new IllegalArgumentException(
                "Full boost RPM is outside the valid operating range."
            );
        }

        if (turboLowMultiplier <= 0 || turboLowMultiplier > 1.0)
        {
            throw new IllegalArgumentException(
                "Turbo low RPM multiplier must be between 0 and 1."
            );
        }

        if (efficiency <= 0 || efficiency > 1.0)
        {
            throw new IllegalArgumentException(
                "Mechanical efficiency must be between 0 and 1."
            );
        }

        if (ersPower < 0)
        {
            throw new IllegalArgumentException(
                "ERS power cannot be negative."
            );
        }

        if (ersPower > 0)
        {
            if (ersStartRPM <= 0 || ersEndRPM <= ersStartRPM)
            {
                throw new IllegalArgumentException(
                    "Invalid ERS deployment RPM range."
                );
            }
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

        minimumOperatingRPM = idle;

        turboSpoolRPM = spoolRPM;
        fullBoostRPM = boostRPM;
        turboLowRPMMultiplier = turboLowMultiplier;

        mechanicalEfficiency = efficiency;

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
        return mechanicalEfficiency;
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
        if (value < 0)
        {
            throttle = 0;
        }
        else if (value > 1)
        {
            throttle = 1;
        }
        else
        {
            throttle = value;
        }
    }

    // Calculates the turbo boost multiplier from engine speed.
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
            (rpm - turboSpoolRPM)
            / (fullBoostRPM - turboSpoolRPM);

        // Smoothstep avoids an artificial discontinuity in boost response.
        double smoothProgress =
            progress * progress * (3.0 - 2.0 * progress);

        return turboLowRPMMultiplier
            + smoothProgress
            * (1.0 - turboLowRPMMultiplier);
    }

    // Produces a smooth torque curve through the engine's key RPM regions.
    public double getBaseTorqueAtRPM(double rpm)
    {
        if (rpm < idleRPM || rpm > maximumRPM)
        {
            return 0;
        }

        if (rpm <= peakTorqueRPM)
        {
            double progress =
                (rpm - idleRPM)
                / (peakTorqueRPM - idleRPM);

            double smoothProgress =
                progress * progress
                * (3.0 - 2.0 * progress);

            double startingTorque =
                maximumTorque * 0.58;

            return startingTorque
                + smoothProgress
                * (maximumTorque - startingTorque);
        }

        if (rpm <= peakPowerRPM)
        {
            double progress =
                (rpm - peakTorqueRPM)
                / (peakPowerRPM - peakTorqueRPM);

            double targetTorque =
                (maximumPower * POWER_CONVERSION)
                / peakPowerRPM;

            double smoothProgress =
                progress * progress
                * (3.0 - 2.0 * progress);

            return maximumTorque
                + smoothProgress
                * (targetTorque - maximumTorque);
        }

        double progress =
            (rpm - peakPowerRPM)
            / (maximumRPM - peakPowerRPM);

        progress = Math.min(1.0, Math.max(0.0, progress));

        double peakPowerTorque =
            (maximumPower * POWER_CONVERSION)
            / peakPowerRPM;

        double endTorque =
            peakPowerTorque * 0.50;

        double smoothProgress =
            progress * progress
            * (3.0 - 2.0 * progress);

        return peakPowerTorque
            + smoothProgress
            * (endTorque - peakPowerTorque);
    }

    // Returns engine torque after turbo behaviour and throttle are applied.
    public double getTorqueAtRPM(double rpm)
    {
        if (rpm < minimumOperatingRPM || rpm > maximumRPM)
        {
            return 0;
        }

        double baseTorque =
            getBaseTorqueAtRPM(rpm);

        double turboMultiplier =
            getTurboMultiplier(rpm);

        double torque =
            baseTorque * turboMultiplier;

        torque *= throttle;

        // Prevent the model from exceeding the declared maximum torque.
        torque =
            Math.min(torque, maximumTorque);

        return torque;
    }

    // Calculates mechanical power from torque and RPM.
    public double getPowerAtRPM(double rpm)
    {
        double torque =
            getTorqueAtRPM(rpm);

        return (torque * rpm)
            / POWER_CONVERSION;
    }

    // Calculates the electrical power available from ERS deployment.
    public double getERSPowerAtRPM(double rpm)
    {
        if (ersMaximumPower <= 0)
        {
            return 0;
        }

        if (rpm < ersDeploymentStartRPM
            || rpm > ersDeploymentEndRPM)
        {
            return 0;
        }

        double centre =
            (ersDeploymentStartRPM
            + ersDeploymentEndRPM) / 2.0;

        double halfRange =
            (ersDeploymentEndRPM
            - ersDeploymentStartRPM) / 2.0;

        double distance =
            Math.abs(rpm - centre);

        double factor =
            1.0 - (distance / halfRange);

        factor =
            Math.max(0, factor);

        return ersMaximumPower
            * factor
            * throttle;
    }

    // Total wheel-side power before gearbox losses.
    public double getTotalPowerAtRPM(double rpm)
    {
        return getPowerAtRPM(rpm)
            + getERSPowerAtRPM(rpm);
    }

    // Total engine-side power after the declared mechanical efficiency.
    public double getUsablePowerAtRPM(double rpm)
    {
        return getTotalPowerAtRPM(rpm)
            * mechanicalEfficiency;
    }

    // Converts mechanical power back into equivalent crank torque.
    public double getTotalTorqueAtRPM(double rpm)
    {
        double power =
            getUsablePowerAtRPM(rpm);

        if (rpm <= 0)
        {
            return 0;
        }

        return (power * POWER_CONVERSION)
            / rpm;
    }

    // Returns the RPM at which a requested power level is reached.
    public double findRPMForPower(double targetPower)
    {
        if (targetPower <= 0)
        {
            return idleRPM;
        }

        double bestRPM = idleRPM;
        double smallestDifference =
            Double.MAX_VALUE;

        for (
            double rpm = idleRPM;
            rpm <= maximumRPM;
            rpm += 50.0
        )
        {
            double power =
                getUsablePowerAtRPM(rpm);

            double difference =
                Math.abs(power - targetPower);

            if (difference < smallestDifference)
            {
                smallestDifference = difference;
                bestRPM = rpm;
            }
        }

        return bestRPM;
    }

    // Indicates whether the engine has reached the rev limiter.
    public boolean isAtRevLimiter(double rpm)
    {
        return rpm >= maximumRPM;
    }

    // Applies a simple hard limiter at the maximum RPM.
    public double applyRevLimiter(double rpm)
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