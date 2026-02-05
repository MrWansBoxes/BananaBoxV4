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
public class AutoTopBlue extends OpMode {

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
        follower.setStartingPose(new Pose(14.939759036144583, 113.83132530120483, Math.toRadians(180)));

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
        public PathChain ShootPretopickup1;
        public PathChain Pickup1toshoot2;
        public PathChain Shoot2tograbfromgate;
        public PathChain Gate1toshoot3;
        public PathChain Shoot3togate2;
        public PathChain Gate2toshoot4;
        public PathChain Shoot4topickup2;
        public PathChain Pickup2toshoot5;
        public PathChain Shoot5topickup3;
        public PathChain Pickup3toshoot6;
        public PathChain Shoot6topark;

        public Paths(Follower follower) {
            Starttoshoot1 = follower.pathBuilder().addPath(
                            new BezierCurve(
                                    new Pose(14.940, 113.831),
                                    new Pose(32.048, 94.759),
                                    new Pose(54.072, 92.096)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))

                    .build();

            ShootPretopickup1 = follower.pathBuilder().addPath(
                            new BezierCurve(
                                    new Pose(54.072, 92.096),
                                    new Pose(67.114, 55.488),
                                    new Pose(9.614, 59.590)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))

                    .build();

            Pickup1toshoot2 = follower.pathBuilder().addPath(
                            new BezierCurve(
                                    new Pose(9.614, 59.590),
                                    new Pose(26.898, 51.102),
                                    new Pose(45.898, 88.898),
                                    new Pose(54.108, 92.060)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(140))

                    .build();

            Shoot2tograbfromgate = follower.pathBuilder().addPath(
                            new BezierCurve(
                                    new Pose(54.108, 92.060),
                                    new Pose(30.458, 58.994),
                                    new Pose(12.157, 59.518)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(140), Math.toRadians(145))

                    .build();

            Gate1toshoot3 = follower.pathBuilder().addPath(
                            new BezierCurve(
                                    new Pose(12.157, 59.518),
                                    new Pose(34.886, 64.301),
                                    new Pose(54.193, 92.169)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(145), Math.toRadians(140))

                    .build();

            Shoot3togate2 = follower.pathBuilder().addPath(
                            new BezierCurve(
                                    new Pose(54.193, 92.169),
                                    new Pose(37.184, 65.440),
                                    new Pose(12.223, 59.602)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(140), Math.toRadians(145))

                    .build();

            Gate2toshoot4 = follower.pathBuilder().addPath(
                            new BezierCurve(
                                    new Pose(12.223, 59.602),
                                    new Pose(40.425, 64.627),
                                    new Pose(54.048, 92.012)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(145), Math.toRadians(180))

                    .build();

            Shoot4topickup2 = follower.pathBuilder().addPath(
                            new BezierCurve(
                                    new Pose(54.048, 92.012),
                                    new Pose(59.602, 82.681),
                                    new Pose(16.747, 83.831)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))

                    .build();

            Pickup2toshoot5 = follower.pathBuilder().addPath(
                            new BezierCurve(
                                    new Pose(16.747, 83.831),
                                    new Pose(38.416, 86.247),
                                    new Pose(54.229, 92.229)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))

                    .build();

            Shoot5topickup3 = follower.pathBuilder().addPath(
                            new BezierCurve(
                                    new Pose(54.229, 92.229),
                                    new Pose(83.922, 30.880),
                                    new Pose(9.928, 35.988)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))

                    .build();

            Pickup3toshoot6 = follower.pathBuilder().addPath(
                            new BezierCurve(
                                    new Pose(9.928, 35.988),
                                    new Pose(48.223, 60.819),
                                    new Pose(54.422, 91.892)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(140))

                    .build();

            Shoot6topark = follower.pathBuilder().addPath(
                            new BezierCurve(
                                    new Pose(54.422, 91.892),
                                    new Pose(33.934, 93.614),
                                    new Pose(18.169, 104.687)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(140), Math.toRadians(90))

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