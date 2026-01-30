package org.firstinspires.ftc.teamcode.pedroPathing.TeleOps;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.geometry.*;
import com.pedropathing.math.*;
import com.pedropathing.paths.*;
import com.pedropathing.paths.HeadingInterpolator;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.pedroPathing.Subsystems.ShooterSubsystem;

import java.util.function.Supplier;

@Configurable
@TeleOp
public class BottomBlueTeleOp extends OpMode {

    // ===== PEDRO PATHING =====
    private Follower follower;
    public static Pose startingPose; // optional starting pose
    private boolean automatedDrive;
    private Supplier<PathChain> pathChain;

    private TelemetryManager telemetryM;

    // ===== SHOOTER =====
    private ShooterSubsystem shooter;

    // ===== TELEOP STATE =====
    private double lastTime = 0.0;
    private boolean shooterActive = false;


    @Override
    public void init() {
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(startingPose == null ? new Pose(72,72,90) : startingPose);
        follower.update();

        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();

        // Shooter subsystem
        shooter = new ShooterSubsystem(hardwareMap);

        // Example path (optional)
        pathChain = () -> follower.pathBuilder()
                .addPath(new Path(new BezierCurve(follower::getPose, new Pose(45, 98))))
                .setHeadingInterpolation(HeadingInterpolator.linearFromPoint(follower::getHeading, Math.toRadians(45), 0.8))
                .build();
    }

    @Override
    public void start() {
        follower.startTeleopDrive();
        lastTime = getRuntime();
        shooter.getLimelight().start();
        shooter.getLimelight().pipelineSwitch(1); // 1 is for blue tracking

    }

    @Override
    public void loop() {
        // ===== PEDRO PATHING UPDATE =====
        follower.update();
        telemetryM.update();

        // TeleOp driving
        if (!automatedDrive) {
            double driveY = -gamepad1.left_stick_y;
            double driveX = -gamepad1.left_stick_x;
            double turn  = -gamepad1.right_stick_x;


            follower.setTeleOpDrive(driveY, driveX, turn, true); // robot-centric
        }

        // ===== SHOOTER UPDATE =====
        if (shooterActive) {
            double currentTime = getRuntime();
            double dt = currentTime - lastTime;
            lastTime = currentTime;

            shooter.update(follower.getPose(), follower.getVelocity(), dt);
        }


        // ===== TELEMETRY =====
        telemetry.addData("Flywheel RPM", shooter.getFilteredRPM());
        telemetry.addData("Hood Angle", shooter.getHoodAngle());
        telemetry.addData("Turret Target (deg)", Math.toDegrees(shooter.getTurretTarget()));
        telemetry.addData("Automated Drive", automatedDrive);
        telemetry.update();

        // ===== AUTOMATED PATH FOLLOWING =====
        if (gamepad1.aWasPressed()) {
            follower.followPath(pathChain.get());
            automatedDrive = true;
        }
        if (automatedDrive && (gamepad1.bWasPressed() || !follower.isBusy())) {
            follower.startTeleopDrive();
            automatedDrive = false;
        }

        // ===== BUTTONS =====

        if (gamepad1.yWasPressed()) {         // tracking active
            shooterActive = !shooterActive;
            lastTime = getRuntime();
        }


    }
}
