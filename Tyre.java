// RaceSpec - Tyre Dynamics Model V2
// Models load-sensitive grip, temperature, pressure, slip and combined tyre usage.

public class Tyre
{
    private String compound;

    private double gripCoefficient;
    private double tyreMass;

    private double nominalWidth;
    private double nominalDiameter;

    private double nominalPressure;
    private double optimumTemperature;

    private double loadSensitivity;
    private double temperatureSensitivity;
    private double pressureSensitivity;

    private double longitudinalGripCoefficient;
    private double lateralGripCoefficient;

    private double slipRatioPeak;
    private double slipAnglePeak;

    private double rollingResistanceCoefficient;

    private double temperature;
    private double pressure;

    private double wear;

    public Tyre(String c, double grip, double mass)
    {
        this(
            c,
            grip,
            mass,
            0.405,
            0.720,
            1.40,
            95.0,
            0.10,
            0.015,
            0.05,
            grip,
            grip * 1.05,
            0.10,
            0.10,
            0.010,
            80.0
        );
    }

    public Tyre(
        String c,
        double grip,
        double mass,
        double width,
        double diameter,
        double pressure,
        double optimumTemp,
        double loadSensitivityValue,
        double temperatureSensitivityValue,
        double pressureSensitivityValue,
        double longitudinalGrip,
        double lateralGrip,
        double peakSlipRatio,
        double peakSlipAngle,
        double rollingResistance,
        double initialTemperature
    )
    {
        if (grip <= 0)
        {
            throw new IllegalArgumentException(
                "Grip coefficient must be greater than zero."
            );
        }

        if (mass <= 0)
        {
            throw new IllegalArgumentException(
                "Tyre mass must be greater than zero."
            );
        }

        if (width <= 0 || diameter <= 0)
        {
            throw new IllegalArgumentException(
                "Tyre dimensions must be greater than zero."
            );
        }

        if (pressure <= 0)
        {
            throw new IllegalArgumentException(
                "Tyre pressure must be greater than zero."
            );
        }

        if (optimumTemp <= 0)
        {
            throw new IllegalArgumentException(
                "Optimum tyre temperature must be greater than zero."
            );
        }

        if (loadSensitivityValue < 0
            || temperatureSensitivityValue < 0
            || pressureSensitivityValue < 0)
        {
            throw new IllegalArgumentException(
                "Tyre sensitivity values cannot be negative."
            );
        }

        if (longitudinalGrip <= 0
            || lateralGrip <= 0)
        {
            throw new IllegalArgumentException(
                "Tyre grip coefficients must be greater than zero."
            );
        }

        if (peakSlipRatio <= 0 || peakSlipAngle <= 0)
        {
            throw new IllegalArgumentException(
                "Tyre slip limits must be greater than zero."
            );
        }

        if (rollingResistance < 0)
        {
            throw new IllegalArgumentException(
                "Rolling resistance cannot be negative."
            );
        }

        compound = c;

        gripCoefficient = grip;
        tyreMass = mass;

        nominalWidth = width;
        nominalDiameter = diameter;

        nominalPressure = pressure;
        optimumTemperature = optimumTemp;

        loadSensitivity = loadSensitivityValue;
        temperatureSensitivity =
            temperatureSensitivityValue;
        pressureSensitivity =
            pressureSensitivityValue;

        longitudinalGripCoefficient = longitudinalGrip;
        lateralGripCoefficient = lateralGrip;

        slipRatioPeak = peakSlipRatio;
        slipAnglePeak = peakSlipAngle;

        rollingResistanceCoefficient =
            rollingResistance;

        temperature = initialTemperature;
        this.pressure = pressure;

        wear = 0.0;
    }

    public String getCompound()
    {
        return compound;
    }

    public double getGripCoefficient()
    {
        return gripCoefficient;
    }

    public double getTyreMass()
    {
        return tyreMass;
    }

    public double getNominalWidth()
    {
        return nominalWidth;
    }

    public double getNominalDiameter()
    {
        return nominalDiameter;
    }

    public double getNominalPressure()
    {
        return nominalPressure;
    }

    public double getOptimumTemperature()
    {
        return optimumTemperature;
    }

    public double getLoadSensitivity()
    {
        return loadSensitivity;
    }

