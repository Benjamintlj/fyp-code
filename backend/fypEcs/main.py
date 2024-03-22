from fastapi import FastAPI
from routes.leaderboard import leaderboard
from routes.spaced_repetition import spaced_repetition
from routes.stats import stats

app = FastAPI()

leaderboard(app)
spaced_repetition(app)
stats(app)
