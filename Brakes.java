// RaceSpec - Brake Dynamics Model V2
// Models brake hardware, weight transfer and tyre-limited braking.

public class Brakes
{
    private double maximumBrakeTorque;
    private double brakingEfficiency;
    private double frontBrakeBias;

    private double maximumBrakeTemperature;
    private double brakeTemperature;
    private double temperatureSensitivity;

    private Vehicle vehicle;
    private Tyre tyre;
    private Aerodynamics aerodynamics;

    public Brakes(
        double torque,
        double efficiency,
        double bias
    )
    {
        if (torque <= 0)
        {
            throw new IllegalArgumentException(
                "Brake torque must be greater than zero."
            );
        }

        if (efficiency <= 0 || efficiency > 1)
        {
            throw new IllegalArgumentException(
                "Brake efficiency must be between 0 and 1."
            );
        }

        if (bias <= 0 || bias >= 1)
        {
            throw new IllegalArgumentException(
                "Brake bias must be between 0 and 1."
            );
        }

        maximumBrakeTorque = torque;
        brakingEfficiency = efficiency;
        frontBrakeBias = bias;

        maximumBrakeTemperature = 900.0;
        brakeTemperature = 100.0;
        temperatureSensitivity = 0.25;
    }

    public void setDynamics(
        Vehicle v,
        Tyre t,
        Aerodynamics aero
    )
    {
        vehicle = v;
        tyre = t;
        aerodynamics = aero;
    }

    public double getMaximumBrakeTorque()
    {
        return maximumBrakeTorque;
    }

    public double getBrakingEfficiency()
    {
        return brakingEfficiency;
    }

    public double getFrontBrakeBias()
    {
        return frontBrakeBias;
    }

    public double getBrakeTemperature()
    {
        return brakeTemperature;
    }

    public double getMaximumBrakeTemperature()
    {
        return maximumBrakeTemperature;
    }

    // Brake temperature reduces usable braking efficiency when overheated.
    public double getTemperatureFactor()
    {
        if (brakeTemperature <= 600.0)
        {
            return 1.0;
        }

        double overheating =
            (brakeTemperature - 600.0)
            / (maximumBrakeTemperature - 600.0);

        return Math.max(
            0.55,
            1.0 - temperatureSensitivity * overheating
        );
    }

    // Hardware-limited total braking force.
    public double getMaximumBrakingForce(
        double wheelRadius
    )
    {
        if (wheelRadius <= 0)
        {
            return 0;
        }

        double effectiveEfficiency =
            brakingEfficiency
            * getTemperatureFactor();

        return (
            maximumBrakeTorque
            * effectiveEfficiency
        ) / wheelRadius;
    }

    public double getFrontBrakingForce(
        double wheelRadius
    )
    {
        return getMaximumBrakingForce(wheelRadius)
            * frontBrakeBias;
    }

    public double getRearBrakingForce(
        double wheelRadius
    )
    {
        return getMaximumBrakingForce(wheelRadius)
            * (1.0 - frontBrakeBias);
    }

    // Calculates dynamic front axle load during braking.
    public double getFrontNormalForce(
        double velocity,
        double deceleration
    )
    {
        if (vehicle == null)
        {
            return 0;
        }

        double aeroDownforce = 0;

        if (aerodynamics != null)
        {
            aeroDownforce =
                aerodynamics.getDownforce(velocity);
        }

        double loadTransfer =
            vehicle.getLongitudinalLoadTransfer(
                -Math.abs(deceleration)
            );

        return vehicle.getStaticFrontAxleLoad()
            + aeroDownforce
            * 0.45
            - loadTransfer;
    }

    // Calculates dynamic rear axle load during braking.
    public double getRearNormalForce(
        double velocity,
        double deceleration
    )
    {
        if (vehicle == null)
        {
            return 0;
        }

        double aeroDownforce = 0;

        if (aerodynamics != null)
        {
            aeroDownforce =
                aerodynamics.getDownforce(velocity);
        }

        double loadTransfer =
            vehicle.getLongitudinalLoadTransfer(
                -Math.abs(deceleration)
            );

        return vehicle.getStaticRearAxleLoad()
            + aeroDownforce
            * 0.55
            + loadTransfer;
    }

