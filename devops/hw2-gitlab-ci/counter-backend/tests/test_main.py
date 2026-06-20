from typing import Awaitable

import pytest
from aiohttp.test_utils import TestClient


@pytest.mark.asyncio
async def test_increment(client: Awaitable[TestClient]):
    c = await client

    r = await c.get("/api/counter")
    assert r.status == 200
    assert (await r.json())["count"] == 0

    r = await c.post("/api/counter")
    assert r.status == 201
    assert (await r.json())["count"] == 1

    r = await c.get("/api/counter")
    assert r.status == 200
    assert (await r.json())["count"] == 1
