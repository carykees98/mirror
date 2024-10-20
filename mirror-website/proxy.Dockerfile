FROM nginx:latest
EXPOSE 80
EXPOSE 443
COPY ./mirror_website/static /var/www/static
RUN rm -f /etc/nginx/conf.d/default.conf
COPY ./docker/nginx.conf /etc/nginx/conf.d/default.conf
