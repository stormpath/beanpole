/*
 * Copyright 2011 Katasoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.katasoft.beanpole.system;

/**
 * Amazon Beanstalk's supported
 * <a href="http://docs.amazonwebservices.com/elasticbeanstalk/latest/dg/index.html?create_deploy_Java.managing.html">
 * system property names</a>.
 *
 * @since 0.1
 */
public enum BeanstalkEnvironmentProperty {

    AWS_ACCESS_KEY_ID,
    AWS_SECRET_KEY,
    JDBC_CONNECTION_STRING,
    PARAM1,
    PARAM2,
    PARAM3,
    PARAM4,
    PARAM5;

    public static final String CUSTOM_PARAM_NAME_PREFIX = "PARAM";

}
