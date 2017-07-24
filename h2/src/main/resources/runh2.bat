echo Starting h2 server - jdbc url: jdbc:h2:tcp://localhost/sakila
java -cp h2-1.3.161.jar org.h2.tools.Server -ifExists -tcp -web -tcpAllowOthers

pause
