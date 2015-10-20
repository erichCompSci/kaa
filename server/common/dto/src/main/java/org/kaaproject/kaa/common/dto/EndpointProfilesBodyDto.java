/*
 * Copyright 2015 CyberVision, Inc.
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

package org.kaaproject.kaa.common.dto;

import java.io.Serializable;
import java.util.List;

public class EndpointProfilesBodyDto implements Serializable {

    private static final long serialVersionUID = -3301431577852472525L;

    private List<EndpointProfileBodyDto> endpointProfilesBody;
    private String next;

    public EndpointProfilesBodyDto() {}

    public EndpointProfilesBodyDto(List<EndpointProfileBodyDto> endpointProfileBody, String next) {
       this.endpointProfilesBody = endpointProfileBody;
       this.next = next;
    }

    public List<EndpointProfileBodyDto> getEndpointProfilesBody() {
        return endpointProfilesBody;
    }

    public void setEndpointProfilesBody(List<EndpointProfileBodyDto> endpointProfileBody) {
        this.endpointProfilesBody = endpointProfileBody;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((endpointProfilesBody == null) ? 0 : endpointProfilesBody.hashCode());
        result = prime * result + ((next == null) ? 0 : next.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EndpointProfilesBodyDto other = (EndpointProfilesBodyDto) obj;
        if (endpointProfilesBody == null) {
            if (other.endpointProfilesBody != null)
                return false;
        } else if (!endpointProfilesBody.equals(other.endpointProfilesBody))
            return false;
        if (next == null) {
            if (other.next != null)
                return false;
        } else if (!next.equals(other.next))
            return false;
        return true;
    }
}