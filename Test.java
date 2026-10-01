public class AerodynamicsTest
{
    public static void main(String[] args)
    {
        System.out.println("=== RaceSpec Aerodynamics Test ===");
        System.out.println();

        // Test vehicle
        Vehicle vehicle = new Vehicle(
                "Test Car",
                800.0,
                0.33,
                1.5,
                0.90,
                2.0,
                0.015
        );

        Aerodynamics aero = new Aerodynamics(vehicle);

        double velocity = 60.0; // m/s

        System.out.println("VEHICLE");
        System.out.println("Name: " + vehicle.getName());
        System.out.println("Mass: " + vehicle.getMass() + " kg");
        System.out.println();

        System.out.println("AERODYNAMICS");
        System.out.println("Velocity: "
                + velocity + " m/s");

        System.out.println("Air Density: "
                + aero.getAirDensity() + " kg/m^3");

        System.out.println("Dynamic Pressure: "
                + aero.getDynamicPressure(velocity) + " Pa");

        System.out.println("Drag: "
                + aero.getDrag(velocity) + " N");

        System.out.println("Downforce: "
                + aero.getDownforce(velocity) + " N");

        System.out.println("Normal Force: "
                + aero.getNormalForce(velocity) + " N");

        System.out.println("Drag Power: "
                + aero.getDragPower(velocity) + " W");

        System.out.println();

        System.out.println("=== TEST COMPLETE ===");
    }
}