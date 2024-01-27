package frc.team2412.robot.subsystems;

import com.revrobotics.CANSparkBase.ControlType;
import com.revrobotics.CANSparkBase.IdleMode;
import com.google.errorprone.annotations.OverridingMethodsMustInvokeSuper;
import com.revrobotics.CANSparkFlex;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkLowLevel.MotorType;
import com.revrobotics.SparkAbsoluteEncoder.Type;
import com.revrobotics.SparkAbsoluteEncoder;
import com.revrobotics.SparkPIDController;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.team2412.robot.Hardware;
import pabeles.concurrency.ConcurrencyOps.Reset;

public class LauncherSubsystem extends SubsystemBase {
	// CONSTANTS
	// MOTOR SPEED VALUES
	public static final double SPEAKER_SHOOT_SPEED = 0.5;
	public static final double AMP_SHOOT_SPEED = 0.2;
	public static final double ANGLE_CHANGE_SPEED = 0.15;

	// HARDWARE
	private final CANSparkFlex launcherTopMotor;
	private final CANSparkFlex launcherBottomMotor;
	private final CANSparkFlex launcherAngleMotor;
	private final CANSparkFlex launcherHoodMotor;
	private final RelativeEncoder launcherTopEncoder;
	private final RelativeEncoder launcherBottomEncoder;
	private final SparkAbsoluteEncoder launcherAngleEncoder;
	private final SparkAbsoluteEncoder launcherHoodEncoder;
	private final SparkPIDController launcherAnglePIDController;
	private final SparkPIDController launcherHoodPIDController;

	GenericEntry launcherSpeed =
			Shuffleboard.getTab("Launcher")
					.addPersistent("Launcher Speed", SPEAKER_SHOOT_SPEED)
					.withSize(2, 1)
					.withWidget(BuiltInWidgets.kTextView)	
					.getEntry();
	// Constructor
	public LauncherSubsystem() {

		// MOTOR INSTANCE VARIBLES
		launcherTopMotor = new CANSparkFlex(Hardware.LAUNCHER_TOP_MOTOR_ID, MotorType.kBrushless);
		launcherBottomMotor = new CANSparkFlex(Hardware.LAUNCHER_BOTTOM_MOTOR_ID, MotorType.kBrushless);
		launcherAngleMotor = new CANSparkFlex(Hardware.LAUNCHER_ANGLE_MOTOR_ID, MotorType.kBrushless);
		launcherHoodMotor = new CANSparkFlex(Hardware.LAUNCHER_HOOD_MOTOR_ID, MotorType.kBrushless);
		launcherTopEncoder = launcherTopMotor.getEncoder();
		launcherBottomEncoder = launcherBottomMotor.getEncoder();
		launcherAngleEncoder =
				launcherAngleMotor.getAbsoluteEncoder(Type.kDutyCycle);
		launcherHoodEncoder =
				launcherHoodMotor.getAbsoluteEncoder(SparkAbsoluteEncoder.Type.kDutyCycle);
		launcherAnglePIDController = launcherAngleMotor.getPIDController();
		launcherHoodPIDController = launcherHoodMotor.getPIDController();

		launcherAnglePIDController.setFeedbackDevice(launcherAngleEncoder);
		launcherHoodPIDController.setFeedbackDevice(launcherHoodEncoder);
		configMotors();
	}

	public void configMotors(){
		launcherTopMotor.restoreFactoryDefaults();
		launcherBottomMotor.restoreFactoryDefaults();
		launcherAngleMotor.restoreFactoryDefaults();
		launcherHoodMotor.restoreFactoryDefaults();

		//idle mode (wow)
		launcherTopMotor.setIdleMode(IdleMode.kBrake);
		launcherBottomMotor.setIdleMode(IdleMode.kBrake);
		launcherAngleMotor.setIdleMode(IdleMode.kBrake);
		launcherHoodMotor.setIdleMode(IdleMode.kBrake);


		//current limit
		launcherTopMotor.setSmartCurrentLimit(20);
		launcherBottomMotor.setSmartCurrentLimit(20);
		launcherAngleMotor.setSmartCurrentLimit(20);
		launcherHoodMotor.setSmartCurrentLimit(20);

		launcherTopMotor.burnFlash();
		launcherBottomMotor.burnFlash();
		launcherAngleMotor.burnFlash();
		launcherHoodMotor.burnFlash();
	}
	
	// stop specific motor method
	public void stopMotor(CANSparkFlex motor) {
		motor.stopMotor();
	}

	public void shoot(double speed) {
		launcherTopMotor.set(speed);
		launcherBottomMotor.set(-speed);
	}
	// returns the degrees of the angle of the launcher
	public double getAngle() {
		// get position returns a double in the form of rotations
		return Units.rotationsToDegrees(launcherAngleEncoder.getPosition());
	}

	public void setAngle(double angle) {
		launcherAnglePIDController.setReference(Units.degreesToRotations(angle), ControlType.kPosition);
	}

	public double getHoodAngle() {
		return Units.rotationsToDegrees(launcherHoodEncoder.getPosition());
	}

	public void setHoodAngle(double angle) {
		launcherHoodPIDController.setReference(Units.degreesToRotations(angle), ControlType.kPosition);
	}

	public void DistanceModifier(double distance, double modifier) {
		double speed = distance / modifier;
	}
	@Override
	public void periodic(){
		launcherSpeed.setDouble(launcherTopEncoder.getVelocity());
	}
}
