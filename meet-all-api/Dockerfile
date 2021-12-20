FROM openjdk:17-slim

RUN apt-get update \
 && apt-get install -y wget gnupg gnupg2 gnupg1 \
 && rm -rf /var/lib/apt/lists/*
# Chrome
RUN sh -c 'echo "deb [arch=amd64] https://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list' \
 && wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - \
 && apt-get update \
 && apt-get install -y google-chrome-stable
# Chrome webdriver
RUN apt-get update \
 && apt-get install -y \
        libglib2.0-dev \
        libnss3 \
        libx11-dev

COPY ./target/meet-all-api-1.0-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]
