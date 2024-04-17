package com.example.fyp_fontend.model.Stats;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class UserStats {
    private static final String TAG = "UserStats";

    public static enum Badge {
        BRONZE,
        SILVER,
        GOLD,
        UNKNOWN
    }

    private static final String BRONZE = "BRONZE";
    private static final String SILVER = "SILVER";
    private static final String GOLD = "GOLD";

    private static Badge stringToBadge(String text) {

        Badge badge;

        switch (text) {
            case "gold":
                badge = Badge.GOLD;
                break;
            case "silver":
                badge = Badge.SILVER;
                break;
            case "bronze":
                badge = Badge.BRONZE;
                break;
            default:
                badge = Badge.UNKNOWN;
                break;
        }

        return badge;
    }

    private static abstract class Stats {
        protected Badge rank;
        protected boolean rankChanged;
        protected int gold, silver, bronze;

        public Stats(JSONObject stats, JSONObject boundary) throws JSONException {
            this.rank = stringToBadge(stats.getString("rank"));
            this.rankChanged = stats.getBoolean("rank_changed");
            this.gold = boundary.getInt("GOLD");
            this.silver = boundary.getInt("SILVER");
            this.bronze = boundary.getInt("BRONZE");
        }

        public Stats(JSONObject boundary) throws JSONException {
            this.rank = Badge.UNKNOWN;
            this.rankChanged = false;
            this.gold = boundary.getInt("GOLD");
            this.silver = boundary.getInt("SILVER");
            this.bronze = boundary.getInt("BRONZE");
        }

        public Badge getRank() {
            return rank;
        }

        public boolean isRankChanged() {
            return rankChanged;
        }

        public int getGold() {
            return gold;
        }

        public int getSilver() {
            return silver;
        }

        public int getBronze() {
            return bronze;
        }
    }

    public static class TotalGems extends Stats {
        int numOfGems;

        TotalGems(JSONObject stats, JSONObject boundary) throws JSONException {
            super(stats, boundary);
            this.numOfGems = stats.getInt("num_of_gems");
        }

        public TotalGems(JSONObject boundary) throws JSONException {
            super(boundary);
            this.numOfGems = 0;
        }

        public int getNumOfGems() {
            return numOfGems;
        }
    }

    public static class Flawless extends Stats {
        private int numOfFlawless;

        Flawless(JSONObject stats, JSONObject boundary) throws JSONException {
            super(stats, boundary);
            this.numOfFlawless = stats.getInt("num_of_flawless");
        }

        Flawless(JSONObject boundary) throws JSONException {
            super(boundary);
            this.numOfFlawless = 0;
        }

        public int getNumOfFlawless() {
            return numOfFlawless;
        }
    }

    public static class SpeedRun extends Stats {
        private int numOfSpeedRuns;

        SpeedRun(JSONObject stats, JSONObject boundary) throws JSONException {
            super(stats, boundary);
            this.numOfSpeedRuns = stats.getInt("num_of_speed_runs");
        }

        SpeedRun(JSONObject boundary) throws JSONException {
            super(boundary);
            this.numOfSpeedRuns = 0;
        }

        public int getNumOfSpeedRuns() {
            return numOfSpeedRuns;
        }
    }


    public static class StreakStats extends Stats {
        private long lastOnline;
        private int streak;

        StreakStats(JSONObject stats, JSONObject boundary) throws JSONException {
            super(stats, boundary);
            this.lastOnline = stats.getLong("last_online");
            this.streak = stats.getInt("streak");
        }

        StreakStats(JSONObject boundary) throws JSONException {
            super(boundary);
            this.lastOnline = 0;
            this.streak = 0;
        }

        public long getLastOnline() {
            return lastOnline;
        }

        public int getStreak() {
            return streak;
        }
    }

    public static class RevisedLessons extends Stats {
        private int numOfRevised;

        RevisedLessons(JSONObject stats, JSONObject boundary) throws JSONException {
            super(stats, boundary);
            this.numOfRevised = stats.getInt("num_of_revised");
        }

        RevisedLessons(JSONObject boundary) throws JSONException {
            super(boundary);
            this.numOfRevised = 0;
        }

        public int getNumOfRevised() {
            return numOfRevised;
        }
    }


    public static class FirstPlace extends Stats {
        private int numOfFirstPlace;

        FirstPlace(JSONObject stats, JSONObject boundary) throws JSONException {
            super(stats, boundary);
            this.numOfFirstPlace = stats.getInt("num_of_first_place");
        }

        FirstPlace(JSONObject boundary) throws JSONException {
            super(boundary);
            this.numOfFirstPlace = 0;
        }

        public int getNumOfFirstPlace() {
            return numOfFirstPlace;
        }
    }

    public static class LessonsCompleted {
        Badge lessonsCompletedRank;
        int numOfLessonsCompleted;
        boolean rankChanged;
        int gold, silver, bronze;

        LessonsCompleted(JSONObject stats, JSONObject boundary) throws JSONException {
            this.lessonsCompletedRank = stringToBadge(stats.getString("lessons_completed_rank"));
            this.numOfLessonsCompleted = stats.getInt("num_of_lessons_completed");
            this.rankChanged = stats.getBoolean("rank_changed");
            this.gold = boundary.getInt("GOLD");
            this.silver = boundary.getInt("SILVER");
            this.bronze = boundary.getInt("BRONZE");
        }

        LessonsCompleted(JSONObject boundary) throws JSONException {
            this.lessonsCompletedRank = Badge.UNKNOWN;
            this.numOfLessonsCompleted = 0;
            this.rankChanged = false;
            this.gold = boundary.getInt("GOLD");
            this.silver = boundary.getInt("SILVER");
            this.bronze = boundary.getInt("BRONZE");
        }

        public Badge getLessonsCompletedRank() {
            return lessonsCompletedRank;
        }

        public int getNumOfLessonsCompleted() {
            return numOfLessonsCompleted;
        }

        public boolean isRankChanged() {
            return rankChanged;
        }

        public int getGold() {
            return gold;
        }

        public int getSilver() {
            return silver;
        }

        public int getBronze() {
            return bronze;
        }
    }

    TotalGems totalGems;
    Flawless flawless;
    SpeedRun speedRun;
    StreakStats streakStats;
    RevisedLessons revisedLessons;
    String username;
    FirstPlace firstPlace;
    LessonsCompleted lessonsCompleted;

    public UserStats(JSONObject json) throws JSONException {
        JSONObject stats = json.getJSONObject("stats");
        JSONObject boundaries = json.getJSONObject("boundaries");

        try {
            this.totalGems = new TotalGems(stats.getJSONObject("total_gems"), boundaries.getJSONObject("total_gems"));
        } catch (JSONException e) {
            this.totalGems = new TotalGems(boundaries.getJSONObject("total_gems"));
        }

        try {
            this.flawless = new Flawless(stats.getJSONObject("flawless"), boundaries.getJSONObject("flawless"));
        } catch (JSONException e) {
            this.flawless = new Flawless(boundaries.getJSONObject("flawless"));
        }

        try {
            this.speedRun = new SpeedRun(stats.getJSONObject("speed_run"), boundaries.getJSONObject("speed_run"));
        } catch (JSONException e) {
            this.speedRun = new SpeedRun(boundaries.getJSONObject("speed_run"));
        }

        try {
            this.streakStats = new StreakStats(stats.getJSONObject("streak_stats"), boundaries.getJSONObject("streak_stats"));
        } catch (JSONException e) {
            this.streakStats = new StreakStats(boundaries.getJSONObject("streak_stats"));
        }

        try {
            this.revisedLessons = new RevisedLessons(stats.getJSONObject("revised_lessons"), boundaries.getJSONObject("revised_lessons"));
        } catch (JSONException e) {
            this.revisedLessons = new RevisedLessons(boundaries.getJSONObject("revised_lessons"));
        }

        try {
            this.firstPlace = new FirstPlace(stats.getJSONObject("first_place"), boundaries.getJSONObject("first_place"));
        } catch (JSONException e) {
            this.firstPlace = new FirstPlace(boundaries.getJSONObject("first_place"));
        }

        try {
            this.lessonsCompleted = new LessonsCompleted(stats.getJSONObject("lessons_completed"), boundaries.getJSONObject("lessons_completed"));
        } catch (JSONException e) {
            this.lessonsCompleted = new LessonsCompleted(boundaries.getJSONObject("lessons_completed"));
        }
    }

    public TotalGems getTotalGems() {
        return totalGems;
    }

    public Flawless getFlawless() {
        return flawless;
    }

    public SpeedRun getSpeedRun() {
        return speedRun;
    }

    public StreakStats getStreakStats() {
        return streakStats;
    }

    public RevisedLessons getRevisedLessons() {
        return revisedLessons;
    }

    public String getUsername() {
        return username;
    }

    public FirstPlace getFirstPlace() {
        return firstPlace;
    }

    public LessonsCompleted getLessonsCompleted() {
        return lessonsCompleted;
    }
}
