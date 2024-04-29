#!/bin/python3

"""
    check_mirror_env.py
    https://github.com/COSI-Lab/
"""

import subprocess
import re
import os

def runCommandWithOutput(args: list[str]) -> str:
    try:
        return subprocess.run(
                args, stdout=subprocess.PIPE
            ).stdout.decode("utf-8").strip()
    except FileNotFoundError:
        return ""

def matches(regex: str, compare: str) -> bool:
    match = re.match(regex, compare)
    return not match == None


# ----- Docker -----

def checkDocker() -> bool:
    print("Checking presence of Docker..................", end="")
    
    return matches(
            r"^Docker version \d+\.\d+\.\d+.*",
            runCommandWithOutput(["docker", "--version"])
    )

def checkDockerCompose() -> bool:
    print("Checking presence of docker-compose v2.......", end="")
    return matches(
            r"^Docker Compose version \d+\.\d+\.\d+.*",
            runCommandWithOutput(["docker", "compose", "version"])
    )


# ----- Toolchain -----
def checkGit() -> bool:
    print("Checking presence of git.....................", end="")
    return matches(
            r"^git version \d+\.\d+\.\d+.*",
            runCommandWithOutput(["git", "--version"])
    )

def checkGCC() -> bool:
    print("Checking presence of g++.....................", end="")
    return matches(
            r"^g\+\+ \(.*\) \d+\.\d+\.\d+.*",
            runCommandWithOutput(["g++", "--version"])
    )

def checkJDK() -> bool:
    print("Checking presence of openjdk 17..............", end="")
    return matches(
            r"^openjdk 17\.\d+\.\d+.*",
            runCommandWithOutput(["java", "--version"])
    )

def checkMaven() -> bool:
    print("Checking presence of Maven...................", end="")
    return matches(
            r"^.{0,4}Apache Maven \d+\.\d+\.\d+.*",
            runCommandWithOutput(["mvn", "--version"])
    )


# ----- Libraries -----

def checkLibZMQ(libs: str) -> bool:
    print("Checking presence of libzmq..................", end="")
    return not libs.find("libzmq.so") == -1

def checkLibCurl(libs: str) -> bool:
    print("Checking presence of libcurl.................", end="")
    return not libs.find("libcurl.so") == -1

def checkLibZ(libs: str) -> bool:
    print("Checking presence of libz....................", end="")
    return not libs.find("libz.so") == -1


# ----- Directories -----

def checkStorage() -> bool:
    print("Checking presence of /storage dir............", end="")
    return os.path.exists("/storage")

def checkEtcNginx() -> bool:
    print("Checking presence of /etc/nginx dir..........", end="")
    return os.path.exists("/etc/nginx")

def checkVarNginx() -> bool:
    print("Checking presence of /var/log/nginx dir......", end="")
    return os.path.exists("/var/log/nginx")



def main():
    print("")
    print("----- Checking requirements for production... -----")
    print("")

    # Check Docker
    print("OK" if checkDocker() == True else "Could not find Docker.")
    print("OK" if checkDockerCompose() == True else "Could not find docker-compose v2.")

    # Check Git
    print("OK" if checkGit() == True else "Could not find git.")

    # Check dirs
    print("OK" if checkStorage() == True else "Directory /storage does not exist.")
    print("OK" if checkEtcNginx() == True else "Directory /etc/nginx does not exist.")
    print("OK" if checkVarNginx() == True else "Directory /var/log/nginx does not exist.")

    print("")
    print("----- Checking requirements for development... -----")
    print("")

    # Check tools
    print("OK" if checkGCC() == True else "Could not find g++.")
    print("OK" if checkJDK() == True else "Could not find openjdk 17.")
    print("OK" if checkMaven() == True else "Could not find Maven.")

    # Check libraries
    libs: str = runCommandWithOutput(["ldconfig", "-p"])
    print("OK" if checkLibZMQ(libs) == True else "Could not find libzmq.")
    print("OK" if checkLibCurl(libs) == True else "Could not find libcurl.")
    print("OK" if checkLibZ(libs) == True else "Could not find libz.")



if(__name__ == "__main__"):
    try:
        main()
    except KeyboardInterrupt:
        print("CTRL-C")
        pass
