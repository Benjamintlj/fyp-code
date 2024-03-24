from starlette.responses import JSONResponse


def health(app):
    @app.get('/health')
    def health_check():
        return JSONResponse(status_code=200, content={'message': 'Healthy'})

    @app.get('/')
    def no_path_health_check():
        return JSONResponse(status_code=200, content={'message': 'Healthy'})
