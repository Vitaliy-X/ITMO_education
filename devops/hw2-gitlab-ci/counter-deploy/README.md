## Counter (Frontend + Backend + Deploy + СI-Templates)

Сборка и публикация образов выполняются через GitLab CI, деплой — через `docker-compose` на выделенную машину.

---

### Пайплайны выполняют:
- сборку и проверку кода (lint, tests)
- сборку Docker-образов и публикацию в Harbor
- деплой на сервер по `docker-compose up --detach`

---

### Переменные CI/CD

| Переменная          | Назначение |
|---------------------|------------|
| `_DEPLOY_TOKEN`     | Токен для доступа к Docker Registry / деплою |
| `PYTHON_IMAGE`      | Образ Python, используемый для сборки backend |
| `NODEJS_IMAGE`      | Образ Node.js, используемый для сборки frontend |
| `KANIKO_IMAGE`      | Образ Kaniko для сборки и push Docker-образов без Docker daemon |
| `HARBOR_HOST`       | Адрес Docker Registry (Harbor) |
| `HARBOR_PROJECT`    | Имя проекта/namespace в Registry |
| `HARBOR_USER`       | Логин для Harbor |
| `HARBOR_PASSWORD`   | Пароль для Harbor |

Все переменные **защищены (Protected)** и **скрыты (Masked)** — т.е. они не попадут в логи и используются только в защищённых ветках (например, `main`).

---
