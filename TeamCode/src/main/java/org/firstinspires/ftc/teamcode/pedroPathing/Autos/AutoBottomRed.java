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
public class AutoBottomRed extends OpMode {

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
    private int launchState3 = 0;
    private int launchState2 = 0;
    private int launchState1 = 0;
    private Timer pathTimer;
    private Timer launchTimer;
    private boolean isDone;



    @Override
    public void init() {
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(88.91211401425178, 9.254156769596198, Math.toRadians(90)));

        paths = new Paths(follower); // Build paths
        pathTimer = new Timer();
        launchTimer = new Timer();
        intake = hardwareMap.get(DcMotor.class, "intake");
        gate = hardwareMap.get(Servo.class, "gate");
        gate.setPosition(0.3);
        top.init(hardwareMap);
        middle.init(hardwareMap);
        bottom.init(hardwareMap);
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

    private void launch3balls() {  // we call this function every time you want to launch 3 balls
        switch (launchState3) {
            case 0:
                gate.setPosition(0.55);  //0.55 open 0.3 closed I think
                launchTimer.resetTimer();
                launchState3++;
                break;

            case 1:
                if (launchTimer.getElapsedTimeSeconds() > 0.1) {
                    intake.setPower(1);
                    isDone = true;
                    launchTimer.resetTimer();
                }
                break;
        }
    }
    private void launch2balls() {  // we call this function every time you want to launch 2 balls
        switch (launchState2) {
            case 0:
                gate.setPosition(0.55);  //0.55 open 0.3 closed I think
                launchTimer.resetTimer();
                launchState2++;
                break;

            case 1:
                intake.setPower(-0.2);
                if (launchTimer.getElapsedTimeSeconds() > 0.2) {
                    intake.setPower(1);
                    isDone = true;
                    launchTimer.resetTimer();
                }
                break;
        }
    }

    private void launch1ball() {  // we call this function every time you want to launch 1 ball
        switch (launchState1) {
            case 0:
                gate.setPosition(0.55);  //0.55 open 0.3 closed I think
                launchTimer.resetTimer();
                launchState1++;
                break;

            case 1:
                intake.setPower(-0.2);
                if (launchTimer.getElapsedTimeSeconds() > 0.3) {
                    intake.setPower(1);
                    isDone = true;
                    launchTimer.resetTimer();
                }
                break;
        }
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
                                    new Pose(88.912, 9.254),

                                    new Pose(82.983, 23.914)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(90))

                    .build();

            Shoot1topickup1 = follower.pathBuilder().addPath(
                            new BezierCurve(
                                    new Pose(82.983, 23.914),
                                    new Pose(113.215, 8.586),
                                    new Pose(128.438, 8.216)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(360))

                    .build();

            Pickup1tointake1 = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(128.438, 8.216),

                                    new Pose(134.214, 8.164)
                            )
                    ).setTangentHeadingInterpolation()

                    .build();

            Intake1toshoot2 = follower.pathBuilder().addPath(
                            new BezierCurve(
                                    new Pose(134.214, 8.164),
                                    new Pose(110.194, 21.343),
                                    new Pose(82.929, 23.919)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(360), Math.toRadians(90))

                    .build();

            Shoot2topark = follower.pathBuilder().addPath(
                            new BezierCurve(
                                    new Pose(82.929, 23.919),
                                    new Pose(95.163, 13.333),
                                    new Pose(108.660, 11.164)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(360))

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

                shooterActive = !shooterActive;

                follower.followPath(paths.Starttoshoot1,true);
                setPathState(1);
                break;

            case 1:

                if (!follower.isBusy() && detectedColorBottom == ColorSensorBottom.DetectedColor.SOMETHING) {
                    launch3balls();
                }
                else if (!follower.isBusy() && detectedColorMiddle == ColorSensorMiddle.DetectedColor.SOMETHING) {
                    launch2balls();
                }
                else if (!follower.isBusy() && detectedColorTop == ColorSensorTop.DetectedColor.SOMETHING) {
                    launch1ball();
                }
                else {
                    isDone = true;
                }

                if (isDone) {
                    isDone = false;
                    launchState3 = 0;
                    launchState2 = 0;
                    launchState1 = 0;
                    gate.setPosition(0.3);
                    follower.followPath(paths.Shoot1topickup1, true);
                    setPathState(2);

                }
                break;

            case 2:

                if (!follower.isBusy()) {
                    follower.followPath(paths.Pickup1tointake1, 0.7,true);
                    setPathState(3);
                }
                break;

            case 3:

                if (!follower.isBusy()) {
                    intake.setPower(0);
                    follower.followPath(paths.Intake1toshoot2, true);
                    setPathState(4);
                }
                break;

            case 4:

                if (!follower.isBusy() && detectedColorBottom == ColorSensorBottom.DetectedColor.SOMETHING) {
                    launch3balls();
                }
                else if (!follower.isBusy() && detectedColorMiddle == ColorSensorMiddle.DetectedColor.SOMETHING) {
                    launch2balls();
                }
                else if (!follower.isBusy() && detectedColorTop == ColorSensorTop.DetectedColor.SOMETHING) {
                    launch1ball();
                }
                else {
                    isDone = true;
                }

                if (isDone) {
                    isDone = false;
                    launchState3 = 0;
                    launchState2 = 0;
                    launchState1 = 0;

                    follower.followPath(paths.Shoot2topark, true);
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