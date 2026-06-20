vendor:
	mkdir -p dist
	poetry export --without-hashes --format constraints.txt --output dist/constraints.txt
	poetry run python -m pip wheel --isolated --requirement dist/constraints.txt --wheel-dir dist/vendor

build: vendor
	poetry build --format wheel

package: build
	cp deploy/install.sh dist/
	cp deploy/counter-backend dist/
	cp deploy/counter-backend.service dist/
	tar --create --verbose --directory dist --file server.tar.gz .
