package com.katasoft.beanpole.system

/**
 * Unit tests for the {@link BeanstalkEnvironmentPropertiesFactory} implementation.
 *
 * @since 0.1
 */
class BeanstalkEnvironmentPropertiesFactoryTest extends GroovyTestCase {

    void testNoProperties() {
        def factory = new BeanstalkEnvironmentPropertiesFactory()
        def props = factory.instance
        assertNotNull props
        assertTrue props.isEmpty()
    }

    void testSimple() {

        final def values = [:]
        for (def propName: BeanstalkEnvironmentProperty.values()) {
            values."${propName.name()}" = "${propName}.value"
        }

        BeanstalkEnvironmentPropertiesFactory factory = new BeanstalkEnvironmentPropertiesFactory() {
            @Override
            protected String getSystemProperty(String name) {
                return values.get(name)
            }
        }

        def map = factory.instance;
        for (def key: map.keySet()) {
            assertEquals map.get(key), values.get(key)
        }
    }

    void testKeyValueParam() {

        BeanstalkEnvironmentPropertiesFactory factory = new BeanstalkEnvironmentPropertiesFactory() {
            @Override
            protected String getSystemProperty(String name) {
                if (name == "PARAM1") {
                    return "key1=value1, key2=value2"
                }
                if (name == "PARAM2") {
                    return "Hello, this is not a key-value pair"
                }
                if (name == "PARAM3") {
                    return " "
                }
                return null;
            }
        }

        def map = factory.instance;
        assertNotNull map.PARAM1
        assertNotNull map.key1
        assertNotNull map.key2
        assertEquals map.PARAM1, "key1=value1, key2=value2"
        assertEquals map.key1, "value1"
        assertEquals map.key2, "value2"

        assertNotNull map.PARAM2
        assertEquals map.PARAM2, "Hello, this is not a key-value pair"

        assertNull map.PARAM3

        //4 = PARAM1, PARAM3, key1, key2
        assertEquals map.size(), 4
    }
}
