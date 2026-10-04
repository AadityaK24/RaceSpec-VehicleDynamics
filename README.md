# RaceSpec — Vehicle Dynamics & Circuit Performance Simulator

RaceSpec is a Java-based vehicle dynamics and circuit-performance modelling project designed to compare a reference Formula 1 car against a user-defined performance concept across multiple circuits.

The project combines **powertrain modelling, gearing, tyre grip, aerodynamics, braking, vehicle dynamics and lap simulation** into a single software model.

The current reference vehicle is the **McLaren MCL39**, while the second vehicle is a configurable **Dream Car** whose parameters can be changed directly through the graphical interface.

---

## Project Objective

RaceSpec was developed to answer a simple engineering question:

> **Which vehicle configuration is better for a given circuit, and why?**

Rather than treating a car as a collection of independent specifications, RaceSpec models the interaction between major vehicle systems.

The software evaluates factors including:

- Engine power and torque
- Gear ratios and final drive
- Wheel torque and drive force
- Tyre grip and load sensitivity
- Aerodynamic drag
- Aerodynamic downforce
- Traction limits
- Braking capability
- Longitudinal load transfer
- Cornering capability
- Circuit characteristics
- Lap-time performance

The final comparison is based on the simulated circuit lap time, with the **lower valid lap time winning**.

---

## Core Engineering Model

RaceSpec uses a modular vehicle-dynamics architecture.

```text
Engine
   ↓
Gearbox
   ↓
Wheel Torque / Drive Force
   ↓
Tyre Traction Limit
   ↓
Vehicle Longitudinal Dynamics
   ↓
Aerodynamic Drag + Rolling Resistance
   ↓
Acceleration / Speed

Aerodynamics ───────────────┐
Tyres ──────────────────────┤
Brakes ─────────────────────┤
Vehicle Dynamics ───────────┤
Circuit Profile ────────────┘
              ↓
        Lap Simulation
              ↓
        Lap Time Result
              ↓
       Vehicle Comparison
              ↓
       Final Circuit Verdict
```

This structure allows individual systems to be upgraded without rebuilding the entire application.

---

## Main Components

| Class | Purpose |
|---|---|
| `Vehicle.java` | Core vehicle geometry, mass properties and load-transfer calculations |
| `Engine.java` | Engine torque, power, RPM and ERS behaviour |
| `Gearbox.java` | Gear ratios, final drive, shifting and wheel torque |
| `Tyre.java` | Grip, temperature, pressure, wear and combined traction limits |
| `Aerodynamics.java` | Drag, downforce, aero balance and DRS behaviour |
| `Brakes.java` | Brake force, braking limits, temperature and dynamic axle loading |
| `Circuit.java` | Circuit geometry and performance characteristics |
| `PerformanceCalculator.java` | Couples the vehicle subsystems into dynamic calculations |
| `LapSimulation.java` | Performs the track-aware lap simulation |
| `LapResult.java` | Stores and reports lap-performance results |
| `McLarenMCL39.java` | Reference MCL39 configuration |
| `DreamCar.java` | Default RaceSpec concept vehicle |
| `VehicleComparator.java` | Produces comparative engineering performance tables |
| `VerdictEngine.java` | Determines the final circuit winner |
| `InputValidator.java` | Validates model inputs |
| `Main.java` | JavaFX V3 simulation and analysis interface |

---

## Vehicle Comparison

RaceSpec currently compares two vehicles:

### McLaren MCL39

The MCL39 is used as the reference configuration.

Where publicly available technical information exists, the model uses documented specifications. Parameters that are not publicly available, such as certain aerodynamic coefficients and detailed vehicle-dynamics calibration values, are represented using clearly documented model assumptions.

### Dream Car

The Dream Car is a fictional high-performance vehicle configuration created specifically for RaceSpec.

Its parameters can be modified through the GUI, including:

- Vehicle mass
- Power
- Torque
- Drag coefficient
- Downforce coefficient
- Tyre grip
- Brake torque
- Front brake bias
- Vehicle name

This allows the user to investigate how changes to the vehicle configuration affect circuit performance.

---

## Circuit Model

RaceSpec contains configurations for **24 circuits**.

The circuit model represents characteristics such as:

- Track length
- Straight-line distance
- Longest straight
- Number of braking zones
- Number of corners
- Representative corner radii
- Apex-speed targets
- Braking distances
- Sector characteristics
- Surface grip
- Speed-profile characteristics

The simulator uses these characteristics to create a circuit-specific lap rather than applying the same generic lap model to every track.

---

## Vehicle Dynamics

The project includes simplified engineering models for several important relationships.

### Aerodynamic Drag

```text
F_drag = 1/2 × ρ × Cd × A × v²
```

### Aerodynamic Downforce

```text
F_downforce = 1/2 × ρ × Cl × A × v²
```

### Longitudinal Load Transfer

```text
ΔFz = (m × a × h) / L
```

where:

- `m` = vehicle mass
- `a` = longitudinal acceleration
- `h` = centre-of-gravity height
- `L` = wheelbase

### Braking Distance

```text
d = v² / (2a)
```

The simulator also applies traction limits, aerodynamic loading, rolling resistance and braking constraints rather than assuming unlimited acceleration or cornering.

---

## Lap Simulation

The lap simulator uses a **fixed physics timestep of 0.02 seconds**.

