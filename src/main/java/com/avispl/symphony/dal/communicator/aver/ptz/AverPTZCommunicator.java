/*
 * Copyright (c) 2021 AVI-SPL Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.communicator.aver.ptz;

import static com.avispl.symphony.dal.communicator.aver.ptz.AverPTZUtils.convertIntToByteArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.avispl.symphony.api.dal.control.Controller;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.Statistics;
import com.avispl.symphony.api.dal.error.CommandFailureException;
import com.avispl.symphony.api.dal.monitor.Monitorable;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.ReplyStatus;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.PayloadType;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.Prefix;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.command.Command;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.command.CommandType;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.AEMode;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.BacklightStatus;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.FocusMode;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.PowerStatus;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.SlowPanTiltStatus;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.SlowShutterStatus;
import com.avispl.symphony.dal.communicator.aver.ptz.enums.payload.param.WBMode;

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
	private String panSpeed;
	private String tiltSpeed;
	private int sequenceNumber = 0;

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
		return Integer.parseInt(cameraID);
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
	 * Retrieves {@code {@link #sequenceNumber}}
	 *
	 * @return value of {@link #sequenceNumber}
	 */
	public int getSequenceNumber() {
		return sequenceNumber;
	}

	/**
	 * Sets {@code sequenceNumber}
	 *
	 * @param sequenceNumber the {@code int} field
	 */
	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	/**
	 * This method is recalled by Symphony to control specific property
	 *
	 * @param controllableProperty This is the property to be controlled
	 */
	@Override
	public void controlProperty(ControllableProperty controllableProperty) {

	}

	/**
	 * This method is recalled by Symphony to control a list of properties
	 *
	 * @param controllableProperties This is the list of properties to be controlled
	 */
	@Override
	public void controlProperties(List<ControllableProperty> controllableProperties) {

	}

	/**
	 * This method is recalled by Symphony to get the list of statistics to be displayed
	 *
	 * @return List<Statistics> This return the list of statistics.
	 */
	@Override
	public List<Statistics> getMultipleStatistics() {
		return Collections.emptyList();
	}

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

							return powerStatus.<Object>map(PowerStatus::getName).orElse(null);
						}
						case FOCUS_MODE: {
							Optional<FocusMode> focusMode = Arrays.stream(FocusMode.values())
									.filter(mode -> mode.getCode() == currentValue)
									.findFirst();

							return focusMode.<Object>map(FocusMode::getName).orElse(null);
						}
						case AE_MODE: {
							Optional<AEMode> aeMode = Arrays.stream(AEMode.values())
									.filter(mode -> mode.getCode() == currentValue)
									.findFirst();

							return aeMode.<Object>map(AEMode::getName).orElse(null);
						}
						case AUTO_SLOW_SHUTTER: {
							Optional<SlowShutterStatus> slowShutterStatus = Arrays.stream(SlowShutterStatus.values())
									.filter(mode -> mode.getCode() == currentValue)
									.findFirst();

							return slowShutterStatus.<Object>map(SlowShutterStatus::getName).orElse(null);
						}
						case SHUTTER_DIRECT:
						case IRIS_DIRECT:
						case GAIN_DIRECT:
						case EXP_COMP_DIRECT:
						case RGAIN:
						case BGAIN: {
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

							return backlightStatus.<Object>map(BacklightStatus::getName).orElse(null);
						}
						case WB_MODE: {
							Optional<WBMode> wbMode = Arrays.stream(WBMode.values())
									.filter(mode -> mode.getCode() == currentValue)
									.findFirst();

							return wbMode.<Object>map(WBMode::getName).orElse(null);
						}
						case SLOW_PAN_TILT: {
							Optional<SlowPanTiltStatus> slowPanTiltStatus = Arrays.stream(SlowPanTiltStatus.values())
									.filter(status -> status.getCode() == currentValue)
									.findFirst();

							return slowPanTiltStatus.<Object>map(SlowPanTiltStatus::getName).orElse(null);
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
}
