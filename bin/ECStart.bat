%ECHO OFF
%ECHO Starting ECS System
PAUSE
%ECHO ECS Monitoring Console
START "MUSEUM ENVIRONMENTAL CONTROL SYSTEM CONSOLE" /NORMAL java ECSConsole %1
%ECHO Starting Window Sensor Console
START "WINDOW SENSOR CONSOLE" /MIN /NORMAL java WindowSensor %1
%ECHO Starting Door Sensor Console
START "DOOR SENSOR CONSOLE" /MIN /NORMAL java DoorSensor %1
%ECHO Starting MotionDetection Sensor Console
START "MOTIONDETECTION SENSOR CONSOLE" /MIN /NORMAL java MotionDetectionSensor %1