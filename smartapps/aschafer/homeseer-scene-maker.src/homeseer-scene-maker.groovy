/**
 *  HomeSeer Scene Maker
 *
 *  Copyright 2017 Andrew Schafer
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "HomeSeer Scene Maker",
    namespace: "aschafer",
    author: "Andrew Schafer",
    description: "Pick a HomeSeer switch and set the different taps to on/off/dim selected lights.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Select the HomeSeer switch...") {
		input "buttonDevice", "capability.button", title: "Which Switch?", multiple: false, required: true
	}
        
   section("Select switches to control for double-tap up...", hideable: true) {
   		input "switcheson", "capability.switch", title: "Which Switch(es) On?", multiple: true, required: false
   		input "switchesoff", "capability.switch", title: "Which Switch(es) Off?", multiple: true, required: false
        input "switchesdim", "capability.switchLevel", title: "Which Switch(es) Dim?", multiple: true, required: false
        input "switchesdimlevel", "number", title: "Dim Level?", multiple: false, required: false, hideWhenEmpty: "switchesdim", defaultValue:50
   
   }
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	// TODO: subscribe to attributes, devices, locations, etc.
    log.debug "Subscribe to button events"
    subscribe(buttonDevice, "button", buttonEvent)
}

def buttonEvent(evt) {
    	def buttonNumber = evt.jsonData.buttonNumber
		def pressType = evt.value
		log.debug "$buttonDevice: Button $buttonNumber was $pressType"
        switch (buttonNumber)
        {
        	case "1": // double tap up
            	log.debug "Double tap up pressed"
            	turnOff(switchesoff)
               	turnOn(switcheson)
				turnDim(switchesdim)
                break
        }


}

def turnOn(devices) {
	if (devices)
    {
		log.debug "Turning On: $devices"
		devices.on()
    }
}

def turnOff(devices) {
	if (devices)
    {
		log.debug "Turning Off: $devices"
		devices.off()
    }
}

def turnDim(devices) {
	if (devices)
    {
		log.debug "Dimming: $devices"
	    def dimLevel
        if (switchesdimlevel)
        	dimLevel = switchesdimlevel
        else
            dimLevel = 50
        log.debug "Set dim level: $dimLevel"
        switchesdim.setLevel(dimLevel)
   }
}