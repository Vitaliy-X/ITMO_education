build:
	npx next build

install: build
	cp --recursive .next/standalone dist
	cp --recursive public dist/public
	cp --recursive .next/static dist/.next/static

package: install
	tar --create --verbose --directory dist --transform 's:.:/opt/counter:' --file server.tar.gz .

clean:
	rm --recursive --force dist
