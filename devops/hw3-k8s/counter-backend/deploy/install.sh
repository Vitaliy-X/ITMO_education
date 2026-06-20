#!/bin/sh
#shellcheck shell=sh
set -eu

HERE=$(dirname -- "$(realpath -- "$0")")

INSTALL_DIR="/opt/counter-backend"

if ! command -v python 1>/dev/null 2>&1; then
    echo "python not found in path"
    exit 1
fi

packages=$(find "${HERE}" "${HERE}/vendor" -maxdepth 1 -iname '*.whl' -exec realpath {} \; -print0 | xargs --null)
python -m venv "${INSTALL_DIR}"
(
  set -eu
  . "${INSTALL_DIR}/bin/activate"
  python -m pip install --isolated --no-index --no-color --no-cache-dir --disable-pip-version-check --no-python-version-warning --no-warn-script-location --no-deps --progress-bar off ${packages}
)
install -D --mode=0755 --target-directory "/usr/local/bin" "${HERE}/counter-backend"
install -D --mode=0644 --target-directory "/etc/systemd/system" "${HERE}/counter-backend.service"