    public double getTemperatureSensitivity()
    {
        return temperatureSensitivity;
    }

    public double getPressureSensitivity()
    {
        return pressureSensitivity;
    }

    public double getLongitudinalGripCoefficient()
    {
        return longitudinalGripCoefficient;
    }

    public double getLateralGripCoefficient()
    {
        return lateralGripCoefficient;
    }

    public double getSlipRatioPeak()
    {
        return slipRatioPeak;
    }

    public double getSlipAnglePeak()
    {
        return slipAnglePeak;
    }

    public double getRollingResistanceCoefficient()
    {
        return rollingResistanceCoefficient;
    }

    public double getTemperature()
    {
        return temperature;
    }

    public double getPressure()
    {
        return pressure;
    }

    public double getWear()
    {
        return wear;
    }

    public void setTemperature(double newTemperature)
    {
        if (newTemperature < 0)
        {
            temperature = 0;
        }
        else
        {
            temperature = newTemperature;
        }
    }

    public void setPressure(double newPressure)
    {
        if (newPressure > 0)
        {
            pressure = newPressure;
        }
    }

    public void setWear(double newWear)
    {
        if (newWear < 0)
        {
            wear = 0;
        }
        else if (newWear > 1)
        {
            wear = 1;
        }
        else
        {
            wear = newWear;
        }
    }

    // Temperature efficiency peaks around the chosen optimum temperature.
    public double getTemperatureFactor()
    {
        double difference =
            Math.abs(temperature - optimumTemperature);

        double normalizedDifference =
            difference / optimumTemperature;

        double factor =
            1.0
            - temperatureSensitivity
            * normalizedDifference
            * normalizedDifference;

        return clamp(factor, 0.60, 1.0);
    }

    // Excess pressure changes the usable friction level.
    public double getPressureFactor()
    {
        double difference =
            Math.abs(pressure - nominalPressure);

        double normalizedDifference =
            difference / nominalPressure;

        double factor =
            1.0
            - pressureSensitivity
            * normalizedDifference
            * 10.0;

        return clamp(factor, 0.75, 1.0);
    }

    // Grip decreases progressively as the tyre accumulates wear.
    public double getWearFactor()
    {
        return 1.0 - 0.15 * wear;
    }

    // Load sensitivity means doubling vertical load does not double
    // the tyre friction coefficient.
    public double getLoadFactor(double normalForce)
    {
        if (normalForce <= 0)
        {
            return 0;
        }

        double referenceLoad = 4000.0;

        double normalizedLoad =
            normalForce / referenceLoad;

        double factor =
            Math.pow(
                normalizedLoad,
                -loadSensitivity
            );

        return clamp(factor, 0.70, 1.15);
    }

    // Effective friction coefficient at the current tyre state.
    public double getEffectiveGripCoefficient(
        double normalForce
    )
    {
        if (normalForce <= 0)
        {
            return 0;
        }

        double coefficient =
            gripCoefficient
            * getLoadFactor(normalForce)
            * getTemperatureFactor()
            * getPressureFactor()
            * getWearFactor();

        return Math.max(0, coefficient);
    }

    // Basic peak grip force while the tyre is in its optimal operating state.
    public double getMaximumGripForce(double normalForce)
    {
        return getMaximumLateralForce(normalForce);
    }

    // Maximum longitudinal force before longitudinal saturation.
    public double getMaximumLongitudinalForce(
        double normalForce
    )
    {
        if (normalForce <= 0)
        {
            return 0;
        }

        double coefficient =
            longitudinalGripCoefficient
            * getLoadFactor(normalForce)
            * getTemperatureFactor()
            * getPressureFactor()
            * getWearFactor();

        return Math.max(0, coefficient * normalForce);
    }

    // Maximum lateral force before lateral saturation.
    public double getMaximumLateralForce(
        double normalForce
    )
    {
        if (normalForce <= 0)
        {
            return 0;
        }

        double coefficient =
            lateralGripCoefficient
            * getLoadFactor(normalForce)
            * getTemperatureFactor()
            * getPressureFactor()
            * getWearFactor();

        return Math.max(0, coefficient * normalForce);
    }

