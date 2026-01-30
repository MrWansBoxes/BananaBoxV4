package org.firstinspires.ftc.teamcode.pedroPathing.Subsystems;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.math.*;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.limelightvision.LLResult;

import com.pedropathing.geometry.*;
import com.pedropathing.VectorCalculator.*;
@Configurable
public class ShooterSubsystem {

    // ===== HARDWARE =====
    private final DcMotorEx flywheel;
    private final DcMotorEx turret;
    private final Servo hood;
    private final Limelight3A limelight;

    // ===== TURRET PID =====
    public static double turretKp = 0.05;
    public static double turretKi = 0.0;
    public static double turretKd = 0.001;
    private double lastTurretError = 0.0;
    private double turretIntegral = 0.0;
    private double turretTargetRad = 0.0;

    // ===== CONSTANTS =====
    private static final double TICKS_PER_REV = 8192.0; // REV encoder
    private static final double TICKS_TO_RAD = 2 * Math.PI / TICKS_PER_REV;

    private static final double MAX_LEAD_RAD = Math.toRadians(5);

    // Hood control constants
    public static double A = 24.0;
    public static double B = 6.0;
    public static double kRPM = 0.001;
    private static final double MAX_HOOD = 25.0;

    // Flywheel constants
    public static double C = -900.0;
    public static double D = 5200.0;

    // ===== STATE =====
    private double filteredRPM = 0.0;
    private double filteredTa = 0.0;

    public ShooterSubsystem(HardwareMap hw) {
        flywheel = hw.get(DcMotorEx.class, "flywheel");
        turret = hw.get(DcMotorEx.class, "turret");
        hood = hw.get(Servo.class, "hood");
        limelight = hw.get(Limelight3A.class, "limelight");

        flywheel.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        turret.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
    }

    /**
     * Call this each loop
     * @param robotPose current robot Pose (from PedroPathing follower)
     * @param robotVel current robot velocity Vector (from PedroPathing follower)
     * @param dt loop delta time in seconds
     */
    public void update(Pose robotPose, Vector robotVel, double dt) {

        LLResult result = limelight.getLatestResult();
        if (result == null || !result.isValid()) return;

        double tx = result.getTx();   // horizontal angle offset to target
        double ta = result.getTa();   // target area

        // Low-pass filter target area
        filteredTa = 0.8 * filteredTa + 0.2 * ta;

        // Estimate distance
        double distance = taToDistance(filteredTa);

        // Flywheel velocity
        double actualRPM = ticksToRPM(flywheel.getVelocity());
        filteredRPM = 0.7 * filteredRPM + 0.3 * actualRPM;

        double targetRPM = C * Math.sqrt(filteredTa) + D;
        flywheel.setVelocity(rpmToTicks(targetRPM));

        // Hood servo
        double hoodAngle = A / Math.sqrt(filteredTa) + B + kRPM * (targetRPM - filteredRPM);
        hood.setPosition(angleToServo(hoodAngle));

        // Motion compensation
        double turretAngle = turret.getCurrentPosition() * TICKS_TO_RAD;
        double vLat = -robotVel.getXComponent() * Math.sin(turretAngle)
                + robotVel.getYComponent() * Math.cos(turretAngle);


        double exitVelocity = rpmToExitVelocity(targetRPM);
        double flightTime = distance / exitVelocity;
        double lateralDisplacement = vLat * flightTime;

        double leadAngle = Math.atan(lateralDisplacement / distance);
        leadAngle = clamp(leadAngle, -MAX_LEAD_RAD, MAX_LEAD_RAD);

        // Turret target = vision offset + motion lead
        turretTargetRad = Math.toRadians(tx) + leadAngle;

        runTurretPID(dt);
    }

    // ===== MANUAL PID LOOP =====
    private void runTurretPID(double dt) {
        double currentRad = turret.getCurrentPosition() * TICKS_TO_RAD;
        double error = turretTargetRad - currentRad;

        turretIntegral += error * dt;
        double derivative = (error - lastTurretError) / dt;

        double output = turretKp * error + turretKi * turretIntegral + turretKd * derivative;
        turret.setPower(clamp(output, -0.7, 0.7));

        lastTurretError = error;
    }

    // ===== HELPERS =====
    private double taToDistance(double ta) {
        return 1.0 / Math.sqrt(ta); // empirical scaling — tune in testing
    }

    private double ticksToRPM(double ticksPerSec) {
        return ticksPerSec * 60.0 / TICKS_PER_REV;
    }

    private double rpmToTicks(double rpm) {
        return rpm * TICKS_PER_REV / 60.0;
    }

    private double rpmToExitVelocity(double rpm) {
        return rpm * 0.002; // empirical constant — tune with practice
    }

    private double angleToServo(double angle) {
        return clamp(angle / MAX_HOOD, 0.0, 1.0);
    }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }
    public Limelight3A getLimelight() {
        return limelight;
    }

    // ===== TELEMETRY GETTERS =====
    public double getFilteredRPM() { return filteredRPM; }
    public double getHoodAngle() { return A / Math.sqrt(filteredTa) + B; }
    public double getTurretTarget() { return turretTargetRad; }
}
