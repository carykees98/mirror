default:
	@echo "Options: dev prod check-env"

dev:
	docker compose -f docker-compose-dev.yaml up --build -d --remove-orphans

prod:
	docker compose -f docker-compose-prod.yaml up --build -d --remove-orphans

check-env:
	python3 ./check-env.py
