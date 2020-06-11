FROM openjdk:8
COPY ./build/libs/Recommender-0.1.jar/ /tmp
WORKDIR /tmp
ENTRYPOINT ["java","-jar", "Recommender-0.1.jar"]