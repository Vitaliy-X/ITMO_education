### Роль `nginx_docker`

Ansible-роль для автоматического развертывания **Nginx в Docker-контейнере**.

Поддерживает HTTP и HTTPS, а также статический контент и самоподписанные SSL-сертификаты.

---

### Требования:

Перед запуском установите необходимые коллекции:

```bash
ansible-galaxy collection install community.docker community.crypto
```

---

### Запуск playbook:

```bash
ansible-playbook -i <inventory> <playbook>
```

**Например:**
```bash
ansible-playbook -i inventories/hosts.yaml nginx_play.yaml -K
```

---

### Основные переменные
```
nginx_container_http_port:    80          # Внешний порт для HTTP
nginx_container_https_port:   443         # Внешний порт для HTTPS
nginx_enable_https:           true        # Включить HTTPS (443 порт, ssl-конфиг)
nginx_use_static:             false       # Подключить статические файлы (volume для /html)
nginx_host_base:              /opt/nginx  # Базовый путь на хосте для данных Nginx
nginx_server_name:            "localhost" # Имя сервера в конфиге nginx
nginx_index:                  index.html  # Имя главной страницы сайта
nginx_static_src:             "static"    # Путь к статическим файлам для копирования
```