# Счетчик - бэкэнд

Бэкэнд счетчика.

## Установка

Выполните следующие команды

```sh
mkdir -p dist
poetry export --without-hashes --format constraints.txt --output dist/constraints.txt
poetry run python -m pip wheel --isolated --requirement dist/constraints.txt --wheel-dir dist/vendor
poetry build --format wheel
```

Или запустите `make build`.

## Развертывание

* Запакуйте собранный сервер командой `make package` в архив `server.tar.gz`;
* Скопируйте на сервер;
* Распакуйте и уставновите скриптом `install.sh`;
