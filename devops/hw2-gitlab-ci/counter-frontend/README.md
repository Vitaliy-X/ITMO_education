# Счетчик - фронтэнд

Фронтэнд счетчика.

## Установка

Выполните следующие команды

```sh
npx next build
cp --recursive .next/standalone dist
cp --recursive public dist/public
cp --recursive .next/static dist/.next/static
```

или запустите `make install`.

Собранный сервер можно запустить командой `node dist/server.js`.

## Развертывание

* Запакуйте собранный сервер командой `make package` в архив `server.tar.gz`;
* Скопируйте на сервер;
* Установите командой `sudo sh -c 'rm --recursive --force /opt/counter && tar --extract --verbose --directory / server.tar.gz'`;
* Скопируйте Systemd службу из `deploy/counter-frontend.service` в `/etc/systemd/system`;
* Включите службу `systemctl enable --now counter-frontend.service`;
