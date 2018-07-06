// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package net.sourceforge.eclipsejetty.starter.console.command;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import net.sourceforge.eclipsejetty.starter.console.AbstractCommand;
import net.sourceforge.eclipsejetty.starter.console.ArgumentException;
import net.sourceforge.eclipsejetty.starter.console.ConsoleAdapter;
import net.sourceforge.eclipsejetty.starter.console.Process;
import net.sourceforge.eclipsejetty.starter.console.util.WildcardUtils;
import net.sourceforge.eclipsejetty.starter.util.Utils;

/**
 * Calls MBeans.
 * 
 * @author Manfred Hantschel
 */
public class MBeanCommand extends AbstractCommand
{

    private final MBeanServer server;

    public MBeanCommand(ConsoleAdapter consoleAdapter)
    {
        super(consoleAdapter, "mbean", "mb");

        server = ManagementFactory.getPlatformMBeanServer();
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.Command#getFormat()
     */
    public String getFormat()
    {
        return "[command [params]]";
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.Command#getDescription()
     */
    public String getDescription()
    {
        return "Access local MBeans.";
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.AbstractCommand#help(net.sourceforge.eclipsejetty.starter.console.Process)
     */
    @Override
    public int help(Process process) throws Exception
    {
        String name = process.args.consumeString();

        if (name == null)
        {
            return super.help(process);
        }

        boolean hit = false;
        List<ObjectName> objectNames = findObjectNames(getLeft(name));
        String operationPattern = Utils.ensure(getRight(name), "*");

        for (ObjectName objectName : objectNames)
        {
            MBeanInfo info = getMBeanInfo(objectName);
            List<MBeanOperationInfo> operationInfos = findMBeanOperationInfos(info, operationPattern, -1);

            for (MBeanOperationInfo operationInfo : operationInfos)
            {
                StringBuilder builder = new StringBuilder(getNames()[0]);

                builder.append(" ").append(getName(objectName));
                builder.append(".").append(operationInfo.getName());

                MBeanParameterInfo[] parameterInfos = operationInfo.getSignature();

                for (MBeanParameterInfo parameterInfo : parameterInfos)
                {
                    builder.append(" ").append(toParameter(toClass(parameterInfo.getType())));
                }

                process.out.println(builder);
                hit = true;
            }
        }

        if (!hit)
        {
            throw new ArgumentException(String.format("No bean operation matches %s", name));
        }

        return 0;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.AbstractCommand#getHelpDescription()
     */
    @Override
    protected String getHelpDescription()
    {
        return "Access local MBeans.\n\n" //
            + "Commands:\n" //
            + "---------\n\n" //
            + "list, l                           List available MBeans.\n"
            + "attr, a <NAME>[.<ATTR>]           Shows the attributes.\n"
            + "call, c <NAME>.<OPER> {<PARAMS>}  Calls the specified operation.\n"//
            + "help, h, ? <NAME>[.<OPER>]        Show help for the specified MBean.\n\n"
            + "The <NAME>, <ATTR> and <OPER> arguments may contain wildcards. If the operation or attribute is unambigous ,"
            + "you can omit the command.\n\n"
            + "The command does not support all types of operations (e.g. commands with arrays are not supported). If you "
            + "need to call these operations, use JConsole or VisualVM.";
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.Command#getOrdinal()
     */
    public int getOrdinal()
    {
        return 2000;
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.sourceforge.eclipsejetty.starter.console.Command#execute(java.lang.String,
     *      net.sourceforge.eclipsejetty.starter.console.Process)
     */
    public int execute(String commandName, Process process) throws Exception
    {
        String command = process.args.consumeString();

        if ((command == null) || ("list".equalsIgnoreCase(command)) || ("l".equalsIgnoreCase(command)))
        {
            return list(process);
        }

        if (("attr".equalsIgnoreCase(command)) || ("a".equalsIgnoreCase(command)))
        {
            return attr(process);
        }

        if (("call".equalsIgnoreCase(command)) || ("c".equalsIgnoreCase(command)))
        {
            return call(process);
        }

        if (("help".equalsIgnoreCase(command)) || ("h".equalsIgnoreCase(command)) || ("?".equalsIgnoreCase(command)))
        {
            return help(process);
        }

        return guess(process, command);
    }

    protected int list(Process process) throws MalformedObjectNameException
    {
        @SuppressWarnings("unchecked")
        Set<ObjectName> objectNames = server.queryNames(new ObjectName("*.*:*"), null);

        for (ObjectName objectName : objectNames)
        {
            process.out.println(getName(objectName));
        }

        return 0;
    }

    protected int attr(Process process) throws MalformedObjectNameException, IntrospectionException,
        InstanceNotFoundException, ReflectionException, MBeanException, AttributeNotFoundException
    {
        String name = process.args.consumeString();

        if (name == null)
        {
            throw new ArgumentException("<NAME> is missing.");
        }

        boolean hit = false;
        List<ObjectName> objectNames = findObjectNames(getLeft(name));
        String attributePattern = Utils.ensure(getRight(name), "*");

        for (ObjectName objectName : objectNames)
        {
            MBeanInfo info = getMBeanInfo(objectName);
            List<MBeanAttributeInfo> attributeInfos = findMBeanAttributeInfos(info, attributePattern);
            hit = attributeInfos.size() > 0;

            attr(process, objectName, attributeInfos);
        }

        if (!hit)
        {
            throw new ArgumentException(String.format("Not matching attribute found: %s %s", name, attributePattern));
        }

        return 0;
    }

    private int attr(Process process, ObjectName objectName, List<MBeanAttributeInfo> attributeInfos)
    {
        for (MBeanAttributeInfo attributeInfo : attributeInfos)
        {
            Object value;

            try
            {
                value = server.getAttribute(objectName, attributeInfo.getName());
            }
            catch (Exception e)
            {
                value = String.format("<%s: %s>", e.getClass().getName(), e.getMessage());
            }

            process.out.println(String.format("%s.%s = %s", getName(objectName), attributeInfo.getName(),
                toString(value)));
        }

        return 0;
    }

    protected int call(Process process) throws MalformedObjectNameException, IntrospectionException,
        InstanceNotFoundException, ReflectionException, InstanceAlreadyExistsException, NotCompliantMBeanException,
        MBeanRegistrationException, MBeanException
    {
        String name = process.args.consumeString();

        if (name == null)
        {
            throw new ArgumentException("<NAME> is missing.");
        }

        ObjectName objectName = findObjectName(getLeft(name));

        if (objectName == null)
        {
            throw new ArgumentException(String.format("No match for %s.", name));
        }

        MBeanInfo info = getMBeanInfo(objectName);
        MBeanOperationInfo operationInfo =
            findMBeanOperationInfo(info, Utils.ensure(getRight(name), "*"), process.args.size());

        if (operationInfo == null)
        {
            throw new ArgumentException(String.format("No match for %s with %s arguments.", name, process.args.size()));
        }

        return call(process, objectName, operationInfo);
    }

    protected int call(Process process, ObjectName objectName, MBeanOperationInfo operationInfo)
        throws ReflectionException, InstanceNotFoundException, MBeanException
    {
        List<Object> params = new ArrayList<Object>();
        List<String> sig = new ArrayList<String>();
        MBeanParameterInfo[] signature = operationInfo.getSignature();

        for (int i = 0; i < signature.length; i += 1)
        {
            try
            {
                params.add(convert(signature[i].getType(), process.args.consumeString()));
                sig.add(signature[i].getType());
            }
            catch (Exception e)
            {
                throw new ArgumentException(String.format("Invalid argument #%d: %s", (i + 1), e.toString()));
            }
        }

        Object result =
            server.invoke(objectName, operationInfo.getName(), params.toArray(), sig.toArray(new String[sig.size()]));

        if ("void".equals(operationInfo.getReturnType()))
        {
            process.out.println("Invocation successful.");
        }
        else
        {
            process.out.println(toString(result));
        }

        return -1;
    }

    protected int guess(Process process, String name) throws IntrospectionException, InstanceNotFoundException,
        ReflectionException, MBeanException, MalformedObjectNameException
    {
        ObjectName objectName = findObjectName(getLeft(name));

        if (objectName == null)
        {
            throw new ArgumentException(String.format("Unknown command: %s", name));
        }

        MBeanInfo info = getMBeanInfo(objectName);

        if (process.args.size() == 0)
        {
            List<MBeanAttributeInfo> attributeInfos = findMBeanAttributeInfos(info, getRight(name));

            if (attributeInfos.size() > 0)
            {
                return attr(process, objectName, attributeInfos);
            }
        }

        MBeanOperationInfo operationInfo =
            findMBeanOperationInfo(info, Utils.ensure(getRight(name), "*"), process.args.size());

        if (operationInfo != null)
        {
            return call(process, objectName, operationInfo);
        }

        throw new ArgumentException(String.format("Invalid argument: %s", name));
    }

    protected MBeanAttributeInfo findMBeanAttributeInfo(MBeanInfo info, String attributeName)
    {
        List<MBeanAttributeInfo> results = findMBeanAttributeInfos(info, attributeName);

        if (results.size() == 0)
        {
            return null;
        }

        if (results.size() > 1)
        {
            throw new ArgumentException(String.format("Non-unique match for %s", attributeName));
        }

        return results.get(0);
    }

    protected List<MBeanAttributeInfo> findMBeanAttributeInfos(MBeanInfo info, String attributePattern)
    {
        List<MBeanAttributeInfo> results = new ArrayList<MBeanAttributeInfo>();

        if (attributePattern != null)
        {
            for (MBeanAttributeInfo attributeInfo : info.getAttributes())
            {
                if (WildcardUtils.match(attributeInfo.getName().toLowerCase(), attributePattern.toLowerCase()))
                {
                    results.add(attributeInfo);
                }
            }
        }

        return results;
    }

    protected MBeanOperationInfo findMBeanOperationInfo(MBeanInfo info, String operationName, int signatureLength)
    {
        List<MBeanOperationInfo> results = findMBeanOperationInfos(info, operationName, signatureLength);

        if (results.size() == 0)
        {
            return null;
        }

        if (results.size() > 1)
        {
            throw new ArgumentException(String.format("Non-unique match for %s", operationName));
        }

        return results.get(0);
    }

    protected List<MBeanOperationInfo> findMBeanOperationInfos(MBeanInfo info, String operationPattern,
        int signatureLength)
    {
        List<MBeanOperationInfo> results = new ArrayList<MBeanOperationInfo>();

        if (operationPattern != null)
        {
            for (MBeanOperationInfo operationInfo : info.getOperations())
            {
                if (!isSupported(operationInfo))
                {
                    continue;
                }

                if ((signatureLength >= 0) && (operationInfo.getSignature().length != signatureLength))
                {
                    continue;
                }

                if (WildcardUtils.match(operationInfo.getName().toLowerCase(), operationPattern.toLowerCase()))
                {
                    results.add(operationInfo);
                }
            }
        }

        return results;
    }

    protected MBeanInfo findMBeanInfo(String name) throws MalformedObjectNameException, IntrospectionException,
        InstanceNotFoundException, ReflectionException
    {
        ObjectName objectName = findObjectName(name);

        return getMBeanInfo(objectName);
    }

    private MBeanInfo getMBeanInfo(ObjectName objectName) throws ReflectionException, IntrospectionException,
        InstanceNotFoundException
    {
        if (objectName == null)
        {
            return null;
        }

        return server.getMBeanInfo(objectName);
    }

    protected ObjectName findObjectName(String name) throws MalformedObjectNameException
    {
        List<ObjectName> results = findObjectNames(name);

        if (results.size() == 0)
        {
            return null;
        }

        if (results.size() > 1)
        {
            throw new ArgumentException(String.format("Non-unique match for %s", name));
        }

        return results.get(0);
    }

    protected List<ObjectName> findObjectNames(String pattern) throws MalformedObjectNameException
    {
        List<ObjectName> results = new ArrayList<ObjectName>();

        if (pattern != null)
        {
            @SuppressWarnings("unchecked")
            Set<ObjectName> objectNames = server.queryNames(new ObjectName("*.*:*"), null);

            for (ObjectName objectName : objectNames)
            {
                if (WildcardUtils.match(getName(objectName).toLowerCase(), pattern.toLowerCase()))
                {
                    results.add(objectName);
                }
            }
        }

        return results;
    }

    protected static String getName(ObjectName objectName)
    {
        String name = objectName.getKeyProperty("name");

        if (name == null)
        {
            name = objectName.getKeyProperty("type");
        }

        name = name.replace(' ', '_');

        return name;
    }

    protected static String getLeft(String argument)
    {
        if (argument == null)
        {
            return null;
        }

        int index = argument.lastIndexOf('.');

        if (index < 0)
        {
            return argument;
        }

        return argument.substring(0, index);
    }

    protected static String getRight(String argument)
    {
        if (argument == null)
        {
            return null;
        }

        int index = argument.lastIndexOf('.');

        if (index < 0)
        {
            return null;
        }

        return argument.substring(index + 1);
    }

    protected static Class<?> toClass(String name)
    {
        if ("boolean".equals(name))
        {
            return Boolean.TYPE;
        }

        if ("byte".equals(name))
        {
            return Byte.TYPE;
        }

        if ("short".equals(name))
        {
            return Short.TYPE;
        }

        if ("int".equals(name))
        {
            return Integer.TYPE;
        }

        if ("long".equals(name))
        {
            return Long.TYPE;
        }

        if ("float".equals(name))
        {
            return Float.TYPE;
        }

        if ("double".equals(name))
        {
            return Double.TYPE;
        }

        if ("char".equals(name))
        {
            return Character.TYPE;
        }

        try
        {
            return Class.forName(name);
        }
        catch (ClassNotFoundException e)
        {
            return null;
        }
    }

    protected static boolean isSupported(MBeanOperationInfo operationInfo)
    {
        MBeanParameterInfo[] signature = operationInfo.getSignature();

        for (MBeanParameterInfo parameterInfo : signature)
        {
            if (!isArgumentSupported(parameterInfo.getType()))
            {
                return false;
            }
        }

        return true;
    }

    protected static Object convert(String typeName, String value) throws IllegalArgumentException
    {
        if ("null".equals(value))
        {
            return null;
        }

        Class<?> type = toClass(typeName);

        if (type == null)
        {
            throw new IllegalArgumentException(String.format("Unsupported parameter %s", toParameter(null)));
        }

        if ((type.isAssignableFrom(Boolean.class)) || (type == Boolean.TYPE))
        {
            return Boolean.valueOf(value);
        }

        if ((type.isAssignableFrom(Byte.class)) || (type == Boolean.TYPE))
        {
            return Byte.decode(value);
        }

        if ((type.isAssignableFrom(Short.class)) || (type == Short.TYPE))
        {
            return Short.decode(value);
        }

        if ((type.isAssignableFrom(Integer.class)) || (type == Integer.TYPE))
        {
            return Integer.decode(value);
        }

        if ((type.isAssignableFrom(Long.class)) || (type == Long.TYPE))
        {
            return Long.decode(value);
        }

        if ((type.isAssignableFrom(Float.class)) || (type == Float.TYPE))
        {
            return Float.valueOf(value);
        }

        if ((type.isAssignableFrom(Double.class)) || (type == Double.TYPE))
        {
            return Double.valueOf(value);
        }

        if ((type.isAssignableFrom(Character.class)) || (type == Character.TYPE))
        {
            if (value.length() != 1)
            {
                throw new IllegalArgumentException(String.format("Invalid character: %s", value));
            }
            return new Character(value.charAt(0));
        }

        if (type.isAssignableFrom(String.class))
        {
            return value;
        }

        throw new IllegalArgumentException(String.format("Unsupported parameter %s", toParameter(type)));
    }

    protected static boolean isArgumentSupported(String typeName)
    {
        Class<?> type = toClass(typeName);

        if (type.isArray())
        {
            return false;
        }

        if (type.isPrimitive())
        {
            return true;
        }

        if (type.isAssignableFrom(Boolean.class))
        {
            return true;
        }

        if (type.isAssignableFrom(Number.class))
        {
            return true;
        }

        if (type.isAssignableFrom(Character.class))
        {
            return true;
        }

        if (type.isAssignableFrom(String.class))
        {
            return true;
        }

        return false;
    }

    protected static String toParameter(Class<?> typeName)
    {
        if (typeName == null)
        {
            typeName = Object.class;
        }

        return "<" + typeName.getSimpleName() + ">";
    }

    protected static String toString(Object object)
    {
        if (object == null)
        {
            return null;
        }

        if (object.getClass().isArray())
        {
            Class<?> componentType = object.getClass().getComponentType();

            if (Boolean.TYPE == componentType)
            {
                return Arrays.toString((boolean[]) object);
            }

            if (Byte.TYPE == object.getClass().getComponentType())
            {
                return Arrays.toString((byte[]) object);
            }

            if (Short.TYPE == object.getClass().getComponentType())
            {
                return Arrays.toString((short[]) object);
            }

            if (Integer.TYPE == object.getClass().getComponentType())
            {
                return Arrays.toString((int[]) object);
            }

            if (Long.TYPE == object.getClass().getComponentType())
            {
                return Arrays.toString((long[]) object);
            }

            if (Float.TYPE == object.getClass().getComponentType())
            {
                return Arrays.toString((float[]) object);
            }

            if (Double.TYPE == object.getClass().getComponentType())
            {
                return Arrays.toString((double[]) object);
            }

            if (Character.TYPE == object.getClass().getComponentType())
            {
                return Arrays.toString((char[]) object);
            }

            return Arrays.toString((Object[]) object);
        }

        return object.toString();
    }
}
