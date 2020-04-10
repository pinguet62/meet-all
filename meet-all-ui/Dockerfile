FROM nginx
COPY ./www /usr/share/nginx/html
CMD sed -i -e "s/^ *listen .*;$/    listen       $PORT;/g" /etc/nginx/conf.d/default.conf && nginx -g 'daemon off;'
