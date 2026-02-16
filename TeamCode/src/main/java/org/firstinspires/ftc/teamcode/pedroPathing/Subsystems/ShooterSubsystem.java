package org.firstinspires.ftc.teamcode.pedroPathing.Subsystems;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.geometry.Pose;
import com.pedropathing.math.Vector;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

@Configurable
public class ShooterSubsystem {

    private final DcMotorEx flywheel1;
    private final DcMotorEx flywheel2;
    private final DcMotorEx turret;
    private final Servo hood;
    private final Limelight3A limelight;

    // Turret PIDF
    public static double turretKp = 0.04;
    public static double turretKi = 0.0;
    public static double turretKd = 0.002;
    public static double turretKf = 0.07;

    // Distance to RPM (from ta)
    public static double C = -850.0;
    public static double D = 5200.0;

    // Distance to the hood
    public static double A = 22.0;    // these will be between 0 and 1
    public static double B = 6.0;

    // Hood angle safety limits (degrees)
    public static double HOOD_MIN_POS = 0.0;
    public static double HOOD_MAX_POS = 0.5;

    // Motion compensation (RPM only)
    public static double RPM_PER_MPS = 320.0;

    // RPM limits
    public static double MIN_RPM = 4200.0;
    public static double MAX_RPM = 5800.0;

    // RPM rate limit
    public static double MAX_RPM_CHANGE = 400; // max RPM change per loop

    private double turretIntegral = 0;
    private double lastError = 0;

    private double filteredTa = 0;
    private static final double TA_ALPHA = 0.15;

    private static final double TICKS_TO_RAD = 2.0 * Math.PI / 8192.0;

    // Track current RPM for rate limiting
    private double currentRPM = 0;

    public ShooterSubsystem(HardwareMap hw) {

        flywheel1 = hw.get(DcMotorEx.class, "flywheel1");
        flywheel2 = hw.get(DcMotorEx.class, "flywheel2");
        turret = hw.get(DcMotorEx.class, "turret");
        hood = hw.get(Servo.class, "hood");
        limelight = hw.get(Limelight3A.class, "limelight");

        flywheel1.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        flywheel2.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

    }

    public void update(Pose pose, Vector velocity, double dt) {

        LLResult result = limelight.getLatestResult();
        if (result == null || !result.isValid()) return;

        // filter for distance
        filteredTa = TA_ALPHA * result.getTa()
                + (1 - TA_ALPHA) * filteredTa;

        // turret tracking
        double txRad = Math.toRadians(result.getTx());

        turretIntegral += txRad * dt;
        double derivative = (txRad - lastError) / dt;

        double output =
                turretKp * txRad +
                        turretKi * turretIntegral +
                        turretKd * derivative +
                        turretKf * Math.signum(txRad);

        turret.setPower(output);
        lastError = txRad;

        // robot motion
        double turretAngle =
                turret.getCurrentPosition() * TICKS_TO_RAD;

        double vForward =
                velocity.getXComponent() * Math.cos(turretAngle)
                        + velocity.getYComponent() * Math.sin(turretAngle);

        // flywheel RPM
        double targetRPM =
                C * Math.sqrt(filteredTa) + D;

        // Backward compensation only
        targetRPM += (-vForward) * RPM_PER_MPS;

        targetRPM = clamp(targetRPM, MIN_RPM, MAX_RPM);

        // RATE LIMITING
        double rpmDiff = targetRPM - currentRPM;
        if (rpmDiff > MAX_RPM_CHANGE) rpmDiff = MAX_RPM_CHANGE;
        if (rpmDiff < -MAX_RPM_CHANGE) rpmDiff = -MAX_RPM_CHANGE;
        currentRPM += rpmDiff;

        flywheel1.setVelocity(currentRPM);
        flywheel2.setVelocity(currentRPM);

        // hood angle
        double hoodPos =
                A * Math.sqrt(filteredTa) + B;

        hoodPos = clamp(hoodPos, HOOD_MIN_POS, HOOD_MAX_POS);

        hood.setPosition(hoodPos);

        double tps1 = flywheel1.getVelocity();
        double tps2 = flywheel2.getVelocity();

        double rpm1 = (tps1 / 28) * 60;
        double rpm2 = (tps2 / 28) * 60;

        double avgRPM = (rpm1 + rpm2) / 2;



        telemetry.addData("Flywheel RPM", avgRPM);
        //   telemetry.addData("Turret Target (deg)", Math.toDegrees(shooter.getTurretTarget()));
        telemetry.addData("Limelight tA", limelight.getLatestResult().getTa());
        telemetry.update();
    }

        private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    public Limelight3A getLimelight() {
        return limelight;
    }

    public DcMotorEx getFlywheel1() {
        return flywheel1;
    }

    public DcMotorEx getFlywheel2() {
        return flywheel2;
    }
}
