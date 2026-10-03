// RaceSpec - Verdict Engine
// Produces a basic circuit-specific comparison result.

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

    // Produces a basic circuit-specific performance score.
    public double calculateScore()
    {
        double velocity = 60.0;

        double powerDifference =
            comparator.getPowerDifference();

        double powerToWeightDifference =
            comparator.getPowerToWeightDifference();

        double topSpeedDifference =
            comparator.getTopSpeedDifference();

        double gripDifference =
            comparator.getGripDifference(velocity);

        double brakingDifference =
            comparator.getBrakingForceDifference(velocity);

        double corneringDifference =
            comparator.getLateralAccelerationDifference(velocity);

        double score = 0.0;

        // High-speed circuits place more emphasis on power and top speed.
        if (circuit.getLongestStraightLength() >= 1.5)
        {
            score += powerDifference * 0.20;
            score += powerToWeightDifference * 0.30;
            score += topSpeedDifference * 0.30;
            score += gripDifference / 5000.0 * 0.05;
            score += brakingDifference / 5000.0 * 0.05;
            score += corneringDifference * 0.10;
        }
        // Tight circuits place more emphasis on braking and cornering.
        else if (circuit.getAverageCornerRadius() <= 110.0)
        {
            score += powerDifference * 0.10;
            score += powerToWeightDifference * 0.10;
            score += topSpeedDifference * 0.10;
            score += gripDifference / 5000.0 * 0.20;
            score += brakingDifference / 5000.0 * 0.25;
            score += corneringDifference * 0.25;
        }
        // Balanced circuits use a more even weighting.
        else
        {
            score += powerDifference * 0.15;
            score += powerToWeightDifference * 0.20;
            score += topSpeedDifference * 0.15;
            score += gripDifference / 5000.0 * 0.15;
            score += brakingDifference / 5000.0 * 0.15;
            score += corneringDifference * 0.20;
        }

        return score;
    }

    // Determines whether the Dream Car has the higher basic score.
    public boolean isDreamCarAhead()
    {
        return calculateScore() > 0;
    }

    // Returns the basic comparison verdict.
    public String getVerdict()
    {
        double score = calculateScore();

        if (score > 0)
        {
            return "Dream Car has the higher predicted performance.";
        }
        else if (score < 0)
        {
            return "McLaren MCL39 has the higher predicted performance.";
        }

        return "The two vehicles have equal predicted performance.";
    }

    // Returns the circuit used for the verdict.
    public Circuit getCircuit()
    {
        return circuit;
    }

    // Returns the underlying comparator.
    public VehicleComparator getComparator()
    {
        return comparator;
    }

    // Displays the current verdict.
    public void printVerdict()
    {
        System.out.println("=== RaceSpec Verdict ===");
        System.out.println();

        System.out.println(
            "Circuit: " + circuit.getName()
        );

        System.out.println(
            "Score: " + calculateScore()
        );

        System.out.println();

        System.out.println(
            "Verdict: " + getVerdict()
        );
    }
}