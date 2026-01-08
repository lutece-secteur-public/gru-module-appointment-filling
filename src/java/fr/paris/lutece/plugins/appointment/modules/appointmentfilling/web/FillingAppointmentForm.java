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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.appointment.modules.appointmentfilling.constant.FillingFormConstants;
import fr.paris.lutece.plugins.appointment.modules.appointmentfilling.service.FillingFormService;
import fr.paris.lutece.plugins.appointment.modules.appointmentfilling.service.IFillingForm;
import fr.paris.lutece.plugins.appointment.web.AppointmentApp;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.xpages.XPage;

@Controller( xpageName = FillingAppointmentForm.XPAGE_NAME, pageTitleI18nKey = FillingAppointmentForm.MESSAGE_DEFAULT_PAGE_TITLE, pagePathI18nKey = FillingAppointmentForm.MESSAGE_DEFAULT_PATH )
public class FillingAppointmentForm extends AppointmentApp
{
    /**
     * UID
     */
    private static final long serialVersionUID = -604178136184768512L;
    /**
     * The name of the XPage
     */
    protected static final String XPAGE_NAME = "appointmentfilling";
    // View
    private static final String DO_FILLING_INFO = "doFillingForm";
    private static final String ERROR_MESSAGE_IDFORM_EMPTY = "appointmentfilling.message.idform_empty";
    private static final String SESSION_NOT_VALIDATED_APPOINTMENT = "appointment.appointmentFormService.notValidatedAppointment";

    /**
     * Filling appointment form
     * 
     * @param request
     * @return XPAGE VIEW_APPOINTMENT_FORM_FIRST_STEP
     * @throws SiteMessageException
     * @throws AccessDeniedException
     */
    @View( value = DO_FILLING_INFO, defaultView = true )
    public XPage doFillingFormAppointment( HttpServletRequest request ) throws SiteMessageException, UserNotSignedException, AccessDeniedException
    {
        String strIdForm = request.getParameter( FillingFormConstants.PARAMETER_ID_FORM );

        if ( StringUtils.isEmpty( strIdForm ) || !StringUtils.isNumeric( strIdForm ) )
        {
            SiteMessageService.setMessage( request, ERROR_MESSAGE_IDFORM_EMPTY, SiteMessage.TYPE_STOP );
        }

        int nIdForm = Integer.parseInt( strIdForm );
        AppointmentDTO appointmentDTO = new AppointmentDTO( );
        IFillingForm fillingFormService = FillingFormService.getService( );

        appointmentDTO = fillingFormService.fillFormAppointmentAttribut( request, appointmentDTO );
        fillingFormService.fillFormAppointmentDynamicAttribut( request, nIdForm, appointmentDTO );
        request.getSession( ).setAttribute( SESSION_NOT_VALIDATED_APPOINTMENT, appointmentDTO );

        return getViewAppointmentForm( request );
    }

}
