/*
* Copyright 2014-2015 CyberVision, Inc.
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

package org.kaaproject.kaa.server.transport;

import java.io.Serializable;

/**
 * Class that represents exception that is thrown when client makes request
 * and uses invalid application token
 */
public class KaaSystemOverloadedException extends Exception {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = 5384747317252225744L;

    /**
     * Instantiates a new invalid application token exception
     *
     * @param message the message
     */
    public KaaSystemOverloadedException(String message) {
        super(message);
    }

}
