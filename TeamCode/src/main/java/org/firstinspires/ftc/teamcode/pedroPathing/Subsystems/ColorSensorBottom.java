package org.firstinspires.ftc.teamcode.pedroPathing.Subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import org.firstinspires.ftc.robotcore.external.Telemetry;
//@TeleOp
public class ColorSensorBottom {

    NormalizedColorSensor colorSensorBottom;

    public enum DetectedColor{
        SOMETHING,
        UNKNOWN
    }

    public void init(HardwareMap hwMap){
        colorSensorBottom = hwMap.get(NormalizedColorSensor.class,"bottom_color");
        colorSensorBottom.setGain(8);
    }

    public DetectedColor getDetectedColor(Telemetry telemetry){
        NormalizedRGBA colors = colorSensorBottom.getNormalizedColors();

        float normRed, normGreen, normBlue;
        normRed = colors.red / colors.alpha;
        normGreen = colors.green / colors.alpha;
        normBlue = colors.blue / colors.alpha;

telemetry.addData("red", normRed);
telemetry.addData("green", normGreen);
telemetry.addData("blue", normBlue);

/*

red, green, blue

Green =<0.05, >0.09, <0.1
Purple =>0.04, <0.09, >0.08

 */
        if (normRed < 0.14 || normRed > 0.153){  // norm RGB values for the green artifact
            return DetectedColor.SOMETHING;
        }
        else {
            return DetectedColor.UNKNOWN;
        }
    }

}