Playback speed is independent of the physics timestep, allowing the graphical interface to accelerate the visualisation without changing the underlying simulation resolution.

During a lap the model evaluates:

- Acceleration on straights
- Automatic gear selection
- Braking for upcoming corners
- Target corner speeds
- Tyre grip limits
- Aerodynamic loading
- DRS behaviour
- Vehicle speed
- Lap progress

The result is then converted into a `LapResult` containing the final lap-performance metrics.

---

## RaceSpec V3 Interface

The V3 interface is built using **JavaFX**.

The application provides:

- Circuit selection
- Lap-count selection
- Simulation playback-speed control
- Play / Pause / Reset
- Simulation and Analysis views
- Custom Dream Car configuration
- Live vehicle telemetry
- Dual-car comparison
- Speed and G-force charts
- Circuit maps
- Live MCL39 and Dream Car track markers
- Final circuit verdict

### Vehicle Visualisation

The interface uses:

- **Orange** — McLaren MCL39
- **Turquoise** — Dream Car

The central circuit display is used as the primary visualisation area, while the surrounding panels provide engineering and telemetry data.

---

## Analysis Output

The Analysis view compares the two vehicles using engineering quantities such as:

- Mass
- Power
- Torque
- Power-to-weight ratio
- Top speed
- Downforce
- Drag
- Tyre grip
- Braking force
- Lateral acceleration
- Lap time
- Average speed
- Maximum speed

The final verdict is based on simulated lap performance.

```text
Lower valid lap time = better circuit performance
```

---

## Validation

Before integrating the V3 interface, the backend model was tested across all 24 circuit configurations.

The validation process checked that:

- Every circuit could be simulated
- Both vehicles produced valid lap results
- The lap simulation completed successfully
- No circuit became trapped in the simulation loop
- The comparison and verdict systems produced consistent results

The current backend validation is a **software/model validation**, not a claim that RaceSpec reproduces official F1 telemetry with engineering-grade accuracy.

The simulator is intended as a **simplified vehicle-dynamics model and comparative engineering tool**.

---

## Data & Modelling Philosophy

RaceSpec separates **known reference information** from **model assumptions**.

Publicly available vehicle and regulatory information is used where appropriate.

Where proprietary or unavailable data is required, RaceSpec uses explicit assumptions for parameters such as:

- Aerodynamic coefficients
- Detailed tyre behaviour
- Brake characteristics
- Powertrain calibration
- Gear ratios
- Vehicle geometry
- Circuit performance profiles

These values are treated as **simulation parameters**, not as official manufacturer data.

This distinction is important because the objective of the project is to demonstrate vehicle-dynamics modelling and software integration rather than reproduce confidential Formula 1 engineering data.

---

## Technology

- **Java**
- **JavaFX**
- Object-oriented programming
- Numerical vehicle-dynamics modelling
- Track-aware simulation
- SVG circuit visualisation
- Git / GitHub

---

## Running the Project

RaceSpec uses the JavaFX libraries supplied with the development environment.

Example compilation command:

```powershell
& "C:\Program Files\BlueJ\jdk\bin\javac.exe" -encoding UTF-8 `
--module-path "C:\Program Files\BlueJ\lib\javafx\lib" `
--add-modules javafx.controls,javafx.graphics,javafx.web `
Vehicle.java Engine.java Gearbox.java Tyre.java Aerodynamics.java `
Brakes.java Circuit.java PerformanceCalculator.java InputValidator.java `
McLarenMCL39.java DreamCar.java LapSimulation.java LapResult.java `
VehicleComparator.java VerdictEngine.java Main.java
```

Run the application with:

```powershell
& "C:\Program Files\BlueJ\jdk\bin\java.exe" `
--module-path "C:\Program Files\BlueJ\lib\javafx\lib" `
--add-modules javafx.controls,javafx.graphics,javafx.web Main
```

Circuit SVG files are stored locally in the `maps` directory.

---

## Project Status

### V1
Initial vehicle-performance calculations and subsystem architecture.

### V2
Major physics and modelling upgrade:

- Vehicle dynamics
- Engine model
- Gearbox model
- Tyre model
- Aerodynamic model
- Brake model
- Circuit model
- Track-aware lap simulation
- Vehicle comparison
- Circuit verdict system

### V3
Graphical simulation interface:

- JavaFX dashboard
- Circuit maps
- Dual-car visualisation
- Live telemetry
- Simulation controls
- Performance charts
- Dream Car configuration
- Analysis interface

---

## Future Development

Potential future improvements include:

- More detailed tyre slip modelling
- Improved thermal modelling
- More advanced suspension/load-transfer behaviour
- Higher-resolution circuit profiles
- Sector-by-sector comparison
- More detailed powertrain energy management
- Improved calibration against public telemetry
- More realistic vehicle trajectory optimisation

---

## Project Philosophy

RaceSpec is built around the idea that vehicle performance is an **interacting system**, not a specification sheet.

A higher-power vehicle does not automatically win every circuit.

Mass, gearing, tyre grip, aerodynamic efficiency, braking performance and circuit characteristics all influence the final result.

RaceSpec attempts to represent those interactions in a single computational model.

---

## Author

**Aaditya Kulkarni**

RaceSpec-VehicleDynamics is an independent engineering and software project developed as part of a personal engineering portfolio.
