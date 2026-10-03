// RaceSpec - Vehicle Comparator
// Compares the McLaren reference car against the RaceSpec Dream Car.

public class VehicleComparator
{
    private PerformanceCalculator referenceCar;
    private PerformanceCalculator dreamCar;

    public VehicleComparator(
        PerformanceCalculator reference,
        PerformanceCalculator dream
    )
    {
        if (reference == null || dream == null)
        {
            throw new IllegalArgumentException(
                "Vehicle performance calculators cannot be null."
            );
        }

        referenceCar = reference;
        dreamCar = dream;
    }

    // Compares maximum engine power.
    public double getPowerDifference()
    {
        return dreamCar.getEngine().getMaximumPower()
                - referenceCar.getEngine().getMaximumPower();
    }

    // Compares maximum engine torque.
    public double getTorqueDifference()
    {
        return dreamCar.getEngine().getMaximumTorque()
                - referenceCar.getEngine().getMaximumTorque();
    }

    // Compares power-to-weight ratio.
    public double getPowerToWeightDifference()
    {
        return dreamCar.getPowerToWeight()
                - referenceCar.getPowerToWeight();
    }

    // Compares estimated top speed.
    public double getTopSpeedDifference()
    {
        return dreamCar.getEstimatedTopSpeed()
                - referenceCar.getEstimatedTopSpeed();
    }

    // Compares maximum tyre grip at a selected velocity.
    public double getGripDifference(double velocity)
    {
        return dreamCar.getMaximumGripForce(velocity)
                - referenceCar.getMaximumGripForce(velocity);
    }

    // Compares aerodynamic drag at a selected velocity.
    public double getDragDifference(double velocity)
    {
        return dreamCar.getAerodynamicDrag(velocity)
                - referenceCar.getAerodynamicDrag(velocity);
    }

    // Compares aerodynamic downforce at a selected velocity.
    public double getDownforceDifference(double velocity)
    {
        return dreamCar.getDownforce(velocity)
                - referenceCar.getDownforce(velocity);
    }

    // Compares maximum braking force at a selected velocity.
    public double getBrakingForceDifference(double velocity)
    {
        return dreamCar.getMaximumBrakingForce(velocity)
                - referenceCar.getMaximumBrakingForce(velocity);
    }

    // Compares maximum lateral acceleration at a selected velocity.
    public double getLateralAccelerationDifference(double velocity)
    {
        return dreamCar.getMaximumLateralAcceleration(velocity)
                - referenceCar.getMaximumLateralAcceleration(velocity);
    }

    // Compares maximum cornering speed for a selected corner radius.
    public double getCorneringSpeedDifference(
        double radius,
        double velocity
    )
    {
        return dreamCar.getMaximumCorneringSpeed(
                    radius,
                    velocity
               )
                - referenceCar.getMaximumCorneringSpeed(
                    radius,
                    velocity
                  );
    }

    // Displays the current basic comparison.
    public void printComparison(double velocity)
    {
        System.out.println("=== RaceSpec Vehicle Comparison ===");
        System.out.println();

        System.out.println("REFERENCE CAR");
        System.out.println(
            "Name: " + referenceCar.getVehicle().getName()
        );

        System.out.println();
        System.out.println("DREAM CAR");
        System.out.println(
            "Name: " + dreamCar.getVehicle().getName()
        );

        System.out.println();

        System.out.println("PERFORMANCE DIFFERENCES");
        System.out.println(
            "Power Difference: "
            + getPowerDifference() + " kW"
        );

        System.out.println(
            "Torque Difference: "
            + getTorqueDifference() + " Nm"
        );

        System.out.println(
            "Power-to-Weight Difference: "
            + getPowerToWeightDifference()
            + " kW/tonne"
        );

        System.out.println(
            "Top Speed Difference: "
            + getTopSpeedDifference()
            + " km/h"
        );

        System.out.println();

        System.out.println("AT " + velocity + " m/s");

        System.out.println(
            "Grip Difference: "
            + getGripDifference(velocity)
            + " N"
        );

        System.out.println(
            "Drag Difference: "
            + getDragDifference(velocity)
            + " N"
        );

        System.out.println(
            "Downforce Difference: "
            + getDownforceDifference(velocity)
            + " N"
        );

        System.out.println(
            "Braking Force Difference: "
            + getBrakingForceDifference(velocity)
            + " N"
        );

        System.out.println(
            "Lateral Acceleration Difference: "
            + getLateralAccelerationDifference(velocity)
            + " m/s^2"
        );
    }

    public PerformanceCalculator getReferenceCar()
    {
        return referenceCar;
    }

    public PerformanceCalculator getDreamCar()
    {
        return dreamCar;
    }
}