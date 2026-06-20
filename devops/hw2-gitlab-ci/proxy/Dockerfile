FROM nginx:1.20

ARG SERVER_NAME=10.101.1.25

COPY nginx.conf /etc/nginx/nginx.conf

RUN mkdir -p /etc/nginx/ssl

RUN openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
    -keyout /etc/nginx/ssl/key.pem \
    -out /etc/nginx/ssl/cert.pem \
    -subj "/C=RU/ST=SPB/L=SPB/O=ITMO/CN=${SERVER_NAME}"

CMD ["nginx", "-g", "daemon off;"]
