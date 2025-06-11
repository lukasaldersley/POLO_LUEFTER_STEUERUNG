This is 'firmware' (an arduino sketch) and a bit of software to configure said arduino.

Scenario: a car has been retrofitted with an AC heating system to pre-warm the engine coolant to make the engine easier to start in winter.
The Heater would be activated by a timed wall outlet. at the same time as the outlet (and therefore heater) activates the arduino starts up and waits for a configurable amount of time before toggling a pin to VCC, closing a relay that starts the defrosting fans of the car (to de-ice the windscreen).

That's all it does, the java application is just to allow non-technical people to set the delay.
As far as I can tell the java portion has project files for BlueJ (a really really baseline 'IDE') and eclipse. It probably requires [JSerialComm](https://github.com/Fazecast/jSerialComm)...
