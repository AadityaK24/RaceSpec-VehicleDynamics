// RaceSpec - Vehicle Comparator V2
// Compares the McLaren MCL39 against the RaceSpec Dream Car.

public class VehicleComparator
{
    private PerformanceCalculator referenceCar;
    private PerformanceCalculator dreamCar;

    public VehicleComparator(
        PerformanceCalculator reference,
        PerformanceCalculator dream
    )
    {
        if (
            reference == null
            || dream == null
        )
        {
            throw new IllegalArgumentException(
                "Vehicle performance calculators cannot be null."
            );
        }

        referenceCar = reference;
        dreamCar = dream;
    }

    // =====================================================
    // BASIC DIFFERENCES
    // =====================================================

    public double getMassDifference()
    {
        return dreamCar.getVehicle().getMass()
            - referenceCar.getVehicle().getMass();
    }

    public double getPowerDifference()
    {
        return
            dreamCar.getEngine().getMaximumPower()
            - referenceCar.getEngine().getMaximumPower();
    }

    public double getTorqueDifference()
    {
        return
            dreamCar.getEngine().getMaximumTorque()
            - referenceCar.getEngine().getMaximumTorque();
    }

    public double getPowerToWeightDifference()
    {
        return
            dreamCar.getPowerToWeight()
            - referenceCar.getPowerToWeight();
    }

    public double getTopSpeedDifference()
    {
        return
            dreamCar.getEstimatedTopSpeed()
            - referenceCar.getEstimatedTopSpeed();
    }

    public double getGripDifference(
        double velocity
    )
    {
        return
            dreamCar.getMaximumGripForce(velocity)
            - referenceCar.getMaximumGripForce(velocity);
    }

    public double getDragDifference(
        double velocity
    )
    {
        return
            dreamCar.getAerodynamicDrag(velocity)
            - referenceCar.getAerodynamicDrag(velocity);
    }

    public double getDownforceDifference(
        double velocity
    )
    {
        return
            dreamCar.getDownforce(velocity)
            - referenceCar.getDownforce(velocity);
    }

    public double getBrakingForceDifference(
        double velocity
    )
    {
        return
            dreamCar.getMaximumBrakingForce(velocity)
            - referenceCar.getMaximumBrakingForce(velocity);
    }

    public double getLateralAccelerationDifference(
        double velocity
    )
    {
        return
            dreamCar.getMaximumLateralAcceleration(velocity)
            - referenceCar.getMaximumLateralAcceleration(velocity);
    }

    public double getCorneringSpeedDifference(
        double radius,
        double velocity
    )
    {
        return
            dreamCar.getMaximumCorneringSpeed(
                radius,
                velocity
            )
            - referenceCar.getMaximumCorneringSpeed(
                radius,
                velocity
            );
    }

    // =====================================================
    // WINNER FUNCTIONS
    // =====================================================

    private String higherIsBetter(
        double reference,
        double dream
    )
    {
        if (dream > reference)
        {
            return "Dream Car";
        }

        if (reference > dream)
        {
            return "MCL39";
        }

        return "Equal";
    }

    private String lowerIsBetter(
        double reference,
        double dream
    )
    {
        if (dream < reference)
        {
            return "Dream Car";
        }

        if (reference < dream)
        {
            return "MCL39";
        }

        return "Equal";
    }

    // =====================================================
    // TABLE OUTPUT
    // =====================================================

    public void printComparisonTable(
        double velocity
    )
    {
        System.out.println();
        System.out.println(
            "==============================================================="
        );

        System.out.println(
            "                  VEHICLE DYNAMICS COMPARISON"
        );

        System.out.println(
            "==============================================================="
        );

        System.out.println();

        printSpecificationTable();

        System.out.println();

        printDynamicTable(
            velocity
        );
    }

    private void printSpecificationTable()
    {
        String referenceName =
            referenceCar.getVehicle().getName();

        String dreamName =
            dreamCar.getVehicle().getName();

        System.out.println(
            "VEHICLE SPECIFICATIONS"
        );

        System.out.println(
            "---------------------------------------------------------------"
        );

        System.out.printf(
            "%-27s %-18s %-18s %-12s%n",
            "Parameter",
            referenceName,
            dreamName,
            "Winner"
        );

        System.out.println(
            "---------------------------------------------------------------"
        );

        printLowerRow(
                  "Mass (kg)",
                   referenceCar.getVehicle().getMass(),
                    dreamCar.getVehicle().getMass()
        );
        

        printHigherRow(
            "Power (kW)",
            referenceCar.getEngine().getMaximumPower(),
            dreamCar.getEngine().getMaximumPower()
        );

        printHigherRow(
            "Torque (Nm)",
            referenceCar.getEngine().getMaximumTorque(),
            dreamCar.getEngine().getMaximumTorque()
        );

        printHigherRow(
            "Power-to-Weight",
            referenceCar.getPowerToWeight(),
            dreamCar.getPowerToWeight()
        );

        printHigherRow(
            "Top Speed (km/h)",
            referenceCar.getEstimatedTopSpeed(),
            dreamCar.getEstimatedTopSpeed()
        );

        System.out.println(
            "---------------------------------------------------------------"
        );
    }

