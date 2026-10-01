public class Aerodynamics
{
    private static final double AIR_DENSITY = 1.225; // kg/m^3

    private Vehicle vehicle;

    public Aerodynamics(Vehicle v)
    {
        if (v == null)
        {
            throw new IllegalArgumentException("Vehicle cannot be null.");
        }

        vehicle = v;
    }

    // Dynamic pressure
    // q = 1/2 * rho * v^2
    public double getDynamicPressure(double velocity)
    {
        if (velocity < 0)
        {
            return 0;
        }

        return 0.5
                * AIR_DENSITY
                * velocity
                * velocity;
    }

    // Aerodynamic drag
    // Fd = 1/2 * rho * Cd * A * v^2
    public double getDrag(double velocity)
    {
        if (velocity < 0)
        {
            return 0;
        }

        return 0.5
                * AIR_DENSITY
                * vehicle.getDragCoefficient()
                * vehicle.getFrontalArea()
                * velocity
                * velocity;
    }

    // Aerodynamic downforce
    // Fdown = 1/2 * rho * Cl * A * v^2
    public double getDownforce(double velocity)
    {
        if (velocity < 0)
        {
            return 0;
        }

        return 0.5
                * AIR_DENSITY
                * vehicle.getDownforceCoefficient()
                * vehicle.getFrontalArea()
                * velocity
                * velocity;
    }

    // Total vertical load on the tyres
    // Normal force = weight + downforce
    public double getNormalForce(double velocity)
    {
        return vehicle.getWeight()
                + getDownforce(velocity);
    }

    // Power required to overcome aerodynamic drag
    // P = Fd * v
    public double getDragPower(double velocity)
    {
        return getDrag(velocity) * Math.max(velocity, 0);
    }

    public double getAirDensity()
    {
        return AIR_DENSITY;
    }

    public Vehicle getVehicle()
    {
        return vehicle;
    }
}