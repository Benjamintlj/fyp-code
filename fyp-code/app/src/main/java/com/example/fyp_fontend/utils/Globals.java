package com.example.fyp_fontend.utils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;

public class Globals {
    public static String userPoolId = "eu-west-1_38UisDTAd";
    public static String clientId = "mt4bm304sfi7h4jsjplmnhsj4";
    public static Regions region = Regions.EU_WEST_1;
    public static String bucketName = "cdkstack-fyplessonbucketb0b7a3cd-veuwgmiuzztu";
    private static String accessKey = "AKIAS6TMA3LVAIEFAZEH";
    private static String secretAccessKey = "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";
    public static String ecsUrl = "http://10.0.2.2:80/";
    private static AWSCredentials awsCredentials;

    public static AWSCredentials getAwsCredentials() {
        if (awsCredentials == null) {
            awsCredentials = new BasicAWSCredentials(accessKey, secretAccessKey);
        }
        return awsCredentials;
    }
}
