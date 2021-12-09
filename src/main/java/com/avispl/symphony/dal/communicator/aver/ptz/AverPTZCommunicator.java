/*
 * Copyright (c) 2021 AVI-SPL Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.aver.ptz;

import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.CLOSE_PARENTHESIS;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.DEFAULT_PRESET;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.DELAY_PERIOD;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.FAKE_COMPLETION;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.HASH;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.IRIS_LEVELS;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.LABEL_END_EXPOSURE_VALUE;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.LABEL_END_GAIN_LEVEL;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.LABEL_END_GAIN_LIMIT_LEVEL;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.LABEL_END_IRIS_LEVEL;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.LABEL_END_SHUTTER_SPEED;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.LABEL_START_EXPOSURE_VALUE;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.LABEL_START_GAIN_LEVEL;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.LABEL_START_GAIN_LIMIT_LEVEL;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.LABEL_START_IRIS_LEVEL;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.LABEL_START_SHUTTER_SPEED;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.MINUS;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.NONE_VALUE;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.PLUS;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.POWER_OFF_STATUS;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.POWER_ON_STATUS;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.RANGE_END_EXPOSURE_VALUE;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.RANGE_END_GAIN_LEVEL;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.RANGE_END_GAIN_LIMIT_LEVEL;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.RANGE_END_IRIS_LEVEL;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.RANGE_END_SHUTTER_SPEED;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.RANGE_START_EXPOSURE_VALUE;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.RANGE_START_GAIN_LEVEL;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.RANGE_START_GAIN_LIMIT_LEVEL;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.RANGE_START_IRIS_LEVEL;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.RANGE_START_SHUTTER_SPEED;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.SHUTTER_VALUES;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.SWITCH_STATUS_OFF;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.SWITCH_STATUS_ON;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZUtils.buildSendPacket;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZUtils.convertIntToByteArray;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZUtils.convertOneByteNumberToTwoBytesArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

import org.springframework.util.CollectionUtils;

import com.avispl.symphony.api.common.error.ResourceConflictException;
import com.avispl.symphony.api.dal.control.Controller;
import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.Statistics;
import com.avispl.symphony.api.dal.error.CommandFailureException;
import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.api.dal.monitor.Monitorable;
import com.avispl.symphony.dal.communicator.aver.ptz.dto.DeviceInfo;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.Index;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.ReplyStatus;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.StatisticsProperty;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.PayloadCategory;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.PayloadType;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.Prefix;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.command.Command;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.command.CommandType;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.AEMode;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.BGainControl;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.BacklightStatus;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.FocusControl;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.FocusMode;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.PanTiltDrive;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.PowerStatus;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.PresetControl;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.RGainControl;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.SlowPanTiltStatus;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.SlowShutterStatus;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.WBMode;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.ZoomControl;

/**
 * Aver PTZ Camera Adapter
 * Company: Aver
 *
 * Properties are divided into groups:
 * Zoom,Focus,Exposure,ImageProcess
 *
 * Supported features are:
 *
 * Controlling:
 * - Power
 * - Zoom
 * - Focus
 * - Image Settings (AE, WB)
 * - Presets
 * - Position
 *
 * Monitoring:
 * - Preset recall and status feedback
 * - Device Mfg
 * - Device Model
 * - Serial Number
 * - Firmware Version
 *
 * @author Harry
 * @version 1.0
 * @since 1.0
 */
public class AverPTZCommunicator extends UDPCommunicator implements Controller, Monitorable {
	private String cameraID = "1";
	private String panSpeed = "1";
	private String tiltSpeed = "1";
	private String zoomSpeed = "1";
	private String focusSpeed = "1";
	private int cameraIDInt = 1;
	private int panSpeedInt = 1;
	private int tiltSpeedInt = 1;
	private int zoomSpeedInt = 1;
	private int focusSpeedInt = 1;
	private int sequenceNumber = 0;
	private int currentPreset = -1;
	private AverPTZRestCommunicator restCommunicator;
	private DeviceInfo deviceInfo;
	private long nextMonitoringCycleTimestamp = System.currentTimeMillis();
	private String powerStatusMessage = null;

	/**
	 * Constructor set command error and success list to be used as well the default camera ID
	 */
	public AverPTZCommunicator() {
		super();
		// set list of command success strings (included at the end of response when command succeeds, typically ending with command prompt)
		this.setCommandSuccessList(Collections.singletonList(UDPCommunicator.getHexByteString(ReplyStatus.COMPLETION.getCode())));

		// set list of error response strings (included at the end of response when command fails, typically ending with command prompt)
		this.setCommandErrorList(Arrays.asList(
				UDPCommunicator.getHexByteString(ReplyStatus.SYNTAX_ERROR_CONTROL.getCode()),
				UDPCommunicator.getHexByteString(ReplyStatus.SYNTAX_ERROR_INQUIRY.getCode()),
				UDPCommunicator.getHexByteString(ReplyStatus.COMMAND_BUFFER_FULL_CONTROL.getCode()),
				UDPCommunicator.getHexByteString(ReplyStatus.COMMAND_BUFFER_FULL_INQUIRY.getCode()),
				UDPCommunicator.getHexByteString(ReplyStatus.NO_SOCKET_CONTROL.getCode()),
				UDPCommunicator.getHexByteString(ReplyStatus.NO_SOCKET_INQUIRY.getCode()),
				UDPCommunicator.getHexByteString(ReplyStatus.COMMAND_NOT_EXECUTABLE_CONTROL.getCode()),
				UDPCommunicator.getHexByteString(ReplyStatus.COMMAND_NOT_EXECUTABLE_INQUIRY.getCode())
		));
	}

	/**
	 * Retrieves {@code {@link #cameraID}}
	 *
	 * @return value of {@link #cameraID}
	 */
	public String getCameraID() {
		return cameraID;
	}

	/**
	 * Sets {@code cameraID}
	 *
	 * @param cameraID the {@code java.lang.String} field
	 */
	public void setCameraID(String cameraID) {
		this.cameraID = cameraID;
	}

	/**
	 * Retrieves {@code {@link #panSpeed}}
	 *
	 * @return value of {@link #panSpeed}
	 */
	public String getPanSpeed() {
		return panSpeed;
	}

	/**
	 * Sets {@code panSpeed}
	 *
	 * @param panSpeed the {@code java.lang.String} field
	 */
	public void setPanSpeed(String panSpeed) {
		this.panSpeed = panSpeed;
	}

	/**
	 * Retrieves {@code {@link #tiltSpeed}}
	 *
	 * @return value of {@link #tiltSpeed}
	 */
	public String getTiltSpeed() {
		return tiltSpeed;
	}

	/**
	 * Sets {@code tiltSpeed}
	 *
	 * @param tiltSpeed the {@code java.lang.String} field
	 */
	public void setTiltSpeed(String tiltSpeed) {
		this.tiltSpeed = tiltSpeed;
	}

	/**
	 * Retrieves {@code {@link #zoomSpeed}}
	 *
	 * @return value of {@link #zoomSpeed}
	 */
	public String getZoomSpeed() {
		return zoomSpeed;
	}

	/**
	 * Sets {@code zoomSpeed}
	 *
	 * @param zoomSpeed the {@code java.lang.String} field
	 */
	public void setZoomSpeed(String zoomSpeed) {
		this.zoomSpeed = zoomSpeed;
	}

	/**
	 * Retrieves {@code {@link #focusSpeed}}
	 *
	 * @return value of {@link #focusSpeed}
	 */
	public String getFocusSpeed() {
		return focusSpeed;
	}

	/**
	 * Sets {@code focusSpeed}
	 *
	 * @param focusSpeed the {@code java.lang.String} field
	 */
	public void setFocusSpeed(String focusSpeed) {
		this.focusSpeed = focusSpeed;
	}

	/**
	 * This method used to init AverRestCommunicator
	 * Need to split into method for testing
	 */
	public void initAverRestCommunicator() throws Exception {
		restCommunicator = new AverPTZRestCommunicator();
		restCommunicator.setLogin(this.getLogin());
		restCommunicator.setPassword(this.getPassword());
		restCommunicator.setHost(this.getHost());
		restCommunicator.init();
	}

