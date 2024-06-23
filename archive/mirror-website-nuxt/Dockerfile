FROM node:latest
EXPOSE 3000
WORKDIR /mirror
RUN apt update && apt upgrade -y
RUN apt install -y git
COPY . /mirror/
RUN npm install
RUN npm run build
ENV NITRO_HOST 0.0.0.0
ENV NITRO_PORT 3000
ENTRYPOINT ["node", ".output/server/index.mjs"]
