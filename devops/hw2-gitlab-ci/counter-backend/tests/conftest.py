import pytest

from counter_backend import create_app


@pytest.fixture
@pytest.mark.asyncio
async def client(aiohttp_client):
    return await aiohttp_client(create_app())
