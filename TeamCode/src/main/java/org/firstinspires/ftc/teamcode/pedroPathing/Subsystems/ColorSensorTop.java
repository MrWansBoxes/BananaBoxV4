package org.firstinspires.ftc.teamcode.pedroPathing.Subsystems;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ColorSensorTop {

    NormalizedColorSensor colorSensorTop;

    public enum DetectedColor{
        SOMETHING,
        UNKNOWN
    }

    public void init(HardwareMap hwMap){
        colorSensorTop = hwMap.get(NormalizedColorSensor.class,"top_color");
        colorSensorTop.setGain(8);
    }

    public DetectedColor getDetectedColor(Telemetry telemetry){
        NormalizedRGBA colors = colorSensorTop.getNormalizedColors();

        float normRed, normGreen, normBlue;
        normRed = colors.red / colors.alpha;
        normGreen = colors.green / colors.alpha;
        normBlue = colors.blue / colors.alpha;

     //   telemetry.addData("red", normRed);
     //   telemetry.addData("green", normGreen);
        //  telemetry.addData("blue", normBlue);
/*

red, green, blue

Green =0.03, 0.1, 0.088
Purple =0.048, 0.07, >0.12

 */
        if (normRed < 0.09 && normGreen < 0.2) {  // norm RGB values for the green artifact
            return DetectedColor.SOMETHING;
        }
        else {
            return DetectedColor.UNKNOWN;
        }
    }

}
