/**
 *  SeveralDoorOneLight
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
    name: "LightOnIfAnySensorOpen",
    namespace: "aschafer",
    author: "Andrew Schafer",
    description: "Triggers a light on when any sensor is opened and triggers the light off when all sensors are closed.  Remembers original state of light so that does not turn it off if it was on when first sensor was opened.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
    section("When any of these doors open...") {
        input "thecabinets", "capability.contactSensor", required: true, multiple: true, title: "Where?"
    }
    section("Turn on this light...") {
        input "theswitch", "capability.switchLevel", required: true
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
	subscribe(thecabinets, "contact", contactHandler)
}


def contactHandler(evt) {
	log.debug "Event Trigger Value: $evt.value"
    
    log.debug "Iterate Contacts"
    def numberopen = 0
    for (cab in thecabinets)
    {
    	log.debug "Current Contact State: $cab.currentContact"
    	if (cab.currentContact == "open")
        {
        	numberopen++
        }
    }
    
    // if this is the first turnon
    if (numberopen == 1 && evt.value == "open")
    {
    	// check if the current light is on and remember dimming level
        // also need to remember dimming level and restore it later
        log.debug "Current Level: $theswitch.currentLevel"
        log.debug "Current Status: $theswitch.currentSwitch"
        state.level = theswitch.currentLevel
        state.status = theswitch.currentSwitch
        
        // now turn on the switch
       	log.debug "Turn On Light"
        theswitch.setLevel(100)
        return
    } 
    
    if (numberopen == 0)
    {        
        log.debug "Check if changed"
        if (theswitch.currentLevel < 99 || theswitch.currentSwitch == "off")
        {
            log.debug "Settings changed while contact opened. Exit out."
            return
        }
        
        log.debug "Restore Level: $state.level"
        log.debug "Restore Status: $state.status"
    	theswitch.setLevel(state.level)  //set dim level first
        if (state.status == "off")
        {
            // originally off, so turn off
        	log.debug "Turn Off Light"
    		theswitch.off()
        }
        else log.debug "Leave Light On"
    }
}