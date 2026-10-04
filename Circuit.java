// RaceSpec - Complete Circuit Dynamics Model V2
// Contains all 24 venues from the originally announced 2026 F1 calendar.
// Circuit length and corner counts use published F1/FIA data.
// Detailed corner-radius and braking profiles are RaceSpec model inputs.

public class Circuit
{
    private String name;

    private double trackLength;
    private double totalStraightLength;
    private double longestStraightLength;

    private int numberOfBrakingZones;
    private int numberOfCorners;

    private double averageCornerRadius;

    private double[] cornerRadii;
    private double[] cornerApexSpeeds;
    private double[] brakingDistances;
    private double[] sectorLengths;

    private double elevationChange;
    private double surfaceGrip;

    private double lowSpeedFactor;
    private double mediumSpeedFactor;
    private double highSpeedFactor;

    private double bankingFactor;

    public Circuit(String circuitName)
    {
        if (
            circuitName == null
            || circuitName.trim().isEmpty()
        )
        {
            throw new IllegalArgumentException(
                "Circuit name cannot be empty."
            );
        }

        loadCircuit(
            circuitName.trim()
        );
    }

    private void loadCircuit(String circuitName)
    {
        switch (circuitName)
        {
            case "Melbourne":
                configure(
                    "Melbourne",
                    5.278, 2.30, 0.95,
                    6, 14, 185,
                    4, 1.00,
                    0.85, 1.00, 1.10,
                    1.00
                );
                break;

            case "Shanghai":
                configure(
                    "Shanghai",
                    5.451, 2.00, 1.17,
                    7, 16, 175,
                    5, 1.00,
                    0.90, 1.00, 1.05,
                    1.00
                );
                break;

            case "Suzuka":
                configure(
                    "Suzuka",
                    5.807, 2.30, 0.80,
                    7, 18, 140,
                    7, 1.00,
                    0.80, 1.00, 1.15,
                    1.00
                );
                break;

            case "Bahrain":
                configure(
                    "Bahrain",
                    5.412, 2.10, 1.10,
                    8, 15, 155,
                    5, 0.99,
                    0.95, 1.00, 1.10,
                    1.00
                );
                break;

            case "Jeddah":
                configure(
                    "Jeddah",
                    6.174, 3.80, 1.20,
                    7, 27, 210,
                    5, 0.99,
                    0.70, 1.00, 1.25,
                    1.00
                );
                break;

            case "Miami":
                configure(
                    "Miami",
                    5.412, 2.10, 1.28,
                    7, 19, 170,
                    5, 1.00,
                    0.80, 1.00, 1.15,
                    1.00
                );
                break;

            case "Montreal":
                configure(
                    "Montreal",
                    4.361, 2.00, 1.17,
                    8, 14, 155,
                    6, 0.99,
                    0.95, 1.00, 1.15,
                    1.00
                );
                break;

            case "Monaco":
                configure(
                    "Monaco",
                    3.337, 1.10, 0.67,
                    8, 19, 95,
                    8, 0.99,
                    1.25, 0.85, 0.55,
                    1.02
                );
                break;

            case "Barcelona":
                configure(
                    "Barcelona",
                    4.657, 1.90, 1.05,
                    6, 14, 175,
                    5, 1.00,
                    0.80, 1.00, 1.10,
                    1.00
                );
                break;

            case "Spielberg":
                configure(
                    "Spielberg",
                    4.326, 2.20, 0.93,
                    7, 10, 205,
                    5, 1.00,
                    0.90, 1.00, 1.20,
                    1.00
                );
                break;

            case "Silverstone":
                configure(
                    "Silverstone",
                    5.891, 2.70, 0.77,
                    6, 18, 160,
                    6, 1.00,
                    0.75, 1.00, 1.20,
                    1.00
                );
                break;

            case "Spa":
                configure(
                    "Spa-Francorchamps",
                    7.004, 3.20, 1.80,
                    7, 19, 190,
                    7, 0.99,
                    0.80, 0.95, 1.20,
                    1.00
                );
                break;

            case "Hungary":
                configure(
                    "Hungaroring",
                    4.381, 1.90, 0.79,
                    7, 14, 125,
                    6, 1.00,
                    1.10, 0.95, 0.70,
                    1.00
                );
                break;

            case "Zandvoort":
                configure(
                    "Zandvoort",
                    4.259, 1.70, 0.68,
                    6, 14, 135,
                    6, 1.00,
                    1.00, 1.00, 0.95,
                    1.04
                );
                break;

            case "Monza":
                configure(
                    "Monza",
                    5.793, 3.60, 1.10,
                    6, 11, 180,
                    6, 1.00,
                    0.85, 0.85, 1.15,
                    1.00
                );
                break;

            case "Madrid":
                configure(
                    "Madrid",
                    5.414, 2.40, 0.98,
                    7, 22, 150,
                    6, 1.00,
                    0.85, 1.00, 1.10,
                    1.08
                );
                break;

            case "Baku":
                configure(
                    "Baku",
                    6.003, 3.20, 2.20,
                    6, 20, 185,
                    5, 0.99,
                    0.80, 0.95, 1.25,
                    1.00
                );
                break;

            case "Singapore":
                configure(
                    "Singapore",
                    4.940, 1.60, 0.83,
                    9, 19, 105,
                    8, 0.98,
                    1.20, 0.90, 0.55,
                    1.00
                );
                break;

            case "Austin":
                configure(
                    "Circuit of the Americas",
                    5.513, 2.50, 1.02,
                    8, 20, 165,
                    6, 1.00,
                    0.90, 1.00, 1.10,
                    1.02
                );
                break;

            case "Mexico City":
                configure(
                    "Mexico City",
                    4.304, 2.00, 1.09,
                    5, 17, 150,
                    5, 0.98,
                    0.85, 1.00, 1.05,
                    1.00
                );
                break;

            case "Interlagos":
                configure(
                    "Interlagos",
                    4.309, 1.80, 0.90,
                    6, 15, 145,
                    5, 1.00,
                    0.90, 1.00, 1.10,
                    1.01
                );
                break;

            case "Las Vegas":
                configure(
                    "Las Vegas Strip Circuit",
                    6.201, 3.50, 1.20,
                    6, 17, 190,
                    5, 0.99,
                    0.70, 0.95, 1.30,
                    1.00
                );
                break;

            case "Lusail":
                configure(
                    "Lusail International Circuit",
                    5.419, 2.10, 1.06,
                    6, 16, 180,
                    5, 0.99,
                    0.80, 1.00, 1.15,
                    1.00
                );
                break;

            case "Abu Dhabi":
                configure(
                    "Yas Marina Circuit",
                    5.281, 2.50, 1.20,
                    7, 16, 155,
                    6, 1.00,
                    0.85, 1.00, 1.10,
                    1.00
                );
                break;

            default:
                throw new IllegalArgumentException(
                    "Circuit not available in RaceSpec."
                );
        }
    }