	/**
	 * {@inheritdoc}
	 * This method is recalled by Symphony to control specific property
	 *
	 * @param controllableProperty This is the property to be controlled
	 */
	@Override
	public void controlProperty(ControllableProperty controllableProperty) throws IOException {
		if (System.currentTimeMillis() < nextMonitoringCycleTimestamp) {
			throw new IllegalStateException("Cannot control while power is " + powerStatusMessage);
		}

		String property = controllableProperty.getProperty();
		String value = String.valueOf(controllableProperty.getValue());

		if (this.logger.isDebugEnabled()) {
			this.logger.debug("controlProperty property " + property);
			this.logger.debug("controlProperty value " + value);
		}

		String[] splitProperty = property.split(String.valueOf(HASH));
		Command command = Command.getByName(splitProperty[0]);

		switch (command) {
			case POWER: {
				if (value.equals(SWITCH_STATUS_ON)) {
					powerStatusMessage = POWER_ON_STATUS;
					performControl(PayloadCategory.CAMERA, Command.POWER, PowerStatus.ON.getCode());
				} else if (value.equals(SWITCH_STATUS_OFF)) {
					powerStatusMessage = POWER_OFF_STATUS;
					performControl(PayloadCategory.CAMERA, Command.POWER, PowerStatus.OFF.getCode());
				}
				// set next monitoring cycle timestamp plus 45s due to the device will not responsive in this time
				nextMonitoringCycleTimestamp = System.currentTimeMillis() + DELAY_PERIOD;
				break;
			}
			case ZOOM: {
				if (Objects.equals(splitProperty[1], ZoomControl.TELE.getName())) {
					performControl(PayloadCategory.CAMERA, Command.ZOOM, (byte) (ZoomControl.TELE.getCode() + zoomSpeedInt));
				} else if (Objects.equals(splitProperty[1], ZoomControl.WIDE.getName())) {
					performControl(PayloadCategory.CAMERA, Command.ZOOM, (byte) (ZoomControl.WIDE.getCode() + zoomSpeedInt));
				}
				performControl(PayloadCategory.CAMERA, Command.ZOOM, ZoomControl.STOP.getCode());
				break;
			}
			case FOCUS: {
				// (1)Name -> Split string by ")" and get the second value of slit string
				String focusControlName = splitProperty[1].split(CLOSE_PARENTHESIS, 2)[1];

				if (Objects.equals(focusControlName, Command.FOCUS_MODE.getName())) {
					if (Objects.equals(value, SWITCH_STATUS_ON)) {
						performControl(PayloadCategory.CAMERA, Command.FOCUS_MODE, FocusMode.MANUAL.getCode());
					} else if (Objects.equals(value, SWITCH_STATUS_OFF)) {
						performControl(PayloadCategory.CAMERA, Command.FOCUS_MODE, FocusMode.AUTO.getCode());
					}
					break;
				}

				if (Objects.equals(focusControlName, Command.FOCUS_ONE_PUSH.getName())) {
					performControl(PayloadCategory.CAMERA, Command.FOCUS_ONE_PUSH);
					break;
				}

				if (Objects.equals(focusControlName, FocusControl.FAR.getName())) {
					performControl(PayloadCategory.CAMERA, Command.FOCUS, (byte) (FocusControl.FAR.getCode() + focusSpeedInt));
				} else if (Objects.equals(focusControlName, FocusControl.NEAR.getName())) {
					performControl(PayloadCategory.CAMERA, Command.FOCUS, (byte) (FocusControl.NEAR.getCode() + focusSpeedInt));
				}
				performControl(PayloadCategory.CAMERA, Command.FOCUS, FocusControl.STOP.getCode());
				break;
			}
			case EXPOSURE: {
				Command exposureCommand = Command.getByName(splitProperty[1]);
				exposureControl(value, exposureCommand);
				break;
			}
			case IMAGE_PROCESS: {
				imageProcessControl(value, splitProperty);
				break;
			}
			case PAN_TILT_DRIVE: {
				// (1)Name -> Split string by ")" and get the second value of slit string
				String panTiltDriveControlName = splitProperty[1].split(CLOSE_PARENTHESIS, 2)[1];

				if (Objects.equals(panTiltDriveControlName, Command.PAN_TILT_HOME.getName())) {
					performControl(PayloadCategory.PAN_TILTER, Command.PAN_TILT_HOME);
					break;
				} else if (Objects.equals(panTiltDriveControlName, Command.SLOW_PAN_TILT.getName())) {
					if (Objects.equals(value, SWITCH_STATUS_ON)) {
						performControl(PayloadCategory.PAN_TILTER, Command.SLOW_PAN_TILT, SlowPanTiltStatus.ON.getCode());
					} else if (Objects.equals(value, SWITCH_STATUS_OFF)) {
						performControl(PayloadCategory.PAN_TILTER, Command.SLOW_PAN_TILT, SlowPanTiltStatus.OFF.getCode());
					}
					break;
				}

				PanTiltDrive pantTiltDrive = PanTiltDrive.getByName(panTiltDriveControlName);
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				outputStream.write(new byte[] { (byte) panSpeedInt, (byte) tiltSpeedInt });
				outputStream.write(pantTiltDrive.getCode());
				performControl(PayloadCategory.PAN_TILTER, Command.PAN_TILT_DRIVE, outputStream.toByteArray());

				outputStream = new ByteArrayOutputStream();
				outputStream.write(new byte[] { (byte) panSpeedInt, (byte) tiltSpeedInt });
				outputStream.write(PanTiltDrive.STOP.getCode());
				performControl(PayloadCategory.PAN_TILTER, Command.PAN_TILT_DRIVE, outputStream.toByteArray());
				break;
			}
			case PRESET: {
				// (1)Name -> Split string by ")" and get the second value of slit string
				String presetControlName = splitProperty[1].split(CLOSE_PARENTHESIS, 2)[1];

				if (Objects.equals(presetControlName, PresetControl.PRESET_VALUE.getName())) {
					try {
						currentPreset = Integer.parseInt(value);
					} catch (NumberFormatException e) {
						// value = "Please select a preset to control"
						currentPreset = -1;
					}
					break;
				}

				if (currentPreset == -1) {
					throw new IllegalArgumentException(DEFAULT_PRESET);
				}

				if (Objects.equals(presetControlName, PresetControl.SET.getName())) {
					performControl(PayloadCategory.CAMERA, Command.PRESET, PresetControl.SET.getCode(), (byte) currentPreset);
				} else if (Objects.equals(presetControlName, PresetControl.RECALL.getName())) {
					performControl(PayloadCategory.CAMERA, Command.PRESET, PresetControl.RECALL.getCode(), (byte) currentPreset);
				}

				// Reset to default preset value each time set/recall a preset
				currentPreset = -1;
				break;
			}
			default: {
				throw new IllegalStateException("Unexpected value: " + command);
			}
		}
	}

	/**
	 * {@inheritdoc}
	 * This method is recalled by Symphony to control a list of properties
	 *
	 * @param controllableProperties This is the list of properties to be controlled
	 */
	@Override
	public void controlProperties(List<ControllableProperty> controllableProperties) throws IOException {
		if (CollectionUtils.isEmpty(controllableProperties)) {
			throw new IllegalArgumentException("AverCommunicator: Controllable properties cannot be null or empty");
		}

		for (ControllableProperty controllableProperty : controllableProperties) {
			controlProperty(controllableProperty);
		}
	}

	/**
	 * {@inheritdoc}
	 * This method is recalled by Symphony to get the list of statistics to be displayed
	 *
	 * @return List<Statistics> This return the list of statistics.
	 */
	@Override
	public List<Statistics> getMultipleStatistics() {
		final ExtendedStatistics extStats = new ExtendedStatistics();
		final Map<String, String> stats = new HashMap<>();
		final List<AdvancedControllableProperty> advancedControllableProperties = new ArrayList<>();
		final StringBuilder errorMessages = new StringBuilder();

		tryParseIntAdapterProperties(errorMessages);
		checkOutOfRange(errorMessages);

		if (errorMessages.toString().length() > 0) {
			throw new IllegalArgumentException(errorMessages.toString());
		}

		if (restCommunicator == null) {
			try {
				initAverRestCommunicator();
				deviceInfo = this.restCommunicator.getDeviceInfo();
			} catch (Exception e) {
				if (this.logger.isErrorEnabled()) {
					this.logger.error("error: Cannot get data from Rest communicator: " + this.host + " port: " + this.port);
				}
				throw new ResourceNotReachableException("Aver rest communicator not reachable for getting data", e);
			}
		}

		// Monitoring capabilities
		populateMonitorCapabilities(stats);

		if (System.currentTimeMillis() < nextMonitoringCycleTimestamp) {
			// If in monitoring cycle -> do not render controllable properties
			stats.put(Command.POWER_STATUS.getName(), powerStatusMessage);
		} else {
			// Reset sequence number to 0 if it reaches the max value of integer
			// (need to check it before all command can be performed)
			if (sequenceNumber == Integer.MAX_VALUE - Command.values().length) {
				sequenceNumber = 0;
			}

			// Control capabilities
			populateControlCapabilities(stats, advancedControllableProperties);
		}

		extStats.setStatistics(stats);
		extStats.setControllableProperties(advancedControllableProperties);

		return Collections.singletonList(extStats);
	}

	/**
	 * This method is used for parse adapter properties from String to int value
	 *
	 * @param errorMessages is the error messages of properties when parse fail
	 */
	private void tryParseIntAdapterProperties(StringBuilder errorMessages) {
		try {
			cameraIDInt = Integer.parseInt(cameraID);
		} catch (NumberFormatException e) {
			errorMessages.append("Camera ID with value ").append(this.cameraID).append(" is wrong format of number. ");
		}

		try {
			panSpeedInt = Integer.parseInt(panSpeed);
		} catch (NumberFormatException e) {
			errorMessages.append("Pan speed with value ").append(this.panSpeed).append(" is wrong format of number. ");
		}

		try {
			tiltSpeedInt = Integer.parseInt(tiltSpeed);
		} catch (NumberFormatException e) {
			errorMessages.append("Tilt speed with value ").append(this.tiltSpeed).append(" is wrong format of number. ");
		}

		try {
			focusSpeedInt = Integer.parseInt(focusSpeed);
		} catch (NumberFormatException e) {
			errorMessages.append("Focus speed with value ").append(this.focusSpeed).append(" is wrong format of number. ");
		}

		try {
			zoomSpeedInt = Integer.parseInt(zoomSpeed);
		} catch (NumberFormatException e) {
			errorMessages.append("Zoom speed with value ").append(this.zoomSpeed).append(" is wrong format of number. ");
		}
	}

