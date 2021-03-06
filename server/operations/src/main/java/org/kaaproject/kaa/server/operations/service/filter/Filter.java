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

package org.kaaproject.kaa.server.operations.service.filter;


/**
 * The interface Filter is used to model filtering of endpoint profiles.
 * 
 * @author ashvayka
 */
public interface Filter {
    
    /**
     * check if profile body matches filter.
     *
     * @param profileBody the profile body
     * @return true, if profile body matches filter
     */
    boolean matches(String profileBody);

    /**
     * updates filter body. Used for performance to avoid recreation of filter
     *
     * @param filterBody the filter body
     */
    void updateFilterBody(String filterBody);
}
