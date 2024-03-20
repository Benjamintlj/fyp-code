from fastapi import FastAPI
from routes.leaderboard import leaderboard
from routes.spaced_repetition import spaced_repetition

app = FastAPI()

leaderboard(app)
spaced_repetition(app)
