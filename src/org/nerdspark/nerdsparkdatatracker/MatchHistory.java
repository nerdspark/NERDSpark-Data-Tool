package org.nerdspark.nerdsparkdatatracker;

import java.io.Serializable;
import java.util.ArrayList;

// This class is used to represent a Match History record.
// Each record has a name, date, and list of shots from the match.

public class MatchHistory implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String name;
	private String date;
	private String team;
	private ArrayList<Shot> shots = new ArrayList<Shot>();
	private Integer ampShots;

    public MatchHistory() {
    }
 
	public MatchHistory(String name, String date, String team, ArrayList<Shot> shots, Integer ampShots) {
		super();
		this.name = name;
		this.date = date;
		this.team = team;
		this.shots = shots;
		this.ampShots = ampShots;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public ArrayList<Shot> getShots() {
		return shots;
	}

	public void setShots(ArrayList<Shot> shots) {
		this.shots = shots;
	}

	public Integer getAmpShots() {
		return ampShots;
	}

	public void setAmpShots(Integer ampShots) {
		this.ampShots = ampShots;
	}

	@Override
	public String toString() {
		return "MatchHistory [name=" + name + ", date=" + date + ", team=" + date + ", shots=" + shots+ ", ampShots=" + ampShots + "]";
	}
	
}
