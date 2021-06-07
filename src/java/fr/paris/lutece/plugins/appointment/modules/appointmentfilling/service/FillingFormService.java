/*
 * Copyright (c) 2002-2021, City of Paris
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
package fr.paris.lutece.plugins.appointment.modules.appointmentfilling.service;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.appointment.modules.appointmentfilling.constant.FillingFormConstants;
import fr.paris.lutece.plugins.appointment.service.EntryService;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

public class FillingFormService implements IFillingForm
{
    private static IFillingForm _singleton;

    /**
     * Returns the unique instance
     * 
     * @return The unique instance
     */
    public static IFillingForm getService( )
    {
        if ( _singleton == null )
        {
            _singleton = new FillingFormService( );
        }

        return _singleton;
    }

    @Override
    public AppointmentDTO fillFormAppointmentAttribut( HttpServletRequest request, AppointmentDTO appointment )
    {
        String strLastName = request.getParameter( AppPropertiesService.getProperty( FillingFormConstants.PROPERTY_USER_LAST_NAME, StringUtils.EMPTY ) );
        String strFirstName = request.getParameter( AppPropertiesService.getProperty( FillingFormConstants.PROPERTY_USER_FIRST_NAME, StringUtils.EMPTY ) );
        String strEmail = request.getParameter( AppPropertiesService.getProperty( FillingFormConstants.PROPERTY_USER_EMAIL, StringUtils.EMPTY ) );

        appointment.setLastName( ( strLastName == null ) ? StringUtils.EMPTY : strLastName );
        appointment.setFirstName( ( strFirstName == null ) ? StringUtils.EMPTY : strFirstName );
        appointment.setEmail( ( strEmail == null ) ? StringUtils.EMPTY : strEmail );

        return appointment;
    }

    @Override
    public AppointmentDTO fillFormAppointmentDynamicAttribut( HttpServletRequest request, int nIdForm, AppointmentDTO appointment )
    {
        EntryFilter filter = new EntryFilter( );
        filter.setIdResource( nIdForm );
        filter.setResourceType( AppointmentFormDTO.RESOURCE_TYPE );
        filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        filter.setFieldDependNull( EntryFilter.FILTER_TRUE );
        filter.setIdIsComment( EntryFilter.FILTER_FALSE );
        filter.setIsOnlyDisplayInBack( EntryFilter.FILTER_FALSE );

        Locale locale = request.getLocale( );

        List<Entry> listEntryFirstLevel = EntryHome.getEntryList( filter );

        for ( Entry entry : listEntryFirstLevel )
        {
            try
            {
                EntryService.getResponseEntry( request, entry.getIdEntry( ), locale, appointment );
            }
            catch( Exception e )
            {
                AppLogService.error( "Erreur de réccupération de reponse de l'entrie: " + entry.getTitle( ) + "id: " + entry.getIdEntry( ) );
            }
        }

        return appointment;
    }
}
