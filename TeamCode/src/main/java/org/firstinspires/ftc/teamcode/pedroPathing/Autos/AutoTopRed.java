package org.firstinspires.ftc.teamcode.pedroPathing.Autos;
import com.pedropathing.util.Timer;
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
public class AutoTopRed extends OpMode {

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
    private Timer pathTimer;
    private Timer shootTimer;
    private double lastTime = 0.0;
    private boolean shooterActive = false;

    @Override
    public void init() {
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(130.02409638554218, 112.86746987951807, Math.toRadians(90)));

        paths = new Paths(follower); // Build paths
        intake = hardwareMap.get(DcMotor.class, "intake");
        gate = hardwareMap.get(Servo.class, "gate");
        bottom.init(hardwareMap);
        middle.init(hardwareMap);
        top.init(hardwareMap);
        pathTimer = new Timer();
        shootTimer = new Timer();
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
        detectedColorTop = top.getDetectedColor(telemetry);
        telemetry.addData("Detected Color Top", detectedColorTop);
        detectedColorMiddle = middle.getDetectedColor(telemetry);
        telemetry.addData("Detected Color Middle", detectedColorMiddle);
        detectedColorBottom = bottom.getDetectedColor(telemetry);
        telemetry.addData("Detected Color Bottom", detectedColorBottom);

        telemetry.update();
        panelsTelemetry.update(telemetry);
    }


    public static class Paths {
        public PathChain Starttoshoot1;
        public PathChain ShootPretopickup1;
        public PathChain Pickup1toshoot2;
        public PathChain Shoot2tograbfromgate;
        public PathChain Gate1toshoot3;
        public PathChain Shoot3topickup2;
        public PathChain Pickup2toshoot4;
        public PathChain Shoot4topickup3;
        public PathChain Pickup3toshoot5;
        public PathChain Shoot5topark;

        public Paths(Follower follower) {
            Starttoshoot1 = follower.pathBuilder().addPath(
                            new BezierCurve(
                                    new Pose(130.024, 112.867),
                                    new Pose(111.952, 94.759),
                                    new Pose(89.928, 92.096)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(0))

                    .build();

            ShootPretopickup1 = follower.pathBuilder().addPath(
                            new BezierCurve(
                                    new Pose(89.928, 92.096),
                                    new Pose(76.886, 55.488),
                                    new Pose(131.641, 59.590)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))

                    .build();

            Pickup1toshoot2 = follower.pathBuilder().addPath(
                            new BezierCurve(
                                    new Pose(131.641, 59.590),
                                    new Pose(117.102, 51.102),
                                    new Pose(98.102, 88.898),
                                    new Pose(89.892, 92.060)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(40))

                    .build();

            Shoot2tograbfromgate = follower.pathBuilder().addPath(
                            new BezierCurve(
                                    new Pose(89.892, 92.060),
                                    new Pose(113.542, 58.994),
                                    new Pose(131.843, 59.518)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(40), Math.toRadians(35))

                    .build();

            Gate1toshoot3 = follower.pathBuilder().addPath(
                            new BezierCurve(
                                    new Pose(131.843, 59.518),
                                    new Pose(109.114, 64.301),
                                    new Pose(89.807, 92.169)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(35), Math.toRadians(40))

                    .build();

            Shoot3topickup2 = follower.pathBuilder().addPath(
                            new BezierCurve(
                                    new Pose(89.807, 92.169),
                                    new Pose(84.398, 82.681),
                                    new Pose(125.142, 83.831)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(40), Math.toRadians(0))

                    .build();

            Pickup2toshoot4 = follower.pathBuilder().addPath(
                            new BezierCurve(
                                    new Pose(125.142, 83.831),
                                    new Pose(105.584, 86.247),
                                    new Pose(89.771, 92.229)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))

                    .build();

            Shoot4topickup3 = follower.pathBuilder().addPath(
                            new BezierCurve(
                                    new Pose(89.771, 92.229),
                                    new Pose(60.078, 30.880),
                                    new Pose(132.594, 35.988)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))

                    .build();

            Pickup3toshoot5 = follower.pathBuilder().addPath(
                            new BezierCurve(
                                    new Pose(132.594, 35.988),
                                    new Pose(95.777, 60.819),
                                    new Pose(89.578, 91.892)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(40))

                    .build();

            Shoot5topark = follower.pathBuilder().addPath(
                            new BezierCurve(
                                    new Pose(89.578, 91.892),
                                    new Pose(88.861, 98.241),
                                    new Pose(83.422, 103.530)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(40), Math.toRadians(90))

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
            case 0:
                shooterActive = true;
                follower.followPath(paths.Starttoshoot1, true);
                setPathState(1);
                break;

            case 1:

                if (!follower.isBusy()) {
                    follower.followPath(paths.ShootPretopickup1, true);
                    setPathState(2);
                }
                break;

            case 2:

                if (!follower.isBusy()) {
                    follower.followPath(paths.Pickup1toshoot2, true);
                    setPathState(3);
                }
                break;

            case 3:

                if (!follower.isBusy()) {
                    follower.followPath(paths.Shoot2tograbfromgate, true);
                    setPathState(4);
                }
                break;

            case 4:

                if (!follower.isBusy()) {
                    follower.followPath(paths.Gate1toshoot3, true);
                    setPathState(5);
                }
                break;

            case 5:

                if (!follower.isBusy()) {
                    follower.followPath(paths.Shoot3topickup2, true);
                    setPathState(6);
                }
                break;

            case 6:

                if (!follower.isBusy()) {
                    follower.followPath(paths.Pickup2toshoot4, true);
                    setPathState(7);
                }
                break;

            case 7:

                if (!follower.isBusy()) {
                    follower.followPath(paths.Shoot4topickup3, true);
                    setPathState(8);
                }
                break;

            case 8:

                if (!follower.isBusy()) {
                    follower.followPath(paths.Pickup3toshoot5, true);
                    setPathState(9);
                }
                break;

            case 9:

                if (!follower.isBusy()) {
                    follower.followPath(paths.Shoot5topark, true);
                    setPathState(-1);
                }
                break;
        }
        return pathState;
    }
    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }
}