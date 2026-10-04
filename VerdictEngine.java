// RaceSpec - Verdict Engine V2
// Produces a circuit-specific engineering verdict.

public class VerdictEngine
{
    private VehicleComparator comparator;
    private Circuit circuit;

    public VerdictEngine(
        VehicleComparator vc,
        Circuit c
    )
    {
        if (vc == null || c == null)
        {
            throw new IllegalArgumentException(
                "Comparator and circuit cannot be null."
            );
        }

        comparator = vc;
        circuit = c;
    }

    // Calculates a basic performance score before lap simulation.
    public double calculatePerformanceScore()
    {
        double velocity = 60.0;

        double score = 0.0;

        // Straight-line performance.
        score +=
            comparator.getPowerToWeightDifference()
            * 0.25;

        score +=
            comparator.getTopSpeedDifference()
            * 0.50;

        // Cornering performance.
        score +=
            comparator.getGripDifference(velocity)
            / 500.0
            * 0.15;

        score +=
            comparator.getLateralAccelerationDifference(
                velocity
            )
            * 0.50;

        // Braking performance.
        score +=
            comparator.getBrakingForceDifference(
                velocity
            )
            / 1000.0
            * 0.10;

        // Track character adjustment.
        if (
            circuit.getCircuitType()
                .equals("High Speed")
        )
        {
            score +=
                comparator.getTopSpeedDifference()
                * 0.20;
        }
        else if (
            circuit.getCircuitType()
                .equals("Technical")
        )
        {
            score +=
                comparator.getLateralAccelerationDifference(
                    velocity
                )
                * 0.20;
        }

        return score;
    }

    // Returns the preliminary winner.
    public String getPreliminaryWinner()
    {
        double score =
            calculatePerformanceScore();

        if (score > 0)
        {
            return "Dream Car";
        }

        if (score < 0)
        {
            return "MCL39";
        }

        return "Equal";
    }

    // Final verdict based directly on simulated lap times.
    public String getLapVerdict(
        LapResult mcl39Result,
        LapResult dreamResult
    )
    {
        if (
            mcl39Result == null
            || dreamResult == null
        )
        {
            throw new IllegalArgumentException(
                "Lap results cannot be null."
            );
        }

        if (
            !mcl39Result.isLapCompleted()
            || !dreamResult.isLapCompleted()
        )
        {
            return "No valid verdict - lap simulation incomplete.";
        }

        double mcl39Time =
            mcl39Result.getLapTime();

        double dreamTime =
            dreamResult.getLapTime();

        if (dreamTime < mcl39Time)
        {
            return "Dream Car";
        }

        if (mcl39Time < dreamTime)
        {
            return "MCL39";
        }

        return "Equal";
    }

    // Calculates the Dream Car's lap-time advantage.
    // Positive value means the Dream Car is faster.
    public double getLapTimeAdvantage(
        LapResult mcl39Result,
        LapResult dreamResult
    )
    {
        if (
            mcl39Result == null
            || dreamResult == null
        )
        {
            throw new IllegalArgumentException(
                "Lap results cannot be null."
            );
        }

        return
            mcl39Result.getLapTime()
            - dreamResult.getLapTime();
    }

    // Percentage improvement of Dream Car over the MCL39.
    public double getLapTimeImprovement(
        LapResult mcl39Result,
        LapResult dreamResult
    )
    {
        double mcl39Time =
            mcl39Result.getLapTime();

        if (mcl39Time <= 0)
        {
            return 0;
        }

        return
            (
                mcl39Time
                - dreamResult.getLapTime()
            )
            / mcl39Time
            * 100.0;
    }

    // Returns the final performance margin.
    public double getPerformanceMargin(
        LapResult mcl39Result,
        LapResult dreamResult
    )
    {
        return Math.abs(
            getLapTimeImprovement(
                mcl39Result,
                dreamResult
            )
        );
    }

    // Final table-based verdict.
    public void printFinalVerdict(
        LapResult mcl39Result,
        LapResult dreamResult
    )
    {
        String winner =
            getLapVerdict(
                mcl39Result,
                dreamResult
            );

        double advantage =
            getLapTimeAdvantage(
                mcl39Result,
                dreamResult
            );

        double improvement =
            getLapTimeImprovement(
                mcl39Result,
                dreamResult
            );

        System.out.println();
        System.out.println(
            "==============================================================="
        );
        System.out.println(
            "                    FINAL CIRCUIT VERDICT"
        );
        System.out.println(
            "==============================================================="
        );

        System.out.println();

        System.out.println(
            "Circuit: "
            + circuit.getName()
        );

        System.out.println();

        System.out.printf(
            "%-27s %-18s %-18s %-12s%n",
            "Metric",
            "McLaren MCL39",
            "Dream Car",
            "Winner"
        );

        System.out.println(
            "---------------------------------------------------------------"
        );

        System.out.printf(
            "%-27s %-18.2f %-18.2f %-12s%n",
            "Lap Time (s)",
            mcl39Result.getLapTime(),
            dreamResult.getLapTime(),
            getLowerWinner(
                mcl39Result.getLapTime(),
                dreamResult.getLapTime()
            )
        );

        System.out.printf(
            "%-27s %-18.2f %-18.2f %-12s%n",
            "Maximum Speed (km/h)",
            mcl39Result.getMaximumVelocity() * 3.6,
            dreamResult.getMaximumVelocity() * 3.6,
            getHigherWinner(
                mcl39Result.getMaximumVelocity(),
                dreamResult.getMaximumVelocity()
            )
        );

        System.out.printf(
            "%-27s %-18.2f %-18.2f %-12s%n",
            "Average Speed (km/h)",
            mcl39Result.getAverageVelocity() * 3.6,
            dreamResult.getAverageVelocity() * 3.6,
            getHigherWinner(
                mcl39Result.getAverageVelocity(),
                dreamResult.getAverageVelocity()
            )
        );

        System.out.println(
            "---------------------------------------------------------------"
        );

        System.out.printf(
            "%-27s %s%n",
            "FINAL WINNER",
            winner
        );

        System.out.printf(
            "%-27s %.3f s%n",
            "Dream Car Lap Advantage",
            advantage
        );

        System.out.printf(
            "%-27s %.2f%%%n",
            "Dream Car Improvement",
            improvement
        );

        System.out.println(
            "==============================================================="
        );
    }

    // Compatibility method for the current RaceSpecTest.
    public double calculateScore()
    {
        return calculatePerformanceScore();
    }

    // Compatibility method for the current RaceSpecTest.
    public boolean isDreamCarAhead()
    {
        return calculatePerformanceScore() > 0;
    }

    // Compatibility method for the current RaceSpecTest.
    public String getVerdict()
    {
        double score =
            calculatePerformanceScore();

        if (score > 0)
        {
            return "Dream Car has the higher predicted performance.";
        }

        if (score < 0)
        {
            return "McLaren MCL39 has the higher predicted performance.";
        }

        return "The two vehicles have equal predicted performance.";
    }

    // Compatibility method for the current RaceSpecTest.
    public void printVerdict()
    {
        System.out.println(
            "=== RaceSpec Verdict ==="
        );

        System.out.println();

        System.out.println(
            "Circuit: "
            + circuit.getName()
        );

        System.out.println(
            "Score: "
            + calculatePerformanceScore()
        );

        System.out.println();

        System.out.println(
            "Verdict: "
            + getVerdict()
        );
    }

    private String getHigherWinner(
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

    private String getLowerWinner(
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

    public Circuit getCircuit()
    {
        return circuit;
    }

    public VehicleComparator getComparator()
    {
        return comparator;
    }
}