all: compile test run

compile:
	touch CMakeLists.txt
	cmake --build ./build

run:
	./build/mirror_dispatch

test:
	./build/mirror_dispatch --test