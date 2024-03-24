package com.example.fyp_fontend.utils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;

public class Globals {
    public static String userPoolId = "eu-west-1_FEAv5LzEp";
    public static String clientId = "728nlfinl5b3n4hcogm3vmfn6b";
    public static Regions region = Regions.EU_WEST_1;
    public static String bucketName = "production-fypstoragestac-newfyplessonbucket35aab5-su3oz664qjai";
    private static String accessKey = "AKIAS6TMA3LVAIEFAZEH";
    private static String secretAccessKey = "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";
    public static String ecsUrl = "http://192.168.1.136:80/";
    private static AWSCredentials awsCredentials;

    public static AWSCredentials getAwsCredentials() {
        if (awsCredentials == null) {
            awsCredentials = new BasicAWSCredentials(accessKey, secretAccessKey);
        }
        return awsCredentials;
    }
}
