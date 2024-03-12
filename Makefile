default:
	@echo "Options: dev prod check-env"

dev:
	docker compose -f docker-compose-dev.yaml up --build -d

prod:
	docker compose -f docker-compose-prod.yaml up --build -d

check-env:
	python3 ./check-env.py