    // Longitudinal force available at a given slip ratio.
    public double getLongitudinalForce(
        double normalForce,
        double slipRatio
    )
    {
        double maximumForce =
            getMaximumLongitudinalForce(normalForce);

        if (maximumForce <= 0)
        {
            return 0;
        }

        double normalizedSlip =
            Math.abs(slipRatio)
            / slipRatioPeak;

        // Smooth rise toward peak grip and progressive fall beyond it.
        double factor;

        if (normalizedSlip <= 1.0)
        {
            factor =
                Math.sin(
                    normalizedSlip
                    * Math.PI
                    / 2.0
                );
        }
        else
        {
            factor =
                1.0 / (
                    1.0
                    + 0.35
                    * (normalizedSlip - 1.0)
                );
        }

        return maximumForce
            * clamp(factor, 0, 1);
    }

    // Lateral force available at a given slip angle.
    public double getLateralForce(
        double normalForce,
        double slipAngle
    )
    {
        double maximumForce =
            getMaximumLateralForce(normalForce);

        if (maximumForce <= 0)
        {
            return 0;
        }

        double angle =
            Math.abs(slipAngle);

        double normalizedAngle =
            angle / slipAnglePeak;

        double factor;

        if (normalizedAngle <= 1.0)
        {
            factor =
                Math.sin(
                    normalizedAngle
                    * Math.PI
                    / 2.0
                );
        }
        else
        {
            factor =
                1.0 / (
                    1.0
                    + 0.30
                    * (normalizedAngle - 1.0)
                );
        }

        return maximumForce
            * clamp(factor, 0, 1);
    }

    // Friction-circle approximation for combined braking/traction and cornering.
    public double getCombinedGripLimit(
        double normalForce,
        double longitudinalDemand,
        double lateralDemand
    )
    {
        double longitudinalLimit =
            getMaximumLongitudinalForce(normalForce);

        double lateralLimit =
            getMaximumLateralForce(normalForce);

        if (longitudinalLimit <= 0
            || lateralLimit <= 0)
        {
            return 0;
        }

        double longitudinalRatio =
            longitudinalDemand / longitudinalLimit;

        double lateralRatio =
            lateralDemand / lateralLimit;

        double utilisation =
            Math.sqrt(
                longitudinalRatio
                * longitudinalRatio
                +
                lateralRatio
                * lateralRatio
            );

        if (utilisation <= 1.0)
        {
            return Math.min(
                longitudinalLimit,
                lateralLimit
            );
        }

        return 1.0 / utilisation;
    }

    // Acceleration available from longitudinal tyre grip.
    public double getMaximumAcceleration(
        double normalForce,
        double vehicleMass
    )
    {
        if (vehicleMass <= 0)
        {
            return 0;
        }

        return getMaximumLongitudinalForce(
            normalForce
        ) / vehicleMass;
    }

    // Basic rolling resistance force.
    public double getRollingResistance(
        double normalForce
    )
    {
        if (normalForce <= 0)
        {
            return 0;
        }

        return rollingResistanceCoefficient
            * normalForce;
    }

    // Updates tyre temperature from a simplified heat model.
    public void updateTemperature(
        double speed,
        double slipRatio,
        double slipAngle,
        double timeStep
    )
    {
        if (timeStep <= 0)
        {
            return;
        }

        double slipEnergy =
            (
                Math.abs(slipRatio)
                + Math.abs(slipAngle)
            )
            * speed;

        double heatInput =
            slipEnergy
            * 0.08
            * timeStep;

        double cooling =
            (
                temperature
                - 25.0
            )
            * 0.015
            * timeStep;

        temperature +=
            heatInput - cooling;

        temperature =
            clamp(
                temperature,
                25.0,
                180.0
            );
    }

    // Updates tyre wear based on slip and time.
    public void updateWear(
        double slipRatio,
        double slipAngle,
        double timeStep
    )
    {
        if (timeStep <= 0)
        {
            return;
        }

        double stress =
            Math.abs(slipRatio)
            + Math.abs(slipAngle);

        double wearRate =
            stress
            * 0.00002
            * timeStep;

        wear += wearRate;

        wear =
            clamp(wear, 0, 1);
    }

    private double clamp(
        double value,
        double minimum,
        double maximum
    )
    {
        return Math.max(
            minimum,
            Math.min(maximum, value)
        );
    }
}