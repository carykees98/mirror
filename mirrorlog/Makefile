all: build_jar build_container run

build_jar:
	mvn -f pom.xml clean install package

build_container:
	docker compose build

run:
	docker compose up --detach

clean:
	docker image prune

attach:
	docker container exec -it mirrorlog /bin/bash

show-logs:
	docker logs mirrorlog

initial-setup:
	mkdir logs