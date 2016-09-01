package fr.paris.lutece.plugins.appointment.modules.appointmentfilling.service;


import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.appointment.business.Appointment;

public class FillingFormService implements IFillingForm
{
	
	public static final String PARAM_LAST_NAME = "lastname" ;
	public static final String PARAM_FIRST_NAME = "firstname" ;
	public static final String PARAM_EMAIL = "email" ;
	
	@Override
	public Appointment fillFormAppointment( HttpServletRequest request) 
	{

		Appointment appointment = null ;
		String strLastName = request.getParameter(PARAM_LAST_NAME) == null ? StringUtils.EMPTY : request.getParameter(PARAM_LAST_NAME)  ;
		String strFirstName = request.getParameter(PARAM_FIRST_NAME) == null ? StringUtils.EMPTY : request.getParameter(PARAM_FIRST_NAME);
		String strEmail = request.getParameter(PARAM_EMAIL) == null ? StringUtils.EMPTY : request.getParameter(PARAM_EMAIL);
		
		if (StringUtils.isNotEmpty(strLastName) || StringUtils.isNotEmpty(strFirstName) || StringUtils.isNotEmpty(strEmail) )
		{
			appointment = new Appointment();
			if (StringUtils.isNotEmpty(strLastName))
			{
				appointment.setLastName(strLastName);
			}
			if (StringUtils.isNotEmpty(strFirstName))
			{
				appointment.setFirstName(strFirstName);
			}
			if (StringUtils.isNotEmpty(strEmail))
			{
				appointment.setEmail(strEmail);
			}
		}
		return appointment ;
	}

}
