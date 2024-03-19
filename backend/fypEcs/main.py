from fastapi import FastAPI
from routes.leaderboard import leaderboard

app = FastAPI()

leaderboard(app)
