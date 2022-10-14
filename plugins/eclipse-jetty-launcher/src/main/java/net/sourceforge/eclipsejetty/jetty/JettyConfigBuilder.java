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
package net.sourceforge.eclipsejetty.jetty;

import net.sourceforge.eclipsejetty.util.DOMBuilder;

public class JettyConfigBuilder
{

    private final DOMBuilder builder;
    private final JettyVersionType version;

    public JettyConfigBuilder(DOMBuilder builder, JettyVersionType version)
    {
        super();

        this.builder = builder;
        this.version = version;
    }

    /**
     * Adds a comment
     * 
     * @param comment the comment
     * @return the builder itself
     */
    public JettyConfigBuilder comment(Object comment)
    {
        builder.comment(comment);

        return this;
    }

    /**
     * Begins an Arg element
     * 
     * @return the builder itself
     */
    public JettyConfigBuilder beginArg()
    {
        builder.begin("Arg");

        return this;
    }

    /**
     * Begins an Arg element
     * 
     * @param name the name
     * @return the builder itself
     */
    public JettyConfigBuilder beginArg(String name)
    {
        beginArg();

        builder.attribute("name", name);

        return this;
    }

    /**
     * Begins an Arg element
     * 
     * @param name the name
     * @param type the type
     * @return the builder itself
     */
    public JettyConfigBuilder beginArg(String name, String type)
    {
        beginArg(name);

        builder.attribute("type", type);

        return this;
    }

    /**
     * Begins an Array element
     * 
     * @param type the type
     * @return the builder itself
     */
    public JettyConfigBuilder beginArray(String type)
    {
        builder.begin("Array").attribute("type", type);

        return this;
    }

    /**
     * Begins an Array element
     * 
     * @param type the type
     * @return the builder itself
     */
    public JettyConfigBuilder beginArray(String id, String type)
    {
        beginArray(type);

        builder.attribute("id", id);

        return this;
    }

    /**
     * Begins a Call element
     * 
     * @param name the name of the method to call
     * @return the builder itself
     */
    public JettyConfigBuilder beginCall(String name)
    {
        builder.begin("Call").attribute("name", name);

        return this;
    }

    /**
     * Begins a Call element
     * 
     * @param id the id
     * @param className the class name
     * @param name the name of the method
     * @return the builder itself;
     */
    public JettyConfigBuilder beginCall(String id, String name)
    {
        beginCall(name);

        builder.attribute("id", id);

        return this;
    }

    /**
     * Begins a Call element
     * 
     * @param id the id
     * @param className the class name
     * @param name the name of the method
     * @return the builder itself;
     */
    public JettyConfigBuilder beginCall(String id, String className, String name)
    {
        beginCall(id, name);

        builder.attribute("class", className);

        return this;
    }

    /**
     * Begins a Configure element
     * 
     * @param id
     * @param className
     * @return the builder itself
     */
    public JettyConfigBuilder beginConfigure(String id, String className)
    {
        builder.begin("Configure").attribute("id", id).attribute("class", className);

        return this;
    }

    /**
     * Begins a Get element
     * 
     * @param name the name
     * @return the builder itself
     */
    public JettyConfigBuilder beginGet(String name)
    {
        builder.begin("Get").attribute("name", name);

        return this;
    }

    /**
     * Begins a Get element
     * 
     * @param id the id
     * @param name the name
     * @return the builder itself
     */
    public JettyConfigBuilder beginGet(String id, String name)
    {
        beginGet(name);

        builder.attribute("id", id);

        return this;
    }

    /**
     * Begins an Item element
     * 
     * @return the builder itself
     */
    public JettyConfigBuilder beginItem()
    {
        builder.begin("Item");

        return this;
    }

    /**
     * Begins a Set element
     * 
     * @param name the name
     * @return the builder itself
     */
    public JettyConfigBuilder beginSet(String name)
    {
        builder.begin("Set").attribute("name", name);

        return this;
    }

    /**
     * Begins a Set element
     * 
     * @param name the name
     * @param type the type
     * @return the builder itself
     */
    public JettyConfigBuilder beginSet(String name, String type)
    {
        beginSet(name);

        builder.attribute("type", type);

        return this;
    }

    /**
     * Begins a New element
     * 
     * @param className the class name
     * @return the builder itself
     */
    public JettyConfigBuilder beginNew(String className)
    {
        builder.begin("New").attribute("class", className);

        return this;
    }

    /**
     * Begins a New element
     * 
     * @param className the class name
     * @return the builder itself
     */
    public JettyConfigBuilder beginNew(String id, String className)
    {
        beginNew(className);

        builder.attribute("id", id);

        return this;
    }

