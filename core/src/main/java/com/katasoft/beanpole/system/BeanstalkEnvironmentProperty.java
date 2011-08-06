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
