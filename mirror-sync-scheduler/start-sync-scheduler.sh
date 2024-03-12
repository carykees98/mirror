if [ $# -eq 1 ]
then
cmake -S . -B build
cmake --build build/ -j$1
./build/syncScheduler
else
echo "enter the number of cores to use for build as a command line argument"
fi