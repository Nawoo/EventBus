%ECHO OFF
%ECHO Starting ECS System
PAUSE
%ECHO ECS Monitoring Console
START "MUSEUM ENVIRONMENTAL CONTROL SYSTEM CONSOLE" /NORMAL java ECSConsole2 %1
%ECHO Starting Temperature Controller Console
START "FIRE CONTROLLER CONSOLE" /MIN /NORMAL java FireController %1
%ECHO Starting "FIRE Sensor Console
START "TEMPERATURE SENSOR CONSOLE" /MIN /NORMAL java FireSensor %1
