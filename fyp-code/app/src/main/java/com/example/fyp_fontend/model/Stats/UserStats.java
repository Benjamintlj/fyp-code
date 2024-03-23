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

        Log.d(TAG, "stringToBadge: " + text);

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

    public static class TotalGems {
        Badge gemsRank;
        int numOfGems;
        boolean rankChanged;
        int gold, silver, bronze;

        TotalGems(JSONObject stats, JSONObject boundary) throws JSONException {
            this.gemsRank = stringToBadge(stats.getString("gems_rank"));
            this.numOfGems = stats.getInt("num_of_gems");
            this.rankChanged = stats.getBoolean("rank_changed");
            this.gold = boundary.getInt("GOLD");
            this.silver = boundary.getInt("SILVER");
            this.bronze = boundary.getInt("BRONZE");
        }

        public Badge getGemsRank() {
            return gemsRank;
        }

        public int getNumOfGems() {
            return numOfGems;
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

    public static class Flawless {
        Badge flawlessRank;
        int numOfFlawless;
        boolean rankChanged;
        int gold, silver, bronze;

        Flawless(JSONObject stats, JSONObject boundary) throws JSONException {
            this.flawlessRank = stringToBadge(stats.getString("flawless_rank"));
            this.numOfFlawless = stats.getInt("num_of_flawless");
            this.rankChanged = stats.getBoolean("rank_changed");
            this.gold = boundary.getInt("GOLD");
            this.silver = boundary.getInt("SILVER");
            this.bronze = boundary.getInt("BRONZE");
        }

        public Badge getFlawlessRank() {
            return flawlessRank;
        }

        public int getNumOfFlawless() {
            return numOfFlawless;
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

    public static class SpeedRun {
        int numOfSpeedRuns;
        boolean rankChanged;
        Badge speedRunRank;
        int gold, silver, bronze;

        SpeedRun(JSONObject stats, JSONObject boundary) throws JSONException {
            this.numOfSpeedRuns = stats.getInt("num_of_speed_runs");
            this.rankChanged = stats.getBoolean("rank_changed");
            this.speedRunRank = stringToBadge(stats.getString("speed_run_rank"));
            this.gold = boundary.getInt("GOLD");
            this.silver = boundary.getInt("SILVER");
            this.bronze = boundary.getInt("BRONZE");
        }

        public int getNumOfSpeedRuns() {
            return numOfSpeedRuns;
        }

        public boolean isRankChanged() {
            return rankChanged;
        }

        public Badge getSpeedRunRank() {
            return speedRunRank;
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

    public static class StreakStats {
        long lastOnline;
        boolean rankChanged;
        int streak;
        Badge streakRank;
        int gold, silver, bronze;

        StreakStats(JSONObject stats, JSONObject boundary) throws JSONException {
            this.lastOnline = stats.getLong("last_online");
            this.rankChanged = stats.getBoolean("rank_changed");
            this.streak = stats.getInt("streak");
            this.streakRank = stringToBadge(stats.getString("streak_rank"));
            this.gold = boundary.getInt("GOLD");
            this.silver = boundary.getInt("SILVER");
            this.bronze = boundary.getInt("BRONZE");
        }

        public long getLastOnline() {
            return lastOnline;
        }

        public boolean isRankChanged() {
            return rankChanged;
        }

        public int getStreak() {
            return streak;
        }

        public Badge getStreakRank() {
            return streakRank;
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

    public static class RevisedLessons {
        int numOfRevised;
        boolean rankChanged;
        Badge revisedRank;
        int gold, silver, bronze;

        RevisedLessons(JSONObject stats, JSONObject boundary) throws JSONException {
            this.numOfRevised = stats.getInt("num_of_revised");
            this.rankChanged = stats.getBoolean("rank_changed");
            this.revisedRank = stringToBadge(stats.getString("revised_rank"));
            this.gold = boundary.getInt("GOLD");
            this.silver = boundary.getInt("SILVER");
            this.bronze = boundary.getInt("BRONZE");
        }

        public int getNumOfRevised() {
            return numOfRevised;
        }

        public boolean isRankChanged() {
            return rankChanged;
        }

        public Badge getRevisedRank() {
            return revisedRank;
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

    public static class FirstPlace {
        int numOfFirstPlace;
        Badge firstPlaceRank;
        boolean rankChanged;
        int gold, silver, bronze;

        FirstPlace(JSONObject stats, JSONObject boundary) throws JSONException {
            this.numOfFirstPlace = stats.getInt("num_of_first_place");
            this.firstPlaceRank = stringToBadge(stats.getString("first_place_rank"));
            this.rankChanged = stats.getBoolean("rank_changed");
            this.gold = boundary.getInt("GOLD");
            this.silver = boundary.getInt("SILVER");
            this.bronze = boundary.getInt("BRONZE");
        }

        public int getNumOfFirstPlace() {
            return numOfFirstPlace;
        }

        public Badge getFirstPlaceRank() {
            return firstPlaceRank;
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

        this.totalGems= new TotalGems(stats.getJSONObject("total_gems"), boundaries.getJSONObject("total_gems"));
        this.flawless = new Flawless(stats.getJSONObject("flawless"), boundaries.getJSONObject("flawless"));
        this.speedRun = new SpeedRun(stats.getJSONObject("speed_run"), boundaries.getJSONObject("speed_run"));
        this.streakStats = new StreakStats(stats.getJSONObject("streak_stats"), boundaries.getJSONObject("streak_stats"));
        this.revisedLessons = new RevisedLessons(stats.getJSONObject("revised_lessons"), boundaries.getJSONObject("revised_lessons"));
        this.username = stats.getString("username");
        this.firstPlace = new FirstPlace(stats.getJSONObject("first_place"), boundaries.getJSONObject("first_place"));
        this.lessonsCompleted = new LessonsCompleted(stats.getJSONObject("lessons_completed"), boundaries.getJSONObject("lessons_completed"));
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