	/**
	 * This method is used for check adapter properties are out of range of not
	 *
	 * @param errorMessages is the error messages of properties when out of range
	 */
	private void checkOutOfRange(StringBuilder errorMessages) {
		if (this.cameraIDInt < 1 || this.cameraIDInt > 7) {
			errorMessages.append("Camera ID with value ").append(this.cameraID).append(" is out of range. Camera ID must between 1 and 7. ");
		}

		if (this.panSpeedInt < 1 || this.panSpeedInt > 24) {
			errorMessages.append("Pan speed with value ").append(this.panSpeed).append(" is out of range. Pan speed must between 1 and 24. ");
		}

		if (this.tiltSpeedInt < 1 || this.tiltSpeedInt > 24) {
			errorMessages.append("Tilt speed with value ").append(this.tiltSpeed).append(" is out of range. Tilt speed must between 1 and 24. ");
		}

		if (this.zoomSpeedInt < 0 || this.zoomSpeedInt > 7) {
			errorMessages.append("Zoom speed with value ").append(this.zoomSpeed).append(" is out of range. Zoom speed must between 0 and 7. ");
		}

		if (this.focusSpeedInt < 0 || this.focusSpeedInt > 7) {
			errorMessages.append("Focus speed with value ").append(this.focusSpeed).append(" is out of range. Focus speed must between 0 and 7.");
		}
	}

	/**
	 * This method is used for populate all monitoring properties:
	 * <li>Device MFG</li>
	 * <li>Device Model</li>
	 * <li>Device serial number</li>
	 * <li>Device firmware version</li>
	 * <li>Device last preset recalled</li>
	 *
	 * @param stats is the map that store all statistics
	 */
	private void populateMonitorCapabilities(Map<String, String> stats) {
		if (deviceInfo == null) {
			throw new ResourceConflictException("Cannot get device information from Rest API");
		}
		stats.put(StatisticsProperty.DEVICE_INFORMATION.getName() + HASH + StatisticsProperty.DEVICE_MFG.getName(), deviceInfo.getDeviceMfg());
		stats.put(StatisticsProperty.DEVICE_INFORMATION.getName() + HASH + StatisticsProperty.DEVICE_MODEL.getName(), deviceInfo.getDeviceModel());
		stats.put(StatisticsProperty.DEVICE_INFORMATION.getName() + HASH + StatisticsProperty.DEVICE_SERIAL_NUMBER.getName(), deviceInfo.getDeviceSerialNumber());
		stats.put(StatisticsProperty.DEVICE_INFORMATION.getName() + HASH + StatisticsProperty.DEVICE_FIRMWARE_VERSION.getName(), deviceInfo.getDeviceFirmwareVersion());
	}

	/**
	 * This method is used for populate all controlling properties:
	 * <li>Power</li>
	 * <li>Zoom</li>
	 * <li>Focus</li>
	 * <li>AE</li>
	 * <li>WB</li>
	 * <li>Pan tilt drive</li>
	 * <li>Preset</li>
	 *
	 * @param stats is the map that store all statistics
	 * @param advancedControllableProperties is the list that store all controllable properties
	 */
	private void populateControlCapabilities(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		// Getting power status from device
		String powerStatus = getPowerStatus();

		if (Objects.equals(powerStatus, NONE_VALUE)) {
			stats.put(Command.POWER.getName(), NONE_VALUE);
			return;
		}

		stats.put(Command.POWER.getName(), "");

		if (Objects.equals(powerStatus, PowerStatus.OFF.getName())) {
			advancedControllableProperties.add(createSwitch(Command.POWER.getName(), 0, PowerStatus.OFF.getName(), PowerStatus.ON.getName()));
		} else if (Objects.equals(powerStatus, PowerStatus.ON.getName())) {
			advancedControllableProperties.add(createSwitch(Command.POWER.getName(), 1, PowerStatus.OFF.getName(), PowerStatus.ON.getName()));

			// Zoom control
			populateZoomControl(stats, advancedControllableProperties);

			// Focus control
			populateFocusControl(stats, advancedControllableProperties);

			// AE control
			populateAEControl(stats, advancedControllableProperties);

			// WB control
			populateWBControl(stats, advancedControllableProperties);

			// Pan tilt control
			populatePanTiltControl(stats, advancedControllableProperties);

			// Preset control
			populatePresetControl(stats, advancedControllableProperties);
		}
	}

	//region Control device

	/**
	 * This method is used to control image process:
	 * <li>RGain</li>
	 * <li>BGain</li>
	 * <li>WBMode</li>
	 * <li>WB One push trigger</li>
	 *
	 * @param value is the value of controllable property
	 * @param splitProperty is the split controllable property
	 */
	private void imageProcessControl(String value, String[] splitProperty) {
		// (1)Name -> Split string by ")" and get the second value of slit string
		String imageProcessControlName = splitProperty[1].split(CLOSE_PARENTHESIS, 2)[1];

		// RGain
		if (Objects.equals(imageProcessControlName, Command.RGAIN.getName() + RGainControl.UP.getName())) {
			performControl(PayloadCategory.CAMERA, Command.RGAIN, RGainControl.UP.getCode());
			return;
		} else if (Objects.equals(imageProcessControlName, Command.RGAIN.getName() + RGainControl.DOWN.getName())) {
			performControl(PayloadCategory.CAMERA, Command.RGAIN, RGainControl.DOWN.getCode());
			return;
		}

		// BGain
		if (Objects.equals(imageProcessControlName, Command.BGAIN.getName() + BGainControl.UP.getName())) {
			performControl(PayloadCategory.CAMERA, Command.BGAIN, BGainControl.UP.getCode());
			return;
		} else if (Objects.equals(imageProcessControlName, Command.BGAIN.getName() + BGainControl.DOWN.getName())) {
			performControl(PayloadCategory.CAMERA, Command.BGAIN, BGainControl.DOWN.getCode());
			return;
		}

		Command imageProcessCommand = Command.getByName(imageProcessControlName);

		switch (imageProcessCommand) {
			case WB_MODE:
				performControl(PayloadCategory.CAMERA, Command.WB_MODE, WBMode.getByName(value).getCode());
				break;

			case WB_ONE_PUSH_TRIGGER: {
				performControl(PayloadCategory.CAMERA, Command.WB_ONE_PUSH_TRIGGER);
				break;
			}
			default: {
				throw new IllegalStateException("Unexpected value: " + Arrays.toString(splitProperty));
			}
		}
	}

	/**
	 * This method is used to control exposure:
	 * <li>AE Mode</li>
	 * <li>Exposure Direct</li>
	 * <li>Gain Direct</li>
	 * <li>Gain Limit Direct</li>
	 * <li>Shutter Direct</li>
	 * <li>Iris Direct</li>
	 *
	 * @param value is the value of controllable property
	 * @param exposureCommand is the command get from controllable property name
	 */
	private void exposureControl(String value, Command exposureCommand) {
		switch (exposureCommand) {
			case BACKLIGHT: {
				if (Objects.equals(value, SWITCH_STATUS_ON)) {
					performControl(PayloadCategory.CAMERA, Command.BACKLIGHT, BacklightStatus.ON.getCode());
				} else if (Objects.equals(value, SWITCH_STATUS_OFF)) {
					performControl(PayloadCategory.CAMERA, Command.BACKLIGHT, BacklightStatus.OFF.getCode());
				}
				break;
			}
			case AE_MODE: {
				performControl(PayloadCategory.CAMERA, Command.AE_MODE, AEMode.getByName(value).getCode());
				break;
			}
			// All of these DIRECT (Except Gain limit) case are share the same logic of SHUTTER_DIRECT
			case GAIN_LIMIT_DIRECT:
				float gainLimitLevel = Float.parseFloat(value);
				performControl(PayloadCategory.CAMERA, exposureCommand, (byte) gainLimitLevel);
				break;
			case EXP_COMP_DIRECT:
			case GAIN_DIRECT:
			case IRIS_DIRECT:
			case SHUTTER_DIRECT: {
				float directValue = Float.parseFloat(value);
				performControl(PayloadCategory.CAMERA, exposureCommand, convertOneByteNumberToTwoBytesArray((byte) directValue));
				break;
			}
			case AUTO_SLOW_SHUTTER: {
				if (Objects.equals(value, SWITCH_STATUS_ON)) {
					performControl(PayloadCategory.CAMERA, Command.AUTO_SLOW_SHUTTER, SlowShutterStatus.ON.getCode());
				} else if (Objects.equals(value, SWITCH_STATUS_OFF)) {
					performControl(PayloadCategory.CAMERA, Command.AUTO_SLOW_SHUTTER, SlowShutterStatus.OFF.getCode());
				}
				break;
			}
			default: {
				throw new IllegalStateException("Unexpected value: " + exposureCommand);
			}
		}
	}

