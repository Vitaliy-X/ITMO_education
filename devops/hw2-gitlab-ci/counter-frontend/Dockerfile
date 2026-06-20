ARG VERSION="18.18.1"
ARG ALPINE_VERSION="3.18"


FROM node:${VERSION}-alpine${ALPINE_VERSION} AS base

SHELL [ "/bin/sh", "-eu", "-c" ]

RUN \
  apk add --no-cache ca-certificates \
  ; rm -rf /var/cache/apk/* /tmp/*

WORKDIR /app

ENV NODE_ENV="production"
ENV NEXT_TELEMETRY_DISABLED="1"
ENV PORT="3000"
ENV HOSTNAME="0.0.0.0"

COPY --chown=node:node dist/ ./

USER node
EXPOSE ${PORT}/tcp
CMD [ "node", "/app/server.js" ]

