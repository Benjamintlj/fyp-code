# New user lambda 

```bash
zip -r out/newUser.zip src/* -x __pycache__/
aws s3 cp out/newUser.zip s3://cdkstack-fypcodebucketb93229d9-hem66ifusozc/newUser.zip
aws lambda update-function-code --function-name CdkStack-fypNewUser2BCA3E2A-xeDRfKiPKP34 --s3-bucket cdkstack-fypcodebucketb93229d9-hem66ifusozc --s3-key newUser.zip
q
```