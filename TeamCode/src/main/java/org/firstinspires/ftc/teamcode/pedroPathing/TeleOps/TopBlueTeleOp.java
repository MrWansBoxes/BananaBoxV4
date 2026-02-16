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
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;


import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.pedroPathing.Subsystems.ColorSensorBottom;
import org.firstinspires.ftc.teamcode.pedroPathing.Subsystems.ColorSensorMiddle;
import org.firstinspires.ftc.teamcode.pedroPathing.Subsystems.ColorSensorTop;
import org.firstinspires.ftc.teamcode.pedroPathing.Subsystems.ShooterSubsystem;

import java.util.function.Supplier;

@Configurable
@TeleOp
public class TopBlueTeleOp extends OpMode {

    int intakeFlag = 0;
    int gateFlag = 0;

    ColorSensorBottom bottom = new ColorSensorBottom();   // gets the color sensor class
    ColorSensorMiddle middle = new ColorSensorMiddle();
    ColorSensorTop top = new ColorSensorTop();
    ColorSensorBottom.DetectedColor detectedColorBottom;
    ColorSensorMiddle.DetectedColor detectedColorMiddle;
    ColorSensorTop.DetectedColor detectedColorTop;
    private DcMotor intake;
    private Servo /*liftleft, liftright,*/ gate;  // servos
    private Follower follower;
    public static Pose startingPose;
    private boolean automatedDrive;
    private boolean slowMode = false;
    private double slowModeMultiplier = 0.5;
    private Supplier<PathChain> pathChain;

    private TelemetryManager telemetryM;
    private ShooterSubsystem shooter;
    private double lastTime = 0.0;



    @Override
    public void init() {
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(startingPose == null ? new Pose(72,72,90) : startingPose);
        follower.update();
        top.init(hardwareMap);
        middle.init(hardwareMap);
        bottom.init(hardwareMap);
        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();
        intake = hardwareMap.get(DcMotor.class, "intake");
        // liftleft = hardwareMap.get(Servo.class, "liftleft");
        //   liftright = hardwareMap.get(Servo.class, "liftright");
        gate = hardwareMap.get(Servo.class, "gate");

        // Shooter subsystem
        shooter = new ShooterSubsystem(hardwareMap);

        // Example path (optional)
        pathChain = () -> follower.pathBuilder()
                .addPath(new Path(new BezierCurve(follower::getPose, new Pose(45, 98))))
                .setHeadingInterpolation(HeadingInterpolator.linearFromPoint(follower::getHeading, Math.toRadians(45), 0.8))
                .build();
        telemetry.addLine("Initialized");

        gate.setPosition(0.55);
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

        follower.update();
        telemetryM.update();

        // TeleOp driving
        if (!automatedDrive) {
            //Make the last parameter false for field-centric
            //In case the drivers want to use a "slowMode" you can scale the vectors
            //This is the normal version to use in the TeleOp
            if (!slowMode) follower.setTeleOpDrive(
                    -gamepad1.left_stick_y,
                    -gamepad1.left_stick_x,
                    -gamepad1.right_stick_x,
                    true // Robot Centric
            );
                //This is how it looks with slowMode on
            else follower.setTeleOpDrive(
                    -gamepad1.left_stick_y * slowModeMultiplier,
                    -gamepad1.left_stick_x * slowModeMultiplier,
                    -gamepad1.right_stick_x * slowModeMultiplier,
                    true // Robot Centric
            );
        }


        detectedColorTop = top.getDetectedColor(telemetry);
        telemetry.addData("Detected Color Top", detectedColorTop);


        detectedColorMiddle = middle.getDetectedColor(telemetry);
        telemetry.addData("Detected Color Middle", detectedColorMiddle);


        detectedColorBottom = bottom.getDetectedColor(telemetry);
        telemetry.addData("Detected Color Bottom", detectedColorBottom);

        // if (detectedColorBottom == ColorSensorBottom.DetectedColor.GREEN)

        // launcher update
            double currentTime = getRuntime();
            double dt = currentTime - lastTime;
            lastTime = currentTime;

            shooter.update(follower.getPose(), follower.getVelocity(), dt);




        if (gamepad1.dpadLeftWasPressed()) {
            follower.followPath(pathChain.get());
            automatedDrive = true;
        }
        if (automatedDrive && (gamepad1.dpadRightWasPressed() || !follower.isBusy())) {
            follower.startTeleopDrive();
            automatedDrive = false;
        }

        if (gamepad1.aWasPressed()) {   // intake in
            if (intakeFlag == 0) {
                intake.setPower(1);
                intakeFlag = 1;
            }
            else if (intakeFlag == 1){
                intake.setPower(0);
                intakeFlag = 0;
            }
        }

        if (gamepad1.bWasPressed()) {   // intake out
            if (intakeFlag == 0) {
                intake.setPower(-1);
                intakeFlag = -1;
            }
            else if (intakeFlag == -1){
                intake.setPower(0);
                intakeFlag = 0;
            }
        }

        if (gamepad1.yWasPressed()) {   // gate open
            if (gateFlag == 0) {
                gate.setPosition(0.3);
                gateFlag = 1;
            }
            else if (gateFlag == 1) {
                gate.setPosition(0.5);
                gateFlag = 0;
            }
        }

    }
}
