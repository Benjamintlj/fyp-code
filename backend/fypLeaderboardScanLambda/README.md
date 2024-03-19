# Leaderboard Scanner

```bash
zip -r out/fypLeaderboardScan.zip src/* -x __pycache__/
aws s3 cp out/fypLeaderboardScan.zip s3://cdkstack-fypcodebucketb93229d9-hem66ifusozc/fypLeaderboardScan.zip
aws lambda update-function-code --function-name CdkStack-fypLeaderboardScanE686BD04-pNVJupedpDGm --s3-bucket cdkstack-fypcodebucketb93229d9-hem66ifusozc --s3-key fypLeaderboardScan.zip
q
```