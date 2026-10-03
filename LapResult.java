// RaceSpec - Lap Result
// Stores the final results produced by a lap simulation.

public class LapResult
{
    private String vehicleName;
    private String circuitName;

    private double lapTime;
    private double distanceTravelled;
    private double finalVelocity;
    private double maximumVelocity;
    private double averageVelocity;

    private boolean lapCompleted;

    public LapResult(
        String vehicleName,
        String circuitName,
        double lapTime,
        double distanceTravelled,
        double finalVelocity,
        double maximumVelocity,
        double averageVelocity,
        boolean lapCompleted
    )
    {
        this.vehicleName = vehicleName;
        this.circuitName = circuitName;
        this.lapTime = lapTime;
        this.distanceTravelled = distanceTravelled;
        this.finalVelocity = finalVelocity;
        this.maximumVelocity = maximumVelocity;
        this.averageVelocity = averageVelocity;
        this.lapCompleted = lapCompleted;
    }

    public String getVehicleName()
    {
        return vehicleName;
    }

    public String getCircuitName()
    {
        return circuitName;
    }

    public double getLapTime()
    {
        return lapTime;
    }

    public double getDistanceTravelled()
    {
        return distanceTravelled;
    }

    public double getFinalVelocity()
    {
        return finalVelocity;
    }

    public double getMaximumVelocity()
    {
        return maximumVelocity;
    }

    public double getAverageVelocity()
    {
        return averageVelocity;
    }

    public boolean isLapCompleted()
    {
        return lapCompleted;
    }

    public void printResult()
    {
        System.out.println("=== RaceSpec Lap Result ===");
        System.out.println();

        System.out.println("VEHICLE");
        System.out.println("Name: " + vehicleName);

        System.out.println();
        System.out.println("CIRCUIT");
        System.out.println("Name: " + circuitName);

        System.out.println();
        System.out.println("RESULTS");
        System.out.println("Lap Time: " + lapTime + " s");
        System.out.println(
            "Distance: " + distanceTravelled + " m"
        );
        System.out.println(
            "Final Velocity: " + finalVelocity + " m/s"
        );
        System.out.println(
            "Maximum Velocity: " + maximumVelocity + " m/s"
        );
        System.out.println(
            "Average Velocity: " + averageVelocity + " m/s"
        );
        System.out.println(
            "Lap Completed: " + lapCompleted
        );
    }
}