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
package net.sourceforge.eclipsejetty.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

/**
 * Utility class to quickly build DOMs
 * 
 * @author Manfred Hantschel
 */
public class DOMBuilder
{

    protected Document document = null;
    protected Node rootNode = null;
    protected Node activeNode = null;

    /**
     * Initializes the builder and creates a new document
     * 
     * @throws IllegalStateException if the parser could not be configured
     */
    public DOMBuilder() throws IllegalStateException
    {
        super();

        try
        {
            reset();
        }
        catch (ParserConfigurationException e)
        {
            throw new IllegalStateException("Could not configure parser", e); //$NON-NLS-1$
        }
    }

    /**
     * Initialized the generator and uses the specified document
     * 
     * @param document the document
     * @param rootNode the node to attach all other nodes to
     */
    public DOMBuilder(Document document, Node rootNode)
    {
        super();

        reset(document, rootNode);
    }

    /**
     * Resets the generator
     * 
     * @return the DOM builder instance
     * @throws ParserConfigurationException sometimes
     */
    public DOMBuilder reset() throws ParserConfigurationException
    {
        Document newDocument = createDocument();

        return reset(newDocument, newDocument);
    }

    /**
     * Resets the generator with the specified document and rootNode
     * 
     * @param newDocument the document
     * @param newRootNode the root node
     * @return the DOM builder instance
     */
    public DOMBuilder reset(Document newDocument, Node newRootNode)
    {
        document = newDocument;
        rootNode = newRootNode;

        activeNode = newRootNode;

        return this;
    }

    /**
     * Creates the document
     * 
     * @return the document
     */
    private Document createDocument() throws IllegalArgumentException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try
        {
            builder = factory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e)
        {
            throw new IllegalArgumentException("Failed to configure parser", e); //$NON-NLS-1$
        }

        return builder.newDocument();
    }

    /**
     * Returns the document
     * 
     * @return the document
     */
    public Document getDocument()
    {
        return document;
    }

    /**
     * Adds a processing instruction for the stylesheet
     * 
     * @param link the link to the stylesheet
     * @return the DOM builder instance
     */
    public DOMBuilder setStylesheet(String link)
    {
        ProcessingInstruction pi =
            document
                .createProcessingInstruction("xml-stylesheet", String.format("type=\"text/xml\" href=\"%s\"", link)); //$NON-NLS-1$ //$NON-NLS-2$

        document.insertBefore(pi, document.getFirstChild());

        return this;
    }

    /**
     * Returns the root node
     * 
     * @return the root node
     */
    public Node getRootNode()
    {
        return rootNode;
    }

    /**
     * Returns the currently active node
     * 
     * @return the currently active node
     */
    public Node getActiveNode()
    {
        return activeNode;
    }

    /**
     * Adds a comment
     * 
     * @param comment the comment
     * @return the DOM builder instance
     */
    public DOMBuilder comment(Object comment)
    {
        Comment commentElement = document.createComment(String.valueOf(comment));

        activeNode.appendChild(commentElement);

        return this;
    }

    /**
     * Begins a new element and adds it at the current position
     * 
     * @param name the name of the element
     * @return the DOM builder instance
     */
    public DOMBuilder begin(String name)
    {
        Element element = document.createElement(name);

        activeNode.appendChild(element);
        activeNode = element;

        return this;
    }

    /**
     * Adds an element to the current element
     * 
     * @param name the name of the attribute
     * @param value the value of the attribute
     * @return the DOM builder instance
     */
    public DOMBuilder attribute(String name, Object value)
    {
        if (value != null)
        {
            ((Element) activeNode).setAttribute(name, String.valueOf(value));
        }

        return this;
    }

    /**
     * Adds text to the current element
     * 
     * @param text the text
     * @return the DOM builder instance
     */
    public DOMBuilder text(Object text)
    {
        if (text != null)
        {
            activeNode.appendChild(document.createTextNode(String.valueOf(text)));
        }

        return this;
    }

    /**
     * Adds an empty element with the specified name
     * 
     * @param name the name
     * @return the DOM builder instance
     */
    public DOMBuilder element(String name)
    {
        begin(name);
        end();

        return this;
    }

    /**
     * Adds an element with the specified name that contains the specified text
     * 
     * @param name the name
     * @param text the text
     * @return the DOM builder instance
     */
    public DOMBuilder element(String name, Object text)
    {
        begin(name);
        text(text);
        end();

        return this;
    }

    /**
     * Adds an element with the specified name and exactly one attribute. The element contains no text.
     * 
     * @param name the name
     * @param attrName the name of the attribute
     * @param attrValue the value of the attribute
     * @return the DOM builder instance
     */
    public DOMBuilder element(String name, String attrName, Object attrValue)
    {
        begin(name);
        attribute(attrName, attrValue);
        end();

        return this;
    }

    /**
     * Adds an element with the specified name and exactly one attribute. The element contains the specified text.
     * 
     * @param name the name
     * @param attrName the name of the attribute
     * @param attrValue the value of the attribute
     * @param text the text
     * @return the DOM builder instance
     */
    public DOMBuilder element(String name, String attrName, Object attrValue, Object text)
    {
        begin(name);
        attribute(attrName, attrValue);
        text(text);
        end();

        return this;
    }

    /**
     * Ends the current node and steps one node back
     * 
     * @return the DOM builder instance
     */
    public DOMBuilder end()
    {
        activeNode = activeNode.getParentNode();

        return this;
    }

    /**
     * Returns a string that contains the XML data
     * 
     * @return the XML as string
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        try
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            try
            {
                write(out, true);
            }
            finally
            {
                out.close();
            }

            return out.toString("UTF-8"); //$NON-NLS-1$
        }
        catch (IOException e)
        {
            throw new IllegalArgumentException("Could not write transformer result"); //$NON-NLS-1$
        }
    }

    /**
     * Exports the specified document to the specified stream
     * 
     * @param out the stream
     * @param formatted true if formatted
     * @throws IOException on occasion
     */
    public void write(OutputStream out, boolean formatted) throws IOException
    {
        TransformerFactory factory = TransformerFactory.newInstance();

        if (formatted)
        {
            factory.setAttribute("indent-number", 4); //$NON-NLS-1$
        }

        Transformer transformer;
        try
        {
            transformer = factory.newTransformer();
        }
        catch (TransformerConfigurationException e)
        {
            throw new IOException(String.format("Failed to create transformer: %s", e)); //$NON-NLS-1$
        }

        transformer.setParameter("encoding", "UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$

        if (formatted)
        {
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        try
        {
            transformer.transform(new DOMSource(document), new StreamResult(out));
        }
        catch (TransformerException e)
        {
            throw new IOException(String.format("Failed to transform node: %s", e)); //$NON-NLS-1$
        }
    }

}
