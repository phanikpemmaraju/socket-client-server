# Multi Score Socket

###### Server Socket Component to listen valid request data sent from Client Socket running on WebSphere.
###### Service call to SUPPO009.
###### Return back the response received from SUPPO009 to the client socket.

# Running locally

    `mvn clean install`  or `mvn spring-boot:run`
            
#### Ports

    application port => 2443
    actuator port => 2443
    server socket port => 8000
    
### Exposed Endpoints

    GET http://localhost:2443/api-docs
    GET http://localhost:2443/manage/health
