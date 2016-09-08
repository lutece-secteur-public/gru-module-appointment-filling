# gru-module-appointment-filling

It provides auto filling the appointment form from a request post and get

Mandatory attributes are

 	"id_form" (identifying technical form)
 	"page" (the xpage name), its value is always equal "appointmentfilling"
 
static attributes
 
	"firstname": first name of user
	"lastname": last name of user
	"email": email of user

dynamic attributes

	it is the generic-attribute attributes
	syntax of the parameter name: attribute+id_entry_of_attribut
