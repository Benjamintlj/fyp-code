from fastapi import FastAPI, Request
from starlette.responses import JSONResponse

from routes.leaderboard import leaderboard
from routes.spaced_repetition import spaced_repetition
from routes.stats import stats
from routes.health import health
import logging
import traceback


app = FastAPI()

leaderboard(app)
spaced_repetition(app)
stats(app)
health(app)


@app.exception_handler(Exception)
async def universal_exception_handler(request: Request, exc: Exception):
    # Log the exception
    logging.error(f"Unhandled exception: {exc}", exc_info=True)

    # Optionally, log the full stack trace
    logging.debug(traceback.format_exc())

    # Return a generic error response
    return JSONResponse(
        status_code=500,
        content={"message": "An internal error occurred."},
    )