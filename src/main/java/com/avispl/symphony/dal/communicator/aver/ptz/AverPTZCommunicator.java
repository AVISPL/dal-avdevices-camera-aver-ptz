/*
 * Copyright (c) 2021 AVI-SPL Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.aver.ptz;

import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.BGAIN_START;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.HASH;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.IRIS_LEVELS;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.LABEL_END_EXPOSURE_VALUE;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.LABEL_END_GAIN_LIMIT_VALUE;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.LABEL_END_IRIS_VALUE;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.LABEL_END_SHUTTER_VALUE;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.LABEL_START_EXPOSURE_VALUE;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.LABEL_START_GAIN_LIMIT_VALUE;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.LABEL_START_IRIS_VALUE;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.LABEL_START_SHUTTER_VALUE;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.MINUS;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.PAN_TILT_PERIOD;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.PLUS;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.RANGE_END_EXPOSURE_VALUE;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.RANGE_END_GAIN_LIMIT_VALUE;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.RANGE_END_IRIS_VALUE;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.RANGE_END_SHUTTER_VALUE;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.RANGE_START_EXPOSURE_VALUE;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.RANGE_START_GAIN_LIMIT_VALUE;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.RANGE_START_IRIS_VALUE;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.RANGE_START_SHUTTER_VALUE;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.RGAIN_START;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.SHUTTER_VALUES;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.SWITCH_STATUS_OFF;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZConstants.SWITCH_STATUS_ON;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZUtils.buildSendPacket;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZUtils.convertIntToByteArray;
import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZUtils.convertOneByteNumberToTwoBytesArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.util.CollectionUtils;

import com.avispl.symphony.api.dal.control.Controller;
import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.Statistics;
import com.avispl.symphony.api.dal.error.CommandFailureException;
import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.api.dal.monitor.Monitorable;
import com.avispl.symphony.dal.communicator.aver.ptz.dto.DeviceInfo;
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
	private final ReentrantLock controlOperationsLock = new ReentrantLock();
	private int cameraID = 1;
	private int panSpeed = 1;
	private int tiltSpeed = 1;
	private int zoomSpeed = 1;
	private int focusSpeed = 1;
	private int sequenceNumber = 0;
	private AverPTZRestCommunicator restCommunicator;
	private DeviceInfo deviceInfo = new DeviceInfo();

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
	public int getCameraID() {
		return cameraID;
	}

	/**
	 * Sets {@code cameraID}
	 *
	 * @param cameraID the {@code int} field
	 */
	public void setCameraID(int cameraID) {
		this.cameraID = cameraID;
	}

	/**
	 * Retrieves {@code {@link #panSpeed}}
	 *
	 * @return value of {@link #panSpeed}
	 */
	public int getPanSpeed() {
		return panSpeed;
	}

	/**
	 * Sets {@code panSpeed}
	 *
	 * @param panSpeed the {@code int} field
	 */
	public void setPanSpeed(int panSpeed) {
		this.panSpeed = panSpeed;
	}

	/**
	 * Retrieves {@code {@link #tiltSpeed}}
	 *
	 * @return value of {@link #tiltSpeed}
	 */
	public int getTiltSpeed() {
		return tiltSpeed;
	}

	/**
	 * Sets {@code tiltSpeed}
	 *
	 * @param tiltSpeed the {@code int} field
	 */
	public void setTiltSpeed(int tiltSpeed) {
		this.tiltSpeed = tiltSpeed;
	}

	/**
	 * Retrieves {@code {@link #zoomSpeed}}
	 *
	 * @return value of {@link #zoomSpeed}
	 */
	public int getZoomSpeed() {
		return zoomSpeed;
	}

	/**
	 * Sets {@code zoomSpeed}
	 *
	 * @param zoomSpeed the {@code int} field
	 */
	public void setZoomSpeed(int zoomSpeed) {
		this.zoomSpeed = zoomSpeed;
	}

	/**
	 * Retrieves {@code {@link #focusSpeed}}
	 *
	 * @return value of {@link #focusSpeed}
	 */
	public int getFocusSpeed() {
		return focusSpeed;
	}

	/**
	 * Sets {@code focusSpeed}
	 *
	 * @param focusSpeed the {@code int} field
	 */
	public void setFocusSpeed(int focusSpeed) {
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
	 * This method is recalled by Symphony to control specific property
	 *
	 * @param controllableProperty This is the property to be controlled
	 */
	@Override
	public void controlProperty(ControllableProperty controllableProperty) {
		String property = controllableProperty.getProperty();
		String value = String.valueOf(controllableProperty.getValue());
		controlOperationsLock.lock();

		if (this.logger.isDebugEnabled()) {
			this.logger.debug("controlProperty property " + property);
			this.logger.debug("controlProperty value " + value);
		}

		try {
			String[] splitProperty = property.split(String.valueOf(HASH));
			Command command = Command.getByName(splitProperty[0]);

			switch (command) {
				case POWER: {
					if (value.equals(SWITCH_STATUS_ON)) {
						powerControl(PowerStatus.ON);
					} else if (value.equals(SWITCH_STATUS_OFF)) {
						powerControl(PowerStatus.OFF);
					}
					break;
				}
				case ZOOM: {
					if (splitProperty[1].equals(ZoomControl.TELE.getName())) {
						zoomControl(ZoomControl.TELE);
					} else if (splitProperty[1].equals(ZoomControl.WIDE.getName())) {
						zoomControl(ZoomControl.WIDE);
					}
					break;
				}
				case FOCUS: {
					if (splitProperty[1].equals(Command.FOCUS_MODE.getName())) {
						if (value.equals(SWITCH_STATUS_ON)) {
							focusModeControl(FocusMode.MANUAL);
						} else if (value.equals(SWITCH_STATUS_OFF)) {
							focusModeControl(FocusMode.AUTO);
						}
						break;
					}

					if (splitProperty[1].equals(Command.FOCUS_ONE_PUSH.getName())) {
						onePushFocus();
						break;
					}

					if (splitProperty[1].equals(FocusControl.FAR.getName())) {
						focusControl(FocusControl.FAR);
					} else if (splitProperty[1].equals(FocusControl.NEAR.getName())) {
						focusControl(FocusControl.NEAR);
					}
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
					if (splitProperty[1].equals(Command.PAN_TILT_HOME.getName())) {
						panTiltHome();
						break;
					} else if (splitProperty[1].equals(Command.SLOW_PAN_TILT.getName())) {
						if (value.equals(SWITCH_STATUS_ON)) {
							slowPanTiltControl(SlowPanTiltStatus.ON);
						} else if (value.equals(SWITCH_STATUS_OFF)) {
							slowPanTiltControl(SlowPanTiltStatus.OFF);
						}
						break;
					}

					PanTiltDrive pantTiltDrive = PanTiltDrive.getByName(splitProperty[1]);
					panTiltDriveControl(pantTiltDrive);
					break;
				}
				case PRESET: {
					if (splitProperty[1].equals(PresetControl.SET.getName())) {
						presetControl(PresetControl.SET, Integer.parseInt(value));
					} else if (splitProperty[1].equals(PresetControl.RECALL.getName())) {
						presetControl(PresetControl.RECALL, Integer.parseInt(value));
					}
					break;
				}
				default: {
					throw new IllegalStateException("Unexpected value: " + command);
				}
			}
		} catch (CommandFailureException ex) {
			if (this.logger.isErrorEnabled()) {
				this.logger.error("error: control command not found");
			}
			throw ex;
		} finally {
			controlOperationsLock.unlock();
		}
	}

	/**
	 * This method is recalled by Symphony to control a list of properties
	 *
	 * @param controllableProperties This is the list of properties to be controlled
	 */
	@Override
	public void controlProperties(List<ControllableProperty> controllableProperties) {
		if (CollectionUtils.isEmpty(controllableProperties)) {
			throw new IllegalArgumentException("AverCommunicator: Controllable properties cannot be null or empty");
		}

		controllableProperties.forEach(this::controlProperty);
	}

	/**
	 * This method is recalled by Symphony to get the list of statistics to be displayed
	 *
	 * @return List<Statistics> This return the list of statistics.
	 */
	@Override
	public List<Statistics> getMultipleStatistics() {
		final ExtendedStatistics extStats = new ExtendedStatistics();
		final Map<String, String> stats = new HashMap<>();
		final List<AdvancedControllableProperty> advancedControllableProperties = new ArrayList<>();

		if (this.cameraID < 1 || this.cameraID > 7) {
			throw new IllegalArgumentException("Camera ID with value " + this.cameraID + " is out of range. Camera ID must between 1 and 7");
		}

		if (this.panSpeed < 0 || this.panSpeed > 24) {
			throw new IllegalArgumentException("Pan speed with value" + this.panSpeed + " is out of range. Pan speed must between 0 and 24");
		}

		if (this.tiltSpeed < 0 || this.tiltSpeed > 24) {
			throw new IllegalArgumentException("Tilt speed with value" + this.tiltSpeed + " is out of range. Tilt speed must between 0 and 24");
		}

		if (this.zoomSpeed < 0 || this.zoomSpeed > 7) {
			throw new IllegalArgumentException("Pan speed with value" + this.zoomSpeed + " is out of range. Zoom speed must between 0 and 7");
		}

		if (this.focusSpeed < 0 || this.focusSpeed > 7) {
			throw new IllegalArgumentException("Tilt speed with value" + this.focusSpeed + " is out of range. Focus speed must between 0 and 7");
		}

		try {
			initAverRestCommunicator();
			deviceInfo = this.restCommunicator.getDeviceInfo();
		} catch (Exception e) {
			if (this.logger.isErrorEnabled()) {
				this.logger.error("error: Cannot get data from Rest communicator: " + this.host + " port: " + this.port);
			}
			throw new ResourceNotReachableException("Aver rest communicator not reachable for getting data", e);
		} finally {
			try {
				restCommunicator.disconnect();
			} catch (Exception e) {
				if (this.logger.isErrorEnabled()) {
					this.logger.error("error: Cannot disconnect from Rest communicator: " + this.host + " port: " + this.port);
				}
			}
		}

		// Monitoring capabilities
		populateMonitorCapabilities(stats);
		// Control capabilities
		populateControlCapabilities(stats, advancedControllableProperties);

		extStats.setStatistics(stats);
		extStats.setControllableProperties(advancedControllableProperties);

		return Collections.singletonList(extStats);
	}

	/**
	 * This method is used for populate all monitoring properties
	 *
	 * @param stats is the map that store all statistics
	 */
	private void populateMonitorCapabilities(Map<String, String> stats) {
		stats.put(StatisticsProperty.DEVICE_INFORMATION.getName() + HASH + StatisticsProperty.DEVICE_MFG.getName(), deviceInfo.getDeviceMfg());
		stats.put(StatisticsProperty.DEVICE_INFORMATION.getName() + HASH + StatisticsProperty.DEVICE_MODEL.getName(), deviceInfo.getDeviceModel());
		stats.put(StatisticsProperty.DEVICE_INFORMATION.getName() + HASH + StatisticsProperty.DEVICE_SERIAL_NUMBER.getName(), deviceInfo.getDeviceSerialNumber());
		stats.put(StatisticsProperty.DEVICE_INFORMATION.getName() + HASH + StatisticsProperty.DEVICE_FIRMWARE_VERSION.getName(), deviceInfo.getDeviceFirmwareVersion());
		stats.put(StatisticsProperty.DEVICE_INFORMATION.getName() + HASH + StatisticsProperty.DEVICE_LAST_PRESET_RECALLED.getName(), this.getLastPresetRecalled());
	}

	/**
	 * This method is used for populate all controlling properties
	 *
	 * @param stats is the map that store all statistics
	 * @param advancedControllableProperties is the list that store all controllable properties
	 */
	private void populateControlCapabilities(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		// Getting power status from device
		PowerStatus powerStatus = getPowerStatus();

		if (powerStatus == null) {
			stats.put(Command.POWER.getName(), "None");
			return;
		}

		stats.put(Command.POWER.getName(), "");

		if (powerStatus.compareTo(PowerStatus.OFF) == 0) {
			advancedControllableProperties.add(createSwitch(Command.POWER.getName(), 0, PowerStatus.OFF.getName(), PowerStatus.ON.getName()));
		} else if (powerStatus.compareTo(PowerStatus.ON) == 0) {
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
	 * This method is used to control image process
	 */
	private void imageProcessControl(String value, String[] splitProperty) {
		// RGain
		if (splitProperty[1].equals(Command.RGAIN.getName() + RGainControl.UP.getName())) {
			rGainControl(RGainControl.UP);
			return;
		} else if (splitProperty[1].equals(Command.RGAIN.getName() + RGainControl.DOWN.getName())) {
			rGainControl(RGainControl.DOWN);
			return;
		}

		// BGain
		if (splitProperty[1].equals(Command.BGAIN.getName() + BGainControl.UP.getName())) {
			bGainControl(BGainControl.UP);
			return;
		} else if (splitProperty[1].equals(Command.BGAIN.getName() + BGainControl.DOWN.getName())) {
			bGainControl(BGainControl.DOWN);
			return;
		}

		Command imageProcessCommand = Command.getByName(splitProperty[1]);
		switch (imageProcessCommand) {
			case WB_MODE:
				wbModeControl(WBMode.getByName(value));
				break;

			case WB_ONE_PUSH_TRIGGER: {
				wbOnePushTrigger();
				break;
			}
			default: {
				throw new IllegalStateException("Unexpected value: " + Arrays.toString(splitProperty));
			}
		}

	}

	/**
	 * This method is used to control exposure
	 */
	private void exposureControl(String value, Command exposureCommand) {
		switch (exposureCommand) {
			case BACKLIGHT: {
				if (value.equals(SWITCH_STATUS_ON)) {
					backlightControl(BacklightStatus.ON);
				} else if (value.equals(SWITCH_STATUS_OFF)) {
					backlightControl(BacklightStatus.OFF);
				}
				break;
			}
			case AE_MODE: {
				aeModeControl(AEMode.getByName(value));
				break;
			}
			case EXP_COMP_DIRECT:
			case GAIN_LIMIT_DIRECT:
			case GAIN_DIRECT:
			case IRIS_DIRECT:
			case SHUTTER_DIRECT: {
				float directValue = Float.parseFloat(value);
				directControl(exposureCommand, (int) directValue);
				break;
			}
			case AUTO_SLOW_SHUTTER: {
				if (value.equals(SWITCH_STATUS_ON)) {
					slowShutterControl(SlowShutterStatus.ON);
				} else if (value.equals(SWITCH_STATUS_OFF)) {
					slowShutterControl(SlowShutterStatus.OFF);
				}
				break;
			}
			default: {
				throw new IllegalStateException("Unexpected value: " + exposureCommand);
			}
		}
	}

	/**
	 * This method is used to control power
	 */
	public void powerControl(PowerStatus powerStatus) {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.POWER.getCode(), powerStatus.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during power " + powerStatus.getName() + " send", e);
			}
		}
	}

	/**
	 * This method is used to control zoom
	 */
	public void zoomControl(ZoomControl zoomControl) {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.ZOOM.getCode(), (byte) (zoomControl.getCode() + zoomSpeed)));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during zoom " + zoomControl.getName() + " send", e);
			}
		}

		zoomStop();
	}

	/**
	 * This method is used to control zoom stop
	 */
	public void zoomStop() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.ZOOM.getCode(), ZoomControl.STOP.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during zoom stop send", e);
			}
		}
	}

	/**
	 * This method is used to control focus
	 */
	public void focusControl(FocusControl focusControl) {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.FOCUS.getCode(), (byte) (focusControl.getCode() + focusSpeed)));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during focus " + focusControl.getName() + " send", e);
			}
		}

		focusStop();
	}

	/**
	 * This method is used to control focus stop
	 */
	public void focusStop() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.FOCUS.getCode(), FocusControl.STOP.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during focus stop send", e);
			}
		}
	}

	/**
	 * This method is used to control focus mode
	 */
	public void focusModeControl(FocusMode mode) {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.FOCUS_MODE.getCode(), mode.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during " + mode.getName() + " mode send", e);
			}
		}
	}

	/**
	 * This method is used to control one push focus
	 */
	public void onePushFocus() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.FOCUS_ONE_PUSH.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during one push focus send", e);
			}
		}
	}

	/**
	 * This method is used to control wb mode
	 */
	public void wbModeControl(WBMode wbMode) {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.WB_MODE.getCode(), wbMode.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during wb " + wbMode + " mode send", e);
			}
		}
	}

	/**
	 * This method is used to control wb one push trigger
	 */
	public void wbOnePushTrigger() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.WB_ONE_PUSH_TRIGGER.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during wb one push trigger send", e);
			}
		}
	}

	/**
	 * This method is used to control RGain
	 */
	public void rGainControl(RGainControl rGainControl) {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.RGAIN.getCode(), rGainControl.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during RGain " + rGainControl.getName() + " send", e);
			}
		}
	}

	/**
	 * This method is used to control BGain
	 */
	public void bGainControl(BGainControl bGainControl) {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.BGAIN.getCode(), bGainControl.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during BGain " + bGainControl.getName() + " send", e);
			}
		}
	}

	/**
	 * This method is used to control AE mode
	 */
	public void aeModeControl(AEMode aeMode) {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.AE_MODE.getCode(), aeMode.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during AE " + aeMode.getName() + " mode send", e);
			}
		}
	}

	/**
	 * This method is used to control slow shutter
	 */
	public void slowShutterControl(SlowShutterStatus status) {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.AUTO_SLOW_SHUTTER.getCode(), status.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during slow shutter " + status.getName() + " send", e);
			}
		}
	}

	/**
	 * This method is used to control shutter direct
	 *
	 * @param value This is the value to direct
	 */
	public void directControl(Command command, int value) {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] param;

			if (command.equals(Command.GAIN_LIMIT_DIRECT)) {
				param = new byte[] { (byte) value };
			} else {
				param = convertOneByteNumberToTwoBytesArray((byte) value);
			}

			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					command.getCode(), param));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during " + command.getName() + " direct send", e);
			}
		}
	}

	/**
	 * This method is used to control backlight
	 */
	public void backlightControl(BacklightStatus status) {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.BACKLIGHT.getCode(), status.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during backlight " + status.getName() + " send", e);
			}
		}
	}

	/**
	 * This method is used to set preset
	 *
	 * @param preset This is preset value to set
	 */
	public void presetControl(PresetControl control, int preset) {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.PRESET.getCode(), control.getCode(), (byte) preset));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during " + control.getName() + " preset send", e);
			}
		}
	}

	/**
	 * This method is used to slow pan tilt on
	 */
	public void slowPanTiltControl(SlowPanTiltStatus status) {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.PAN_TILTER.getCode(),
					Command.SLOW_PAN_TILT.getCode(), status.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during slow pan tilt " + status.getName() + " send", e);
			}
		}
	}

	/**
	 * This method is used to drive pan tilt up
	 */
	public void panTiltDriveControl(PanTiltDrive panTiltDrive) {
		try {
			int currentSeqNum = ++sequenceNumber;
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			outputStream.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
			outputStream.write(panTiltDrive.getCode());

			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.PAN_TILTER.getCode(),
					Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during pan tilt " + panTiltDrive.getName() + " send", e);
			}
		}

		panTiltStop();
	}

	/**
	 * This method is used to drive pan tilt stop
	 */
	public void panTiltStop() {
		try {
			Thread.sleep(PAN_TILT_PERIOD);
			int currentSeqNum = ++sequenceNumber;
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			outputStream.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
			outputStream.write(PanTiltDrive.STOP.getCode());

			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.PAN_TILTER.getCode(),
					Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during pan tilt stop send", e);
			}
		}
	}

	/**
	 * This method is used to drive pan tilt home
	 */
	public void panTiltHome() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.PAN_TILTER.getCode(),
					Command.PAN_TILT_HOME.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during pan tilt home send", e);
			}
		}
	}
	//endregion

	//region Populate control properties
	//--------------------------------------------------------------------------------------------------------------------------------

	/**
	 * This method is used for populate all zoom control properties
	 *
	 * @param stats is the map that store all statistics
	 * @param advancedControllableProperties is the list that store all controllable properties
	 */
	private void populateZoomControl(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		// Zoom
		stats.put(Command.ZOOM.getName() + HASH + ZoomControl.TELE.getName(), "");
		stats.put(Command.ZOOM.getName() + HASH + ZoomControl.WIDE.getName(), "");

		advancedControllableProperties.add(createButton(Command.ZOOM.getName() + HASH + ZoomControl.TELE.getName(), PLUS));
		advancedControllableProperties.add(createButton(Command.ZOOM.getName() + HASH + ZoomControl.WIDE.getName(), MINUS));
	}

	/**
	 * This method is used for populate all focus control properties
	 *
	 * @param stats is the map that store all statistics
	 * @param advancedControllableProperties is the list that store all controllable properties
	 */
	private void populateFocusControl(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		// Getting focus mode
		FocusMode focusMode = getFocusStatus();

		if (focusMode == null) {
			stats.put(Command.FOCUS.getName() + HASH + Command.FOCUS_MODE.getName(), "None");
			return;
		}

		stats.put(Command.FOCUS.getName() + HASH + Command.FOCUS_ONE_PUSH.getName(), "");
		stats.put(Command.FOCUS.getName() + HASH + Command.FOCUS_MODE.getName(), "");
		advancedControllableProperties.add(createButton(Command.FOCUS.getName() + HASH + Command.FOCUS_ONE_PUSH.getName(), Command.FOCUS_ONE_PUSH.getName()));

		if (focusMode.compareTo(FocusMode.AUTO) == 0) {
			advancedControllableProperties.add(createSwitch(Command.FOCUS.getName() + HASH + Command.FOCUS_MODE.getName(), 0, FocusMode.AUTO.getName(), FocusMode.MANUAL.getName()));
		} else if (focusMode.compareTo(FocusMode.MANUAL) == 0) {
			advancedControllableProperties.add(createSwitch(Command.FOCUS.getName() + HASH + Command.FOCUS_MODE.getName(), 1, FocusMode.AUTO.getName(), FocusMode.MANUAL.getName()));
			stats.put(Command.FOCUS.getName() + HASH + FocusControl.FAR.getName(), "");
			stats.put(Command.FOCUS.getName() + HASH + FocusControl.NEAR.getName(), "");

			advancedControllableProperties.add(createButton(Command.FOCUS.getName() + HASH + FocusControl.FAR.getName(), MINUS));
			advancedControllableProperties.add(createButton(Command.FOCUS.getName() + HASH + FocusControl.NEAR.getName(), PLUS));
		}
	}

	/**
	 * This method is used for populate all AE control properties
	 *
	 * @param stats is the map that store all statistics
	 * @param advancedControllableProperties is the list that store all controllable properties
	 */
	private void populateAEControl(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		AEMode aeMode = this.getAEMode();
		if (aeMode == null) {
			stats.put(Command.EXPOSURE.getName() + HASH + Command.AE_MODE.getName(), "None");
			return;
		}

		stats.put(Command.EXPOSURE.getName() + HASH + Command.AE_MODE.getName(), "");

		List<String> aeModeList = new ArrayList<>();
		aeModeList.add(AEMode.FULL_AUTO.getName());
		aeModeList.add(AEMode.IRIS_PRIORITY.getName());
		aeModeList.add(AEMode.SHUTTER_PRIORITY.getName());
		aeModeList.add(AEMode.MANUAL.getName());

		advancedControllableProperties.add(createDropdown(Command.EXPOSURE.getName() + HASH + Command.AE_MODE.getName(), aeModeList, aeMode.getName()));

		// Getting auto slow shutter status
		SlowShutterStatus autoSlowShutterStatus = getAutoSlowShutterStatus();

		switch (aeMode) {
			case FULL_AUTO: {
				// Populate backlight control
				populateBacklightControl(stats, advancedControllableProperties);

				// Populate exposure control
				populateExposureControl(stats, advancedControllableProperties);

				// Populate gain limit control
				populateGainLimitControl(stats, advancedControllableProperties);

				// Populate slow shutter control
				populateAutoSlowShutterControl(stats, advancedControllableProperties, autoSlowShutterStatus);
				break;
			}
			case SHUTTER_PRIORITY: {
				// Populate exposure control
				populateExposureControl(stats, advancedControllableProperties);

				// Populate gain limit control
				populateGainLimitControl(stats, advancedControllableProperties);

				// Populate shutter control
				populateShutterControl(stats, advancedControllableProperties);
				break;
			}
			case IRIS_PRIORITY: {
				// Populate exposure control
				populateExposureControl(stats, advancedControllableProperties);

				// Populate gain limit control
				populateGainLimitControl(stats, advancedControllableProperties);

				// Populate slow shutter control
				populateAutoSlowShutterControl(stats, advancedControllableProperties, autoSlowShutterStatus);

				// Populate iris control
				populateIrisControl(stats, advancedControllableProperties);
				break;
			}
			case MANUAL:
				// Populate shutter control
				populateShutterControl(stats, advancedControllableProperties);

				// Populate gain control
				populateGainControl(stats, advancedControllableProperties);

				// Populate iris control
				populateIrisControl(stats, advancedControllableProperties);
		}
	}

	/**
	 * This method is used for populate all backlight control properties
	 *
	 * @param stats is the map that store all statistics
	 * @param advancedControllableProperties is the list that store all controllable properties
	 */
	private void populateBacklightControl(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		// Getting backlight status
		BacklightStatus backlightStatus = getBacklightStatus();
		if (backlightStatus == null) {
			stats.put(Command.EXPOSURE.getName() + HASH + Command.BACKLIGHT.getName(), "None");
			return;
		}

		stats.put(Command.EXPOSURE.getName() + HASH + Command.BACKLIGHT.getName(), "");

		if (backlightStatus.compareTo(BacklightStatus.OFF) == 0) {
			advancedControllableProperties.add(createSwitch(Command.EXPOSURE.getName() + HASH + Command.BACKLIGHT.getName(), 0, BacklightStatus.OFF.getName(), BacklightStatus.ON.getName()));
		} else if (backlightStatus.compareTo(BacklightStatus.ON) == 0) {
			advancedControllableProperties.add(createSwitch(Command.EXPOSURE.getName() + HASH + Command.BACKLIGHT.getName(), 1, BacklightStatus.OFF.getName(), BacklightStatus.ON.getName()));
		}
	}

	/**
	 * This method is used for populate all Exposure control properties
	 *
	 * @param stats is the map that store all statistics
	 * @param advancedControllableProperties is the list that store all controllable properties
	 */
	private void populateExposureControl(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		stats.put(Command.EXPOSURE.getName() + HASH + Command.EXP_COMP_DIRECT.getName(), "");
		stats.put(Command.EXPOSURE.getName() + HASH + Command.EXP_COMP_CURRENT.getName(), this.getExposureValue());
		advancedControllableProperties.add(
				createSlider(Command.EXPOSURE.getName() + HASH + Command.EXP_COMP_DIRECT.getName(), LABEL_START_EXPOSURE_VALUE, LABEL_END_EXPOSURE_VALUE,
						RANGE_START_EXPOSURE_VALUE, RANGE_END_EXPOSURE_VALUE, Float.parseFloat(this.getExposureValue()) + 5));
	}

	/**
	 * This method is used for populate all gain limit control properties
	 *
	 * @param stats is the map that store all statistics
	 * @param advancedControllableProperties is the list that store all controllable properties
	 */
	private void populateGainLimitControl(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		stats.put(Command.EXPOSURE.getName() + HASH + Command.GAIN_LIMIT_DIRECT.getName(), "");
		stats.put(Command.EXPOSURE.getName() + HASH + Command.GAIN_LIMIT_CURRENT.getName(), this.getGainLimitLevel());
		// Gain limit level: 24, 27, 30,..., 48 -> Value in slider: (gain limit level - 24) /3
		advancedControllableProperties.add(
				createSlider(Command.EXPOSURE.getName() + HASH + Command.GAIN_LIMIT_DIRECT.getName(), LABEL_START_GAIN_LIMIT_VALUE, LABEL_END_GAIN_LIMIT_VALUE,
						RANGE_START_GAIN_LIMIT_VALUE, RANGE_END_GAIN_LIMIT_VALUE, (Float.parseFloat(this.getGainLimitLevel()) - 24) / 3));
	}

	/**
	 * This method is used for auto slow shutter control properties
	 *
	 * @param stats is the map that store all statistics
	 * @param advancedControllableProperties is the list that store all controllable properties
	 * @param autoSlowShutterStatus is the status of auto slow shutter
	 */
	private void populateAutoSlowShutterControl(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, SlowShutterStatus autoSlowShutterStatus) {
		if (autoSlowShutterStatus == null) {
			stats.put(Command.EXPOSURE.getName() + HASH + Command.AUTO_SLOW_SHUTTER.getName(), "None");
			return;
		}

		stats.put(Command.EXPOSURE.getName() + HASH + Command.AUTO_SLOW_SHUTTER.getName(), "");

		if (autoSlowShutterStatus.compareTo(SlowShutterStatus.ON) == 0) {
			advancedControllableProperties.add(createSwitch(Command.EXPOSURE.getName() + HASH + Command.AUTO_SLOW_SHUTTER.getName(), 1, SlowShutterStatus.OFF.getName(), SlowShutterStatus.ON.getName()));
		} else if (autoSlowShutterStatus.compareTo(SlowShutterStatus.OFF) == 0) {
			advancedControllableProperties.add(createSwitch(Command.EXPOSURE.getName() + HASH + Command.AUTO_SLOW_SHUTTER.getName(), 0, SlowShutterStatus.OFF.getName(), SlowShutterStatus.ON.getName()));
		}
	}

	/**
	 * This method is used for populate all shutter control properties
	 *
	 * @param stats is the map that store all statistics
	 * @param advancedControllableProperties is the list that store all controllable properties
	 */
	private void populateShutterControl(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		stats.put(Command.EXPOSURE.getName() + HASH + Command.SHUTTER_DIRECT.getName(), "");
		stats.put(Command.EXPOSURE.getName() + HASH + Command.SHUTTER_CURRENT.getName(), this.getShutterSpeed().getValue());
		advancedControllableProperties.add(
				createSlider(Command.EXPOSURE.getName() + HASH + Command.SHUTTER_DIRECT.getName(), LABEL_START_SHUTTER_VALUE, LABEL_END_SHUTTER_VALUE, RANGE_START_SHUTTER_VALUE,
						RANGE_END_SHUTTER_VALUE, this.getShutterSpeed().getKey().floatValue()));
	}

	/**
	 * This method is used for populate all iris control properties
	 *
	 * @param stats is the map that store all statistics
	 * @param advancedControllableProperties is the list that store all controllable properties
	 */
	private void populateIrisControl(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		stats.put(Command.EXPOSURE.getName() + HASH + Command.IRIS_DIRECT.getName(), "");
		stats.put(Command.EXPOSURE.getName() + HASH + Command.IRIS_CURRENT.getName(), this.getIrisLevel().getValue());
		advancedControllableProperties.add(
				createSlider(Command.EXPOSURE.getName() + HASH + Command.IRIS_DIRECT.getName(), LABEL_START_IRIS_VALUE, LABEL_END_IRIS_VALUE, RANGE_START_IRIS_VALUE,
						RANGE_END_IRIS_VALUE, this.getIrisLevel().getKey().floatValue()));
	}

	/**
	 * This method is used for populate all gain control properties
	 *
	 * @param stats is the map that store all statistics
	 * @param advancedControllableProperties is the list that store all controllable properties
	 */
	private void populateGainControl(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		stats.put(Command.EXPOSURE.getName() + HASH + Command.GAIN_DIRECT.getName(), "");
		stats.put(Command.EXPOSURE.getName() + HASH + Command.GAIN_CURRENT.getName(), this.getGainLevel());
		advancedControllableProperties.add(
				createSlider(Command.EXPOSURE.getName() + HASH + Command.GAIN_DIRECT.getName(), "0", "48", (float) 0, 48F, Float.parseFloat(this.getGainLevel())));
	}

	/**
	 * This method is used for populate all WB control properties
	 *
	 * @param stats is the map that store all statistics
	 * @param advancedControllableProperties is the list that store all controllable properties
	 */
	private void populateWBControl(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		stats.put(Command.IMAGE_PROCESS.getName() + HASH + Command.WB_MODE.getName(), "");

		List<String> wbModeList = new ArrayList<>();
		wbModeList.add(WBMode.AUTO.getName());
		wbModeList.add(WBMode.INDOOR.getName());
		wbModeList.add(WBMode.OUTDOOR.getName());
		wbModeList.add(WBMode.ONE_PUSH_WB.getName());
		wbModeList.add(WBMode.MANUAL.getName());

		WBMode wbMode = this.getWBMode();
		advancedControllableProperties.add(createDropdown(Command.IMAGE_PROCESS.getName() + HASH + Command.WB_MODE.getName(), wbModeList, wbMode.getName()));

		if (WBMode.MANUAL.equals(wbMode)) {
			stats.put(Command.IMAGE_PROCESS.getName() + HASH + Command.RGAIN.getName() + RGainControl.UP.getName(), "");
			stats.put(Command.IMAGE_PROCESS.getName() + HASH + Command.RGAIN.getName() + RGainControl.DOWN.getName(), "");
			stats.put(Command.IMAGE_PROCESS.getName() + HASH + Command.RGAIN_INQ.getName(), this.getRGain());

			stats.put(Command.IMAGE_PROCESS.getName() + HASH + Command.BGAIN.getName() + BGainControl.UP.getName(), "");
			stats.put(Command.IMAGE_PROCESS.getName() + HASH + Command.BGAIN.getName() + BGainControl.DOWN.getName(), "");
			stats.put(Command.IMAGE_PROCESS.getName() + HASH + Command.BGAIN_INQ.getName(), this.getBGain());

			advancedControllableProperties.add(
					createButton(Command.IMAGE_PROCESS.getName() + HASH + Command.RGAIN.getName() + RGainControl.UP.getName(), RGainControl.UP.getName()));
			advancedControllableProperties.add(
					createButton(Command.IMAGE_PROCESS.getName() + HASH + Command.RGAIN.getName() + RGainControl.DOWN.getName(), RGainControl.DOWN.getName()));

			advancedControllableProperties.add(
					createButton(Command.IMAGE_PROCESS.getName() + HASH + Command.BGAIN.getName() + BGainControl.UP.getName(), BGainControl.UP.getName()));
			advancedControllableProperties.add(
					createButton(Command.IMAGE_PROCESS.getName() + HASH + Command.BGAIN.getName() + BGainControl.DOWN.getName(), BGainControl.DOWN.getName()));

		} else if (WBMode.ONE_PUSH_WB.equals(wbMode)) {
			stats.put(Command.IMAGE_PROCESS.getName() + HASH + Command.WB_ONE_PUSH_TRIGGER.getName(), "");
			advancedControllableProperties.add(
					createButton(Command.IMAGE_PROCESS.getName() + HASH + Command.WB_ONE_PUSH_TRIGGER.getName(), Command.WB_ONE_PUSH_TRIGGER.getName()));
		}
	}

	/**
	 * This method is used for populate all pan tilt control properties
	 *
	 * @param stats is the map that store all statistics
	 * @param advancedControllableProperties is the list that store all controllable properties
	 */
	private void populatePanTiltControl(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		// Pan tilt drive
		stats.put(Command.PAN_TILT_DRIVE.getName() + HASH + PanTiltDrive.UP.getName(), "");
		advancedControllableProperties.add(createButton(Command.PAN_TILT_DRIVE.getName() + HASH + PanTiltDrive.UP.getName(), PanTiltDrive.UP.getName()));

		stats.put(Command.PAN_TILT_DRIVE.getName() + HASH + PanTiltDrive.DOWN.getName(), "");
		advancedControllableProperties.add(createButton(Command.PAN_TILT_DRIVE.getName() + HASH + PanTiltDrive.DOWN.getName(), PanTiltDrive.DOWN.getName()));

		stats.put(Command.PAN_TILT_DRIVE.getName() + HASH + PanTiltDrive.LEFT.getName(), "");
		advancedControllableProperties.add(createButton(Command.PAN_TILT_DRIVE.getName() + HASH + PanTiltDrive.LEFT.getName(), PanTiltDrive.LEFT.getName()));

		stats.put(Command.PAN_TILT_DRIVE.getName() + HASH + PanTiltDrive.RIGHT.getName(), "");
		advancedControllableProperties.add(createButton(Command.PAN_TILT_DRIVE.getName() + HASH + PanTiltDrive.RIGHT.getName(), PanTiltDrive.RIGHT.getName()));

		stats.put(Command.PAN_TILT_DRIVE.getName() + HASH + PanTiltDrive.UP_LEFT.getName(), "");
		advancedControllableProperties.add(createButton(Command.PAN_TILT_DRIVE.getName() + HASH + PanTiltDrive.UP_LEFT.getName(), PanTiltDrive.UP_LEFT.getName()));

		stats.put(Command.PAN_TILT_DRIVE.getName() + HASH + PanTiltDrive.UP_RIGHT.getName(), "");
		advancedControllableProperties.add(createButton(Command.PAN_TILT_DRIVE.getName() + HASH + PanTiltDrive.UP_RIGHT.getName(), PanTiltDrive.UP_RIGHT.getName()));

		stats.put(Command.PAN_TILT_DRIVE.getName() + HASH + PanTiltDrive.DOWN_LEFT.getName(), "");
		advancedControllableProperties.add(createButton(Command.PAN_TILT_DRIVE.getName() + HASH + PanTiltDrive.DOWN_LEFT.getName(), PanTiltDrive.DOWN_LEFT.getName()));

		stats.put(Command.PAN_TILT_DRIVE.getName() + HASH + PanTiltDrive.DOWN_RIGHT.getName(), "");
		advancedControllableProperties.add(createButton(Command.PAN_TILT_DRIVE.getName() + HASH + PanTiltDrive.DOWN_RIGHT.getName(), PanTiltDrive.DOWN_RIGHT.getName()));

		stats.put(Command.PAN_TILT_DRIVE.getName() + HASH + Command.PAN_TILT_HOME.getName(), "");
		advancedControllableProperties.add(createButton(Command.PAN_TILT_DRIVE.getName() + HASH + Command.PAN_TILT_HOME.getName(), Command.PAN_TILT_HOME.getName()));

		// Getting slow pan tilt status
		SlowPanTiltStatus slowPanTiltStatus = getSlowPanTiltStatus();

		if (slowPanTiltStatus == null) {
			stats.put(Command.PAN_TILT_DRIVE.getName() + HASH + Command.SLOW_PAN_TILT.getName(), "None");
			return;
		}

		stats.put(Command.PAN_TILT_DRIVE.getName() + HASH + Command.SLOW_PAN_TILT.getName(), "");

		if (slowPanTiltStatus.compareTo(SlowPanTiltStatus.OFF) == 0) {
			advancedControllableProperties.add(createSwitch(Command.PAN_TILT_DRIVE.getName() + HASH + Command.SLOW_PAN_TILT.getName(), 0, SlowPanTiltStatus.OFF.getName(), SlowPanTiltStatus.ON.getName()));
		} else if (slowPanTiltStatus.compareTo(SlowPanTiltStatus.ON) == 0) {
			advancedControllableProperties.add(createSwitch(Command.PAN_TILT_DRIVE.getName() + HASH + Command.SLOW_PAN_TILT.getName(), 1, SlowPanTiltStatus.OFF.getName(), SlowPanTiltStatus.ON.getName()));
		}
	}

	/**
	 * This method is used for populate all preset control properties
	 *
	 * @param stats is the map that store all statistics
	 * @param advancedControllableProperties is the list that store all controllable properties
	 */
	private void populatePresetControl(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		stats.put(Command.PRESET.getName() + HASH + PresetControl.SET.getName(), "");
		advancedControllableProperties.add(createNumeric(Command.PRESET.getName() + HASH + PresetControl.SET.getName()));

		stats.put(Command.PRESET.getName() + HASH + PresetControl.RECALL.getName(), "");
		advancedControllableProperties.add(createNumeric(Command.PRESET.getName() + HASH + PresetControl.RECALL.getName()));
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
					buildSendPacket(cameraID, currentSeqNum, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), PayloadCategory.CAMERA.getCode(), Command.PRESET.getCode()));

			return String.valueOf(digestResponse(response, currentSeqNum, CommandType.INQUIRY, Command.PRESET));
		} catch (Exception e) {
			if (this.logger.isErrorEnabled()) {
				this.logger.error("error during get last preset recalled send", e);
			}
		}
		return "None";
	}

	/**
	 * This method is used to get the current display power status
	 *
	 * @return String This returns the power status
	 */
	private PowerStatus getPowerStatus() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(
					buildSendPacket(cameraID, currentSeqNum, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), PayloadCategory.CAMERA.getCode(), Command.POWER.getCode()));

			PowerStatus status = (PowerStatus) digestResponse(response, currentSeqNum, CommandType.INQUIRY, Command.POWER);

			if (status == null) {
				return PowerStatus.OFF;
			} else {
				return status;
			}
		} catch (Exception e) {
			if (this.logger.isErrorEnabled()) {
				this.logger.error("error during get power send", e);
			}
		}
		return null;
	}

	/**
	 * This method is used to get the current display focus status
	 *
	 * @return String This returns the focus status
	 */
	private FocusMode getFocusStatus() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(
					buildSendPacket(cameraID, currentSeqNum, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), PayloadCategory.CAMERA.getCode(), Command.FOCUS_MODE.getCode()));

			FocusMode mode = (FocusMode) digestResponse(response, currentSeqNum, CommandType.INQUIRY, Command.FOCUS_MODE);

			if (mode == null) {
				return FocusMode.AUTO;
			} else {
				return mode;
			}
		} catch (Exception e) {
			if (this.logger.isErrorEnabled()) {
				this.logger.error("error during get focus mode", e);
			}
		}
		return null;
	}

	/**
	 * This method is used to get the current display backlight status
	 *
	 * @return String This returns the backlight status
	 */
	private BacklightStatus getBacklightStatus() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(
					buildSendPacket(cameraID, currentSeqNum, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), PayloadCategory.CAMERA.getCode(), Command.BACKLIGHT.getCode()));

			BacklightStatus status = (BacklightStatus) digestResponse(response, currentSeqNum, CommandType.INQUIRY, Command.BACKLIGHT);

			if (status == null) {
				return BacklightStatus.OFF;
			} else {
				return status;
			}
		} catch (Exception e) {
			if (this.logger.isErrorEnabled()) {
				this.logger.error("error during get backlight status", e);
			}
		}
		return null;
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
					buildSendPacket(cameraID, currentSeqNum, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), PayloadCategory.CAMERA.getCode(), Command.AE_MODE.getCode()));

			AEMode mode = (AEMode) digestResponse(response, currentSeqNum, CommandType.INQUIRY, Command.AE_MODE);

			if (mode == null) {
				return AEMode.FULL_AUTO;
			} else {
				return mode;
			}
		} catch (Exception e) {
			if (this.logger.isErrorEnabled()) {
				this.logger.error("error during get AE mode", e);
			}
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
					buildSendPacket(cameraID, currentSeqNum, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), PayloadCategory.CAMERA.getCode(), Command.EXP_COMP_DIRECT.getCode()));
			// Exposure value: -4 -> 4, Value get from device: 1 -> 9 => Exposure value = value from device - 5
			return String.valueOf((int) digestResponse(response, currentSeqNum, CommandType.INQUIRY, Command.EXP_COMP_DIRECT) - 5);
		} catch (Exception e) {
			if (this.logger.isErrorEnabled()) {
				this.logger.error("error during get exposure value", e);
			}
		}
		return LABEL_START_EXPOSURE_VALUE;
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
					buildSendPacket(cameraID, currentSeqNum, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), PayloadCategory.CAMERA.getCode(), Command.SHUTTER_DIRECT.getCode()));

			int index = (int) digestResponse(response, currentSeqNum, CommandType.INQUIRY, Command.SHUTTER_DIRECT);

			return new SimpleEntry<>(index, SHUTTER_VALUES.get(index));
		} catch (Exception e) {
			if (this.logger.isErrorEnabled()) {
				this.logger.error("error during get shutter speed", e);
			}
		}
		return new SimpleEntry<>(0, SHUTTER_VALUES.get(0));
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
					buildSendPacket(cameraID, currentSeqNum, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), PayloadCategory.CAMERA.getCode(), Command.IRIS_DIRECT.getCode()));

			int index = (int) digestResponse(response, currentSeqNum, CommandType.INQUIRY, Command.IRIS_DIRECT);
			return new SimpleEntry<>(index, IRIS_LEVELS.get(index));
		} catch (Exception e) {
			if (this.logger.isErrorEnabled()) {
				this.logger.error("error during get iris level", e);
			}
		}
		return new SimpleEntry<>(0, IRIS_LEVELS.get(0));
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
					buildSendPacket(cameraID, currentSeqNum, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), PayloadCategory.CAMERA.getCode(), Command.GAIN_DIRECT.getCode()));

			return String.valueOf(digestResponse(response, currentSeqNum, CommandType.INQUIRY, Command.GAIN_DIRECT));
		} catch (Exception e) {
			if (this.logger.isErrorEnabled()) {
				this.logger.error("error during get gain level", e);
			}
		}
		return LABEL_START_IRIS_VALUE;
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
					buildSendPacket(cameraID, currentSeqNum, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), PayloadCategory.CAMERA.getCode(), Command.GAIN_LIMIT_DIRECT.getCode()));
			// Gain limit level: 24, 27, ... , 48. Value get from device: 0,1, ... , 8 => gain limit level = (value from device + 24 ) * 3
			return String.valueOf(24 + (int) digestResponse(response, currentSeqNum, CommandType.INQUIRY, Command.GAIN_LIMIT_DIRECT) * 3);
		} catch (Exception e) {
			if (this.logger.isErrorEnabled()) {
				this.logger.error("error during get gain limit level", e);
			}
		}
		return LABEL_START_GAIN_LIMIT_VALUE;
	}

	/**
	 * This method is used to get the current display current WB mode
	 *
	 * @return String This returns the WB mode
	 */
	private WBMode getWBMode() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(
					buildSendPacket(cameraID, currentSeqNum, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), PayloadCategory.CAMERA.getCode(), Command.WB_MODE.getCode()));

			WBMode mode = (WBMode) digestResponse(response, currentSeqNum, CommandType.INQUIRY, Command.WB_MODE);

			if (mode == null) {
				return WBMode.MANUAL;
			} else {
				return mode;
			}
		} catch (Exception e) {
			if (this.logger.isErrorEnabled()) {
				this.logger.error("error during get WB mode", e);
			}
			return WBMode.MANUAL;
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
					buildSendPacket(cameraID, currentSeqNum, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), PayloadCategory.CAMERA.getCode(), Command.RGAIN_INQ.getCode()));

			return String.valueOf(digestResponse(response, currentSeqNum, CommandType.INQUIRY, Command.RGAIN_INQ));

		} catch (Exception e) {
			if (this.logger.isErrorEnabled()) {
				this.logger.error("error during get RGain value", e);
			}
		}
		return RGAIN_START;
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
					buildSendPacket(cameraID, currentSeqNum, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), PayloadCategory.CAMERA.getCode(), Command.BGAIN_INQ.getCode()));

			return String.valueOf(digestResponse(response, currentSeqNum, CommandType.INQUIRY, Command.BGAIN_INQ));

		} catch (Exception e) {
			if (this.logger.isErrorEnabled()) {
				this.logger.error("error during get BGain value", e);
			}
		}
		return BGAIN_START;
	}

	/**
	 * This method is used to get the current display current slow pan tilt status
	 *
	 * @return String This returns the slow pan tilt status
	 */
	private SlowPanTiltStatus getSlowPanTiltStatus() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(
					buildSendPacket(cameraID, currentSeqNum, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), PayloadCategory.PAN_TILTER.getCode(), Command.SLOW_PAN_TILT.getCode()));

			SlowPanTiltStatus status = (SlowPanTiltStatus) digestResponse(response, currentSeqNum, CommandType.INQUIRY, Command.SLOW_PAN_TILT);

			if (status == null) {
				return SlowPanTiltStatus.OFF;
			} else {
				return status;
			}
		} catch (Exception e) {
			if (this.logger.isErrorEnabled()) {
				this.logger.error("error during get slow pan tilt status", e);
			}
		}
		return null;
	}

	/**
	 * This method is used to get the current display current auto slow shutter status
	 *
	 * @return String This returns the auto slow shutter status
	 */
	private SlowShutterStatus getAutoSlowShutterStatus() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(
					buildSendPacket(cameraID, currentSeqNum, PayloadType.INQUIRY.getCode(), CommandType.INQUIRY.getCode(), PayloadCategory.CAMERA.getCode(), Command.AUTO_SLOW_SHUTTER.getCode()));

			SlowShutterStatus status = (SlowShutterStatus) digestResponse(response, currentSeqNum, CommandType.INQUIRY, Command.AUTO_SLOW_SHUTTER);

			if (status == null) {
				return SlowShutterStatus.OFF;
			} else {
				return status;
			}
		} catch (Exception e) {
			if (this.logger.isErrorEnabled()) {
				this.logger.error("error during get slow auto slow shutter status", e);
			}
		}
		return null;
	}
	//--------------------------------------------------------------------------------------------------------------------------------
	//endregion

	/**
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
						if (this.logger.isErrorEnabled()) {
							this.logger.error("error: Unexpected completion packet: " + this.host + " port: " + this.port);
						}
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
							return (int) reply[2];
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
							break;
					}
				}
			} else {
				if (this.logger.isErrorEnabled()) {
					this.logger.error("error: Unexpected sequence number: " + this.host + " port: " + this.port);
				}
				throw new IllegalStateException("Unexpected sequence number");
			}
		} else {
			if (this.logger.isErrorEnabled()) {
				this.logger.error("error: Unexpected reply: " + this.host + " port: " + this.port);
			}
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
		button.setGracePeriod(1000L);

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

	/***
	 * Create AdvancedControllableProperty preset instance
	 * @param name name of the control
	 * @return AdvancedControllableProperty preset instance
	 */
	private AdvancedControllableProperty createNumeric(String name) {
		AdvancedControllableProperty.Numeric numeric = new AdvancedControllableProperty.Numeric();

		return new AdvancedControllableProperty(name, new Date(), numeric, 0);
	}
	//--------------------------------------------------------------------------------------------------------------------------------
	//endregion
}
