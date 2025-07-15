FROM tomcat:11-jdk21-temurin

# Remove the ROOT app thats already there (in case)
WORKDIR /app

# Copy the WAR file to the Tomcat webapps directory ROOT.war
COPY target/Hospital-Management-System-1.6-SNAPSHOT.war /usr/local/tomcat/webapps/app.war

# Debug: List contents of the deployed WAR after copying
RUN cd /usr/local/tomcat/webapps && \
    jar -tf app.war > /usr/local/tomcat/webapps/war-contents.txt && \
    cat /usr/local/tomcat/webapps/war-contents.txt

EXPOSE 8080

#CMD ["catalina.sh", "run"]
