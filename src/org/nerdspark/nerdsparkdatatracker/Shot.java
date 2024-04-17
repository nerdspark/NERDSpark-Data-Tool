package org.nerdspark.nerdsparkdatatracker;

import java.io.Serializable;

// This class is used to represent a shot in a match.  
// Each shot will have a time, x coordinate, y coordinate and outcome.

public class Shot implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer shotNumber;
	private String shotDate;
	private Integer fieldXCoord;
	private Integer fieldYCoord;
	private Integer speakerXCoord;
	private Integer speakerYCoord;
	private String outcome;
	private String mode;

	public Shot(Integer shotNumber, String shotDate, Integer fieldXCoord, Integer fieldYCoord, Integer speakerXCoord, Integer speakerYCoord, String outcome, String mode) {
		super();
		this.shotNumber = shotNumber;
		this.shotDate = shotDate;
		this.fieldXCoord = fieldXCoord;
		this.fieldYCoord = fieldYCoord;
		this.speakerXCoord = speakerXCoord;
		this.speakerYCoord = speakerYCoord;
		this.outcome = outcome;
		this.mode = mode;
	}

	public Integer getShotNumber() {
		return shotNumber;
	}

	public void setShotNumber(Integer shotNumber) {
		this.shotNumber = shotNumber;
	}

	public String getShotDate() {
		return shotDate;
	}

	public void setShotDate(String date) {
		this.shotDate = date;
	}

	public Integer getFieldXCoord() {
		return fieldXCoord;
	}

	public void setFieldXCoord(Integer fieldXCoord) {
		this.fieldXCoord = fieldXCoord;
	}

	public Integer getFieldYCoord() {
		return fieldYCoord;
	}

	public void setFieldYCoord(Integer fieldYCoord) {
		this.fieldYCoord = fieldYCoord;
	}

	public Integer getSpeakerXCoord() {
		return speakerXCoord;
	}

	public void setSpeakerXCoord(Integer speakerXCoord) {
		this.speakerXCoord = speakerXCoord;
	}

	public Integer getSpeakerYCoord() {
		return speakerYCoord;
	}

	public void setSpeakerYCoord(Integer speakerYCoord) {
		this.speakerYCoord = speakerYCoord;
	}

	public String getOutcome() {
		return outcome;
	}

	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	@Override
	public String toString() {
		return "{" +  mode + ", " + shotNumber + ", " + outcome + ", "+ shotDate  + ", (" + fieldXCoord + ", " + fieldYCoord + "), (" + speakerXCoord + ", " + speakerYCoord + "}";
	}

}
