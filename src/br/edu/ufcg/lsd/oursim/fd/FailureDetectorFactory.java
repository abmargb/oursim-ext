/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.edu.ufcg.lsd.oursim.fd;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * This class represents a factory for FailureDetector instances.
 */
public class FailureDetectorFactory {

    private static final String FIXED_FD_NAME = "fixed";
    private static final String CHEN_FD_NAME = "chen";
    private static final String BERTIER_FD_NAME = "bertier";
    private static final String PHIACCRUAL_FD_NAME = "phiaccrual";
    private static final String SLICED_FD_NAME = "sliced";
    
    /**
     * Creates a FailureDetector instance based on its name and its properties.
     * @param fdName
     *            the name of the failure detector
     * @param className
     *            the fully classified failure detector class name,
     *            null if fdName corresponds to a failure detector
     *            implemented by default.
     * @param properties
     *            the options of the failure detector to be created.
     * @return the created FailureDetector instance
     * @throws Exception if a failure detector instance could not be created. 
     */
    public FailureDetector createFd(String fdName, String className,
            Map<String, String> properties) throws Exception {
        
        if (fdName == null) {
            throw new IllegalArgumentException(
                    "No failure detector name was defined.");
        }
        
        Class<?> fdClass = null;
        
        if (className != null) {
            fdClass = Class.forName(className);
        } else {
            if (fdName.equals(FIXED_FD_NAME)) {
                fdClass = FixedPingFailureDetector.class;
            } else if (fdName.equals(CHEN_FD_NAME)) {
                fdClass = ChenFailureDetector.class;
            } else if (fdName.equals(BERTIER_FD_NAME)) {
                fdClass = BertierFailureDetector.class;
            } else if (fdName.equals(PHIACCRUAL_FD_NAME)) {
                fdClass = PhiAccrualFailureDetector.class;
            } else if (fdName.equals(SLICED_FD_NAME)) {
                fdClass = SlicedPingFailureDetector.class;
            }
        }

        if (fdClass == null) {
            throw new IllegalArgumentException("There is no corresponding " +
            		"failure detector class for name " + fdName);
        }
        
        return (FailureDetector) fdClass.getConstructor(Map.class).newInstance(properties);
    }
    
    /**
     * @return The names of the available failure 
     *           detectors mechanisms
     */
    public List<String> getAvailableDetectorsNames() {
        return Arrays.asList(new String[] {FIXED_FD_NAME, CHEN_FD_NAME, 
                BERTIER_FD_NAME, PHIACCRUAL_FD_NAME, SLICED_FD_NAME});
    }
}
