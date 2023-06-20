# Use Ubuntu as the base image
FROM ubuntu:latest

# Update the package manager and install OpenJDK 17
RUN apt-get update && apt-get install -y openjdk-17-jre-headless

# Copy the pre-compiled JAR file to the container
COPY lobster/lobster.jar /app/lobster.jar

# Copy the bot.properties file to the container
COPY lobster/bot.properties /app/bot.properties

# Set the working directory to the location of the JAR file
WORKDIR /app

# Run the JAR file with the specified command-line arguments
CMD ["java", "-jar", "lobster.jar"]