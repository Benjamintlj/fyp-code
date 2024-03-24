# Leaderboard Lambda

```bash
zip -r out/fypUpdateLeaderboards.zip src/* -x __pycache__/
aws s3 cp out/fypUpdateLeaderboards.zip s3://cdkstack-fypcodebucketb93229d9-hem66ifusozc/fypUpdateLeaderboards.zip
aws lambda update-function-code --function-name FypStack-fypUpdateLeaderboards43E6BBC2-eJaRjLstcW1c --s3-bucket cdkstack-fypcodebucketb93229d9-hem66ifusozc --s3-key fypUpdateLeaderboards.zip
q
```