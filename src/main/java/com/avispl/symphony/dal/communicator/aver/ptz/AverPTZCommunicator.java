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
	private int sequenceNumber = 0;
	private AverPTZRestCommunicator restCommunicator;
	private DeviceInfo deviceInfo;

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

		try {
			// Power
			if (property.startsWith(Command.POWER.getName())) {
				if (value.equals("1")) {
					powerOn();
				} else if (value.equals("0")) {
					powerOff();
				}
			}

			// Zoom
			else if (property.startsWith(Command.ZOOM.getName())) {
				if (property.endsWith(ZoomControl.TELE.getName())) {
					zoomTele();
				} else if (property.endsWith(ZoomControl.WIDE.getName())) {
					zoomWide();
				}
			}

			// Focus
			else if (property.startsWith(Command.FOCUS.getName())) {
				if (property.endsWith(Command.FOCUS_MODE.getName())) {
					if (value.equals("1")) {
						manualFocus();
					} else if (value.equals("0")) {
						autoFocus();
					}
				} else if (property.endsWith(Command.FOCUS_ONE_PUSH.getName())) {
					onePushFocus();
				} else if (property.endsWith(FocusControl.FAR.getName())) {
					focusFar();
				} else if (property.endsWith(FocusControl.NEAR.getName())) {
					focusNear();
				}
			}

			// Exposure
			else if (property.startsWith(Command.EXPOSURE.getName())) {
				// Backlight
				if (property.endsWith(Command.BACKLIGHT.getName())) {
					if (value.equals("1")) {
						backlightOn();
					} else if (value.equals("0")) {
						backlightOff();
					}
				}

				// AE mode
				else if (property.endsWith(Command.AE_MODE.getName())) {
					if (value.equals(AEMode.FULL_AUTO.getName())) {
						aeFullAuto();
					} else if (value.equals(AEMode.MANUAL.getName())) {
						aeManual();
					} else if (value.equals(AEMode.IRIS_PRIORITY.getName())) {
						aeIrisPriority();
					} else if (value.equals(AEMode.SHUTTER_PRIORITY.getName())) {
						aeShutterPriority();
					}
				}

				// Direct
				else if (property.endsWith(Command.EXP_COMP_DIRECT.getName())) {
					float exposureValue = Float.parseFloat(value);
					expCompDirect((int) exposureValue);
				} else if (property.endsWith(Command.GAIN_LIMIT_DIRECT.getName())) {
					float gainLimitLevel = Float.parseFloat(value);
					gainLimitDirect((int) gainLimitLevel);
				} else if (property.endsWith(Command.GAIN_DIRECT.getName())) {
					float gainLevel = Float.parseFloat(value);
					gainDirect((int) gainLevel);
				} else if (property.endsWith(Command.IRIS_DIRECT.getName())) {
					float irisLevel = Float.parseFloat(value);
					irisDirect((int) irisLevel);
				} else if (property.endsWith(Command.SHUTTER_DIRECT.getName())) {
					float shutterSpeed = Float.parseFloat(value);
					shutterDirect((int) shutterSpeed);
				}

				// Auto slow shutter
				else if (property.endsWith(Command.AUTO_SLOW_SHUTTER.getName())) {
					if (value.equals("1")) {
						slowShutterOn();
					} else if (value.equals("0")) {
						slowShutterOff();
					}
				}
			}

			// Image process
			else if (property.startsWith(Command.IMAGE_PROCESS.getName())) {
				// WB mode
				if (property.endsWith(Command.WB_MODE.getName())) {
					if (value.equals(WBMode.AUTO.getName())) {
						wbAuto();
					} else if (value.equals(WBMode.INDOOR.getName())) {
						wbIndoor();
					} else if (value.equals(WBMode.OUTDOOR.getName())) {
						wbOutdoor();
					} else if (value.equals(WBMode.ONE_PUSH_WB.getName())) {
						wbOnePush();
					} else if (value.equals(WBMode.MANUAL.getName())) {
						wbManual();
					}
				}

				// WB one push trigger
				else if (property.endsWith(Command.WB_ONE_PUSH_TRIGGER.getName())) {
					wbOnePushTrigger();
				}

				// RGain
				else if (property.endsWith(Command.RGAIN.getName() + RGainControl.UP.getName())) {
					rGainUp();
				} else if (property.endsWith(Command.RGAIN.getName() + RGainControl.DOWN.getName())) {
					rGainDown();
				}

				// BGain
				else if (property.endsWith(Command.BGAIN.getName() + BGainControl.UP.getName())) {
					bGainUp();
				} else if (property.endsWith(Command.BGAIN.getName() + BGainControl.DOWN.getName())) {
					bGainDown();
				}
			}

			// Pan tilt drive
			else if (property.startsWith(Command.PAN_TILT_DRIVE.getName())) {
				if (property.endsWith(PanTiltDrive.UP.getName())) {
					panTiltUp();
				} else if (property.endsWith(PanTiltDrive.DOWN.getName())) {
					panTiltDown();
				} else if (property.endsWith(PanTiltDrive.UP_LEFT.getName())) {
					panTiltUpLeft();
				} else if (property.endsWith(PanTiltDrive.UP_RIGHT.getName())) {
					panTiltUpRight();
				} else if (property.endsWith(PanTiltDrive.DOWN_LEFT.getName())) {
					panTiltDownLeft();
				} else if (property.endsWith(PanTiltDrive.DOWN_RIGHT.getName())) {
					pantTiltDownRight();
				} else if (property.endsWith(PanTiltDrive.LEFT.getName())) {
					panTiltLeft();
				} else if (property.endsWith(PanTiltDrive.RIGHT.getName())) {
					panTiltRight();
				} else if (property.endsWith(Command.PAN_TILT_HOME.getName())) {
					panTiltHome();
				} else if (property.endsWith(Command.SLOW_PAN_TILT.getName())) {
					if (value.equals("1")) {
						slowPanTiltOn();
					} else if (value.equals("0")) {
						slowPanTiltOff();
					}
				}
			}

			// Preset
			else if (property.startsWith(Command.PRESET.getName())) {
				if (property.endsWith(PresetControl.SET.getName())) {
					setPreset(Integer.parseInt(value));
				} else if (property.endsWith(PresetControl.RECALL.getName())) {
					recallPreset(Integer.parseInt(value));
				}
			}
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
		controllableProperties.forEach(p -> {
			try {
				controlProperty(p);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
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

		// Monitoring capabilities
		try {
			initAverRestCommunicator();
			deviceInfo = this.restCommunicator.getDeviceInfo();
			populateMonitorCapabilities(stats);
			// Control capabilities
			populateControlCapabilities(stats, advancedControllableProperties);
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
		stats.put(Command.POWER.getName(), "");

		assert powerStatus != null;
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
	 * This method is used to control power on
	 */
	public void powerOn() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.POWER.getCode(), PowerStatus.ON.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during power on send", e);
			}
		}
	}

	/**
	 * This method is used to control power off
	 */
	public void powerOff() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.POWER.getCode(), PowerStatus.OFF.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during power off send", e);
			}
		}
	}

	/**
	 * This method is used to control zoom tele
	 */
	public void zoomTele() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.ZOOM.getCode(), ZoomControl.TELE.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during zoom tele send", e);
			}
		}
	}

	/**
	 * This method is used to control zoom wide
	 */
	public void zoomWide() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.ZOOM.getCode(), ZoomControl.WIDE.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during zoom wide send", e);
			}
		}
	}

	/**
	 * This method is used to control focus far
	 */
	public void focusFar() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.FOCUS.getCode(), FocusControl.FAR.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during focus far send", e);
			}
		}
	}

	/**
	 * This method is used to control focus near
	 */
	public void focusNear() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.FOCUS.getCode(), FocusControl.NEAR.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during focus near send", e);
			}
		}
	}

	/**
	 * This method is used to control auto-focus mode
	 */
	public void autoFocus() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.FOCUS_MODE.getCode(), FocusMode.AUTO.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during auto-focus mode send", e);
			}
		}
	}

	/**
	 * This method is used to control manual focus mode
	 */
	public void manualFocus() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.FOCUS_MODE.getCode(), FocusMode.MANUAL.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during focus mode send", e);
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
	 * This method is used to control wb auto mode
	 */
	public void wbAuto() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.WB_MODE.getCode(), WBMode.AUTO.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during wb auto mode send", e);
			}
		}
	}

	/**
	 * This method is used to control wb indoor mode
	 */
	public void wbIndoor() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.WB_MODE.getCode(), WBMode.INDOOR.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during wb indoor mode send", e);
			}
		}
	}

	/**
	 * This method is used to control wb outdoor mode
	 */
	public void wbOutdoor() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.WB_MODE.getCode(), WBMode.OUTDOOR.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during wb outdoor mode send", e);
			}
		}
	}

	/**
	 * This method is used to control wb one push mode
	 */
	public void wbOnePush() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.WB_MODE.getCode(), WBMode.ONE_PUSH_WB.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during wb one push mode send", e);
			}
		}
	}

	/**
	 * This method is used to control wb manual mode
	 */
	public void wbManual() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.WB_MODE.getCode(), WBMode.MANUAL.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during wb manual mode send", e);
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
	 * This method is used to control RGain up
	 */
	public void rGainUp() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.RGAIN.getCode(), RGainControl.UP.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during RGain up send", e);
			}
		}
	}

	/**
	 * This method is used to control RGain down
	 */
	public void rGainDown() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.RGAIN.getCode(), RGainControl.DOWN.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during RGain down send", e);
			}
		}
	}

	/**
	 * This method is used to control BGain up
	 */
	public void bGainUp() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.BGAIN.getCode(), BGainControl.UP.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during BGain up send", e);
			}
		}
	}

	/**
	 * This method is used to control BGain down
	 */
	public void bGainDown() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.BGAIN.getCode(), BGainControl.DOWN.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during BGain down send", e);
			}
		}
	}

	/**
	 * This method is used to control AE full auto mode
	 */
	public void aeFullAuto() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.AE_MODE.getCode(), AEMode.FULL_AUTO.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during AE full auto mode send", e);
			}
		}
	}

	/**
	 * This method is used to control AE manual mode
	 */
	public void aeManual() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.AE_MODE.getCode(), AEMode.MANUAL.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during AE manual mode send", e);
			}
		}
	}

	/**
	 * This method is used to control AE iris priority mode
	 */
	public void aeIrisPriority() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.AE_MODE.getCode(), AEMode.IRIS_PRIORITY.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during AE iris priority mode send", e);
			}
		}
	}

	/**
	 * This method is used to control AE shutter priority mode
	 */
	public void aeShutterPriority() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.AE_MODE.getCode(), AEMode.SHUTTER_PRIORITY.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during AE shutter priority mode send", e);
			}
		}
	}

	/**
	 * This method is used to control slow shutter on
	 */
	public void slowShutterOn() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.AUTO_SLOW_SHUTTER.getCode(), SlowShutterStatus.ON.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during slow shutter on send", e);
			}
		}
	}

	/**
	 * This method is used to control slow shutter off
	 */
	public void slowShutterOff() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.AUTO_SLOW_SHUTTER.getCode(), SlowShutterStatus.OFF.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during slow shutter off send", e);
			}
		}
	}

	/**
	 * This method is used to control shutter direct
	 *
	 * @param shutterSpeed This is shutter speed value to direct
	 */
	public void shutterDirect(int shutterSpeed) {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.SHUTTER_DIRECT.getCode(), convertOneByteNumberToTwoBytesArray((byte) shutterSpeed)));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during shutter direct send", e);
			}
		}
	}

	/**
	 * This method is used to control iris level
	 *
	 * @param irisLevel This is iris level value to direct
	 */
	public void irisDirect(int irisLevel) {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.IRIS_DIRECT.getCode(), convertOneByteNumberToTwoBytesArray((byte) irisLevel)));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during iris direct send", e);
			}
		}
	}

	/**
	 * This method is used to control gain level
	 *
	 * @param gainLevel This is gain level value to direct
	 */
	public void gainDirect(int gainLevel) {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.GAIN_DIRECT.getCode(), convertOneByteNumberToTwoBytesArray((byte) gainLevel)));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during gain direct send", e);
			}
		}
	}

	/**
	 * This method is used to control gain limit level
	 *
	 * @param gainLimitLevel This is gain limit level value to direct
	 */
	public void gainLimitDirect(int gainLimitLevel) {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.GAIN_LIMIT_DIRECT.getCode(), (byte) gainLimitLevel));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during gain limit direct send", e);
			}
		}
	}

	/**
	 * This method is used to control exposure value
	 *
	 * @param exposureValue This is exposure value to direct
	 */
	public void expCompDirect(int exposureValue) {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.EXP_COMP_DIRECT.getCode(), convertOneByteNumberToTwoBytesArray((byte) exposureValue)));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during exp comp direct send", e);
			}
		}
	}

	/**
	 * This method is used to backlight on
	 */
	public void backlightOn() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.BACKLIGHT.getCode(), BacklightStatus.ON.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during backlight on send", e);
			}
		}
	}

	/**
	 * This method is used to backlight off
	 */
	public void backlightOff() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.BACKLIGHT.getCode(), BacklightStatus.OFF.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during backlight off send", e);
			}
		}
	}

	/**
	 * This method is used to set preset
	 *
	 * @param preset This is preset value to set
	 */
	public void setPreset(int preset) {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.PRESET.getCode(), PresetControl.SET.getCode(), (byte) preset));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during set preset send", e);
			}
		}
	}

	/**
	 * This method is used to recall preset
	 *
	 * @param preset This is preset value to recall
	 */
	public void recallPreset(int preset) {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.CAMERA.getCode(),
					Command.PRESET.getCode(), PresetControl.RECALL.getCode(), (byte) preset));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during recall preset send", e);
			}
		}
	}

	/**
	 * This method is used to slow pan tilt on
	 */
	public void slowPanTiltOn() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.PAN_TILTER.getCode(),
					Command.SLOW_PAN_TILT.getCode(), SlowPanTiltStatus.ON.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during slow pan tilt on send", e);
			}
		}
	}

	/**
	 * This method is used to slow pan tilt off
	 */
	public void slowPanTiltOff() {
		try {
			int currentSeqNum = ++sequenceNumber;
			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.PAN_TILTER.getCode(),
					Command.SLOW_PAN_TILT.getCode(), SlowPanTiltStatus.OFF.getCode()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during slow pan tilt off send", e);
			}
		}
	}

	/**
	 * This method is used to drive pan tilt up
	 */
	public void panTiltUp() {
		try {
			int currentSeqNum = ++sequenceNumber;
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			outputStream.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
			outputStream.write(PanTiltDrive.UP.getCode());

			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.PAN_TILTER.getCode(),
					Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during pan tilt up send", e);
			}
		}
	}

	/**
	 * This method is used to drive pan tilt down
	 */
	public void panTiltDown() {
		try {
			int currentSeqNum = ++sequenceNumber;
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			outputStream.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
			outputStream.write(PanTiltDrive.DOWN.getCode());

			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.PAN_TILTER.getCode(),
					Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during pan tilt down send", e);
			}
		}
	}

	/**
	 * This method is used to drive pan tilt left
	 */
	public void panTiltLeft() {
		try {
			int currentSeqNum = ++sequenceNumber;
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			outputStream.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
			outputStream.write(PanTiltDrive.LEFT.getCode());

			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.PAN_TILTER.getCode(),
					Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during pan tilt left send", e);
			}
		}
	}

	/**
	 * This method is used to drive pan tilt right
	 */
	public void panTiltRight() {
		try {
			int currentSeqNum = ++sequenceNumber;
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			outputStream.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
			outputStream.write(PanTiltDrive.RIGHT.getCode());

			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.PAN_TILTER.getCode(),
					Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during pan tilt right send", e);
			}
		}
	}

	/**
	 * This method is used to drive pan tilt up left
	 */
	public void panTiltUpLeft() {
		try {
			int currentSeqNum = ++sequenceNumber;
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			outputStream.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
			outputStream.write(PanTiltDrive.UP_LEFT.getCode());

			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.PAN_TILTER.getCode(),
					Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during pan tilt up left send", e);
			}
		}
	}

	/**
	 * This method is used to drive pan tilt up right
	 */
	public void panTiltUpRight() {
		try {
			int currentSeqNum = ++sequenceNumber;
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			outputStream.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
			outputStream.write(PanTiltDrive.UP_RIGHT.getCode());

			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.PAN_TILTER.getCode(),
					Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during pan tilt up right send", e);
			}
		}
	}

	/**
	 * This method is used to drive pan tilt down left
	 */
	public void panTiltDownLeft() {
		try {
			int currentSeqNum = ++sequenceNumber;
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			outputStream.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
			outputStream.write(PanTiltDrive.DOWN_LEFT.getCode());

			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.PAN_TILTER.getCode(),
					Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during pan tilt down left send", e);
			}
		}
	}

	/**
	 * This method is used to drive pan tilt down right
	 */
	public void pantTiltDownRight() {
		try {
			int currentSeqNum = ++sequenceNumber;
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			outputStream.write(new byte[] { (byte) panSpeed, (byte) tiltSpeed });
			outputStream.write(PanTiltDrive.DOWN_RIGHT.getCode());

			byte[] response = send(buildSendPacket(cameraID, currentSeqNum, PayloadType.COMMAND.getCode(), CommandType.COMMAND.getCode(), PayloadCategory.PAN_TILTER.getCode(),
					Command.PAN_TILT_DRIVE.getCode(), outputStream.toByteArray()));

			digestResponse(response, currentSeqNum, CommandType.COMMAND, null);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("error during pan tilt down right send", e);
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

		advancedControllableProperties.add(createButton(Command.ZOOM.getName() + HASH + ZoomControl.TELE.getName(), ZoomControl.TELE.getName()));
		advancedControllableProperties.add(createButton(Command.ZOOM.getName() + HASH + ZoomControl.WIDE.getName(), ZoomControl.WIDE.getName()));
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

		stats.put(Command.FOCUS.getName() + HASH + Command.FOCUS_ONE_PUSH.getName(), "");
		stats.put(Command.FOCUS.getName() + HASH + Command.FOCUS_MODE.getName(), "");
		advancedControllableProperties.add(createButton(Command.FOCUS.getName() + HASH + Command.FOCUS_ONE_PUSH.getName(), Command.FOCUS_ONE_PUSH.getName()));

		assert focusMode != null;

		if (focusMode.compareTo(FocusMode.AUTO) == 0) {
			advancedControllableProperties.add(createSwitch(Command.FOCUS.getName() + HASH + Command.FOCUS_MODE.getName(), 0, FocusMode.AUTO.getName(), FocusMode.MANUAL.getName()));
		} else if (focusMode.compareTo(FocusMode.MANUAL) == 0) {
			advancedControllableProperties.add(createSwitch(Command.FOCUS.getName() + HASH + Command.FOCUS_MODE.getName(), 1, FocusMode.AUTO.getName(), FocusMode.MANUAL.getName()));
			stats.put(Command.FOCUS.getName() + HASH + FocusControl.FAR.getName(), "");
			stats.put(Command.FOCUS.getName() + HASH + FocusControl.NEAR.getName(), "");

			advancedControllableProperties.add(createButton(Command.FOCUS.getName() + HASH + FocusControl.FAR.getName(), FocusControl.FAR.getName()));
			advancedControllableProperties.add(createButton(Command.FOCUS.getName() + HASH + FocusControl.NEAR.getName(), FocusControl.NEAR.getName()));
		}
	}

	/**
	 * This method is used for populate all AE control properties
	 *
	 * @param stats is the map that store all statistics
	 * @param advancedControllableProperties is the list that store all controllable properties
	 */
	private void populateAEControl(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		stats.put(Command.EXPOSURE.getName() + HASH + Command.AE_MODE.getName(), "");

		List<String> aeModeList = new ArrayList<>();
		aeModeList.add(AEMode.FULL_AUTO.getName());
		aeModeList.add(AEMode.IRIS_PRIORITY.getName());
		aeModeList.add(AEMode.SHUTTER_PRIORITY.getName());
		aeModeList.add(AEMode.MANUAL.getName());

		AEMode aeMode = this.getAEMode();
		assert aeMode != null;
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
				assert autoSlowShutterStatus != null;
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
				assert autoSlowShutterStatus != null;
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
		stats.put(Command.EXPOSURE.getName() + HASH + Command.BACKLIGHT.getName(), "");
		assert backlightStatus != null;

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
		stats.put(Command.PAN_TILT_DRIVE.getName() + HASH + Command.SLOW_PAN_TILT.getName(), "");

		assert slowPanTiltStatus != null;
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
		return null;
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
		button.setGracePeriod(30L);

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
