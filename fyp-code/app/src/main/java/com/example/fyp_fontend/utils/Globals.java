package com.example.fyp_fontend.utils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;

public class Globals {
    public static String userPoolId = "eu-west-1_DEqgmxhzb";
    public static String clientId = "4iu7nof85r2hlk2nabt2o0doa9";
    public static Regions region = Regions.EU_WEST_1;
    public static String bucketName = "cdkstack-fyplessonbucketb0b7a3cd-veuwgmiuzztu";
    private static String accessKey = "AKIAS6TMA3LVAIEFAZEH";
    private static String secretAccessKey = "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";
    private static AWSCredentials awsCredentials;

    public static AWSCredentials getAwsCredentials() {
        if (awsCredentials == null) {
            awsCredentials = new BasicAWSCredentials(accessKey, secretAccessKey);
        }
        return awsCredentials;
    }
}