    private void printDynamicTable(
        double velocity
    )
    {
        System.out.println(
            "DYNAMIC PERFORMANCE @ "
            + velocity
            + " m/s"
        );

        System.out.println(
            "---------------------------------------------------------------"
        );

        System.out.printf(
            "%-27s %-18s %-18s %-12s%n",
            "Parameter",
            "McLaren MCL39",
            "Dream Car",
            "Winner"
        );

        System.out.println(
            "---------------------------------------------------------------"
        );

        printHigherRow(
            "Downforce (N)",
            referenceCar.getDownforce(velocity),
            dreamCar.getDownforce(velocity)
        );

        printLowerRow(
            "Drag (N)",
            referenceCar.getAerodynamicDrag(velocity),
            dreamCar.getAerodynamicDrag(velocity)
        );

        printHigherRow(
            "Tyre Grip (N)",
            referenceCar.getMaximumGripForce(velocity),
            dreamCar.getMaximumGripForce(velocity)
        );

        printHigherRow(
            "Braking Force (N)",
            referenceCar.getMaximumBrakingForce(velocity),
            dreamCar.getMaximumBrakingForce(velocity)
        );

        printHigherRow(
            "Lateral Accel (m/s^2)",
            referenceCar.getMaximumLateralAcceleration(
                velocity
            ),
            dreamCar.getMaximumLateralAcceleration(
                velocity
            )
        );

        System.out.println(
            "---------------------------------------------------------------"
        );
    }

    private void printHigherRow(
        String parameter,
        double reference,
        double dream
    )
    {
        System.out.printf(
            "%-27s %-18.2f %-18.2f %-12s%n",
            parameter,
            reference,
            dream,
            higherIsBetter(
                reference,
                dream
            )
        );
    }

    private void printLowerRow(
        String parameter,
        double reference,
        double dream
    )
    {
        System.out.printf(
            "%-27s %-18.2f %-18.2f %-12s%n",
            parameter,
            reference,
            dream,
            lowerIsBetter(
                reference,
                dream
            )
        );
    }

    // =====================================================
    // LAP COMPARISON
    // =====================================================

    public void printLapComparison(
        LapResult referenceResult,
        LapResult dreamResult
    )
    {
        if (
            referenceResult == null
            || dreamResult == null
        )
        {
            throw new IllegalArgumentException(
                "Lap results cannot be null."
            );
        }

        System.out.println();
        System.out.println(
            "LAP PERFORMANCE"
        );

        System.out.println(
            "---------------------------------------------------------------"
        );

        System.out.printf(
            "%-27s %-18s %-18s %-12s%n",
            "Parameter",
            "McLaren MCL39",
            "Dream Car",
            "Winner"
        );

        System.out.println(
            "---------------------------------------------------------------"
        );

        printLowerRow(
            "Lap Time (s)",
            referenceResult.getLapTime(),
            dreamResult.getLapTime()
        );

        printHigherRow(
            "Maximum Speed (km/h)",
            referenceResult.getMaximumVelocity() * 3.6,
            dreamResult.getMaximumVelocity() * 3.6
        );

        printHigherRow(
            "Average Speed (km/h)",
            referenceResult.getAverageVelocity() * 3.6,
            dreamResult.getAverageVelocity() * 3.6
        );

        printHigherRow(
            "Distance (m)",
            referenceResult.getDistanceTravelled(),
            dreamResult.getDistanceTravelled()
        );

        System.out.println(
            "---------------------------------------------------------------"
        );
    }

    // =====================================================
    // LAP DIFFERENCES
    // =====================================================

    public double getLapTimeDifference(
        LapResult referenceResult,
        LapResult dreamResult
    )
    {
        return
            dreamResult.getLapTime()
            - referenceResult.getLapTime();
    }

    public double getMaximumSpeedDifference(
        LapResult referenceResult,
        LapResult dreamResult
    )
    {
        return
            dreamResult.getMaximumVelocity()
            - referenceResult.getMaximumVelocity();
    }

    public double getAverageSpeedDifference(
        LapResult referenceResult,
        LapResult dreamResult
    )
    {
        return
            dreamResult.getAverageVelocity()
            - referenceResult.getAverageVelocity();
    }

    // Positive percentage means the Dream Car is faster.
    public double getLapTimeImprovement(
        LapResult referenceResult,
        LapResult dreamResult
    )
    {
        double referenceTime =
            referenceResult.getLapTime();

        if (referenceTime <= 0)
        {
            return 0;
        }

        return
            (
                referenceTime
                - dreamResult.getLapTime()
            )
            / referenceTime
            * 100.0;
    }

    public String getLapWinner(
        LapResult referenceResult,
        LapResult dreamResult
    )
    {
        return lowerIsBetter(
            referenceResult.getLapTime(),
            dreamResult.getLapTime()
        );
    }

    // =====================================================
    // ACCESS
    // =====================================================

    public PerformanceCalculator getReferenceCar()
    {
        return referenceCar;
    }

    public PerformanceCalculator getDreamCar()
    {
        return dreamCar;
    }
    public void printComparison(double velocity)
{
    printComparisonTable(velocity);
}
}