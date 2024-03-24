# ECS

```bash
export LEADER_BOARD_TABLE="Production-fypStorageStack-fypLeaderboardTableC629E552-1MRBNEWBT8FRM"
export SPACED_REPETITION_TABLE="Production-fypStorageStack-fypSpacedRepetitionTableC0439FF4-H9TDP2WGKE51"
export USER_STATS_TABLE="Production-fypStorageStack-fypUserStatsTable19CB9CD5-1C6WU1JU5Q0K6"
export USER_POOL_ID="eu-west-1_38UisDTAd"
uvicorn main:app --host=0.0.0.0 --port=80
```

```bash
docker build -t fyp-ecs-image .
aws ecr get-login-password --region eu-west-1 | docker login --username AWS --password-stdin 203163753194.dkr.ecr.eu-west-1.amazonaws.com
docker tag fyp-ecs-image:latest 203163753194.dkr.ecr.eu-west-1.amazonaws.com/fyp-repository:latest
docker push 203163753194.dkr.ecr.eu-west-1.amazonaws.com/fyp-repository:latest
```

```bash
docker build -t fyp-ecs-image .
docker run -p 80:80 -e LEADER_BOARD_TABLE="CdkStack-fypLeaderboardTableC629E552-VCAUF62P2PGK" -e SPACED_REPETITION_TABLE="CdkStack-fypSpacedRepetitionTableC0439FF4-12818ACAYO25I" -e USER_STATS_TABLE="CdkStack-fypUserStatsTable19CB9CD5-FF4DGTX2OLHB" -e USER_POOL_ID="eu-west-1_38UisDTAd" fyp-ecs-image
```