    /**
     * Begins a Ref element
     * 
     * @param id the id
     * @return the builder itself
     */
    public JettyConfigBuilder beginRef(String id)
    {
        builder.begin("Ref");

        switch (version)
        {
            case JETTY_7:
            case JETTY_8:
                builder.attribute("id", id);
                break;
            case JETTY_9:
            case JETTY_10:
            case JETTY_11:
                builder.attribute("refid", id);
                break;
            default:
                throw new IllegalArgumentException("Invalid version: " + version);
        }

        return this;
    }

    /**
     * Adds an Arg element
     * 
     * @param arg the argument
     * @return the builder itself
     */
    public JettyConfigBuilder arg(Object arg)
    {
        return arg(null, arg);
    }

    /**
     * Adds an Arg element
     * 
     * @param name the name
     * @param arg the argument
     * @return the builder itself
     */
    public JettyConfigBuilder arg(String name, Object arg)
    {
        beginArg(name, getType(arg));
        {
            builder.text(arg);
        }
        end();

        return this;
    }

    /**
     * Adds an Arg element with a String array
     * 
     * @param items the items
     * @return the builder itself
     */
    public JettyConfigBuilder argArray(Object... items)
    {
        beginArg();
        {
            array(items);
        }
        end();

        return this;
    }

    /**
     * Adds an Arg element with a reference
     * 
     * @param id the reference
     * @return the builder itself
     */
    public JettyConfigBuilder argRef(String id)
    {
        return argRef(null, id);
    }

    /**
     * Adds an Arg element with a reference
     * 
     * @param id the reference
     * @return the builder itself
     */
    public JettyConfigBuilder argRef(String name, String id)
    {
        beginArg(name);
        {
            ref(id);
        }
        end();

        return this;
    }

    /**
     * Adds an array element
     * 
     * @param id the id
     * @param values the values
     * @return the builder itself;
     */
    public JettyConfigBuilder array(Object... values)
    {
        return array(null, values);
    }

    /**
     * Adds an array element
     * 
     * @param id the id
     * @param values the values
     * @return the builder itself;
     */
    public JettyConfigBuilder array(String id, Object... values)
    {
        beginArray(id, getType(values[0]));
        {
            for (Object value : values)
            {
                item(value);
            }
        }
        end();

        return this;
    }

    /**
     * Adds an (empty) call element
     * 
     * @param name the name of the method
     * @return the builder itself
     */
    public JettyConfigBuilder call(String name)
    {
        return beginCall(name).end();
    }

    /**
     * Adds a call element
     * 
     * @param id the id
     * @param className the class name
     * @param name the name of the method
     * @return the builder itself
     */
    public JettyConfigBuilder call(String id, String className, String name)
    {
        return beginCall(id, className, name).end();
    }

    /**
     * Adds an item element
     * 
     * @param value the value
     * @return the builder itself
     */
    public JettyConfigBuilder item(Object value)
    {
        beginItem();
        builder.text(value);
        end();

        return this;
    }

    /**
     * Adds a property element
     * 
     * @param name the name
     * @return the builder itself
     */
    public JettyConfigBuilder property(String name)
    {
        builder.begin("Property").attribute("name", name).end();

        return this;
    }

    /**
     * Adds a property element
     * 
     * @param name the name
     * @param defaultValue the default value
     * @return the builder itself
     */
    public JettyConfigBuilder property(String name, String defaultValue)
    {
        builder.begin("Property").attribute("name", name).attribute("default", defaultValue).end();

        return this;
    }

    /**
     * Adds a Ref element
     * 
     * @param id the id
     * @return the builder itself;
     */
    public JettyConfigBuilder ref(String id)
    {
        return beginRef(id).end();
    }

    /**
     * Adds a Set element
     * 
     * @param name the name
     * @param value the value
     * @return the builder itself
     */
    public JettyConfigBuilder set(String name, Object value)
    {
        beginSet(name);
        {
            builder.text(value);
        }
        end();

        return this;
    }

    /**
     * Adds a Set element with an array
     * 
     * @param name the name
     * @param values the values
     * @return the builder itself
     */
    public JettyConfigBuilder setArray(String name, Object... values)
    {
        beginSet(name);
        {
            array(values);
        }
        end();

        return this;
    }

    /**
     * Ends the current element
     * 
     * @return the builder itself
     */
    public JettyConfigBuilder end()
    {
        builder.end();

        return this;
    }

    private static String getType(Object object)
    {
        if (object == null)
        {
            return null;
        }

        return object.getClass().getName();
    }
}
