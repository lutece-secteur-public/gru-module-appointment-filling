package fr.paris.lutece.plugins.appointment.modules.appointmentfilling.service;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.appointment.business.Appointment;


public interface IFillingForm 
{
	public Appointment fillFormAppointment(HttpServletRequest request)  ; 
}