    private void configure(
        String circuitName,
        double length,
        double straightLength,
        double longestStraight,
        int brakingZones,
        int corners,
        double averageRadius,
        int seed,
        double grip,
        double lowSpeed,
        double mediumSpeed,
        double highSpeed,
        double banking
    )
    {
        name = circuitName;

        trackLength = length;
        totalStraightLength = straightLength;
        longestStraightLength = longestStraight;

        numberOfBrakingZones = brakingZones;
        numberOfCorners = corners;
        averageCornerRadius = averageRadius;

        elevationChange =
            getEstimatedElevation(circuitName);

        surfaceGrip = grip;

        lowSpeedFactor = lowSpeed;
        mediumSpeedFactor = mediumSpeed;
        highSpeedFactor = highSpeed;

        bankingFactor = banking;

        sectorLengths = new double[]
        {
            length * 0.32,
            length * 0.33,
            length * 0.35
        };

        cornerRadii =
            buildCornerRadii(
                corners,
                averageRadius,
                seed
            );

        cornerApexSpeeds =
            buildCornerSpeeds(
                corners,
                lowSpeed,
                mediumSpeed,
                highSpeed,
                seed
            );

        brakingDistances =
            buildBrakingProfile(
                brakingZones,
                longestStraight,
                corners,
                seed
            );
    }

    // Creates a representative corner-radius distribution.
    // These are model inputs, not claimed survey measurements.
    private double[] buildCornerRadii(
        int corners,
        double averageRadius,
        int seed
    )
    {
        double[] values =
            new double[corners];

        for (int i = 0; i < corners; i++)
        {
            double pattern =
                0.72
                + (
                    ((i * 37 + seed * 11) % 57)
                    / 100.0
                );

            values[i] =
                averageRadius * pattern;
        }

        return values;
    }

    // Creates representative apex speeds from track character.
    private double[] buildCornerSpeeds(
        int corners,
        double lowFactor,
        double mediumFactor,
        double highFactor,
        int seed
    )
    {
        double[] values =
            new double[corners];

        for (int i = 0; i < corners; i++)
        {
            int pattern =
                (i * 17 + seed * 7) % 3;

            if (pattern == 0)
            {
                values[i] =
                    55.0
                    + 35.0 * lowFactor;
            }
            else if (pattern == 1)
            {
                values[i] =
                    80.0
                    + 60.0 * mediumFactor;
            }
            else
            {
                values[i] =
                    120.0
                    + 65.0 * highFactor;
            }
        }

        return values;
    }