	/**
	 * This method used to perform control of all properties by send, receive command from device
	 *
	 * @param payloadCategory is the category of payload of the command to be sent
	 * @param command is the command to be sent
	 * @param param is the param of command to be sent
	 */
	public void performControl(PayloadCategory payloadCategory, Command command, byte... param) {
		byte[] request = new byte[0];
		byte[] response = new byte[0];

		try {
			int currentSeqNum = ++sequenceNumber;
			request = buildSendPacket(cameraIDInt, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), payloadCategory.getCode(),
					command.getCode(), param);
			response = send(request);

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			this.logger.error("error during command " + command.getName() + " send", e);
			throw new CommandFailureException(this.getHost(), getHexByteString(request), getHexByteString(response), e);
		}
	}
	//endregion

	//region Populate control properties
	//--------------------------------------------------------------------------------------------------------------------------------

	/**
	 * This method is used for populate all zoom control properties (Tele/Wide)
	 *
	 * @param stats is the map that store all statistics
	 * @param advancedControllableProperties is the list that store all controllable properties
	 */
	private void populateZoomControl(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		// Populate zoom tele button
		populateButtonControl(stats, advancedControllableProperties, Command.ZOOM.getName() + HASH + ZoomControl.TELE.getName(), PLUS);

		// Populate zoom wide button
		populateButtonControl(stats, advancedControllableProperties, Command.ZOOM.getName() + HASH + ZoomControl.WIDE.getName(), MINUS);
	}

	/**
	 * This method is used for populate all focus control properties (Focus near/far, focus mode, focus on push)
	 *
	 * @param stats is the map that store all statistics
	 * @param advancedControllableProperties is the list that store all controllable properties
	 */
	private void populateFocusControl(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		// Populate focus mode switch
		String focusMode = this.getFocusStatus();

		// Populate focus one push button
		populateButtonControl(stats, advancedControllableProperties, Command.FOCUS.getName() + HASH + Index.TWO.getName() + Command.FOCUS_ONE_PUSH.getName(), Command.FOCUS_ONE_PUSH.getName());

		if (Objects.equals(focusMode, NONE_VALUE)) {
			stats.put(Command.FOCUS.getName() + HASH + Index.ONE.getName() + Command.FOCUS_MODE.getName(), NONE_VALUE);
			return;
		}

		stats.put(Command.FOCUS.getName() + HASH + Index.ONE.getName() + Command.FOCUS_MODE.getName(), focusMode);

		if (Objects.equals(focusMode, FocusMode.AUTO.getName())) {
			advancedControllableProperties.add(createSwitch(Command.FOCUS.getName() + HASH + Index.ONE.getName() + Command.FOCUS_MODE.getName(), 0, FocusMode.AUTO.getName(), FocusMode.MANUAL.getName()));
		} else if (Objects.equals(focusMode, FocusMode.MANUAL.getName())) {
			advancedControllableProperties.add(createSwitch(Command.FOCUS.getName() + HASH + Index.ONE.getName() + Command.FOCUS_MODE.getName(), 1, FocusMode.AUTO.getName(), FocusMode.MANUAL.getName()));

			// Populate focus near button
			populateButtonControl(stats, advancedControllableProperties, Command.FOCUS.getName() + HASH + Index.THREE.getName() + FocusControl.NEAR.getName(), PLUS);

			// Populate focus far button
			populateButtonControl(stats, advancedControllableProperties, Command.FOCUS.getName() + HASH + Index.FOUR.getName() + FocusControl.FAR.getName(), MINUS);
		}
	}

	/**
	 * This method is used for populate all AE control properties:
	 * AE Full Auto mode:
	 * <li>Backlight control</li>
	 * <li>Exposure control</li>
	 * <li>Gain limit control</li>
	 * <li>Auto slow shutter control</li>
	 *
	 * AE Shutter Priority mode:
	 * <li>Exposure control</li>
	 * <li>Gain limit control</li>
	 * <li>Shutter control</li>
	 *
	 * AE Iris Priority mode:
	 * <li>Exposure control</li>
	 * <li>Gain limit control</li>
	 * <li>Auto slow shutter control</li>
	 * <li>Iris control</li>
	 *
	 * Manual:
	 * <li>Shutter control</li>
	 * <li>Gain control</li>
	 * <li>Iris control</li>
	 *
	 * @param stats is the map that store all statistics
	 * @param advancedControllableProperties is the list that store all controllable properties
	 */
	private void populateAEControl(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		AEMode aeMode = this.getAEMode();
		if (aeMode == null) {
			stats.put(Command.EXPOSURE.getName() + HASH + Command.AE_MODE.getName(), NONE_VALUE);
			return;
		}

		stats.put(Command.EXPOSURE.getName() + HASH + Command.AE_MODE.getName(), aeMode.getName());

		List<String> aeModeList = new ArrayList<>();
		aeModeList.add(AEMode.FULL_AUTO.getName());
		aeModeList.add(AEMode.IRIS_PRIORITY.getName());
		aeModeList.add(AEMode.SHUTTER_PRIORITY.getName());
		aeModeList.add(AEMode.MANUAL.getName());

		advancedControllableProperties.add(createDropdown(Command.EXPOSURE.getName() + HASH + Command.AE_MODE.getName(), aeModeList, aeMode.getName()));

		// Getting auto slow shutter status
		String autoSlowShutterStatus = getAutoSlowShutterStatus();

		switch (aeMode) {
			case FULL_AUTO: {
				// Populate backlight switch control
				String backlightStatus = this.getBacklightStatus();
				populateSwitchControl(stats, advancedControllableProperties, Command.EXPOSURE.getName() + HASH + Command.BACKLIGHT.getName(), backlightStatus, BacklightStatus.OFF.getName(),
						BacklightStatus.ON.getName());

				// Populate exposure control
				// Exposure value: -4 -> 4, Value on slider: 1 -> 9 => Value on slider = Exposure value + 5
				String exposureValue = this.getExposureValue();
				populateSliderControl(stats, advancedControllableProperties, Command.EXPOSURE.getName() + HASH + Command.EXP_COMP_DIRECT.getName(),
						Command.EXPOSURE.getName() + HASH + Command.EXP_COMP_CURRENT.getName(), exposureValue, LABEL_START_EXPOSURE_VALUE, LABEL_END_EXPOSURE_VALUE, RANGE_START_EXPOSURE_VALUE,
						RANGE_END_EXPOSURE_VALUE, Float.parseFloat(exposureValue) + 5);

				// Populate gain limit control
				// Gain limit level: 24, 27, 30,..., 48 -> Value in slider: (gain limit level - 24) /3
				String gainLimitLevel = this.getGainLimitLevel();
				populateSliderControl(stats, advancedControllableProperties, Command.EXPOSURE.getName() + HASH + Command.GAIN_LIMIT_DIRECT.getName(),
						Command.EXPOSURE.getName() + HASH + Command.GAIN_LIMIT_CURRENT.getName(), gainLimitLevel, LABEL_START_GAIN_LIMIT_LEVEL, LABEL_END_GAIN_LIMIT_LEVEL, RANGE_START_GAIN_LIMIT_LEVEL,
						RANGE_END_GAIN_LIMIT_LEVEL, (Float.parseFloat(gainLimitLevel) - 24) / 3);

				// Populate slow shutter control
				populateSwitchControl(stats, advancedControllableProperties, Command.EXPOSURE.getName() + HASH + Command.AUTO_SLOW_SHUTTER.getName(), autoSlowShutterStatus, SlowShutterStatus.OFF.getName(),
						SlowShutterStatus.ON.getName());
				break;
			}
			case SHUTTER_PRIORITY: {
				// Populate exposure control
				// Exposure value: -4 -> 4, Value on slider: 1 -> 9 => Value on slider = Exposure value + 5
				String exposureValue = this.getExposureValue();
				populateSliderControl(stats, advancedControllableProperties, Command.EXPOSURE.getName() + HASH + Command.EXP_COMP_DIRECT.getName(),
						Command.EXPOSURE.getName() + HASH + Command.EXP_COMP_CURRENT.getName(), exposureValue, LABEL_START_EXPOSURE_VALUE, LABEL_END_EXPOSURE_VALUE, RANGE_START_EXPOSURE_VALUE,
						RANGE_END_EXPOSURE_VALUE, Float.parseFloat(exposureValue) + 5);

				// Populate gain limit control
				// Gain limit level: 24, 27, 30,..., 48 -> Value in slider: (gain limit level - 24) /3
				String gainLimitLevel = this.getGainLimitLevel();
				populateSliderControl(stats, advancedControllableProperties, Command.EXPOSURE.getName() + HASH + Command.GAIN_LIMIT_DIRECT.getName(),
						Command.EXPOSURE.getName() + HASH + Command.GAIN_LIMIT_CURRENT.getName(), gainLimitLevel, LABEL_START_GAIN_LIMIT_LEVEL, LABEL_END_GAIN_LIMIT_LEVEL, RANGE_START_GAIN_LIMIT_LEVEL,
						RANGE_END_GAIN_LIMIT_LEVEL, (Float.parseFloat(gainLimitLevel) - 24) / 3);

				// Populate shutter control
				Entry<Integer, String> shutterSpeed = this.getShutterSpeed();
				populateSliderControl(stats, advancedControllableProperties, Command.EXPOSURE.getName() + HASH + Command.SHUTTER_DIRECT.getName(),
						Command.EXPOSURE.getName() + HASH + Command.SHUTTER_CURRENT.getName(), shutterSpeed.getValue(), LABEL_START_SHUTTER_SPEED, LABEL_END_SHUTTER_SPEED, RANGE_START_SHUTTER_SPEED,
						RANGE_END_SHUTTER_SPEED, shutterSpeed.getKey().floatValue());
				break;
			}
			case IRIS_PRIORITY: {
				// Populate exposure control
				// Exposure value: -4 -> 4, Value on slider: 1 -> 9 => Value on slider = Exposure value + 5
				String exposureValue = this.getExposureValue();
				populateSliderControl(stats, advancedControllableProperties, Command.EXPOSURE.getName() + HASH + Command.EXP_COMP_DIRECT.getName(),
						Command.EXPOSURE.getName() + HASH + Command.EXP_COMP_CURRENT.getName(), exposureValue, LABEL_START_EXPOSURE_VALUE, LABEL_END_EXPOSURE_VALUE, RANGE_START_EXPOSURE_VALUE,
						RANGE_END_EXPOSURE_VALUE, Float.parseFloat(exposureValue) + 5);

				// Populate gain limit control
				// Gain limit level: 24, 27, 30,..., 48 -> Value in slider: (gain limit level - 24) /3
				String gainLimitLevel = this.getGainLimitLevel();
				populateSliderControl(stats, advancedControllableProperties, Command.EXPOSURE.getName() + HASH + Command.GAIN_LIMIT_DIRECT.getName(),
						Command.EXPOSURE.getName() + HASH + Command.GAIN_LIMIT_CURRENT.getName(), gainLimitLevel, LABEL_START_GAIN_LIMIT_LEVEL, LABEL_END_GAIN_LIMIT_LEVEL, RANGE_START_GAIN_LIMIT_LEVEL,
						RANGE_END_GAIN_LIMIT_LEVEL, (Float.parseFloat(gainLimitLevel) - 24) / 3);

				// Populate slow shutter control
				populateSwitchControl(stats, advancedControllableProperties, Command.EXPOSURE.getName() + HASH + Command.AUTO_SLOW_SHUTTER.getName(), autoSlowShutterStatus, SlowShutterStatus.OFF.getName(),
						SlowShutterStatus.ON.getName());

				// Populate iris control
				Entry<Integer, String> irisLevel = this.getIrisLevel();
				populateSliderControl(stats, advancedControllableProperties, Command.EXPOSURE.getName() + HASH + Command.IRIS_DIRECT.getName(),
						Command.EXPOSURE.getName() + HASH + Command.IRIS_CURRENT.getName(), irisLevel.getValue(), LABEL_START_IRIS_LEVEL, LABEL_END_IRIS_LEVEL, RANGE_START_IRIS_LEVEL,
						RANGE_END_IRIS_LEVEL, irisLevel.getKey().floatValue());
				break;
			}
			case MANUAL:
				// Populate shutter control
				Entry<Integer, String> shutterSpeed = this.getShutterSpeed();
				populateSliderControl(stats, advancedControllableProperties, Command.EXPOSURE.getName() + HASH + Command.SHUTTER_DIRECT.getName(),
						Command.EXPOSURE.getName() + HASH + Command.SHUTTER_CURRENT.getName(), shutterSpeed.getValue(), LABEL_START_SHUTTER_SPEED, LABEL_END_SHUTTER_SPEED, RANGE_START_SHUTTER_SPEED,
						RANGE_END_SHUTTER_SPEED, shutterSpeed.getKey().floatValue());

				// Populate gain control
				String gainLevel = this.getGainLevel();
				populateSliderControl(stats, advancedControllableProperties, Command.EXPOSURE.getName() + HASH + Command.GAIN_DIRECT.getName(),
						Command.EXPOSURE.getName() + HASH + Command.GAIN_CURRENT.getName(), gainLevel, LABEL_START_GAIN_LEVEL, LABEL_END_GAIN_LEVEL, RANGE_START_GAIN_LEVEL,
						RANGE_END_GAIN_LEVEL, Float.parseFloat(gainLevel));

				// Populate iris control
				Entry<Integer, String> irisLevel = this.getIrisLevel();
				populateSliderControl(stats, advancedControllableProperties, Command.EXPOSURE.getName() + HASH + Command.IRIS_DIRECT.getName(),
						Command.EXPOSURE.getName() + HASH + Command.IRIS_CURRENT.getName(), irisLevel.getValue(), LABEL_START_IRIS_LEVEL, LABEL_END_IRIS_LEVEL, RANGE_START_IRIS_LEVEL,
						RANGE_END_IRIS_LEVEL, irisLevel.getKey().floatValue());
				break;
			default:
				throw new IllegalStateException("Unexpected AEMode: " + aeMode);
		}
	}

	/**
	 * This method is used for populate all WB control properties:
	 * WB Mode (Auto, Indoor, Outdoor, One push wb, manual)
	 * Manual (RGain, BGain)
	 * One push wb (One push trigger)
	 *
	 * @param stats is the map that store all statistics
	 * @param advancedControllableProperties is the list that store all controllable properties
	 */
	private void populateWBControl(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		List<String> wbModeList = new ArrayList<>();
		wbModeList.add(WBMode.AUTO.getName());
		wbModeList.add(WBMode.INDOOR.getName());
		wbModeList.add(WBMode.OUTDOOR.getName());
		wbModeList.add(WBMode.ONE_PUSH_WB.getName());
		wbModeList.add(WBMode.MANUAL.getName());

		String wbMode = this.getWBMode();

		stats.put(Command.IMAGE_PROCESS.getName() + HASH + Index.ONE.getName() + Command.WB_MODE.getName(), wbMode);
		advancedControllableProperties.add(createDropdown(Command.IMAGE_PROCESS.getName() + HASH + Index.ONE.getName() + Command.WB_MODE.getName(), wbModeList, wbMode));

		if (Objects.equals(WBMode.MANUAL.getName(), wbMode)) {
			String rGainValue = this.getRGain();
			String bGainValue = this.getBGain();

			if (Objects.equals(bGainValue, NONE_VALUE)) {
				stats.put(Command.IMAGE_PROCESS.getName() + HASH + Index.TWO.getName() + Command.BGAIN_INQ.getName(), NONE_VALUE);
			} else {
				stats.put(Command.IMAGE_PROCESS.getName() + HASH + Index.TWO.getName() + Command.BGAIN_INQ.getName(), bGainValue);
			}

			if (Objects.equals(rGainValue, NONE_VALUE)) {
				stats.put(Command.IMAGE_PROCESS.getName() + HASH + Index.THREE.getName() + Command.RGAIN_INQ.getName(), NONE_VALUE);
			} else {
				stats.put(Command.IMAGE_PROCESS.getName() + HASH + Index.FIVE.getName() + Command.RGAIN_INQ.getName(), rGainValue);
			}

			// Populate BGain up button
			populateButtonControl(stats, advancedControllableProperties, Command.IMAGE_PROCESS.getName() + HASH + Index.THREE.getName() + Command.BGAIN.getName() + BGainControl.UP.getName(),
					BGainControl.UP.getName());
			// Populate BGain down button
			populateButtonControl(stats, advancedControllableProperties, Command.IMAGE_PROCESS.getName() + HASH + Index.FOUR.getName() + Command.BGAIN.getName() + BGainControl.DOWN.getName(),
					BGainControl.DOWN.getName());

			// Populate RGain up button
			populateButtonControl(stats, advancedControllableProperties, Command.IMAGE_PROCESS.getName() + HASH + Index.SIX.getName() + Command.RGAIN.getName() + RGainControl.UP.getName(),
					RGainControl.UP.getName());
			// Populate RGain down button
			populateButtonControl(stats, advancedControllableProperties, Command.IMAGE_PROCESS.getName() + HASH + Index.SEVEN.getName() + Command.RGAIN.getName() + RGainControl.DOWN.getName(),
					RGainControl.DOWN.getName());

		} else if (Objects.equals(WBMode.ONE_PUSH_WB.getName(), wbMode)) {
			// Populate one push WB button
			populateButtonControl(stats, advancedControllableProperties, Command.IMAGE_PROCESS.getName() + HASH + Index.TWO.getName() + Command.WB_ONE_PUSH_TRIGGER.getName(),
					Command.WB_ONE_PUSH_TRIGGER.getName());
		}
	}

	/**
	 * This method is used for populate all pan tilt control properties:
	 * <li>Pan tilt drive (up/down/left/right/up left/up right/down left/down right</li>
	 * <li>Pan tilt home</li>
	 * <li>Slow pan tilt mode</li>
	 *
	 * @param stats is the map that store all statistics
	 * @param advancedControllableProperties is the list that store all controllable properties
	 */
	private void populatePanTiltControl(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		// Populate slow pan tilt switch
		String slowPanTiltStatus = getSlowPanTiltStatus();
		populateSwitchControl(stats, advancedControllableProperties, Command.PAN_TILT_DRIVE.getName() + HASH + Index.ZERO.getName() + Command.SLOW_PAN_TILT.getName(), slowPanTiltStatus,
				SlowPanTiltStatus.OFF.getName(), SlowPanTiltStatus.ON.getName());

		// Populate pan tilt drive home button
		populateButtonControl(stats, advancedControllableProperties, Command.PAN_TILT_DRIVE.getName() + HASH + Index.ONE.getName() + Command.PAN_TILT_HOME.getName(), Command.PAN_TILT_HOME.getName());
		// Populate pan tilt drive up button
		populateButtonControl(stats, advancedControllableProperties, Command.PAN_TILT_DRIVE.getName() + HASH + Index.TWO.getName() + PanTiltDrive.UP.getName(), PanTiltDrive.UP.getName());
		// Populate pan tilt drive down button
		populateButtonControl(stats, advancedControllableProperties, Command.PAN_TILT_DRIVE.getName() + HASH + Index.THREE.getName() + PanTiltDrive.DOWN.getName(), PanTiltDrive.DOWN.getName());
		// Populate pan tilt drive left button
		populateButtonControl(stats, advancedControllableProperties, Command.PAN_TILT_DRIVE.getName() + HASH + Index.FOUR.getName() + PanTiltDrive.LEFT.getName(), PanTiltDrive.LEFT.getName());
		// Populate pan tilt drive right button
		populateButtonControl(stats, advancedControllableProperties, Command.PAN_TILT_DRIVE.getName() + HASH + Index.FIVE.getName() + PanTiltDrive.RIGHT.getName(), PanTiltDrive.RIGHT.getName());
		// Populate pan tilt drive up left button
		populateButtonControl(stats, advancedControllableProperties, Command.PAN_TILT_DRIVE.getName() + HASH + Index.SIX.getName() + PanTiltDrive.UP_LEFT.getName(), PanTiltDrive.UP_LEFT.getName());
		// Populate pan tilt drive up right button
		populateButtonControl(stats, advancedControllableProperties, Command.PAN_TILT_DRIVE.getName() + HASH + Index.SEVEN.getName() + PanTiltDrive.UP_RIGHT.getName(), PanTiltDrive.UP_RIGHT.getName());
		// Populate pan tilt drive down left button
		populateButtonControl(stats, advancedControllableProperties, Command.PAN_TILT_DRIVE.getName() + HASH + Index.EIGHT.getName() + PanTiltDrive.DOWN_LEFT.getName(), PanTiltDrive.DOWN_LEFT.getName());
		// Populate pan tilt drive down right button
		populateButtonControl(stats, advancedControllableProperties, Command.PAN_TILT_DRIVE.getName() + HASH + Index.NINE.getName() + PanTiltDrive.DOWN_RIGHT.getName(), PanTiltDrive.DOWN_RIGHT.getName());
	}

	/**
	 * This method is used for populate all preset control properties (preset set and recall)
	 *
	 * @param stats is the map that store all statistics
	 * @param advancedControllableProperties is the list that store all controllable properties
	 */
	private void populatePresetControl(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		// Populate switch preset to select
		List<String> presetList = new ArrayList<>();
		presetList.add(DEFAULT_PRESET);

		for (int i = 0; i <= 255; ++i) {
			presetList.add(String.valueOf(i));
		}

		String presetValue = currentPreset == -1 ? DEFAULT_PRESET : String.valueOf(currentPreset);

		stats.put(Command.PRESET.getName() + HASH + Index.TWO.getName() + PresetControl.PRESET_VALUE.getName(), presetValue);
		advancedControllableProperties.add(createDropdown(Command.PRESET.getName() + HASH + Index.TWO.getName() + PresetControl.PRESET_VALUE.getName(), presetList, presetValue));

		stats.put(Command.PRESET.getName() + HASH + Index.ONE.getName() + PresetControl.LAST_PRESET_RECALLED.getName(), this.getLastPresetRecalled());

		// Populate set preset button
		populateButtonControl(stats, advancedControllableProperties, Command.PRESET.getName() + HASH + Index.THREE.getName() + PresetControl.SET.getName(), PresetControl.SET.getName());

		// Populate recall preset button
		populateButtonControl(stats, advancedControllableProperties, Command.PRESET.getName() + HASH + Index.FOUR.getName() + PresetControl.RECALL.getName(), PresetControl.RECALL.getName());
	}

	/**
	 * This method is used for populate slider control
	 *
	 * @param stats is the map that store all statistics
	 * @param advancedControllableProperties is the list that store all controllable properties
	 * @param propertyName is the property name of slider
	 * @param currentPropertyName is the label for current value of property
	 * @param propertyValue is the current value of property
	 * @param labelStart is the label start of slider
	 * @param labelEnd is the label end of slider
	 * @param rangeStart is the range start of slider
	 * @param rangeEnd is the range end of slider
	 * @param initialValue is the initial value of slider
	 */
	private void populateSliderControl(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, String propertyName,
			String currentPropertyName, String propertyValue, String labelStart, String labelEnd, float rangeStart, float rangeEnd, float initialValue) {

		if (Objects.equals(propertyValue, NONE_VALUE)) {
			stats.put(propertyName, NONE_VALUE);
			stats.put(currentPropertyName, NONE_VALUE);
			return;
		}

		stats.put(propertyName, String.valueOf(initialValue));
		stats.put(currentPropertyName, propertyValue);
		advancedControllableProperties.add(createSlider(propertyName, labelStart, labelEnd, rangeStart, rangeEnd, initialValue));
	}

	/**
	 * This method is used for populate switch control
	 *
	 * @param stats is the map that store all statistics
	 * @param advancedControllableProperties is the list that store all controllable properties
	 * @param propertyName is the property name of switch control
	 * @param currentStatus is the current status of switch control
	 * @param labelOff is the label off of switch control
	 * @param labelOn is the label on of switch control
	 */
	private void populateSwitchControl(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, String propertyName, String currentStatus,
			String labelOff, String labelOn) {
		if (Objects.equals(currentStatus, NONE_VALUE)) {
			stats.put(propertyName, NONE_VALUE);
			return;
		}

		if (Objects.equals(currentStatus, labelOn)) {
			stats.put(propertyName, String.valueOf(1));
			advancedControllableProperties.add(createSwitch(propertyName, 1, labelOff, labelOn));
		} else if (Objects.equals(currentStatus, labelOff)) {
			stats.put(propertyName, String.valueOf(0));
			advancedControllableProperties.add(createSwitch(propertyName, 0, labelOff, labelOn));
		}
	}

	/**
	 * This method is used for populate button control
	 *
	 * @param stats is the map that store all statistics
	 * @param advancedControllableProperties is the list that store all controllable properties
	 * @param propertyName is the property name of button control
	 * @param buttonLabel is the label of button control
	 */
	private void populateButtonControl(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, String propertyName, String buttonLabel) {
		stats.put(propertyName, "");
		advancedControllableProperties.add(createButton(propertyName, buttonLabel));
	}
	//--------------------------------------------------------------------------------------------------------------------------------
	//endregion

	//region Get current value from device
	//--------------------------------------------------------------------------------------------------------------------------------

	/**
	 * This method is used to get the current display last preset recalled
	 *
	 * @return String This returns the last preset recalled.
	 */
	private String getLastPresetRecalled() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(
					buildSendPacket(cameraIDInt, currentSeqNum, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), PayloadCategory.CAMERA.getCode(), Command.PRESET.getCode()));

			return String.valueOf(digestResponse(response, currentSeqNum, CommandType.INQUIRY, Command.PRESET));
		} catch (Exception e) {
			this.logger.error("error during get last preset recalled send", e);
		}
		return NONE_VALUE;
	}

	/**
	 * This method is used to get the current display power status
	 *
	 * @return String This returns the power status
	 */
	private String getPowerStatus() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(
					buildSendPacket(cameraIDInt, currentSeqNum, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), PayloadCategory.CAMERA.getCode(), Command.POWER.getCode()));

			PowerStatus status = (PowerStatus) digestResponse(response, currentSeqNum, CommandType.INQUIRY, Command.POWER);

			if (status == null) {
				return PowerStatus.OFF.getName();
			} else {
				return status.getName();
			}
		} catch (Exception e) {
			this.logger.error("error during get power send", e);
		}
		return NONE_VALUE;
	}

	/**
	 * This method is used to get the current display focus status
	 *
	 * @return String This returns the focus status
	 */
	private String getFocusStatus() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(
					buildSendPacket(cameraIDInt, currentSeqNum, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), PayloadCategory.CAMERA.getCode(), Command.FOCUS_MODE.getCode()));

			FocusMode mode = (FocusMode) digestResponse(response, currentSeqNum, CommandType.INQUIRY, Command.FOCUS_MODE);

			if (mode == null) {
				return FocusMode.AUTO.getName();
			} else {
				return mode.getName();
			}
		} catch (Exception e) {
			this.logger.error("error during get focus mode", e);
		}
		return NONE_VALUE;
	}

	/**
	 * This method is used to get the current display backlight status
	 *
	 * @return String This returns the backlight status
	 */
	private String getBacklightStatus() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(
					buildSendPacket(cameraIDInt, currentSeqNum, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), PayloadCategory.CAMERA.getCode(), Command.BACKLIGHT.getCode()));

			BacklightStatus status = (BacklightStatus) digestResponse(response, currentSeqNum, CommandType.INQUIRY, Command.BACKLIGHT);

			if (status == null) {
				return BacklightStatus.OFF.getName();
			} else {
				return status.getName();
			}
		} catch (Exception e) {
			this.logger.error("error during get backlight status", e);
		}
		return NONE_VALUE;
	}

	/**
	 * This method is used to get the current display current AE mode
	 *
	 * @return String This returns the AE mode
	 */
	private AEMode getAEMode() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(
					buildSendPacket(cameraIDInt, currentSeqNum, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), PayloadCategory.CAMERA.getCode(), Command.AE_MODE.getCode()));

			AEMode mode = (AEMode) digestResponse(response, currentSeqNum, CommandType.INQUIRY, Command.AE_MODE);

			if (mode == null) {
				return AEMode.FULL_AUTO;
			} else {
				return mode;
			}
		} catch (Exception e) {
			this.logger.error("error during get AE mode", e);
		}
		return null;
	}

	/**
	 * This method is used to get the current display current exposure value
	 *
	 * @return String This returns the exposure value
	 */
	private String getExposureValue() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(
					buildSendPacket(cameraIDInt, currentSeqNum, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), PayloadCategory.CAMERA.getCode(), Command.EXP_COMP_DIRECT.getCode()));
			// Exposure value: -4 -> 4, Value get from device: 1 -> 9 => Exposure value = value from device - 5
			return String.valueOf((int) digestResponse(response, currentSeqNum, CommandType.INQUIRY, Command.EXP_COMP_DIRECT) - 5);
		} catch (Exception e) {
			this.logger.error("error during get exposure value", e);
		}
		return NONE_VALUE;
	}

	/**
	 * This method is used to get the current display current shutter speed
	 *
	 * @return Entry<Integer, String> This returns the entry for shutter speed
	 * key: int value of slider, value: string value of shutter speed
	 */
	private Entry<Integer, String> getShutterSpeed() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(
					buildSendPacket(cameraIDInt, currentSeqNum, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), PayloadCategory.CAMERA.getCode(), Command.SHUTTER_DIRECT.getCode()));

			int index = (int) digestResponse(response, currentSeqNum, CommandType.INQUIRY, Command.SHUTTER_DIRECT);

			return new SimpleEntry<>(index, SHUTTER_VALUES.get(index));
		} catch (Exception e) {
			this.logger.error("error during get shutter speed", e);
		}
		return new SimpleEntry<>(0, NONE_VALUE);
	}

	/**
	 * This method is used to get the current display current iris level
	 *
	 * @return Entry<Integer, String> This returns the entry for iris level
	 * key: int value of slider, value: string value of iris level
	 */
	private Entry<Integer, String> getIrisLevel() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(
					buildSendPacket(cameraIDInt, currentSeqNum, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), PayloadCategory.CAMERA.getCode(), Command.IRIS_DIRECT.getCode()));

			int index = (int) digestResponse(response, currentSeqNum, CommandType.INQUIRY, Command.IRIS_DIRECT);
			return new SimpleEntry<>(index, IRIS_LEVELS.get(index));
		} catch (Exception e) {
			this.logger.error("error during get iris level", e);
		}
		return new SimpleEntry<>(0, NONE_VALUE);
	}

	/**
	 * This method is used to get the current display current gain level
	 *
	 * @return String This returns the gain level
	 */
	private String getGainLevel() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(
					buildSendPacket(cameraIDInt, currentSeqNum, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), PayloadCategory.CAMERA.getCode(), Command.GAIN_DIRECT.getCode()));

			return String.valueOf(digestResponse(response, currentSeqNum, CommandType.INQUIRY, Command.GAIN_DIRECT));
		} catch (Exception e) {
			this.logger.error("error during get gain level", e);
		}
		return NONE_VALUE;
	}

	/**
	 * This method is used to get the current display current gain limit level
	 *
	 * @return String This returns the gain limit level
	 */
	private String getGainLimitLevel() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(
					buildSendPacket(cameraIDInt, currentSeqNum, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), PayloadCategory.CAMERA.getCode(), Command.GAIN_LIMIT_DIRECT.getCode()));
			// Gain limit level: 24, 27, ... , 48. Value get from device: 0,1, ... , 8 => gain limit level = (value from device + 24 ) * 3
			return String.valueOf(24 + (int) digestResponse(response, currentSeqNum, CommandType.INQUIRY, Command.GAIN_LIMIT_DIRECT) * 3);
		} catch (Exception e) {
			this.logger.error("error during get gain limit level", e);
		}
		return NONE_VALUE;
	}

	/**
	 * This method is used to get the current display current WB mode
	 *
	 * @return String This returns the WB mode
	 */
	private String getWBMode() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(
					buildSendPacket(cameraIDInt, currentSeqNum, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), PayloadCategory.CAMERA.getCode(), Command.WB_MODE.getCode()));

			WBMode mode = (WBMode) digestResponse(response, currentSeqNum, CommandType.INQUIRY, Command.WB_MODE);

			if (mode == null) {
				return WBMode.MANUAL.getName();
			} else {
				return mode.getName();
			}
		} catch (Exception e) {
			this.logger.error("error during get WB mode", e);
			return WBMode.MANUAL.getName();
		}
	}

	/**
	 * This method is used to get the current display current RGain value
	 *
	 * @return String This returns the RGain value
	 */
	private String getRGain() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(
					buildSendPacket(cameraIDInt, currentSeqNum, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), PayloadCategory.CAMERA.getCode(), Command.RGAIN_INQ.getCode()));

			return String.valueOf(digestResponse(response, currentSeqNum, CommandType.INQUIRY, Command.RGAIN_INQ));

		} catch (Exception e) {
			this.logger.error("error during get RGain value", e);
		}
		return NONE_VALUE;
	}

	/**
	 * This method is used to get the current display current BGain value
	 *
	 * @return String This returns the BGain value
	 */
	private String getBGain() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(
					buildSendPacket(cameraIDInt, currentSeqNum, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), PayloadCategory.CAMERA.getCode(), Command.BGAIN_INQ.getCode()));

			return String.valueOf(digestResponse(response, currentSeqNum, CommandType.INQUIRY, Command.BGAIN_INQ));

		} catch (Exception e) {
			this.logger.error("error during get BGain value", e);
		}
		return NONE_VALUE;
	}

	/**
	 * This method is used to get the current display current slow pan tilt status
	 *
	 * @return String This returns the slow pan tilt status
	 */
	private String getSlowPanTiltStatus() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(
					buildSendPacket(cameraIDInt, currentSeqNum, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), PayloadCategory.PAN_TILTER.getCode(), Command.SLOW_PAN_TILT.getCode()));

			SlowPanTiltStatus status = (SlowPanTiltStatus) digestResponse(response, currentSeqNum, CommandType.INQUIRY, Command.SLOW_PAN_TILT);

			if (status == null) {
				return SlowPanTiltStatus.OFF.getName();
			} else {
				return status.getName();
			}
		} catch (Exception e) {
			this.logger.error("error during get slow pan tilt status", e);
		}
		return NONE_VALUE;
	}

	/**
	 * This method is used to get the current display current auto slow shutter status
	 *
	 * @return String This returns the auto slow shutter status
	 */
	private String getAutoSlowShutterStatus() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(
					buildSendPacket(cameraIDInt, currentSeqNum, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), PayloadCategory.CAMERA.getCode(), Command.AUTO_SLOW_SHUTTER.getCode()));

			SlowShutterStatus status = (SlowShutterStatus) digestResponse(response, currentSeqNum, CommandType.INQUIRY, Command.AUTO_SLOW_SHUTTER);

			if (status == null) {
				return SlowShutterStatus.OFF.getName();
			} else {
				return status.getName();
			}
		} catch (Exception e) {
			this.logger.error("error during get slow auto slow shutter status", e);
		}
		return NONE_VALUE;
	}

	//--------------------------------------------------------------------------------------------------------------------------------
	//endregion

	/**
	 * {@inheritdoc}
	 * This method is used to send command to device
	 *
	 * @param outputData This is a byte array of command to be sent
	 * @return byte[] This returns the response receive from device
	 */
	@Override
	protected byte[] internalSend(byte[] outputData) throws IOException {
		DatagramPacket request = new DatagramPacket(outputData, outputData.length, this.address, this.port);
		this.write(request);

		// If send command power off -> device return nothing -> no need wait to receive
		if (Objects.equals(outputData[11], Command.POWER.getCode()[0]) && Objects.equals(outputData[12], PowerStatus.OFF.getCode())) {
			System.arraycopy(outputData, 4, FAKE_COMPLETION, 4, 4); // Copy sequence number
			return FAKE_COMPLETION;
		}

		return this.read(outputData);
	}

	/**
	 * {@inheritdoc}
	 * This method is used to read data from device
	 *
	 * @param command This is a byte array of command to check done reading or not
	 * @return byte[] This returns the response receive from device
	 */
	@Override
	protected byte[] read(byte[] command) throws IOException {
		String responseString;
		boolean hasACK = false;
		byte[] response;

		do {
			response = super.read(command);
			responseString = getHexByteString(response);

			// If this is ACK packet, need to save to the flag to check done reading
			if (responseString.endsWith(getHexByteString(ReplyStatus.ACK.getCode()))) {
				hasACK = true;
			}

		} while (!doneReading(command, responseString, hasACK));

		return response;
	}

	/**
	 * This method is used to check when done reading
	 *
	 * @param command This is a command parameter for method read executed before throw error (if it has ACK)
	 * @param responseString This is a string represented for response to be checked
	 * @param hasACK This is a boolean value to check if it has ACK packet or not (ACK -> ERROR -> COMPLETION, if it has error, need to read COMPLETION packet left)
	 * @return boolean This is a boolean value return if done reading or not
	 */
	public boolean doneReading(byte[] command, String responseString, boolean hasACK) throws CommandFailureException, IOException {
		String commandString = getHexByteString(command);
		Iterator<String> iterator = this.getCommandErrorList().iterator();

		String string;
		do {
			if (!iterator.hasNext()) {
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				outputStream.write(Prefix.PAYLOAD_TYPE.getPrefixCode());
				outputStream.write(PayloadType.INQUIRY.getCode());

				// If type is INQUIRY command, just need to check command error list
				if (commandString.startsWith(getHexByteString(outputStream.toByteArray()))) {
					this.logger.trace("Done reading, found inquiry packet reply from: " + this.getHost() + " port: " + this.getPort());
					return true;
				}

				iterator = this.getCommandSuccessList().iterator();

				do {
					if (!iterator.hasNext()) {
						return false;
					}

					string = iterator.next();
				} while (!responseString.endsWith(string));

				if (this.logger.isTraceEnabled()) {
					this.logger.trace("Done reading, found success string: " + string + " from: " + this.getHost() + " port: " + this.getPort());
				}

				return true;
			}

			string = iterator.next();
		} while (!responseString.endsWith(string));

		if (this.logger.isTraceEnabled()) {
			this.logger.trace("Done reading, found error string: " + string + " from: " + this.getHost() + " port: " + this.getPort());
		}

		// if it has ACK packet first -> Error packet, 1 Completion packet left to receive from Device
		if (hasACK) {
			super.read(command);
		}

		throw new CommandFailureException(this.getHost(), commandString, responseString);
	}

	/**
	 * This method is used to digest the response received from the device
	 *
	 * @param response This is the response to be digested
	 * @param sequenceNum This is the sequence number of send packet
	 * @param commandType This is the type of command to be digested
	 * @param expectedCommand This is the expected command to be digested
	 * @return Object This returns the result digested from the response.
	 */
	public Object digestResponse(byte[] response, int sequenceNum, CommandType commandType, Command expectedCommand) {

		if (response[1] == PayloadType.REPLY.getCode()) {
			byte[] responseSeqNum = Arrays.copyOfRange(response, 4, 8);
			byte[] expectedSeqNum = convertIntToByteArray(sequenceNum);

			if (Arrays.equals(expectedSeqNum, responseSeqNum)) {
				int payloadLength = response[3];
				byte[] reply = Arrays.copyOfRange(response, 8, 8 + payloadLength);
				byte currentValue = reply[2];

				if (commandType == CommandType.COMMAND) {
					if (!Arrays.equals(ReplyStatus.COMPLETION.getCode(), reply)) {
						this.logger.error("error: Unexpected completion packet: " + this.host + " port: " + this.port);
						throw new IllegalStateException("Unexpected completion packet");
					}
				} else if (commandType == CommandType.INQUIRY) {
					switch (expectedCommand) {
						case POWER: {
							Optional<PowerStatus> powerStatus = Arrays.stream(PowerStatus.values())
									.filter(status -> status.getCode() == currentValue)
									.findFirst();

							return powerStatus.orElse(null);
						}
						case FOCUS_MODE: {
							Optional<FocusMode> focusMode = Arrays.stream(FocusMode.values())
									.filter(mode -> mode.getCode() == currentValue)
									.findFirst();

							return focusMode.orElse(null);
						}
						case AE_MODE: {
							Optional<AEMode> aeMode = Arrays.stream(AEMode.values())
									.filter(mode -> mode.getCode() == currentValue)
									.findFirst();
							return aeMode.orElse(null);
						}
						case AUTO_SLOW_SHUTTER: {
							Optional<SlowShutterStatus> slowShutterStatus = Arrays.stream(SlowShutterStatus.values())
									.filter(mode -> mode.getCode() == currentValue)
									.findFirst();

							return slowShutterStatus.orElse(null);
						}
						case SHUTTER_DIRECT:
						case IRIS_DIRECT:
						case GAIN_DIRECT:
						case EXP_COMP_DIRECT:
						case RGAIN_INQ:
						case BGAIN_INQ: {
							return reply[4] * 16 + reply[5];
						}
						case GAIN_LIMIT_DIRECT:
						case PRESET: {
							return Byte.toUnsignedInt(reply[2]);
						}
						case BACKLIGHT: {
							Optional<BacklightStatus> backlightStatus = Arrays.stream(BacklightStatus.values())
									.filter(status -> status.getCode() == currentValue)
									.findFirst();

							return backlightStatus.orElse(null);
						}
						case WB_MODE: {
							Optional<WBMode> wbMode = Arrays.stream(WBMode.values())
									.filter(mode -> mode.getCode() == currentValue)
									.findFirst();

							return wbMode.orElse(null);
						}
						case SLOW_PAN_TILT: {
							Optional<SlowPanTiltStatus> slowPanTiltStatus = Arrays.stream(SlowPanTiltStatus.values())
									.filter(status -> status.getCode() == currentValue)
									.findFirst();

							return slowPanTiltStatus.orElse(null);
						}
						default:
							throw new IllegalStateException("Unexpected command: " + expectedCommand);
					}
				}
			} else {
				this.logger.error("error: Unexpected sequence number: " + this.host + " port: " + this.port);
				throw new IllegalStateException("Unexpected sequence number");
			}
		} else {
			this.logger.error("error: Unexpected reply: " + this.host + " port: " + this.port);
			throw new IllegalStateException("Unexpected reply");
		}

		return null;
	}

	//region Create controllable property
	//--------------------------------------------------------------------------------------------------------------------------------

	/**
	 * Instantiate Text controllable property
	 *
	 * @param name name of the property
	 * @param label default button label
	 * @return instance of AdvancedControllableProperty with AdvancedControllableProperty.Button as type
	 */
	private AdvancedControllableProperty createButton(String name, String label) {
		AdvancedControllableProperty.Button button = new AdvancedControllableProperty.Button();
		button.setLabel(label);
		button.setLabelPressed("Running...");
		button.setGracePeriod(100L);

		return new AdvancedControllableProperty(name, new Date(), button, "");
	}

	/**
	 * Create a switch controllable property
	 *
	 * @param name name of the switch
	 * @param status initial switch state (0|1)
	 * @return AdvancedControllableProperty button instance
	 */
	private AdvancedControllableProperty createSwitch(String name, int status, String labelOff, String labelOn) {
		AdvancedControllableProperty.Switch toggle = new AdvancedControllableProperty.Switch();
		toggle.setLabelOff(labelOff);
		toggle.setLabelOn(labelOn);

		return new AdvancedControllableProperty(name, new Date(), toggle, status);
	}

	/***
	 * Create AdvancedControllableProperty slider instance
	 *
	 * @param name name of the control
	 * @param initialValue initial value of the control
	 * @param rangeStart start value for the slider
	 * @param rangeEnd end value for the slider
	 *
	 * @return AdvancedControllableProperty slider instance
	 */
	private AdvancedControllableProperty createSlider(String name, String labelStart, String labelEnd, Float rangeStart, Float rangeEnd, Float initialValue) {
		AdvancedControllableProperty.Slider slider = new AdvancedControllableProperty.Slider();
		slider.setLabelStart(labelStart);
		slider.setLabelEnd(labelEnd);
		slider.setRangeStart(rangeStart);
		slider.setRangeEnd(rangeEnd);

		return new AdvancedControllableProperty(name, new Date(), slider, initialValue);
	}

	/***
	 * Create AdvancedControllableProperty preset instance
	 * @param name name of the control
	 * @param initialValue initial value of the control
	 * @return AdvancedControllableProperty preset instance
	 */
	private AdvancedControllableProperty createDropdown(String name, List<String> values, String initialValue) {
		AdvancedControllableProperty.DropDown dropDown = new AdvancedControllableProperty.DropDown();
		dropDown.setOptions(values.toArray(new String[0]));
		dropDown.setLabels(values.toArray(new String[0]));

		return new AdvancedControllableProperty(name, new Date(), dropDown, initialValue);
	}
	//--------------------------------------------------------------------------------------------------------------------------------
	//endregion
}
