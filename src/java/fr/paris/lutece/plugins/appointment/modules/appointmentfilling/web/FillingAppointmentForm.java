/*
 * Copyright (c) 2002-2022, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.appointment.modules.appointmentfilling.web;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.appointment.modules.appointmentfilling.constant.FillingFormConstants;
import fr.paris.lutece.plugins.appointment.modules.appointmentfilling.service.IFillingForm;
import fr.paris.lutece.plugins.appointment.service.FormService;
import fr.paris.lutece.plugins.appointment.web.AppointmentApp;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.util.url.UrlItem;

/**
 * Pre-filled booking: a link carrying the user's information opens the booking of a form with them filled in.
 */
@SessionScoped
@Named( "appointment-filling.xpage.appointmentfilling" )
@Controller( xpageName = FillingAppointmentForm.XPAGE_NAME, pageTitleI18nKey = AppointmentApp.MESSAGE_DEFAULT_PAGE_TITLE, pagePathI18nKey = AppointmentApp.MESSAGE_DEFAULT_PATH )
public class FillingAppointmentForm extends MVCApplication
{
    private static final long serialVersionUID = -604178136184768512L;
    protected static final String XPAGE_NAME = "appointmentfilling";
    private static final String DO_FILLING_INFO = "doFillingForm";
    private static final String ERROR_MESSAGE_IDFORM_EMPTY = "module.appointment.appointmentfilling.message.idform_empty";
    private static final String PARAMETER_PAGE = "page";
    private static final String PARAMETER_VIEW = "view";
    private static final String PARAMETER_STARTING_DATE_TIME = "starting_date_time";
    private static final String PARAMETER_NB_PLACES_TO_TAKE = "nbPlacesToTake";
    private static final String XPAGE_APPOINTMENT = "appointment";

    @Inject
    private IFillingForm _fillingFormService;

    @Inject
    private AppointmentApp _appointmentApp;

    /**
     * Prepare the booking of the requested form with the information of the link, then open it: at the slot of the
     * link when it carries a valid one, else at the calendar where the user picks it.
     *
     * @param request
     *            the request
     * @return the redirection to the booking
     * @throws SiteMessageException
     *             if the link names no existing form
     */
    @View( value = DO_FILLING_INFO, defaultView = true )
    public XPage doFillingFormAppointment( HttpServletRequest request ) throws SiteMessageException
    {
        String strIdForm = request.getParameter( FillingFormConstants.PARAMETER_ID_FORM );
        if ( !StringUtils.isNumeric( strIdForm ) || FormService.findFormLightByPrimaryKey( Integer.parseInt( strIdForm ) ) == null )
        {
            SiteMessageService.setMessage( request, ERROR_MESSAGE_IDFORM_EMPTY, SiteMessage.TYPE_STOP );
        }
        int nIdForm = Integer.parseInt( strIdForm );
        AppointmentDTO appointmentDTO = _fillingFormService.fillFormAppointmentAttribut( request, new AppointmentDTO( ) );
        _fillingFormService.fillFormAppointmentDynamicAttribut( request, nIdForm, appointmentDTO );
        appointmentDTO.setIdForm( nIdForm );
        _appointmentApp.prefillAppointment( appointmentDTO );

        UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + AppPathService.getPortalUrl( ) );
        url.addParameter( PARAMETER_PAGE, XPAGE_APPOINTMENT );
        url.addParameter( FillingFormConstants.PARAMETER_ID_FORM, nIdForm );
        String strNbPlaces = request.getParameter( PARAMETER_NB_PLACES_TO_TAKE );
        if ( StringUtils.isNumeric( strNbPlaces ) )
        {
            url.addParameter( PARAMETER_NB_PLACES_TO_TAKE, strNbPlaces );
        }
        LocalDateTime startingDateTime = parseDateTime( request.getParameter( PARAMETER_STARTING_DATE_TIME ) );
        if ( startingDateTime == null )
        {
            url.addParameter( PARAMETER_VIEW, AppointmentApp.VIEW_APPOINTMENT_CALENDAR );
        }
        else
        {
            url.addParameter( PARAMETER_VIEW, AppointmentApp.VIEW_APPOINTMENT_FORM );
            url.addParameter( PARAMETER_STARTING_DATE_TIME, startingDateTime.toString( ) );
        }
        return redirect( request, url.getUrl( ) );
    }

    /**
     * Read a slot start of the link.
     *
     * @param strDateTime
     *            the value of the link
     * @return the date time, null when absent or malformed
     */
    private static LocalDateTime parseDateTime( String strDateTime )
    {
        if ( StringUtils.isBlank( strDateTime ) )
        {
            return null;
        }
        try
        {
            return LocalDateTime.parse( strDateTime );
        }
        catch( DateTimeParseException e )
        {
            return null;
        }
    }
}
