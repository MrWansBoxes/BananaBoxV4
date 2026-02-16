package org.firstinspires.ftc.teamcode.pedroPathing.Subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import org.firstinspires.ftc.robotcore.external.Telemetry;
//@TeleOp
public class ColorSensorMiddle {

    NormalizedColorSensor colorSensorMiddle;

    public enum DetectedColor {
        SOMETHING,
        UNKNOWN
    }

    public void init(HardwareMap hwMap) {
        colorSensorMiddle = hwMap.get(NormalizedColorSensor.class, "middle_color");
        colorSensorMiddle.setGain(11);
    }

    public DetectedColor getDetectedColor(Telemetry telemetry) {
        NormalizedRGBA colors = colorSensorMiddle.getNormalizedColors();

        float normRed, normGreen, normBlue;
        normRed = colors.red / colors.alpha;
        normGreen = colors.green / colors.alpha;
        normBlue = colors.blue / colors.alpha;

    //    telemetry.addData("red", normRed);
    //    telemetry.addData("green", normGreen);
    //    telemetry.addData("blue", normBlue);

/*

red, green, blue

Green =<0.05, >0.09, <0.1
Purple =>0.04, <0.09, >0.08

 */
        if (normRed > 0.11 || normRed < 0.09) {  // norm RGB values for the green artifact
            return DetectedColor.SOMETHING;
        } else {
            return DetectedColor.UNKNOWN;
        }
    }
}
