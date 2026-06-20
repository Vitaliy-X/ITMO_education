FROM python:3.11-slim-bookworm

WORKDIR /app

COPY server.tar.gz /app/
RUN tar -xzf server.tar.gz && ./install.sh

EXPOSE 8080
CMD ["/opt/counter-backend/bin/python", "-m", "counter_backend"]
