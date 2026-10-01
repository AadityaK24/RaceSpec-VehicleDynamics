/*
 * RaceSpec - Vehicle Dynamics & Performance Analysis
 *
 * Circuit
 * -------
 * Stores the simplified engineering characteristics of a
 * racing circuit used by the RaceSpec simulation.
 *
 * The user does not enter these values manually.
 * Main.java will allow the user to select a circuit from
 * a dropdown menu, after which this class loads the
 * corresponding circuit data.
 *
 * Note:
 * The circuit model is intentionally simplified so that
 * the assumptions and calculations remain understandable
 * and reproducible.
 */

public class Circuit
{
    // Basic circuit identification
    private String name;

    // Track length in kilometres
    private double trackLength;

    // Total length of straight sections in kilometres
    private double totalStraightLength;

    // Longest individual straight section in kilometres
    private double longestStraightLength;

    // Number of significant braking zones
    private int numberOfBrakingZones;

    // Total number of corners represented by the model
    private int numberOfCorners;

    // Simplified average corner radius in metres
    private double averageCornerRadius;


    /*
     * Constructor
     *
     * Only the circuit name is required.
     * The remaining engineering characteristics are
     * loaded automatically from the circuit database
     * contained within this class.
     */
    public Circuit(String circuitName)
    {
        if (circuitName == null || circuitName.trim().isEmpty())
        {
            throw new IllegalArgumentException(
                "Circuit name cannot be empty."
            );
        }

        loadCircuit(circuitName.trim());
    }


    /*
     * Loads the predefined engineering characteristics
     * for the selected circuit.
     *
     * RaceSpec uses the same circuit model for both the
     * Dream Car and the McLaren reference configuration,
     * allowing a direct comparison under identical conditions.
     */
    private void loadCircuit(String circuitName)
    {
        switch (circuitName)
        {
            case "Monza":
                name = "Monza";
                trackLength = 5.793;
                totalStraightLength = 3.6;
                longestStraightLength = 1.12;
                numberOfBrakingZones = 6;
                numberOfCorners = 11;
                averageCornerRadius = 180.0;
                break;


            case "Monaco":
                name = "Monaco";
                trackLength = 3.337;
                totalStraightLength = 1.1;
                longestStraightLength = 0.67;
                numberOfBrakingZones = 8;
                numberOfCorners = 19;
                averageCornerRadius = 95.0;
                break;


            case "Silverstone":
                name = "Silverstone";
                trackLength = 5.891;
                totalStraightLength = 2.7;
                longestStraightLength = 0.77;
                numberOfBrakingZones = 6;
                numberOfCorners = 18;
                averageCornerRadius = 160.0;
                break;


            case "Suzuka":
                name = "Suzuka";
                trackLength = 5.807;
                totalStraightLength = 2.3;
                longestStraightLength = 0.80;
                numberOfBrakingZones = 7;
                numberOfCorners = 18;
                averageCornerRadius = 140.0;
                break;


            case "Spa":
                name = "Spa-Francorchamps";
                trackLength = 7.004;
                totalStraightLength = 3.2;
                longestStraightLength = 1.80;
                numberOfBrakingZones = 7;
                numberOfCorners = 19;
                averageCornerRadius = 190.0;
                break;


            default:
                throw new IllegalArgumentException(
                    "Circuit not available in RaceSpec."
                );
        }
    }


    // Returns the display name of the circuit
    public String getName()
    {
        return name;
    }


    // Returns total circuit length in kilometres
    public double getTrackLength()
    {
        return trackLength;
    }


    // Returns combined length of straight sections in kilometres
    public double getTotalStraightLength()
    {
        return totalStraightLength;
    }


    // Returns the longest straight section in kilometres
    public double getLongestStraightLength()
    {
        return longestStraightLength;
    }


    // Returns the number of significant braking zones
    public int getNumberOfBrakingZones()
    {
        return numberOfBrakingZones;
    }


    // Returns the number of modelled corners
    public int getNumberOfCorners()
    {
        return numberOfCorners;
    }


    // Returns the simplified average corner radius in metres
    public double getAverageCornerRadius()
    {
        return averageCornerRadius;
    }
}