    // Finds the maximum tyre-limited braking force.
    public double getTyreLimitedBrakingForce(
        double velocity,
        double deceleration
    )
    {
        if (vehicle == null || tyre == null)
        {
            return 0;
        }

        double frontLoad =
            Math.max(
                0,
                getFrontNormalForce(
                    velocity,
                    deceleration
                )
            );

        double rearLoad =
            Math.max(
                0,
                getRearNormalForce(
                    velocity,
                    deceleration
                )
            );

        double frontGrip =
            tyre.getMaximumLongitudinalForce(
                frontLoad
            );

        double rearGrip =
            tyre.getMaximumLongitudinalForce(
                rearLoad
            );

        return frontGrip + rearGrip;
    }

    // Returns the actual braking force after hardware and tyre limits.
    public double getActualBrakingForce(
        double wheelRadius,
        double velocity,
        double deceleration
    )
    {
        double hardwareLimit =
            getMaximumBrakingForce(
                wheelRadius
            );

        if (vehicle == null || tyre == null)
        {
            return hardwareLimit;
        }

        double tyreLimit =
            getTyreLimitedBrakingForce(
                velocity,
                deceleration
            );

        return Math.min(
            hardwareLimit,
            tyreLimit
        );
    }

    // Solves braking acceleration using an iterative load-transfer model.
    public double getBrakingAcceleration(
        double wheelRadius,
        double vehicleMass
    )
    {
        if (wheelRadius <= 0 || vehicleMass <= 0)
        {
            return 0;
        }

        double hardwareForce =
            getMaximumBrakingForce(
                wheelRadius
            );

        if (vehicle == null || tyre == null)
        {
            return hardwareForce / vehicleMass;
        }

        double acceleration =
            hardwareForce / vehicleMass;

        for (int i = 0; i < 8; i++)
        {
            double tyreForce =
                getTyreLimitedBrakingForce(
                    acceleration > 0 ? 0 : 0,
                    -acceleration
                );

            double actualForce =
                Math.min(
                    hardwareForce,
                    tyreForce
                );

            acceleration =
                actualForce / vehicleMass;
        }

        return acceleration;
    }

    // Improved braking calculation using a specified vehicle speed.
    public double getBrakingAcceleration(
        double wheelRadius,
        double vehicleMass,
        double velocity
    )
    {
        if (
            wheelRadius <= 0
            || vehicleMass <= 0
        )
        {
            return 0;
        }

        double hardwareForce =
            getMaximumBrakingForce(
                wheelRadius
            );

        if (vehicle == null || tyre == null)
        {
            return hardwareForce / vehicleMass;
        }

        double acceleration =
            hardwareForce / vehicleMass;

        for (int i = 0; i < 10; i++)
        {
            double frontLoad =
                Math.max(
                    0,
                    getFrontNormalForce(
                        velocity,
                        -acceleration
                    )
                );

            double rearLoad =
                Math.max(
                    0,
                    getRearNormalForce(
                        velocity,
                        -acceleration
                    )
                );

            double frontGrip =
                tyre.getMaximumLongitudinalForce(
                    frontLoad
                );

            double rearGrip =
                tyre.getMaximumLongitudinalForce(
                    rearLoad
                );

            double tyreForce =
                frontGrip + rearGrip;

            double actualForce =
                Math.min(
                    hardwareForce,
                    tyreForce
                );

            double newAcceleration =
                actualForce / vehicleMass;

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

        return acceleration;
    }

    public double getStoppingDistance(
        double wheelRadius,
        double vehicleMass,
        double velocity
    )
    {
        if (
            wheelRadius <= 0
            || vehicleMass <= 0
            || velocity <= 0
        )
        {
            return 0;
        }

        double acceleration =
            getBrakingAcceleration(
                wheelRadius,
                vehicleMass,
                velocity
            );

        if (acceleration <= 0)
        {
            return 0;
        }

        return (
            velocity * velocity
        ) / (
            2.0 * acceleration
        );
    }

    // Updates brake temperature from braking work.
    public void updateTemperature(
        double brakingForce,
        double velocity,
        double timeStep
    )
    {
        if (timeStep <= 0)
        {
            return;
        }

        double heatInput =
            Math.abs(
                brakingForce
                * velocity
            )
            * 0.000003
            * timeStep;

        double cooling =
            (
                brakeTemperature
                - 25.0
            )
            * 0.02
            * timeStep;

        brakeTemperature +=
            heatInput - cooling;

        brakeTemperature =
            Math.max(
                25.0,
                Math.min(
                    maximumBrakeTemperature,
                    brakeTemperature
                )
            );
    }

    public void resetTemperature()
    {
        brakeTemperature = 100.0;
    }
}
