package org.usfirst.frc.team25.scouting.data;

import org.usfirst.frc.team25.scouting.data.models.Autonomous;
import org.usfirst.frc.team25.scouting.data.models.PostMatch;
import org.usfirst.frc.team25.scouting.data.models.ScoutEntry;
import org.usfirst.frc.team25.scouting.data.models.TeleOp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import static org.usfirst.frc.team25.scouting.data.Statistics.average;

/**
 * Object model containing individual reports of teams in events and methods to process data
 * Not used during the 2018 season
 *
 * @author sng
 */
public class TeamReport {

    private final transient ArrayList<ScoutEntry> entries;
    private final int teamNum;
    private int noShowCount;
    private final String[] autoIntMetricNames = new String[]{"cargoShipHatches", "rocketHatches", "cargoShipCargo",
            "rocketCargo"};
    private final String[] teleMetricNames = new String[]{"cargoShipHatches", "rocketLevelOneHatches",
            "rocketLevelTwoHatches",
            "rocketLevelThreeHatches", "cargoShipCargo", "rocketLevelOneCargo", "rocketLevelTwoCargo",
            "rocketLevelThreeCargo"};
    private final String[] overallMetricNames = new String[]{"calculatedPointContribution", "calculatedSandstormPoints",
            "calculatedTeleOpPoints"};
    private String teamName, frequentRobotCommentStr, allComments;
    private HashMap<String, Double> averages, standardDeviations;
    private HashMap<String, Integer> counts;



    public TeamReport(int teamNum) {
        this.teamNum = teamNum;
        entries = new ArrayList<>();
        teamName = "";
        frequentRobotCommentStr = "";
        noShowCount = 0;
        averages = new HashMap<>();
        standardDeviations = new HashMap<>();
        counts = new HashMap<>();

    }

    /**
     * Generates an easily-readable report with relevant stats on an team's capability
     *
     * @return A formatted string with relevant aggregate team stats
     */
    public String getQuickStatus() {

        String statusString = "Team " + getTeamNum();

        if (!getTeamName().isEmpty()) {
            statusString += " - " + getTeamName();
        }

        statusString += "\n\nSandstorm:";

        for (String metric : autoIntMetricNames) {
            statusString += "\nAvg. " + StringProcessing.convertCamelToSentenceCase(metric) + ": " + Statistics.round
                    (averages.get(metric), 2);
        }

        statusString += "\nHAB line cross: " + Statistics.round(counts.get("totalCross") / (double) entries.size() * 100, 2) + "% ("
                + counts.get("totalCross") + "/" + entries.size() + ")";

        statusString += "\nHAB line lvl 1: ";

        if (counts.get("levelOneStart") > 0) {
            statusString += Statistics.round(counts.get("levelOneCross") / (double) counts.get("levelOneStart") * 100
                    , 2) + "% ("
                    + counts.get("levelOneCross") + "/" + counts.get("levelOneStart") + ")";
        } else {
            statusString += "0%";
        }

        statusString += "\nHAB line lvl 2: ";

        if (counts.get("levelTwoStart") > 0) {
            statusString += Statistics.round(counts.get("levelTwoCross") / (double) counts.get("levelTwoStart") * 100
                    , 2) + "% ("
                    + counts.get("levelTwoCross") + "/" + counts.get("levelTwoStart") + ")";
        } else {
            statusString += "0%";
        }


        statusString += "\n\nTele-Op:";

        for (String metric : teleMetricNames) {
            statusString += "\nAvg. " + StringProcessing.convertCamelToSentenceCase(metric) + ": " + Statistics.round
                    (averages.get(metric), 2);
        }


        statusString += "\n\nEndgame:\n";

        if (counts.get("totalClimbAttempt") > 0) {
            statusString += "\ntotalClimbPercentages:" + Statistics.round(counts.get("totalClimbAttempt") / (double)
                            counts.get("totalClimbSuccess") * 100
                    , 2) + "% ("
                    + counts.get("totalClimbAttempt") + "/" + counts.get("totalClimbSuccess") + ")";
        } else {
            statusString += "\nTotal Climb Percent: 0%";
        }
        if (counts.get("levelOneClimbAttempt") > 0) {
            statusString += "\nLevelOneClimbPercent" + Statistics.round(counts.get("levelOneClimbAttempt") / (double)
                            counts.get("levelOneClimbSuccess") * 100
                    , 2) + "% ("
                    + counts.get("levelOneClimbAttempt") + "/" + counts.get("levelOneClimbSuccess") + ")";
        } else {
            statusString += "\nLevel One Climb Percent: 0%";
        }
        if (counts.get("levelTwoClimbAttempt") > 0) {
            statusString += "\nLevelTwoClimbPercent" + Statistics.round(counts.get("levelTwoClimbAttempt") / (double)
                            counts.get("levelTwoClimbSuccess") * 100
                    , 2) + "% ("
                    + counts.get("levelTwoClimbAttempt") + "/" + counts.get("levelTwoClimbSuccess") + ")";
        } else {
            statusString += "\nLevel Two Climb Percent: 0%";
        }
        if (counts.get("levelThreeClimbAttempt") > 0) {
            statusString += "\nlevelThreeClimbPercent" + Statistics.round(counts.get("levelThreeClimbAttempt") /
                            (double) counts.get("levelThreeClimbSuccess") * 100
                    , 2) + "% ("
                    + counts.get("levelThreeClimbAttempt") + "/" + counts.get("levelThreeClimbSuccess") + ")";
        } else {
            statusString += "\nLevel Three Climb Percent: 0%";
        }
        //System.out.println("Hello, World!");- HI Spencer-Karan
        ArrayList<Object> postList = SortersFilters.filterDataObject(entries, PostMatch.class);

        statusString += "\n\nOverall:\n";

        for (String metric : overallMetricNames) {
            statusString += "\nAvg. " + StringProcessing.convertCamelToSentenceCase(metric) + ": " + Statistics.round
                    (averages.get(metric), 2);
        }

        statusString += "\n\nCommon quick comments:\n" + frequentRobotCommentStr;


        return statusString;
    }

