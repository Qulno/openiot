package org.openiot.scheduler.client.ui.cli;


/**
 *    Copyright (c) 2011-2014, OpenIoT
 *    
 *    This file is part of OpenIoT.
 *
 *    OpenIoT is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, version 3 of the License.
 *
 *    OpenIoT is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with OpenIoT.  If not, see <http://www.gnu.org/licenses/>.
 *
 *     Contact: OpenIoT mailto: info@openiot.eu
 */

import java.io.StringReader;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientRequestFactory;
import org.jboss.resteasy.client.ClientResponse;
import org.openiot.commons.sensortypes.model.MeasurementCapability;
import org.openiot.commons.sensortypes.model.SensorType;
import org.openiot.commons.sensortypes.model.SensorTypes;
import org.openiot.commons.sensortypes.model.Unit;


/**
 * @author Nikos Kefalakis (nkef) e-mail: nkef@ait.edu.gr
 *
 */
public class SchedulerClientCLI 
{
	static ClientRequestFactory clientRequestFactory;	

	public static void welcomeMessage() 
	{
		ClientRequest welcomeMessageClientRequest = clientRequestFactory
				.createRelativeRequest("/rest/services");

		welcomeMessageClientRequest.accept(MediaType.TEXT_PLAIN);
		try {
			String str = welcomeMessageClientRequest.get(String.class).getEntity();
			System.out.println(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void discoverSensors() 
	{
		try {
			ClientRequest discoverSensorsClientRequest = clientRequestFactory
					.createRelativeRequest("/rest/services/discoverSensors");

			discoverSensorsClientRequest.queryParameter("userID", "userIDString");
			discoverSensorsClientRequest.queryParameter("longitude", 100.1);
			discoverSensorsClientRequest.queryParameter("latitude", 200.2);
			discoverSensorsClientRequest.queryParameter("radius", 30F);
			
			discoverSensorsClientRequest.accept("application/xml");


			ClientResponse<String> response;
			String str = null;

			try {
				response = discoverSensorsClientRequest.get(String.class);

				if (response.getStatus() != 200) {
					throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
				}

				str = response.getEntity();
				System.out.println(str);
			} catch (Exception e) {
				e.printStackTrace();
			}

			JAXBContext context = JAXBContext.newInstance(SensorTypes.class);
			Unmarshaller um = context.createUnmarshaller();
			SensorTypes sensorTypes = (SensorTypes) um.unmarshal(new StreamSource(new StringReader(str)));

			
			for (SensorType sensorType : sensorTypes.getSensorType()){
				System.out.println("sensorType.getId():"+sensorType.getId());
				System.out.println("sensorType.getName():"+sensorType.getName());
				for (MeasurementCapability measurementCapability : sensorType.getMeasurementCapability()){
					System.out.println("measurementCapability.getId():"+measurementCapability.getId());
					System.out.println("measurementCapability.getName():"+measurementCapability.getType());
					
					for(Unit unit: measurementCapability.getUnit()){
						System.out.println("unit.getName():"+unit.getName());
						System.out.println("unit.getType():"+unit.getType());
						
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

		
	public static void main(String[] args) throws Exception 
	{
		clientRequestFactory = new ClientRequestFactory(UriBuilder.fromUri(
				"http://localhost:8080/scheduler.core").build());

		// welcomeMessage();
		discoverSensors();
	}
}
