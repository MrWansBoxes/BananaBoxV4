package org.firstinspires.ftc.teamcode.pedroPathing.Autos;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.TelemetryManager;
import com.bylazar.telemetry.PanelsTelemetry;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.follower.Follower;
import com.pedropathing.paths.PathChain;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.pedroPathing.Subsystems.ColorSensorBottom;
import org.firstinspires.ftc.teamcode.pedroPathing.Subsystems.ColorSensorMiddle;
import org.firstinspires.ftc.teamcode.pedroPathing.Subsystems.ColorSensorTop;
import org.firstinspires.ftc.teamcode.pedroPathing.Subsystems.ShooterSubsystem;

@Autonomous
@Configurable // Panels
public class AutoBottomBlue extends OpMode {

    ColorSensorBottom bottom = new ColorSensorBottom();   // gets the color sensor class
    ColorSensorMiddle middle = new ColorSensorMiddle();
    ColorSensorTop top = new ColorSensorTop();
    ColorSensorBottom.DetectedColor detectedColorBottom;
    ColorSensorMiddle.DetectedColor detectedColorMiddle;
    ColorSensorTop.DetectedColor detectedColorTop;
    private DcMotor intake;
    private Servo gate;  // servos
    private TelemetryManager panelsTelemetry; // Panels Telemetry instance
    public Follower follower; // Pedro Pathing follower instance
    private int pathState; // Current autonomous path state (state machine)
    private Paths paths; // Paths defined in the Paths class
    private ShooterSubsystem shooter;
    private double lastTime = 0.0;
    private boolean shooterActive = false;

    @Override
    public void init() {
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(55.08788598574822, 9.254156769596198, Math.toRadians(90)));

        paths = new Paths(follower); // Build paths
// Shooter subsystem
        shooter = new ShooterSubsystem(hardwareMap);
        panelsTelemetry.debug("Status", "Initialized");
        panelsTelemetry.update(telemetry);
    }
    @Override
    public void start() {
        lastTime = getRuntime();
        shooter.getLimelight().start();
        shooter.getLimelight().pipelineSwitch(1); // 1 is for blue tracking
    }
    @Override
    public void loop() {
        follower.update(); // Update Pedro Pathing
        pathState = autonomousPathUpdate(); // Update autonomous state machine

        // launcher update
        if (shooterActive) {
            double currentTime = getRuntime();
            double dt = currentTime - lastTime;
            lastTime = currentTime;

            shooter.update(follower.getPose(), follower.getVelocity(), dt);
        }

        // Log values to Panels and Driver Station
        panelsTelemetry.debug("Path State", pathState);
        panelsTelemetry.debug("X", follower.getPose().getX());
        panelsTelemetry.debug("Y", follower.getPose().getY());
        panelsTelemetry.debug("Heading", follower.getPose().getHeading());
        panelsTelemetry.update(telemetry);
    }


    public static class Paths {
        public PathChain Starttoshoot1;
        public PathChain Shoot1topickup1;
        public PathChain Pickup1tointake1;
        public PathChain Intake1toshoot2;
        public PathChain Shoot2topark;

        public Paths(Follower follower) {
            Starttoshoot1 = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(55.088, 9.254),

                                    new Pose(61.017, 23.914)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(90))

                    .build();

            Shoot1topickup1 = follower.pathBuilder().addPath(
                            new BezierCurve(
                                    new Pose(61.017, 23.914),
                                    new Pose(30.785, 8.586),
                                    new Pose(7.466, 26.530)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(270))

                    .build();

            Pickup1tointake1 = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(7.466, 26.530),

                                    new Pose(7.629, 9.627)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(270), Math.toRadians(270))

                    .build();

            Intake1toshoot2 = follower.pathBuilder().addPath(
                            new BezierCurve(
                                    new Pose(7.629, 9.627),
                                    new Pose(33.806, 21.343),
                                    new Pose(61.071, 23.919)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(270), Math.toRadians(90))

                    .build();

            Shoot2topark = follower.pathBuilder().addPath(
                            new BezierCurve(
                                    new Pose(61.071, 23.919),
                                    new Pose(48.837, 13.333),
                                    new Pose(35.340, 11.164)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(90))

                    .build();
        }
    }

    /* You could check for
                - Follower State: "if(!follower.isBusy()) {}"
                - Time: "if(pathTimer.getElapsedTimeSeconds() > 1) {}"
                - Robot Position: "if(follower.getPose().getX() > 36) {}"
                */
    public int autonomousPathUpdate() {
        switch (pathState) {
            // Add your state machine Here
            // Access paths with paths.pathName
            // Refer to the Pedro Pathing Docs (Auto Example) for an example state machine
        }
        return pathState;
    }
}