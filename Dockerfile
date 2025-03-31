# Use an official openjdk runtime as a parent image
FROM openjdk:11-jdk

# Set the working directory in the container
WORKDIR /usr/src/app

# Copy the user-submitted code (Java file) into the container
COPY . .

# Compile the user-submitted Java code
RUN javac Main.java

# Command to run the Java program
CMD ["java", "Main"]

# Use an official Python runtime as a parent image
FROM python:3.9-slim

# Set the working directory in the container
WORKDIR /usr/src/app

# Copy the user-submitted Python code into the container
COPY . .

# Command to run the Python program
CMD ["python", "main.py"]

# Use an official C++ runtime image
FROM gcc:latest

# Set the working directory in the container
WORKDIR /usr/src/app

# Copy the user-submitted code (C++ file) into the container
COPY . .

# Compile the C++ code
RUN g++ -o main main.cpp

# Command to run the C++ program
CMD ["./main"]
