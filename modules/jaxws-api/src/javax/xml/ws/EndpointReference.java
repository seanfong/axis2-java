/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package javax.xml.ws;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.spi.Provider;
import java.io.ByteArrayOutputStream;

public abstract class EndpointReference {
    protected EndpointReference() {
    }
    
    public static EndpointReference readFrom(Source eprInfoset) {
        return Provider.provider().readEndpointReference(eprInfoset);
    }
    
    public abstract void writeTo(Result result);
    
    public <T> T getPort(Class<T> serviceEndpointInterface, WebServiceFeature... features) {
        return Provider.provider().getPort(this, serviceEndpointInterface, features);
    }
    
    @Override
    public String toString() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writeTo(new StreamResult(baos));
        
        return baos.toString();
    }
}