    // Creates representative braking-zone distances.
    private double[] buildBrakingProfile(
        int zones,
        double longestStraight,
        int corners,
        int seed
    )
    {
        double[] values =
            new double[zones];

        double base =
            65.0
            + Math.min(
                45.0,
                longestStraight * 20.0
            );

        for (int i = 0; i < zones; i++)
        {
            double variation =
                ((i * 19 + seed * 5) % 40)
                - 20;

            values[i] =
                Math.max(
                    40.0,
                    base + variation
                );
        }

        return values;
    }

    private double getEstimatedElevation(
        String circuitName
    )
    {
        switch (circuitName)
        {
            case "Melbourne":
                return 4.0;

            case "Shanghai":
                return 7.0;

            case "Suzuka":
                return 40.0;

            case "Bahrain":
                return 17.0;

            case "Jeddah":
                return 15.0;

            case "Miami":
                return 4.0;

            case "Montreal":
                return 8.0;

            case "Monaco":
                return 42.0;

            case "Barcelona":
                return 30.0;

            case "Spielberg":
                return 65.0;

            case "Silverstone":
                return 11.0;

            case "Spa-Francorchamps":
                return 102.0;

            case "Hungaroring":
                return 35.0;

            case "Zandvoort":
                return 10.0;

            case "Monza":
                return 21.0;

            case "Madrid":
                return 25.0;

            case "Baku":
                return 30.0;

            case "Singapore":
                return 5.0;

            case "Circuit of the Americas":
                return 41.0;

            case "Mexico City":
                return 22.0;

            case "Interlagos":
                return 43.0;

            case "Las Vegas Strip Circuit":
                return 8.0;

            case "Lusail International Circuit":
                return 8.0;

            case "Yas Marina Circuit":
                return 12.0;

            default:
                return 0;
        }
    }

    public String getName()
    {
        return name;
    }

    public double getTrackLength()
    {
        return trackLength;
    }

    public double getTotalStraightLength()
    {
        return totalStraightLength;
    }

    public double getLongestStraightLength()
    {
        return longestStraightLength;
    }

    public int getNumberOfBrakingZones()
    {
        return numberOfBrakingZones;
    }

    public int getNumberOfCorners()
    {
        return numberOfCorners;
    }

    public double getAverageCornerRadius()
    {
        return averageCornerRadius;
    }

    public double getCornerRadius(int corner)
    {
        if (
            corner < 1
            || corner > cornerRadii.length
        )
        {
            return 0;
        }

        return cornerRadii[corner - 1];
    }

    public double getCornerApexSpeed(int corner)
    {
        if (
            corner < 1
            || corner > cornerApexSpeeds.length
        )
        {
            return 0;
        }

        return cornerApexSpeeds[corner - 1];
    }

    public double getBrakingDistance(int zone)
    {
        if (
            zone < 1
            || zone > brakingDistances.length
        )
        {
            return 0;
        }

        return brakingDistances[zone - 1];
    }

    public double getSectorLength(int sector)
    {
        if (
            sector < 1
            || sector > sectorLengths.length
        )
        {
            return 0;
        }

        return sectorLengths[sector - 1];
    }

    public double getElevationChange()
    {
        return elevationChange;
    }

    public double getSurfaceGrip()
    {
        return surfaceGrip;
    }

    public double getLowSpeedFactor()
    {
        return lowSpeedFactor;
    }

    public double getMediumSpeedFactor()
    {
        return mediumSpeedFactor;
    }

    public double getHighSpeedFactor()
    {
        return highSpeedFactor;
    }

    public double getBankingFactor()
    {
        return bankingFactor;
    }

    public String getCircuitType()
    {
        if (
            lowSpeedFactor > highSpeedFactor
            && lowSpeedFactor > mediumSpeedFactor
        )
        {
            return "Technical";
        }

        if (
            highSpeedFactor > 1.15
            && longestStraightLength >= 1.0
        )
        {
            return "High Speed";
        }

        return "Balanced";
    }

    public double getAverageCorneringDemand()
    {
        double total = 0;

        for (int i = 0; i < cornerRadii.length; i++)
        {
            if (cornerRadii[i] > 0)
            {
                total +=
                    1.0 / cornerRadii[i];
            }
        }

        return
            total / cornerRadii.length;
    }

    public double getTotalBrakingDistance()
    {
        double total = 0;

        for (
            int i = 0;
            i < brakingDistances.length;
            i++
        )
        {
            total += brakingDistances[i];
        }

        return total;
    }
} 