public class Vehicle
{
    private String name;
    private double mass;
    private double wheelRadius;
    private double frontalArea;
    private double dragCoefficient;
    private double downforceCoefficient;
    private double rollingResistanceCoefficient;

    public Vehicle(String n, double m, double wr, double fa,
                   double cd, double cl, double crr)
    {
        name = n;
        mass = m;
        wheelRadius = wr;
        frontalArea = fa;
        dragCoefficient = cd;
        downforceCoefficient = cl;
        rollingResistanceCoefficient = crr;
    }

    public String getName()
    {
        return name;
    }

    public double getMass()
    {
        return mass;
    }

    public double getWheelRadius()
    {
        return wheelRadius;
    }

    public double getFrontalArea()
    {
        return frontalArea;
    }

    public double getDragCoefficient()
    {
        return dragCoefficient;
    }

    public double getDownforceCoefficient()
    {
        return downforceCoefficient;
    }

    public double getRollingResistanceCoefficient()
    {
        return rollingResistanceCoefficient;
    }

    public double getWeight()
    {
        return mass * 9.81;
    }
}