// RaceSpec - Circuit Dynamics Model V2
// Stores track geometry and performance-demand characteristics.

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
            case "Monza":
                name = "Monza";

                // Verified F1 circuit length.
                trackLength = 5.793;

                // Track-demand approximations.
                totalStraightLength = 3.60;
                longestStraightLength = 1.10;

                numberOfBrakingZones = 6;
                numberOfCorners = 11;
                averageCornerRadius = 180.0;

                sectorLengths = new double[]
                {
                    1.50, 1.60, 2.693
                };

                cornerRadii = new double[]
                {
                    160, 95, 80, 110, 145,
                    190, 150, 135, 180, 95, 110
                };

                cornerApexSpeeds = new double[]
                {
                    82, 78, 78, 92, 105,
                    185, 165, 150, 95, 105, 115
                };

                brakingDistances = new double[]
                {
                    120, 95, 80, 90, 70, 85
                };

                elevationChange = 21.0;
                surfaceGrip = 1.00;

                lowSpeedFactor = 0.85;
                mediumSpeedFactor = 0.85;
                highSpeedFactor = 1.15;
                break;

            case "Monaco":
                name = "Monaco";

                trackLength = 3.337;

                totalStraightLength = 1.10;
                longestStraightLength = 0.67;

                numberOfBrakingZones = 8;
                numberOfCorners = 19;
                averageCornerRadius = 95.0;

                sectorLengths = new double[]
                {
                    1.20, 0.95, 1.187
                };

                cornerRadii = new double[]
                {
                    90, 55, 45, 42, 35,
                    38, 50, 48, 70, 55,
                    40, 45, 42, 38, 35,
                    48, 55, 65, 90
                };

                cornerApexSpeeds = new double[]
                {
                    55, 72, 70, 65, 58,
                    60, 68, 82, 78, 72,
                    65, 58, 60, 65, 72,
                    75, 82, 92, 110
                };

                brakingDistances = new double[]
                {
                    85, 60, 55, 45, 40,
                    45, 55, 60
                };

                elevationChange = 42.0;
                surfaceGrip = 0.99;

                lowSpeedFactor = 1.25;
                mediumSpeedFactor = 0.85;
                highSpeedFactor = 0.55;
                break;

            case "Silverstone":
                name = "Silverstone";

                trackLength = 5.891;

                totalStraightLength = 2.70;
                longestStraightLength = 0.77;

                numberOfBrakingZones = 6;
                numberOfCorners = 18;
                averageCornerRadius = 160.0;

                sectorLengths = new double[]
                {
                    1.88, 1.54, 2.471
                };

                cornerRadii = new double[]
                {
                    210, 240, 190, 170, 150, 135,
                    145, 180, 220, 250, 210, 170,
                    140, 155, 185, 220, 250, 180
                };

                cornerApexSpeeds = new double[]
                {
                    135, 175, 105, 100, 110, 120,
                    130, 145, 160, 175, 150, 135,
                    120, 110, 105, 155, 165, 130
                };

                brakingDistances = new double[]
                {
                    95, 75, 90, 65, 85, 75
                };

                elevationChange = 11.0;
                surfaceGrip = 1.00;

                lowSpeedFactor = 0.75;
                mediumSpeedFactor = 1.00;
                highSpeedFactor = 1.20;
                break;

            case "Suzuka":
                name = "Suzuka";

                trackLength = 5.807;

                totalStraightLength = 2.30;
                longestStraightLength = 0.80;

                numberOfBrakingZones = 7;
                numberOfCorners = 18;
                averageCornerRadius = 140.0;

                sectorLengths = new double[]
                {
                    1.85, 1.75, 2.207
                };

                cornerRadii = new double[]
                {
                    210, 180, 165, 150, 135, 120,
                    110, 130, 160, 185, 160, 140,
                    125, 105, 90, 95, 125, 180
                };

                cornerApexSpeeds = new double[]
                {
                    155, 160, 165, 145, 135, 125,
                    120, 115, 105, 150, 165, 135,
                    115, 95, 90, 105, 115, 145
                };

                brakingDistances = new double[]
                {
                    95, 75, 85, 70, 90, 65, 80
                };

                elevationChange = 40.0;
                surfaceGrip = 1.00;

                lowSpeedFactor = 0.80;
                mediumSpeedFactor = 1.00;
                highSpeedFactor = 1.15;
                break;

            case "Spa":
                name = "Spa-Francorchamps";

                trackLength = 7.004;

                totalStraightLength = 3.20;
                longestStraightLength = 1.80;

                numberOfBrakingZones = 7;
                numberOfCorners = 19;
                averageCornerRadius = 190.0;

                sectorLengths = new double[]
                {
                    2.61, 2.28, 2.114
                };

                cornerRadii = new double[]
                {
                    90, 130, 180, 210, 220,
                    190, 175, 145, 120, 105,
                    110, 130, 145, 165, 180,
                    150, 120, 100, 90
                };

                cornerApexSpeeds = new double[]
                {
                    85, 135, 165, 175, 180,
                    170, 160, 150, 135, 120,
                    140, 150, 160, 175, 185,
                    165, 145, 120, 100
                };

                brakingDistances = new double[]
                {
                    110, 90, 80, 75, 85, 70, 120
                };

                elevationChange = 102.0;
                surfaceGrip = 0.99;

                lowSpeedFactor = 0.80;
                mediumSpeedFactor = 0.95;
                highSpeedFactor = 1.20;
                break;

            default:
                throw new IllegalArgumentException(
                    "Circuit not available in RaceSpec."
                );
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

    // Returns a simple track-demand classification.
    public String getCircuitType()
    {
        if (lowSpeedFactor > highSpeedFactor)
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

    // Estimates the average cornering demand.
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

    // Estimates the total braking demand represented by the model.
    public double getTotalBrakingDistance()
    {
        double total = 0;

        for (int i = 0; i < brakingDistances.length; i++)
        {
            total += brakingDistances[i];
        }

        return total;
    }
}