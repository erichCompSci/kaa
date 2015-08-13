/*
 * Copyright 2014 CyberVision, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kaaproject.kaa.server.control.service.sdk;

import java.util.List;

import org.kaaproject.kaa.common.dto.admin.SdkPropertiesDto;
import org.kaaproject.kaa.server.common.thrift.gen.control.Sdk;
import org.kaaproject.kaa.server.common.zk.gen.BootstrapNodeInfo;
import org.kaaproject.kaa.server.control.service.sdk.event.EventFamilyMetadata;

public class ReducedCSdkGenerator extends CSdkGenerator {
	
	private static final String GENERIC_SCHEMA = "{ \"type\" : \"record\", \"name\" : \"GenericSchema\", \"fields\" : [ { \"name\" : \"json\", \"type\" : \"string\" } ] }";
    
	public Sdk generateSdk(String buildVersion,
            List<BootstrapNodeInfo> bootstrapNodes, String sdkToken,
            SdkPropertiesDto sdkProperties,
            String profileSchemaBody,
            String notificationSchemaBody,
            String configurationProtocolSchemaBody,
            String configurationBaseSchemaBody,
            byte[] defaultConfigurationData,
            List<EventFamilyMetadata> eventFamilies,
            String logSchemaBody) throws Exception {
		return super.generateSdk(buildVersion, bootstrapNodes, sdkToken, sdkProperties,
				          GENERIC_SCHEMA, GENERIC_SCHEMA, GENERIC_SCHEMA,
				          GENERIC_SCHEMA, defaultConfigurationData, eventFamilies, GENERIC_SCHEMA);
	}
	
	protected String getTemplateDir() {
    	return "sdk/reduced-c";
    }
    
    protected String getSdkPrefix() {
    	return "kaa-client-sdk-";
    }
    
    protected String getSdkNamePattern() {
    	return getSdkPrefix() + "p{}-c{}-n{}-l{}.tar.gz";
    }
    
    protected String getSdkDir() {
    	return "sdk/c";
    }
    
}
