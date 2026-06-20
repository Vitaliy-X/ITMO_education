import logging
import os
import sys
from dataclasses import dataclass

from aiohttp import web
from aiohttp.web import Application, Request, Response, get, json_response, post


@dataclass
class State:
    count: int = 0


def create_app() -> Application:
    app = Application()
    app["state"] = State()
    app.add_routes(
        [
            get("/api/counter", get_counter),
            post("/api/counter", increment_counter),
        ]
    )
    return app


async def get_counter(request: Request) -> Response:
    state: State = request.app["state"]
    return json_response(status=200, data={"count": state.count})


async def increment_counter(request: Request) -> Response:
    state: State = request.app["state"]
    state.count += 1
    return json_response(status=201, data={"count": state.count})


def main():
    logging.basicConfig(
        format="[%(levelname)s] %(message)s",
        level="INFO",
        stream=sys.stderr,
    )
    host = str(os.getenv("HOST", "0.0.0.0"))
    port = int(os.getenv("PORT", "8080"))
    web.run_app(create_app(), host=host, port=port)