    public String getTeamName() {
        return teamName;
    }

    /**
     * Method to fetch the nickname of a team from a file
     *
     * @param dataLocation location of the TeamNameList file generated by <code>exportTeamList</code>
     */
    public void autoGetTeamName(File dataLocation) {
        String data = FileManager.getFileString(dataLocation);
        String[] values = data.split(",\n");

        for (String value : values) {

            if (value.split(",")[0].equals(Integer.toString(teamNum))) {

                teamName = value.split(",")[1];
                return;
            }
        }
    }

    public void findFrequentComments() {

        HashMap<String, Integer> commentFrequencies = new HashMap<>();
        if (entries.size() > 0) {
            for (String key : entries.get(0).getPostMatch().getRobotQuickCommentSelections().keySet()) {
                commentFrequencies.put(key, 0);
                for (ScoutEntry entry : entries) {
                    if (entry.getPostMatch().getRobotQuickCommentSelections().get(key)) {
                        commentFrequencies.put(key, 1 + commentFrequencies.get(key));
                    }
                }
            }
        }

        ArrayList<String> frequentRobotComment = new ArrayList<>();

        for (String key : commentFrequencies.keySet()) {

            // Feel free to change this ratio
            if (commentFrequencies.get(key) >= entries.size() / 4.0) {
                frequentRobotComment.add(key);
            }
        }

        for (String comment : frequentRobotComment) {

            frequentRobotCommentStr += StringProcessing.removeCommasBreaks(comment) + " \n";
        }

        allComments = "";
        for (ScoutEntry entry : entries) {
            if (!entry.getPostMatch().getRobotComment().equals("")) {
                allComments += entry.getPostMatch().getRobotComment() + "; ";
            }

        }


    }

    public void addEntry(ScoutEntry entry) {
        entry.getPostMatch().setRobotComment(StringProcessing.removeCommasBreaks(entry.getPostMatch().getRobotComment
                ()));

        entries.add(entry);
    }

    public ArrayList<ScoutEntry> getEntries() {
        return this.entries;
    }


    public int getTeamNum() {
        return teamNum;
    }

    public void calculateStats() {

        String[] numberStringNames = new String[]{"One", "Two", "Three", "total"};

        String[] iterativeMetricSuffixes = new String[]{"Start", "Cross", "ClimbAttempt", "ClimbSuccess"};

        for (String prefix : numberStringNames) {
            for (String suffix : iterativeMetricSuffixes) {
                counts.put("level" + prefix + suffix, 0);
            }
        }

        for (ScoutEntry entry : entries) {

            incrementCount("level" + numberStringNames[entry.getPreMatch().getStartingLevel() - 1] + "Start");
            if (entry.getAutonomous().isCrossHabLine()) {
                incrementCount("level" + numberStringNames[entry.getPreMatch().getStartingLevel() - 1] + "Cross");
                incrementCount("totalCross");
            }

            if (entry.getTeleOp().isAttemptHabClimb()) {
                incrementCount("level" + numberStringNames[entry.getTeleOp().getAttemptHabClimbLevel() - 1] +
                        "ClimbAttempt");

                incrementCount("totalClimbAttempt");
            }

            if (entry.getTeleOp().isSuccessHabClimb()) {
                incrementCount("level" + numberStringNames[entry.getTeleOp().getSuccessHabClimbLevel() - 1] +
                        "ClimbSuccess");
                if (entry.getTeleOp().getSuccessHabClimbLevel() != entry.getTeleOp().getAttemptHabClimbLevel()) {
                    incrementCount("level" + numberStringNames[entry.getTeleOp().getSuccessHabClimbLevel() - 1] +
                            "ClimbAttempt");
                }

                incrementCount("totalClimbSuccess");
            }

        }


        ArrayList<Object> autoList = SortersFilters.filterDataObject(entries, Autonomous.class);
        ArrayList<Object> teleList = SortersFilters.filterDataObject(entries, TeleOp.class);
        ArrayList<Object> overallList = new ArrayList();

        for (ScoutEntry entry : entries) {
            overallList.add(entry);
        }

        for (String metric : autoIntMetricNames) {
            averages.put(metric, average(autoList, metric));
        }
        for (String metric : teleMetricNames) {
            averages.put(metric, average(teleList, metric));
        }
        for (String metric : overallMetricNames) {
            averages.put(metric, average(overallList, metric));
        }


    }

    private void incrementCount(String metricName) {
        if (counts.containsKey(metricName)) {
            counts.put(metricName, counts.get(metricName) + 1);
        } else {
            counts.put(metricName, 1);
        }
    }

    public void filterNoShow() {
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getPreMatch().isRobotNoShow()) {
                entries.remove(i);
                i--;
                noShowCount++;
            }
        }
    }

    public HashMap<String, Double> getAverages() {
        return averages;
    }

    public HashMap<String, Double> getStandardDeviations() {
        return standardDeviations;
    }

    public HashMap<String, Integer> getCounts() {
        return counts;
    }
}
